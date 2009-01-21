package net.brokentrain.ftf.ui.gui.settings;

import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;

import net.brokentrain.ftf.core.settings.Config;
import net.brokentrain.ftf.core.settings.Const;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.ProxyUtil;
import net.brokentrain.ftf.ui.gui.util.URLUtil;

public class SettingsSaver {

    private Config coreConfig;

    private Config guiConfig;

    private Config servicesConfig;

    public SettingsSaver(GUI fetcherGui) {

        try {
            /* Get the usual core config path */
            coreConfig = new Config(Const.CORE_FILE);
            guiConfig = new Config(FileUtil.GUI_FILE);
            servicesConfig = new Config(Const.SERVICES_FILE);

            /* Wipe this before we save it */
            coreConfig.clear();
            guiConfig.clear();
            servicesConfig.clear();
        } catch (IOException ioe) {
            GUI.log.warn("Couldn't load configuration!", ioe);
        }
    }

    private void saveCoreBoolean(String section, String key, boolean state) {
        coreConfig.set(section, key, String.valueOf(state));
    }

    public synchronized void saveCoreSettings() {

        /* General */
        if (SettingsRegistry.investigate) {
            saveCoreBoolean("general", "investigate",
                    SettingsRegistry.investigate);
        }

        if (SettingsRegistry.glimpse) {
            saveCoreBoolean("general", "glimpse", SettingsRegistry.glimpse);
        }

        /* Connection */
        if (SettingsRegistry.readTimeout != 3000) {
            saveCoreValue("connection", "readTimeout", String
                    .valueOf(SettingsRegistry.readTimeout));
        }

        if (SettingsRegistry.connectionTimeout != 3000) {
            saveCoreValue("connection", "connectionTimeout", String
                    .valueOf(SettingsRegistry.connectionTimeout));
        }

        /* Proxy */
        saveProxySettings();

        try {
            coreConfig.save();
            GUI.log.debug("Saved core settings");
        } catch (IOException ioe) {
            GUI.log.warn("Could not save core settings!", ioe);
        }
    }

    private void saveCoreValue(String section, String key, String value) {
        coreConfig.set(section, key, value);
    }

    private void saveGUIBoolean(String section, String key, boolean state) {
        guiConfig.set(section, key, String.valueOf(state));
    }

