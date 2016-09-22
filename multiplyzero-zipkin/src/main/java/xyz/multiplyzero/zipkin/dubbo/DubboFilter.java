package xyz.multiplyzero.zipkin.dubbo;

import static com.github.kristofa.brave.IdConversion.convertToLong;

import java.util.ArrayList;
import java.util.Collection;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.ClientRequestAdapter;
import com.github.kristofa.brave.ClientResponseAdapter;
import com.github.kristofa.brave.IdConversion;
import com.github.kristofa.brave.KeyValueAnnotation;
import com.github.kristofa.brave.ServerRequestAdapter;
import com.github.kristofa.brave.ServerResponseAdapter;
import com.github.kristofa.brave.SpanId;
import com.github.kristofa.brave.TraceData;
import com.github.kristofa.brave.http.BraveHttpHeaders;
import com.twitter.zipkin.gen.Endpoint;

import lombok.Setter;
import xyz.multiplyzero.zipkin.client.utils.ZipkinUtils;

@Activate(group = { Constants.PROVIDER, Constants.CONSUMER })
public class DubboFilter implements Filter {
    @Setter
    private Brave brave = null;

    @Override
    public Result invoke(Invoker<?> invoker, final Invocation invocation) throws RpcException {
        final RpcContext context = RpcContext.getContext();
        final String host = context.getRemoteAddressString();
        final int port = context.getRemotePort();
        final String serviceInterface = context.getUrl().getServiceInterface();
        String methodName = invocation.getMethodName();
        Object[] args = invocation.getArguments();
        final String methodAndArgs = ZipkinUtils.methodAndArgs(methodName, args);
        boolean isConsumer = context.isConsumerSide();
        boolean isProvider = context.isProviderSide();
        final RpcInvocation rpcInvocation = (RpcInvocation) invocation;

        if (isConsumer) {// 消费者时候 发出请求 clientSend clientResive
            brave.clientRequestInterceptor().handle(new ClientRequestAdapter() {
                @Override
                public Endpoint serverAddress() {
                    Endpoint endPoint = Endpoint.create("dubbo.consumer", ZipkinUtils.ipToInt(host), port);
                    return endPoint;
                }

                @Override
                public Collection<KeyValueAnnotation> requestAnnotations() {
                    Collection<KeyValueAnnotation> kvs = new ArrayList<>();
                    kvs.add(KeyValueAnnotation.create("dubbo.consumer.url", context.getUrl().toServiceString()));
                    kvs.add(KeyValueAnnotation.create("dubbo.consumer.method", methodAndArgs));
                    return kvs;
                }

                @Override
                public String getSpanName() {
                    String spanName = serviceInterface;
                    return spanName;
                }

                @Override
                public void addSpanIdToRequest(SpanId spanId) {
                    if (spanId == null) {
                        rpcInvocation.setAttachment(BraveHttpHeaders.Sampled.getName(), "0");
                    } else {
                        rpcInvocation.setAttachment(BraveHttpHeaders.Sampled.getName(), "1");
                        rpcInvocation.setAttachment(BraveHttpHeaders.TraceId.getName(),
                                IdConversion.convertToString(spanId.traceId));
                        rpcInvocation.setAttachment(BraveHttpHeaders.SpanId.getName(),
                                IdConversion.convertToString(spanId.spanId));
                        if (spanId.nullableParentId() != null) {
                            rpcInvocation.setAttachment(BraveHttpHeaders.ParentSpanId.getName(),
                                    IdConversion.convertToString(spanId.parentId));
                        }
                    }
                }
            });
        } else if (isProvider) {
            brave.serverRequestInterceptor().handle(new ServerRequestAdapter() {
                @Override
                public Collection<KeyValueAnnotation> requestAnnotations() {
                    Collection<KeyValueAnnotation> kvs = new ArrayList<>();
                    kvs.add(KeyValueAnnotation.create("dubbo.provider.url", context.getUrl().toServiceString()));
                    kvs.add(KeyValueAnnotation.create("dubbo.provider.method", methodAndArgs));
                    return kvs;
                }

                @Override
                public TraceData getTraceData() {
                    final String sampled = invocation.getAttachment(BraveHttpHeaders.Sampled.getName());
                    if (sampled != null) {
                        if (sampled.equals("0") || sampled.toLowerCase().equals("false")) {
                            return TraceData.builder().sample(false).build();
                        } else {
                            final String parentSpanId = invocation
                                    .getAttachment(BraveHttpHeaders.ParentSpanId.getName());
                            final String traceId = invocation.getAttachment(BraveHttpHeaders.TraceId.getName());
                            final String spanId = invocation.getAttachment(BraveHttpHeaders.SpanId.getName());

                            if (traceId != null && spanId != null) {
                                SpanId span = SpanId.builder().traceId(convertToLong(traceId))
                                        .spanId(convertToLong(spanId))
                                        .parentId(parentSpanId == null ? null : convertToLong(parentSpanId)).build();
                                return TraceData.builder().sample(true).spanId(span).build();
                            }
                        }
                    }
                    return TraceData.builder().build();
                }

                @Override
                public String getSpanName() {
                    String spanName = serviceInterface;
                    return spanName;
                }
            });
        }

        try {
            Result result = invoker.invoke(rpcInvocation);
            this.finallydo(isConsumer, isProvider, result.getException());
            return result;
        } catch (Exception e) {
            this.finallydo(isConsumer, isProvider, e);
            throw e;
        }
    }

    private void finallydo(boolean isConsumer, boolean isProvider, final Throwable e) {
        if (isConsumer) {
            brave.clientResponseInterceptor().handle(new ClientResponseAdapter() {
                @Override
                public Collection<KeyValueAnnotation> responseAnnotations() {
                    Collection<KeyValueAnnotation> kvs = new ArrayList<>();
                    if (e != null) {
                        kvs.add(KeyValueAnnotation.create("dubbo.consumer.error", ZipkinUtils.errorToString(e)));
                        kvs.add(KeyValueAnnotation.create("dubbo.consumer.success", "faild"));
                    } else {
                        kvs.add(KeyValueAnnotation.create("dubbo.consumer.success", "success"));
                    }
                    return kvs;
                }
            });
        } else if (isProvider) {
            brave.serverResponseInterceptor().handle(new ServerResponseAdapter() {
                @Override
                public Collection<KeyValueAnnotation> responseAnnotations() {
                    Collection<KeyValueAnnotation> kvs = new ArrayList<>();
                    if (e != null) {
                        kvs.add(KeyValueAnnotation.create("dubbo.provider.error", ZipkinUtils.errorToString(e)));
                        kvs.add(KeyValueAnnotation.create("dubbo.provider.success", "faild"));
                    } else {
                        kvs.add(KeyValueAnnotation.create("dubbo.provider.success", "success"));
                    }
                    return kvs;
                }
            });
        }
    }
}
