package net.brokentrain.ftf.core.data;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

import net.brokentrain.ftf.ui.gui.util.FileUtil;

/**
 * Represents a match from a non-URL based hit and implements the FileStore
 * functionality accordingly. A file based match can be created by providing
 * useful metadata {@link #FileData(HashMap)} to associate the file with.
 */
public class FileData implements DataStore, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private DataStore.ContentType contentType;

    private Date lastModified;

    private String name;

    private long size;

    private String path;

    /**
     * Construct a new data object for a particular match.
     * 
     * @param file
     *            The file to get the data from.
     */
    public FileData(File file) {
        name = file.getName();
        path = file.getPath();
        size = file.length();
        lastModified = new Date(file.lastModified());

        String extension = FileUtil.getExtension(file);

        if (extension.compareToIgnoreCase("pdf") == 0) {
            contentType = ContentType.PDF;
        } else if (extension.compareToIgnoreCase("html") == 0) {
            contentType = ContentType.HTML;
        } else if (extension.compareToIgnoreCase("txt") == 0) {
            contentType = ContentType.TEXT;
        }
    }

    /**
     * Return the content type for this match.
     * 
     * @return The content type for this match.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Return the filename for this match.
     * 
     * @return The filename for this match.
     */
    public String getFilename() {
        return name;
    }

    /**
     * Return the last modification time for this match.
     * 
     * @return The last modification time for this match.
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Return the path for this match.
     * 
     * @return The path for this match.
     */
    public String getPath() {
        return path;
    }

    /**
     * Return the size for this match.
     * 
     * @return The size of this match.
     */
    public String getSize() {
        return String.valueOf(size);
    }
}
