package net.brokentrain.ftf.ui.gui.properties;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.Statusbar;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.ProxyUtil;

import org.eclipse.swt.SWT;

public class PropertyChangeManager {

    private GUI fetcherGui;

    private boolean displaySingleTab;

    private List<ServiceEntry> defaultServices;

    private HashMap<String, ServiceEntry> services;

    private boolean focusNewTabs;

    private boolean openBrowserExtern;

    private boolean openBrowserInNewTab;

    private boolean reportErrors;

    private boolean highlightTerms;

    private boolean glimpse;

    private boolean investigate;

    private boolean showMetadataPane;

    private boolean showSystrayIcon;

    private boolean showTabCloseButton;

    private boolean showTrayPopup;

    private boolean showTooltip;

    private boolean showStatusbar;

    private boolean showToolbar;

    private boolean simpleTabs;

    private boolean tabPositionIsTop;

    private boolean terrierUseDownloadDirectory;

    private boolean trayOnExit;

    private boolean trayOnStartup;

    private Hashtable<String, String> proxySettingsSave;

    private int connectionTimeout;

    private int readTimeout;

    private String customBrowserArguments;

    private String customBrowserPath;

    private String defaultHomepage;

    private String downloadDirectoryPath;

    private String terrierSearchDirectory;

    private Statusbar statusBar;

