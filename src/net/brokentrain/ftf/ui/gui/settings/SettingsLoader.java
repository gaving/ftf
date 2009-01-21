package net.brokentrain.ftf.ui.gui.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.brokentrain.ftf.core.settings.Config;
import net.brokentrain.ftf.core.settings.Const;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.ServiceManager;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.ProxyUtil;

public class SettingsLoader {

    private ServiceManager serviceManager;

    private Config coreConfig;

    private Config guiConfig;

    public SettingsLoader(GUI fetcherGui) {
        serviceManager = SettingsManager.getServiceManager();

        try {

            if (!FileUtil.exists(Const.CONFIG_DIRECTORY)) {
                FileUtil.makeDirectory(Const.CONFIG_DIRECTORY);
            }

            if (!FileUtil.exists(Const.CORE_FILE)) {
                FileUtil.makeFile(Const.CORE_FILE);
            }

            if (!FileUtil.exists(FileUtil.GUI_FILE)) {
                FileUtil.makeFile(FileUtil.GUI_FILE);
            }

            /* Get the core preferences */
            coreConfig = new Config(Const.CORE_FILE);
            guiConfig = new Config(FileUtil.GUI_FILE);
        } catch (IOException e) {
            GUI.log.warn("Couldn't load core level configuration");
        }
    }

    public boolean getBoolean(Config config, String section, String key,
            boolean init) {
        String value = config.get(section, key);

        if (value != null) {
            return Boolean.valueOf(value).booleanValue();
        }

        return init;
    }

    public boolean getCoreBoolean(String section, String key, boolean init) {
        return getBoolean(coreConfig, section, key, init);
    }

    public String getCoreValue(String section, String key) {
        return getValue(coreConfig, section, key, "", true);
    }

    public String getCoreValue(String section, String key, String init) {
        return getValue(coreConfig, section, key, init, true);
    }

    public boolean getGUIBoolean(String section, String key, boolean init) {
        return getBoolean(guiConfig, section, key, init);
    }

    public String getGUIValue(String section, String key, String init) {
        return getValue(guiConfig, section, key, init, true);
    }

    public String getValue(Config config, String section, String key,
            String init, boolean trim) {
        String value = config.get(section, key);

        if (value == null) {
            value = init;
        }

        if (trim) {
            return value.trim();
        }

        return value;
    }

    public List<ServiceEntry> loadAvailableServices() {
        return serviceManager.getAvailableServices();
    }

    public String[] loadComboHistory() {

        /* Load in combo history */
        File historyFile = new File(FileUtil.getResource("search_history"));

        if (historyFile.exists()) {
            Object historyObject = FileUtil.readObject(historyFile);

            if ((historyObject != null) && (historyObject instanceof String[])) {

                String[] queryHistory = (String[]) historyObject;

                if (queryHistory.length > 0) {
                    return queryHistory;
                }
            } else {
                GUI.log.debug("Couldn't load previous query history");
            }
        }

        return null;
    }

    public List<ServiceEntry> loadDefaultServices() {
        return serviceManager.getSelectedServices();
    }

    public void loadProxySettings() {

        String proxySet = "false";
        String proxyHost = "";
        String proxyPort = "";

        if (coreConfig.hasSection("proxy")) {
            proxySet = getCoreValue("proxy", "proxySet");
            proxyHost = getCoreValue("proxy", "proxyHost");
            proxyPort = getCoreValue("proxy", "proxyPort");
        }

        ProxyUtil.setUseProxy(proxySet);
        ProxyUtil.setHost(proxyHost);
        ProxyUtil.setPort(proxyPort);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> loadRecentHistory() {

        /* Load in combo history */
        File historyFile = new File(FileUtil.getResource("recent_history"));

        if (historyFile.exists()) {
            Object historyObject = FileUtil.readObject(historyFile);

            if ((historyObject != null) && (historyObject instanceof ArrayList)) {

                ArrayList<String> recentHistory = (ArrayList<String>) historyObject;

                if (!recentHistory.isEmpty()) {
                    return recentHistory;
                }
            } else {
                GUI.log.debug("Couldn't load recent history");
            }
        }

        return new ArrayList<String>();
    }

    public HashMap<String, ServiceEntry> loadServices() {
        return serviceManager.getServiceEntries();
    }

}
