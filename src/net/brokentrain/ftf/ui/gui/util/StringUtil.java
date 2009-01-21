package net.brokentrain.ftf.ui.gui.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class StringUtil {

    public static String createFileName(String str) {

        str = str.replaceAll(" \\| ", "_");
        str = str.replaceAll(">", "_");
        str = str.replaceAll(": ", "_");
        str = str.replaceAll(" ", "_");
        str = str.replaceAll("\\?", "_");

        if (str.matches("[_]+")) {
            str = "fetcher";
        }

        return str;
    }

    public static String createPropertyName(String str) {
        str = str.replaceAll("_", " ");
        str = str.substring(0, 1).toUpperCase() + str.substring(1);
        str = str + ":";
        return str;
    }

    public static String dateToFileName(String date) {
        String separator = "_";
        date = date.replaceAll(" ", separator);
        date = date.replaceAll("\\.", separator);
        date = date.replaceAll(":", separator);
        date = date.replaceAll("-", separator);
        date = date.replaceAll("/", separator);
        return date;
    }

    /**
     * Ellipsize a string, keeping it readable
     * 
     * @param string
     *            of text
     * @param length
     *            to cut string down to
     * @return The ellipsied string.
     */
    public static String ellipsize(String string, int length) {
        String ellipsis = "...";

        if (length >= string.length()) {
            return string;
        }

        int trim = length - ellipsis.length();
        return string.substring(0, trim / 2) + ellipsis
                + string.substring(string.length() - trim / 2);
    }

    /**
     * Format the current date to a String using the selected Locale and do not
     * add the time to the String
     * 
     * @return String Formatted Date
     */
    public static String formatDate() {
        return formatDate(new Date());
    }

    /**
     * Format the date to a String using the selected Locale.
     * 
     * @param date
     *            The date to format
     * @return String Formatted Date
     */
    public static String formatDate(Date date) {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(date);
    }

    public static boolean isset(String str) {
        return ((str != null) && (str.length() > 0));
    }

    public static String join(List<String> list) {
        return join(list, null);
    }

    public static String join(List<String> list, char separator) {
        return join(list, String.valueOf(separator));
    }

    public static String join(List<String> list, String separator) {
        if (list == null) {
            return null;
        }
        if (list.isEmpty()) {
            return "";
        }
        int seplen;
        if (separator == null) {
            seplen = 0;
        } else {
            seplen = separator.length();
        }
        int len = -seplen;
        for (String s : list) {
            len += seplen;
            if (s != null) {
                len += s.length();
            }
        }
        StringBuffer buf = new StringBuffer(len);
        boolean first = true;
        for (String s : list) {
            if (first) {
                first = false;
            } else {
                if (seplen != 0) {
                    buf.append(separator);
                }
            }
            if (s != null) {
                buf.append(s);
            }
        }
        return buf.toString();
    }

    public static ArrayList<String> split(String s, char delim) {
        ArrayList<String> rv = new ArrayList<String>();
        int length = s.length();
        int cur = -1;
        int next;

        if (length == 0) {
            return rv;
        }

        while (true) {
            next = s.indexOf(delim, cur + 1);
            if (next == -1) {
                rv.add(s.substring(cur + 1));
                break;
            } else if (next == length - 1) {
                rv.add(s.substring(cur + 1, next));
                break;
            } else {
                rv.add(s.substring(cur + 1, next));
                cur = next;
            }
        }
        return rv;
    }

    private StringUtil() {
    }
}