    public synchronized void saveGUISettings() {

        /* Browser */
        if (!SettingsRegistry.customBrowser.equals("path")) {
            saveGUIValue("browser", "customBrowser",
                    SettingsRegistry.customBrowser);
        }

        if (!SettingsRegistry.customBrowserArguments.equals("value")) {
            saveGUIValue("browser", "customBrowserArguments",
                    SettingsRegistry.customBrowserArguments);
        }

        /* System tray */
        if (SettingsRegistry.showSystrayIcon) {
            saveGUIBoolean("tray", "showSystrayIcon",
                    SettingsRegistry.showSystrayIcon);
        }

        if (!SettingsRegistry.showTrayPopup) {
            saveGUIBoolean("tray", "showTrayPopup",
                    SettingsRegistry.showTrayPopup);
        }

        if (SettingsRegistry.trayOnExit) {
            saveGUIBoolean("tray", "trayOnExit", SettingsRegistry.trayOnExit);
        }

        if (SettingsRegistry.trayOnStartup) {
            saveGUIBoolean("tray", "trayOnStartup",
                    SettingsRegistry.trayOnStartup);
        }

        /* View */
        if (!SettingsRegistry.focusNewTabs) {
            saveGUIBoolean("view", "focusNewTabs",
                    SettingsRegistry.focusNewTabs);
        }

        if (SettingsRegistry.displaySingleTab) {
            saveGUIBoolean("view", "displaySingleTab",
                    SettingsRegistry.displaySingleTab);
        }

        if (!SettingsRegistry.reportErrors) {
            saveGUIBoolean("view", "reportErrors",
                    SettingsRegistry.reportErrors);
        }

        if (!SettingsRegistry.showTabCloseButton) {
            saveGUIBoolean("view", "showTabCloseButton",
                    SettingsRegistry.showTabCloseButton);
        }

        if (!SettingsRegistry.showStatusbar) {
            saveGUIBoolean("view", "showStatusbar",
                    SettingsRegistry.showStatusbar);
        }

        if (!SettingsRegistry.showToolbar) {
            saveGUIBoolean("view", "showToolbar", SettingsRegistry.showToolbar);
        }

        if (!SettingsRegistry.simpleTabs) {
            saveGUIBoolean("view", "simpleTabs", SettingsRegistry.simpleTabs);
        }

        if (SettingsRegistry.tabPositionIsTop) {
            saveGUIBoolean("view", "tabPositionIsTop",
                    SettingsRegistry.tabPositionIsTop);
        }

        /* Browser tab */
        if (!SettingsRegistry.defaultHomepage.equals(URLUtil.FTF_START)) {
            saveGUIValue("browserTab", "homepage",
                    SettingsRegistry.defaultHomepage);
        }

        /* Transfer tab */
        if (!SettingsRegistry.downloadDirectoryPath.equals(System
                .getProperty("core.dir"))) {
            saveGUIValue("transferTab", "downloadDirectory",
                    SettingsRegistry.downloadDirectoryPath);
        }

        /* Query tab */
        if (!SettingsRegistry.showMetadataPane) {
            saveGUIBoolean("queryTab", "showMetadataPane",
                    SettingsRegistry.showMetadataPane);
        }

        if (!SettingsRegistry.highlightTerms) {
            saveGUIBoolean("queryTab", "highlightTerms",
                    SettingsRegistry.highlightTerms);
        }

        if (!SettingsRegistry.showTooltip) {
            saveGUIBoolean("queryTab", "showTooltip",
                    SettingsRegistry.showTooltip);
        }

        /* Save our open history */
        saveOpenHistory();

        try {
            guiConfig.save();
            GUI.log.debug("Saved gui settings");
        } catch (IOException ioe) {
            GUI.log.warn("Could not save gui settings!", ioe);
        }
    }

    private void saveGUIValue(String section, String key, String value) {
        guiConfig.set(section, key, value);
    }

    private void saveOpenHistory() {

        GUI.log.debug("Saving recent items");

        if ((SettingsRegistry.openHistory != null)
                && (!SettingsRegistry.openHistory.isEmpty())) {

            File openHistoryFile = new File(FileUtil
                    .getResource("recent_items"));

            FileUtil.writeObject(SettingsRegistry.openHistory, openHistoryFile);
        }
    }

    private void saveProxySettings() {
        Hashtable<String, String> settings = ProxyUtil.proxySettings;

        for (String key : settings.keySet()) {
            String value = settings.get(key);
            saveCoreValue("proxy", key, value);
        }
    }

    public synchronized void saveServiceSettings() {

        /* Check that we have any available sources */
        if (SettingsRegistry.services != null) {

            HashMap<String, ServiceEntry> services = SettingsRegistry.services;

            /* Save the status of each to the core preferences */
            for (String serviceName : services.keySet()) {

                ServiceEntry serviceEntry = services.get(serviceName);

                /* Save the essential properties */
                servicesConfig.set(serviceName, "enabled", String
                        .valueOf(serviceEntry.isSelected()));
                servicesConfig.set(serviceName, "controller", serviceEntry
                        .getController());
                servicesConfig.set(serviceName, "description", serviceEntry
                        .getDescription());
                servicesConfig.set(serviceName, "max_results", serviceEntry
                        .getMaxResults());

                /* Save extended properties */
                HashMap<String, String> extendedProperties = serviceEntry
                        .getProperties();

                /* Iterate over the keys contained in this service */
                for (String key : extendedProperties.keySet()) {

                    /* Get the value for this key */
                    String value = extendedProperties.get(key);

                    /* Save to section */
                    servicesConfig.set(serviceName, key, value);
                }
            }
        }

        try {
            servicesConfig.save();
            GUI.log.debug("Saved service settings");
        } catch (IOException ioe) {
            GUI.log.warn("Could not save service settings!", ioe);
        }
    }

}
