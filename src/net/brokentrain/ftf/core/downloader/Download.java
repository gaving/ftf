package net.brokentrain.ftf.core.downloader;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Observable;

import org.apache.log4j.Logger;

/**
 * Represents an active Download thread which is responsible for retrieving a
 * given resources page to disk. A download takes a specific URL to download
 * {@link #Download(URL)}. Once a downloaded has been created it is started
 * immediately and can be controlled directly through provided methods such as
 * {@link #pause pause()}, {@link #resume resume()} and
 * {@link #cancel cancel()}.
 * 
 * At any point a downloads progress can be monitored through use of {@link
 * #getStatus getStatus()}, {@link #getProgress getProgress()}, {@link
 * #getSize()}, {@link #getDownloaded getDownloaded()} and {@link #getURL
 * getURL()} respectively.
 */
public class Download extends Observable implements Runnable {

    private static final Logger log = Logger.getLogger(Download.class);

    /* Maximum size of download buffer */
    private static final int MAX_BUFFER_SIZE = 1024;

    /**
     * Textual representation of our different download states.
     */
    public static final String STATUSES[] = { "Downloading", "Paused",
            "Complete", "Cancelled", "Error" };

    /**
     * The downloading state.
     */
    public static final int DOWNLOADING = 0;

    /**
     * The paused state.
     */
    public static final int PAUSED = 1;

    /**
     * The completed state.
     */
    public static final int COMPLETE = 2;

    /**
     * The cancelled state.
     */
    public static final int CANCELLED = 3;

    /**
     * The error'd state.
     */
    public static final int ERROR = 4;

    private DownloadManager downloadManager;

    private URL url;

    private String fileName;

    private int size;

    private int downloaded;

    private int status;

    /**
     * Construct a download for a given URL.
     * 
     * @param url
     *            The url to download.
     */
    public Download(URL url) {
        this.url = url;

        downloadManager = DownloadManager.getDownloadManager();
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        download();
    }

    /**
     * Construct a download for a given URL with added filename.
     * 
     * @param url
     *            The url to download.
     * @param fileName
     *            The filename to write to.
     */
    public Download(URL url, String fileName) {
        this.url = url;
        this.fileName = fileName;

        downloadManager = DownloadManager.getDownloadManager();
        size = -1;
        downloaded = 0;
        status = DOWNLOADING;
        download();
    }

    /**
     * Cancels a download.
     */
    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    private void download() {
        Thread thread = new Thread(this);
        thread.start();
    }

    private void error() {
        status = ERROR;
        stateChanged();
    }

    /**
     * Return the amount that has been downloaded this far.
     * 
     * @return The amount that has been downloaded.
     */
    public float getDownloaded() {
        return ((float) downloaded / 1024);
    }

    /**
     * Return a filename for particular URL.
     * 
     * @param url
     *            The URL to download from.
     * @param showFilename
     *            True if the filename should be shown.
     * @return The filename to save to.
     */
    public String getFileName(URL url, boolean showFilename) {

        String fileName = url.getFile();
        String downloadDir = downloadManager.getDownloadDir();
        if ((downloadDir != null) && (!showFilename)) {

            /* Use given filename if possible */
            if (this.fileName != null) {
                return downloadDir.concat("/" + this.fileName);
            } else {
                return downloadDir.concat(fileName.substring(fileName
                        .lastIndexOf('/')));
            }
        } else {
            if (this.fileName != null) {
                return this.fileName;
            } else {
                return fileName.substring(fileName.lastIndexOf('/') + 1);
            }
        }
    }

    /**
     * Return the total progress this far as a percentage.
     * 
     * @return The progress this far.
     */
    public float getProgress() {
        return ((float) downloaded / size) * 100;
    }

    /**
     * Return the total size of the download.
     * 
     * @return The total size of the download.
     */
    public float getSize() {
        return ((float) size / 1024);
    }

    /**
     * Return the total progress this far as a percentage.
     * 
     * @return The progress this far.
     */
    public String getStatus() {
        return STATUSES[status];
    }

    /**
     * Return the URL currently being download.
     * 
     * @return The URL currently being downloaded.
     */
    public String getURL() {
        return url.toString();
    }

    /**
     * Pauses a download.
     */
    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    /**
     * Resumes a download.
     */
    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    /**
     * Start downloading.
     */
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
            connection.connect();

            /* Make sure response code is within 200 range! */
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            /* Check for valid content length */
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }

            /*
             * Set the size for this download if it hasn't been already set
             */
            if (size == -1) {
                size = contentLength;
                stateChanged();
            }

            /* Open a file for this download and seek to the end of it */
            file = new RandomAccessFile(getFileName(url, false), "rw");

            /* Allows resume ability */
            file.seek(downloaded);

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {

                /*
                 * Size buffer according to how much of the file is left to
                 * download
                 */
                byte buffer[];
                if (size - downloaded > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded];
                }

                /* Read from server into buffer */
                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }

                /* Write buffer to file */
                file.write(buffer, 0, read);
                downloaded += read;
                stateChanged();
            }

            /*
             * Change status to complete if this point was reached because
             * downloading has finished
             */
            if (status == DOWNLOADING) {
                status = COMPLETE;
                stateChanged();
            }
        } catch (Exception e) {
            error();
        } finally {

            /* Close file */
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                    log.error(e.getMessage());
                }

                /* Close connection */
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        log.error(e.getMessage());
                    }
                }
            }
        }
    }

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }

    /**
     * Return the string representation of the download.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return getURL() + "(" + getProgress() + ")";
    }
}
