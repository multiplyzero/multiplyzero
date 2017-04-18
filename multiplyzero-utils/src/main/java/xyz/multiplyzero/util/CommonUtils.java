package xyz.multiplyzero.util;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSONObject;

public final class CommonUtils {
    public static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    public static int envOr(String key, int fallback) {
        return System.getenv(key) != null ? Integer.parseInt(System.getenv(key)) : fallback;
    }

    public static String envOr(String key, String fallback) {
        return System.getenv(key) != null ? System.getenv(key) : fallback;
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(errorMessageTemplate, errorMessageArgs));
        }
    }

    public static <T> T checkNotNull(T reference, String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
        return reference;
    }

    public static <T extends Comparable<? super T>> List<T> sortedList(Collection<T> in) {
        if (in == null || in.isEmpty()) {
            return Collections.emptyList();
        }
        if (in.size() == 1) {
            return Collections.singletonList(in.iterator().next());
        }
        Object[] array = in.toArray();
        Arrays.sort(array);
        List result = Arrays.asList(array);
        return Collections.unmodifiableList(result);
    }

    public static long midnightUTC(long epochMillis) {
        Calendar day = Calendar.getInstance(UTC);
        day.setTimeInMillis(epochMillis);
        day.set(Calendar.MILLISECOND, 0);
        day.set(Calendar.SECOND, 0);
        day.set(Calendar.MINUTE, 0);
        day.set(Calendar.HOUR_OF_DAY, 0);
        return day.getTimeInMillis();
    }

    public static List<Date> getDays(long endTs, Long lookback) {
        long to = midnightUTC(endTs);
        long from = midnightUTC(endTs - (lookback != null ? lookback : endTs));

        List<Date> days = new ArrayList<>();
        for (long time = from; time <= to; time += TimeUnit.DAYS.toMillis(1)) {
            days.add(new Date(time));
        }
        return days;
    }

    public static long lowerHexToUnsignedLong(String lowerHex) {
        char[] array = lowerHex.toCharArray();
        if (array.length < 1 || array.length > 16) {
            throw isntLowerHexLong(lowerHex);
        }
        long result = 0;
        for (char c : array) {
            result <<= 4;
            if (c >= '0' && c <= '9') {
                result |= c - '0';
            } else if (c >= 'a' && c <= 'f') {
                result |= c - 'a' + 10;
            } else {
                throw isntLowerHexLong(lowerHex);
            }
        }
        return result;
    }

    public static NumberFormatException isntLowerHexLong(String lowerHex) {
        throw new NumberFormatException(lowerHex + " should be a 1 to 16 character lower-hex string with no prefix");
    }

    public static String toLowerHex(long v) {
        char[] data = new char[16];
        writeHexLong(data, 0, v);
        return new String(data);
    }

    public static void writeHexLong(char[] data, int pos, long v) {
        writeHexByte(data, pos + 0, (byte) ((v >>> 56L) & 0xff));
        writeHexByte(data, pos + 2, (byte) ((v >>> 48L) & 0xff));
        writeHexByte(data, pos + 4, (byte) ((v >>> 40L) & 0xff));
        writeHexByte(data, pos + 6, (byte) ((v >>> 32L) & 0xff));
        writeHexByte(data, pos + 8, (byte) ((v >>> 24L) & 0xff));
        writeHexByte(data, pos + 10, (byte) ((v >>> 16L) & 0xff));
        writeHexByte(data, pos + 12, (byte) ((v >>> 8L) & 0xff));
        writeHexByte(data, pos + 14, (byte) (v & 0xff));
    }

    public static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
            'f' };

    public static void writeHexByte(char[] data, int pos, byte b) {
        data[pos + 0] = HEX_DIGITS[(b >> 4) & 0xf];
        data[pos + 1] = HEX_DIGITS[b & 0xf];
    }

    public static AssertionError assertionError(String message, Throwable cause) {
        AssertionError error = new AssertionError(message);
        error.initCause(cause);
        throw error;
    }

    public static int ipToInt(String ip) {
        try {
            int index = ip.indexOf(":");
            String ips[] = ip.substring(0, index <= 0 ? ip.length() : index).split("\\.");
            int r = 0;
            for (int i = 0, len = ips.length; i < len; i++) {
                r |= (Integer.parseInt(ips[i]) << (24 - i * 8));
            }
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return 127 << 24 | 1;
        }

    }

    public static String errorToString(Throwable e) {
        if (e != null) {
            StringBuilder sb = new StringBuilder();
            for (StackTraceElement s : e.getStackTrace()) {
                sb.append(s.toString()).append(System.lineSeparator());
            }
            return sb.toString();
        }
        return "";
    }

    public static String methodAndArgs(String method, Object... args) {
        StringBuilder sb = new StringBuilder(method + "(");
        if (args.length > 0) {
            for (Object arg : args) {
                if (arg == null) {
                    sb.append(null + ",");
                } else {
                    sb.append(JSONObject.toJSONString(arg) + ",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.append(")").toString();
    }

    private CommonUtils() {
    }
}
