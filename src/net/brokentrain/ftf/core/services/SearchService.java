package net.brokentrain.ftf.core.services;

import java.net.URI;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.settings.ServiceEntry;

/**
 * Responsible for providing common access to "Search Services". API services
 * such as Google and Yahoo! can use this service but also various web scrapers.
 * The SearchService interface can be implemented anywhere a search service is
 * needed by providing an implementation of the various methods below.
 * 
 * @see LocalSearchService
 */
public interface SearchService {

    /**
     * Start the search.
     */
    public void doSearch();

    /**
     * Return all results and associated metadata.
     * 
     * @return A collection of results and metadata.
     */
    public HashMap<URI, Resource> getResults();

    /**
     * Returns how long the search took in milliseconds .
     * 
     * @return The time in milliseconds.
     */
    public long getSearchTime();

    /**
     * Returns the total number of results obtained by the search.
     * 
     * @return The total number of results.
     */
    public Integer getTotalResults();

    /**
     * Returns if the current search contains any results.
     * 
     * @return True if the search returned any results, false otherwise.
     */
    public boolean hasResults();

    /**
     * Set any additional data a service might need.
     * 
     * @param configFile
     *            The global configuration file
     */
    public void setData(ServiceEntry serviceEntry);

    /**
     * Set if a service should glimpse at a URL for data.
     * 
     * @param glimpse
     *            True or False depending on if it should glimpse.
     */
    public void setGlimpse(boolean glipmse);

    /**
     * Set a maximum number of results to return.
     * 
     * @param maxResults
     *            The maximum results to return.
     */
    public void setMaxResults(Integer maxResults);

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query);

}
