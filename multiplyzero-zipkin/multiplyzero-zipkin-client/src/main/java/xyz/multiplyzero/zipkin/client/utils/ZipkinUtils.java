package xyz.multiplyzero.zipkin.client.utils;

public class ZipkinUtils {
    // public static int ipToInt(String ip) {
    // try {
    // int index = ip.indexOf(":");
    // String ips[] = ip.substring(0, index <= 0 ? ip.length() :
    // index).split("\\.");
    // int r = 0;
    // for (int i = 0, len = ips.length; i < len; i++) {
    // r |= (Integer.parseInt(ips[i]) << (24 - i * 8));
    // }
    // return r;
    // } catch (Exception e) {
    // e.printStackTrace();
    // return 127 << 24 | 1;
    // }
    //
    // }
    //
    // public static String errorToString(Throwable e) {
    // if (e != null) {
    // StringBuilder sb = new StringBuilder();
    // for (StackTraceElement s : e.getStackTrace()) {
    // sb.append(s.toString()).append(System.lineSeparator());
    // }
    // return sb.toString();
    // }
    // return "";
    // }
    //
    // public static String methodAndArgs(String method, Object... args) {
    // StringBuilder sb = new StringBuilder(method + "(");
    // if (args.length > 0) {
    // for (Object arg : args) {
    // if (arg == null) {
    // sb.append(null + ",");
    // } else {
    // sb.append(JSONObject.toJSONString(arg) + ",");
    // }
    // }
    // sb.deleteCharAt(sb.length() - 1);
    // }
    //
    // return sb.append(")").toString();
    // }
}
