package net.brokentrain.ftf.core.data;

import java.util.ArrayList;

/**
 * This interface represents a particular match that is URL orientated. It is
 * best used by services that retrieve any results from Web pages.
 * 
 * @see DataStore
 */
public interface URLStore extends DataStore {

    /**
     * Return the encoding type for this URL store.
     * 
     * @return The encoding type for this data store.
     */
    public String getEncoding();

    /**
     * Return the prices that may be relevant to this URL store.
     * 
     * @return A list of potential prices for this URL store.
     */
    public ArrayList<String> getPrices();

    /**
     * Return the last known HTTP response code for this URL store.
     * 
     * @return The latest response code for this URL store.
     */
    public String getResponseCode();

    /**
     * Return the URL for this URL store.
     * 
     * @return The URL of this URL store.
     */
    public String getURL();
}
