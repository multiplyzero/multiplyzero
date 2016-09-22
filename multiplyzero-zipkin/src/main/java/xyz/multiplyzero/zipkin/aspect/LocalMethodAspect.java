//package xyz.multiplyzero.zipkin.aspect;
//
//import com.github.kristofa.brave.Brave;
//
//import lombok.Setter;
//import xyz.multiplyzero.zipkin.client.utils.MethodDone;
//import xyz.multiplyzero.zipkin.client.utils.ZipkinUtils;
//
//
//public class LocalMethodAspect {
//    @Setter
//    private Brave brave;
//    @Setter
//    private String annotationPrefix;
//
//    public <R> R localTracer(String component, String operation, MethodDone<R> method) throws Throwable {
//        brave.localTracer().startNewSpan(component, operation);
//        brave.localTracer().submitAnnotation(annotationPrefix + ZipkinUtils.SUFFIX_BEGIN);
//        try {
//            R r = method.done();
//            localFinally(null);
//            return r;
//        } catch (Throwable e) {
//            localFinally(e);
//            throw e;
//        }
//    }
//
//    private void localFinally(Throwable e) {
//        brave.localTracer().submitAnnotation(annotationPrefix + ZipkinUtils.SUFFIX_END);
//        if (e != null) {
//            brave.localTracer().submitBinaryAnnotation(annotationPrefix + ZipkinUtils.SUFFIX_ERROR,
//                    ZipkinUtils.errorToString(e));
//            brave.localTracer().submitBinaryAnnotation(annotationPrefix + ZipkinUtils.SUFFIX_BACK,
//                    ZipkinUtils.RESULT_FAILED);
//        } else {
//            brave.localTracer().submitBinaryAnnotation(annotationPrefix + ZipkinUtils.SUFFIX_BACK,
//                    ZipkinUtils.RESULT_SUCCESS);
//        }
//        brave.localTracer().finishSpan();
//    }
//}
