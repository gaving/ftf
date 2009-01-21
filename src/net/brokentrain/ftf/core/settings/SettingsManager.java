package net.brokentrain.ftf.core.settings;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;

public class SettingsManager {

    private static final Logger log = Logger.getLogger(SettingsManager.class);

    private static SettingsManager instance;

    /**
     * Return the associated service manager.
     * 
     * @return The service manager.
     */
    public static ServiceManager getServiceManager() {
        return ServiceManager.getServiceManager();
    }

    /**
     * Gets an existing Source Manager.
     * 
     * @return The existing service managers instance.
     */
    public static SettingsManager getSettingsManager() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    private ArrayList<NodeFilter> tokens;

    private ArrayList<Pattern> patterns;

    private ArrayList<Pattern> blacklist;

    private Config coreConfig;

    private boolean proxySet;

    private String proxyHost;

    private String proxyPort;

    private boolean glimpse;

    private boolean investigate;

    /**
     * Constructs a new Source Manager.
     */
    public SettingsManager() {

        /* Compile blacklist for use later */
        compileBlackList();

        /* Compile custom patterns for use later */
        compilePatterns();

        /* Compile potential string tokens */
        compileTokens();

        ConnectionManager manager = Parser.getConnectionManager();

        /* Enable redirection processing by default */
        manager.setRedirectionProcessingEnabled(true);

        /* Enable cookie processing by default */
        manager.setCookieProcessingEnabled(true);

        try {

            coreConfig = new Config(Const.CORE_FILE);
        } catch (IOException ioe) {
            log.error("Core configuration file does not exist!", ioe);
        }

        /* No core preferences are found, use defaults */
        if (coreConfig == null) {
            log
                    .warn("Could not find core configuration file, using default preferences");
            return;
        }

        /* Use existing properties */
        Properties sysProps = System.getProperties();

        /* Attempt to load in proxy settings */
        String proxySet = coreConfig.get("proxy", "proxySet");

        /* Proxy specific preferences */
        if ((proxySet != null) && Boolean.valueOf(proxySet)) {
            log.debug("Proxy information detected, setting..");

            /* Show that we are using a proxy */
            this.proxySet = true;

            /* Get additional proxy information */
            proxyHost = coreConfig.get("proxy", "proxyHost");
            proxyPort = coreConfig.get("proxy", "proxyPort");

            if ((proxyHost != null) && (proxyPort != null)) {
                log.debug("Host: " + proxyHost + " Port:" + proxyPort);

                /* Set proxy properties */
                sysProps.put("proxySet", "true");
                sysProps.put("proxyHost", proxyHost);
                sysProps.put("proxyPort", proxyPort);

                /* Set new properties */
                System.setProperties(sysProps);
            }
        }

        /* Read timeout values */
        String readTimeout = coreConfig.get("connection", "readTimeout");
        String connectTimeout = coreConfig.get("connection",
                "connectionTimeout");

        if ((readTimeout != null) && !readTimeout.equals("")) {
            log.debug("Read timeout information found, setting..");
            log.debug("Value: " + readTimeout);
            sysProps.put("sun.net.client.defaultReadTimeout", readTimeout);
        } else {

            /* Default read timeout */
            log.debug("Setting default read timeout..");
            sysProps.put("sun.net.client.defaultReadTimeout", "3000");
        }

        if ((connectTimeout != null) && !connectTimeout.equals("")) {
            log.debug("Connection timeout information found, setting..");
            log.debug("Value: " + connectTimeout);
            sysProps
                    .put("sun.net.client.defaultConnectTimeout", connectTimeout);
        } else {

            /* Default connect timeout */
            log.debug("Setting default connect timeout..");
            sysProps.put("sun.net.client.defaultConnectTimeout", "3000");
        }

        glimpse = Boolean.valueOf(coreConfig.get("general", "glimpse"));
        investigate = Boolean.valueOf(coreConfig.get("general", "investigate"));

        /* Set the properties */
        System.setProperties(sysProps);
    }

    private void compileBlackList() {
        blacklist = new ArrayList<Pattern>();

        try {
            BufferedReader file = new BufferedReader(new FileReader(
                    Const.BLACKLIST_FILE));
            String pattern = "";
            while ((pattern = file.readLine()) != null) {
                blacklist.add(Pattern
                        .compile(pattern, Pattern.CASE_INSENSITIVE));
            }
            file.close();
        } catch (IOException ioe) {
            log.error("Could not compile blacklist pattern!", ioe);
        }
    }

    private void compilePatterns() {
        patterns = new ArrayList<Pattern>();

        try {
            BufferedReader file = new BufferedReader(new FileReader(
                    Const.PATTERNS_FILE));
            String pattern = "";
            while ((pattern = file.readLine()) != null) {
                patterns
                        .add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
            }
            file.close();
        } catch (IOException ioe) {
            log.error("Could not compile pattern!", ioe);
        }
    }

    private void compileTokens() {
        tokens = new ArrayList<NodeFilter>();

        /* Look for standard HTML anchor tag */
        NodeFilter anchorFilter = new TagNameFilter("A");

        try {
            BufferedReader file = new BufferedReader(new FileReader(
                    Const.TOKENS_FILE));
            String token = "";
            while ((token = file.readLine()) != null) {

                /* Construct a filter for this token */
                tokens.add(new AndFilter(anchorFilter, new HasChildFilter(
                        new StringFilter(token), true)));
            }
            file.close();
        } catch (IOException ioe) {
            log.error("Could not compile token!", ioe);
        }
    }

    /**
     * Retrieves a list of potentially bad matches.
     * 
     * @return A list of bad matches.
     */
    public ArrayList<Pattern> getBlackList() {
        return blacklist;
    }

    /**
     * Retrieves a list of currently set regex patterns.
     * 
     * @return A list of regex patterns.
     */
    public ArrayList<Pattern> getPatterns() {
        return patterns;
    }

    /**
     * Return the proxy host.
     * 
     * @return The proxy host.
     */
    public String getProxyHost() {
        return proxyHost;
    }

    /**
     * Return the proxy port.
     * 
     * @return The proxy port.
     */
    public String getProxyPort() {
        return proxyPort;
    }

    /**
     * Return if a proxy has been set.
     * 
     * @return True or false depending if a proxy has been set.
     */
    public boolean getProxySet() {
        return proxySet;
    }

    /**
     * Retrieves a list of potential string tokens.
     * 
     * @return A list of string tokens.
     */
    public ArrayList<NodeFilter> getTokens() {
        return tokens;
    }

    public boolean isGlimpsing() {
        return glimpse;
    }

    public boolean isInvestigating() {
        return investigate;
    }

    public void setGlimpse(boolean glimpse) {
        this.glimpse = glimpse;
    }

    public void setInvestigate(boolean investigate) {
        this.investigate = investigate;
    }
}
