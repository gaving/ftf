package net.brokentrain.ftf.ui.gui.util;

import java.util.regex.Pattern;

public class RegExUtil {

    private static final String URL_REGEX = "(www([\\wv\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?)|(http|ftp|https):\\/\\/[\\w]+(.[\\w]+)([\\wv\\-\\.,@?^=%&:/~\\+#]*[\\w\\-\\@?^=%&/~\\+#])?";

    private static final Pattern URL_REGEX_PATTERN = Pattern.compile(URL_REGEX);

    public static boolean isValidURL(String url) {
        return URL_REGEX_PATTERN.matcher(url).matches();
    }

    private RegExUtil() {
    }
}
