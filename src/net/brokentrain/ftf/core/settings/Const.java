package net.brokentrain.ftf.core.settings;

/**
 * Responsible for storing hard-coded configuration paths.
 */
public class Const {

    /**
     * Where the configuration files are expected to reside.
     */
    public static final String CONFIG_DIRECTORY = "cfg/";

    /**
     * The expected path of the services file.
     */
    public static final String SERVICES_FILE = Const.CONFIG_DIRECTORY
            + "services.ini";

    /**
     * The expected path of the user file.
     */
    public static final String CORE_FILE = Const.CONFIG_DIRECTORY + "core.ini";

    /**
     * The expected path of the blacklist file.
     */
    public static final String BLACKLIST_FILE = Const.CONFIG_DIRECTORY
            + "blacklist.txt";

    /**
     * The expected path of the patterns file.
     */
    public static final String PATTERNS_FILE = Const.CONFIG_DIRECTORY
            + "patterns.txt";

    /**
     * The expected path of the tokens file.
     */
    public static final String TOKENS_FILE = Const.CONFIG_DIRECTORY
            + "tokens.txt";
}
