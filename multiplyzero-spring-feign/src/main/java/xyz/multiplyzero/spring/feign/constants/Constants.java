package xyz.multiplyzero.spring.feign.constants;

import java.util.regex.Pattern;

public class Constants {

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String DEFAULT_CONFIG_NAMESPACE = "eureka";

    public static final String DEFAULT_CONFIG_FILE = "eureka-client";
}
