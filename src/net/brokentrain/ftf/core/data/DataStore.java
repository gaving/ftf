package net.brokentrain.ftf.core.data;

import java.util.Date;

/**
 * This interface represents a particular match that can be either URL or File
 * orientated in nature. This is used specifically to treat all matches the same
 * to begin with, but can be further broken down into specifics through the use
 * of their own interfaces e.g. URLStore. Any basic result has a particular
 * particular content type, size, filename and last modified time associated
 * with it no matter where it is from.
 * 
 * @see URLStore
 */
public interface DataStore {

    /**
     * Represents the three main types of results that are considered as
     * suitable match content types.
     */
    public enum ContentType {
        PDF, HTML, TEXT
    };

    /**
     * Return the content type for this data store.
     * 
     * @return The content type for this data store.
     */
    public ContentType getContentType();

    /**
     * Return the filename for this data store.
     * 
     * @return The filename for this data store.
     */
    public String getFilename();

    /**
     * Return the last modification time for this data store.
     * 
     * @return The last modification time for this data store.
     */
    public Date getLastModified();

    /**
     * Return the size for this data store.
     * 
     * @return The size of this data store.
     */
    public String getSize();

}
