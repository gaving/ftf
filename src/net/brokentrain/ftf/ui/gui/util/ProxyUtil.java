package net.brokentrain.ftf.ui.gui.util;

import java.util.Hashtable;

public class ProxyUtil {

    public static Hashtable<String, String> proxySettings = new Hashtable<String, String>();

    public static boolean isUseProxy() {
        return proxySettings.get("proxySet").equals("true");
    }

    public static void setHost(String host) {
        proxySettings.put("proxyHost", host);
    }

    public static void setPort(String port) {
        proxySettings.put("proxyPort", port);
    }

    public static void setUseProxy(String use) {
        proxySettings.put("proxySet", use);
    }

    private ProxyUtil() {
    }
}
