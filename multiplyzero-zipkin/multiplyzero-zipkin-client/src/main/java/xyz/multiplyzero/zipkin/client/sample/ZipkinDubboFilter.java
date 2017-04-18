package xyz.multiplyzero.zipkin.client.sample;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.alibaba.dubbo.rpc.RpcInvocation;

import lombok.Setter;
import xyz.multiplyzero.util.CommonUtils;
import xyz.multiplyzero.zipkin.client.IdConversion;
import xyz.multiplyzero.zipkin.client.TraceKeys;
import xyz.multiplyzero.zipkin.client.TransportHeaders;
import xyz.multiplyzero.zipkin.client.ZeroZipkin;
import zipkin.Endpoint;
import zipkin.Span;

@Activate(group = { Constants.PROVIDER, Constants.CONSUMER })
public class ZipkinDubboFilter implements Filter {
    @Setter
    private ZeroZipkin zeroZipkin = null;

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext context = RpcContext.getContext();
        String host = context.getRemoteAddressString();
        int port = context.getRemotePort();
        String serviceInterface = context.getUrl().getServiceInterface();
        String methodName = invocation.getMethodName();
        Object[] args = invocation.getArguments();
        String methodAndArgs = CommonUtils.methodAndArgs(methodName, args);
        boolean isConsumer = context.isConsumerSide();
        boolean isProvider = context.isProviderSide();
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        Endpoint endpoint = Endpoint.create("dubbo.consumer", CommonUtils.ipToInt(host), port);
        if (isConsumer) {// 消费者时候 发出请求 clientSend clientResive
            Span span = zeroZipkin.startSpan(serviceInterface);
            zeroZipkin.sendAnnotation(TraceKeys.CLIENT_SEND, endpoint);
            zeroZipkin.sendBinaryAnnotation("dubbo.consumer.url", context.getUrl().toServiceString(), endpoint);
            zeroZipkin.sendBinaryAnnotation("dubbo.consumer.method", methodAndArgs, endpoint);
            rpcInvocation.setAttachment(TransportHeaders.Sampled.getName(), "1");
            rpcInvocation.setAttachment(TransportHeaders.TraceId.getName(), IdConversion.convertToString(span.traceId));
            rpcInvocation.setAttachment(TransportHeaders.SpanId.getName(), IdConversion.convertToString(span.id));
            if (span.parentId != null) {
                rpcInvocation.setAttachment(TransportHeaders.ParentSpanId.getName(),
                        IdConversion.convertToString(span.parentId));
            }

        } else if (isProvider) {
            final String parentId = invocation.getAttachment(TransportHeaders.ParentSpanId.getName());
            final String traceId = invocation.getAttachment(TransportHeaders.TraceId.getName());
            final String id = invocation.getAttachment(TransportHeaders.SpanId.getName());
            if (traceId == null) {
                zeroZipkin.startSpan(serviceInterface);
            } else {
                zeroZipkin.startSpan(IdConversion.convertToLong(id), IdConversion.convertToLong(traceId),
                        parentId == null ? null : IdConversion.convertToLong(parentId), serviceInterface);
            }
            zeroZipkin.sendAnnotation(TraceKeys.SERVER_RECV, endpoint);
            zeroZipkin.sendBinaryAnnotation("dubbo.provider.url", context.getUrl().toServiceString(), endpoint);
            zeroZipkin.sendBinaryAnnotation("dubbo.provider.method", methodAndArgs, endpoint);
        }
        try {
            Result result = invoker.invoke(rpcInvocation);
            this.finallydo(isConsumer, isProvider, result.getException(), endpoint);
            return result;
        } catch (Exception e) {
            this.finallydo(isConsumer, isProvider, e, endpoint);
            throw e;
        }
    }

    private void finallydo(boolean isConsumer, boolean isProvider, Throwable e, Endpoint endpoint) {
        if (isConsumer) {
            zeroZipkin.sendAnnotation(TraceKeys.CLIENT_RECV, endpoint);
            if (e != null) {
                zeroZipkin.sendBinaryAnnotation("dubbo.consumer.error", CommonUtils.errorToString(e), endpoint);
                zeroZipkin.sendBinaryAnnotation("dubbo.consumer.back", "failed", endpoint);
            } else {
                zeroZipkin.sendBinaryAnnotation("dubbo.consumer.back", "success", endpoint);
            }
        } else if (isProvider) {
            zeroZipkin.sendAnnotation(TraceKeys.SERVER_SEND, endpoint);
            if (e != null) {
                zeroZipkin.sendBinaryAnnotation("dubbo.provider.error", CommonUtils.errorToString(e), endpoint);
                zeroZipkin.sendBinaryAnnotation("dubbo.provider.back", "failed", endpoint);
            } else {
                zeroZipkin.sendBinaryAnnotation("dubbo.provider.back", "success", endpoint);
            }
        }
        zeroZipkin.finishSpan();
    }
}