    public PropertyChangeManager(GUI fetcherGui) {
        this.fetcherGui = fetcherGui;

        this.statusBar = fetcherGui.getStatusbar();
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public String getCustomBrowserArguments() {
        return customBrowserArguments;
    }

    public String getCustomBrowserPath() {
        return customBrowserPath;
    }

    public String getDefaultHomepage() {
        return defaultHomepage;
    }

    public List<ServiceEntry> getDefaultServices() {
        return defaultServices;
    }

    public String getDownloadDirectoryPath() {
        return downloadDirectoryPath;
    }

    public int getMaxConnectionCount() {
        return readTimeout;
    }

    public Hashtable<String, String> getProxySettingsSave() {
        return proxySettingsSave;
    }

    public HashMap<String, ServiceEntry> getServices() {
        return services;
    }

    public String getTerrierSearchDirectory() {
        return terrierSearchDirectory;
    }

    public boolean getTerrierUseDownloadDirectory() {
        return terrierUseDownloadDirectory;
    }

    public boolean isDisplaySingleTab() {
        return displaySingleTab;
    }

    public boolean isFocusNewTabs() {
        return focusNewTabs;
    }

    public boolean isGlimpsing() {
        return glimpse;
    }

    public boolean isHighlightingTerms() {
        return highlightTerms;
    }

    public boolean isInvestigating() {
        return investigate;
    }

    public boolean isOpenBrowserExtern() {
        return openBrowserExtern;
    }

    public boolean isOpenBrowserInNewTab() {
        return openBrowserInNewTab;
    }

    public boolean isReportErrors() {
        return reportErrors;
    }

    public boolean isShowMetadataPane() {
        return showMetadataPane;
    }

    public boolean isShowStatusbar() {
        return showStatusbar;
    }

    public boolean isShowSystrayIcon() {
        return showSystrayIcon;
    }

    public boolean isShowTabCloseButton() {
        return showTabCloseButton;
    }

    public boolean isShowToolbar() {
        return showToolbar;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public boolean isShowTrayPopup() {
        return showTrayPopup;
    }

    public boolean isSimpleTabs() {
        return simpleTabs;
    }

    public boolean isTabPositionTop() {
        return tabPositionIsTop;
    }

    public boolean isTrayOnExit() {
        return trayOnExit;
    }

    public boolean isTrayOnStartup() {
        return trayOnStartup;
    }

    public void saveProperties() {

        /* Browser */
        SettingsRegistry.customBrowser = customBrowserPath;
        SettingsRegistry.customBrowserArguments = customBrowserArguments;

        /* Connection */
        SettingsRegistry.readTimeout = readTimeout;
        SettingsRegistry.connectionTimeout = connectionTimeout;

        SettingsRegistry.defaultServices = defaultServices;

        /* Proxy */
        ProxyUtil.setUseProxy(proxySettingsSave.get("proxySet"));
        ProxyUtil.setHost(proxySettingsSave.get("proxyHost"));
        ProxyUtil.setPort(proxySettingsSave.get("proxyPort"));

        if (Boolean.valueOf(proxySettingsSave.get("proxySet"))) {

            Properties sysProps = System.getProperties();

            String proxyHost = proxySettingsSave.get("proxyHost");
            String proxyPort = proxySettingsSave.get("proxyPort");

            GUI.log.debug("Proxy information detected, setting..");
            GUI.log.debug("Host: " + proxyHost + " Port:" + proxyPort);
            statusBar.setText("New proxy information set.");

            /* Set proxy properties */
            sysProps.put("proxySet", "true");
            sysProps.put("proxyHost", proxyHost);
            sysProps.put("proxyPort", proxyPort);

            /* Set new properties */
            System.setProperties(sysProps);
        }

        /* System Tray */
        if (SettingsRegistry.useSystemTray()
                && (SettingsRegistry.showSystrayIcon != showSystrayIcon)) {
            SettingsRegistry.showSystrayIcon = showSystrayIcon;
            fetcherGui.enableSystrayIcon(SettingsRegistry.showSystrayIcon);
        }
        SettingsRegistry.trayOnStartup = trayOnStartup;
        SettingsRegistry.trayOnExit = trayOnExit;
        SettingsRegistry.showTrayPopup = showTrayPopup;

        /* View */
        if (SettingsRegistry.simpleTabs != simpleTabs) {
            fetcherGui.getTabManager().getTabFolder().setSimple(simpleTabs);
            SettingsRegistry.simpleTabs = simpleTabs;
        }

        if (SettingsRegistry.displaySingleTab != displaySingleTab) {
            SettingsRegistry.displaySingleTab = displaySingleTab;
            fetcherGui.getTabManager().getTabFolder().setTabHeight(
                    SettingsRegistry.displaySingleTab ? 0 : -1);

            if (SettingsRegistry.displaySingleTab) {
                fetcherGui.getTabManager().getTabFolder().setTabPosition(
                        SWT.BOTTOM);
            } else {
                fetcherGui.getTabManager().getTabFolder().setTabPosition(
                        SettingsRegistry.tabPositionIsTop ? SWT.TOP
                                : SWT.BOTTOM);
            }

            fetcherGui.getTabManager().getTabFolder().layout();
        }

        SettingsRegistry.showTabCloseButton = showTabCloseButton;
        SettingsRegistry.focusNewTabs = focusNewTabs;
        SettingsRegistry.reportErrors = reportErrors;
        SettingsRegistry.showStatusbar = showStatusbar;
        SettingsRegistry.showToolbar = showToolbar;

        /* Browser Tab */
        SettingsRegistry.defaultHomepage = defaultHomepage;

        /* Query tab */
        SettingsRegistry.showMetadataPane = showMetadataPane;

        /* Transfer tab */
        SettingsRegistry.downloadDirectoryPath = downloadDirectoryPath;

        /* Terrier service */
        SettingsRegistry.terrierUseDownloadDirectory = terrierUseDownloadDirectory;
        SettingsRegistry.terrierSearchDirectory = terrierSearchDirectory;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public void setCustomBrowserArguments(String customBrowserArguments) {
        this.customBrowserArguments = customBrowserArguments;
    }

    public void setCustomBrowserPath(String customBrowserPath) {
        this.customBrowserPath = customBrowserPath;
    }

    public void setDefaultHomepage(String defaultHomepage) {
        this.defaultHomepage = defaultHomepage;
    }

    public void setDefaultServices(List<ServiceEntry> defaultServices) {
        this.defaultServices = defaultServices;
    }

    public void setDisplaySingleTab(boolean displaySingleTab) {
        this.displaySingleTab = displaySingleTab;
    }

    public void setDownloadDirectoryPath(String downloadDirectoryPath) {
        this.downloadDirectoryPath = downloadDirectoryPath;
    }

    public void setFocusNewTabs(boolean focusNewTabs) {
        this.focusNewTabs = focusNewTabs;
    }

    public void setGlimpse(boolean glimpse) {
        this.glimpse = glimpse;
    }

    public void setHighlightTerms(boolean highlightTerms) {
        this.highlightTerms = highlightTerms;
    }

    public void setInvestigate(boolean investigate) {
        this.investigate = investigate;
    }

    public void setOpenBrowserExtern(boolean openBrowserExtern) {
        this.openBrowserExtern = openBrowserExtern;
    }

    public void setOpenBrowserInNewTab(boolean openBrowserInNewTab) {
        this.openBrowserInNewTab = openBrowserInNewTab;
    }

    public void setProxySettings(Hashtable<String, String> proxySettings) {
        proxySettingsSave = new Hashtable<String, String>();
        for (String key : proxySettings.keySet()) {
            String value = proxySettings.get(key);
            proxySettingsSave.put(key, value);
        }
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void setReportErrors(boolean reportErrors) {
        this.reportErrors = reportErrors;
    }

    public void setServices(HashMap<String, ServiceEntry> services) {
        this.services = services;
    }

    public void setShowMetadataPane(boolean showMetadataPane) {
        this.showMetadataPane = showMetadataPane;
    }

    public void setShowStatusbar(boolean showStatusbar) {
        this.showStatusbar = showStatusbar;
    }

    public void setShowSystrayIcon(boolean showSystrayIcon) {
        this.showSystrayIcon = showSystrayIcon;
    }

    public void setShowTabCloseButton(boolean showTabCloseButton) {
        this.showTabCloseButton = showTabCloseButton;
    }

    public void setShowToolbar(boolean showToolbar) {
        this.showToolbar = showToolbar;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public void setShowTrayPopup(boolean showTrayPopup) {
        this.showTrayPopup = showTrayPopup;
    }

    public void setSimpleTabs(boolean simpleTabs) {
        this.simpleTabs = simpleTabs;
    }

    public void setTabPositionTop(boolean tabPositionIsTop) {
        this.tabPositionIsTop = tabPositionIsTop;
    }

    public void setTerrierSearchDirectory(String terrierSearchDirectory) {
        this.terrierSearchDirectory = terrierSearchDirectory;
    }

    public void setTerrierUseDownloadDirectory(
            boolean terrierUseDownloadDirectory) {
        this.terrierUseDownloadDirectory = terrierUseDownloadDirectory;
    }

    public void setTrayOnExit(boolean trayOnExit) {
        this.trayOnExit = trayOnExit;
    }

    public void setTrayOnStartup(boolean trayOnStartup) {
        this.trayOnStartup = trayOnStartup;
    }

}
