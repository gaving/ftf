package net.brokentrain.ftf.core.downloader;

import java.io.File;

import org.apache.log4j.Logger;

/**
 * Responsible for managing each download that is currently progressing. A
 * download manager can and should only be created once over the entire lifetime
 * of the program. For this reason, requests for the active download manager
 * should be made through {@link #getDownloadManager()} directly which will only
 * create a specific manager on the first call.
 * 
 * Once created, the download directory can be obtained with {@link
 * #getDownloadDir getDownloadDir()} and similarly set with {@link
 * #setDownloadDir setDownloadDir()}.
 */
public class DownloadManager {

    private static final Logger log = Logger.getLogger(DownloadManager.class);

    private static DownloadManager instance;

    /**
     * Gets an existing Download Manager.
     * 
     * @return The existing download managers instance.
     */
    public static DownloadManager getDownloadManager() {
        if (instance == null) {
            log.debug("Creating new download manager");
            instance = new DownloadManager();
        } else {
            log.debug("Using existing download manager");
        }
        return instance;
    }

    private String path;

    /**
     * Constructs a new download manager.
     */
    public DownloadManager() {

        /* Use the users home directory by default */
        path = System.getProperty("user.dir");
    }

    /**
     * Return the active download directory for all downloads.
     * 
     * @return The active download directory.
     */
    public String getDownloadDir() {
        return path;
    }

    /**
     * Sets a download directory to use for saving to.
     * 
     * @param path
     *            The download directory to set.
     */
    public void setDownloadDir(String path) {
        File dirFile = new File(path);

        /* Ensure that it exists and is an actual directory */
        if ((dirFile.exists()) && (dirFile.isDirectory())) {
            this.path = dirFile.getAbsolutePath();
        } else {
            log.error("The given download directory does not exist or is not a"
                    + " directory!");
        }
    }

}
