package net.brokentrain.ftf.ui.gui.settings;

import java.util.HashMap;
import java.util.List;

import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;

public class SettingsRegistry {

    public static int _IS_DEBUG;

    public static String _MAJOR_VERSION = "0.3";

    public static int connectionTimeout;

    public static String customBrowser;

    public static String customBrowserArguments;

    public static String defaultHomepage;

    public static boolean displaySingleTab;

    public static String downloadDirectoryPath;

    public static boolean highlightTerms;

    public static List<ServiceEntry> availableServices;

    public static List<ServiceEntry> defaultServices;

    public static boolean focusNewTabs;

    public static boolean glimpse;

    public static boolean investigate;

    public static boolean isShellMaximized;

    public static int readTimeout;

    public static boolean openBrowserExtern;

    public static boolean openNewBrowserWindow;

    public static List<String> openHistory;

    public static String[] queryHistory;

    public static Rectangle shellBounds;

    public static boolean reportErrors;

    public static HashMap<String, ServiceEntry> services;

    public static boolean showMetadataPane;

    public static boolean showSystrayIcon;

    public static boolean showStatusbar;

    public static boolean showTabCloseButton;

    public static boolean showToolbar;

    public static boolean showTooltip;

    public static boolean showTrayPopup;

    public static boolean simpleTabs;

    public static boolean tabPositionIsTop;

    public static boolean trayOnExit;

    public static boolean trayOnStartup;

    public static boolean terrierUseDownloadDirectory;

    public static String terrierSearchDirectory;

    private static final boolean isLinux = SWT.getPlatform().equalsIgnoreCase(
            "gtk");

    private static final boolean isMac = SWT.getPlatform().equalsIgnoreCase(
            "carbon");

    private static final boolean isSolaris = SWT.getPlatform()
            .equalsIgnoreCase("motif");

    private static final boolean isWindows = SWT.getPlatform()
            .equalsIgnoreCase("win32");

    public static boolean isLinux() {
        return isLinux;
    }

    public static boolean isMac() {
        return isMac;
    }

    public static boolean isSolaris() {
        return isSolaris;
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean useSystemTray() {
        return (isWindows() || isLinux() || isMac());
    }

    private SettingsRegistry() {

    }

}
