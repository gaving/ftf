package net.brokentrain.ftf.core.services;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.DOIArticle;
import net.brokentrain.ftf.core.services.lookup.DOILookup;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;

/**
 * Implements a basic wrapper around the DOI lookup facility. A DOI lookup is
 * created with no parameters {@link #DOIProvider()} and given a specific DOI to
 * lookup {@link #setQuery setQuery(String)} and started.
 * 
 * @see DOILookup
 */
public class DOIProvider implements SearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected SearchService create() {
            return new DOIProvider();
        }
    }

    private static final Logger log = Logger.getLogger(DOIProvider.class);

    static {
        ServiceFactory.addFactory("DOIProvider", new Factory());
    }

    private DOILookup doiLookup;

    private ExecutionTimer timer;

    private String query;

    private boolean glimpse;

    /**
     * Construct a new DOI Search.
     */
    public DOIProvider() {
        timer = new ExecutionTimer();
        doiLookup = new DOILookup();
        glimpse = true;
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        /* Set the query */
        doiLookup.setQuery(query);

        /* Start looking up */
        doiLookup.lookup();

        /* Stop timing */
        timer.stop();
    }

    /**
     * Return all search metadata.
     * 
     * @return A collection of metadata.
     */
    public HashMap<URI, Resource> getResults() {
        HashMap<URI, Resource> results = new HashMap<URI, Resource>();

        try {
            DOIArticle article = ((DOIArticle) doiLookup.getArticle());

            URI resultURI = new URI(article.getURL());

            Resource resource = new Resource();
            resource.setArticle(article);
            resource.setData(new URLData(resultURI.toURL(), glimpse));

            results.put(resultURI, resource);
        } catch (MalformedURLException mue) {
            log.error(mue.getMessage(), mue);
        } catch (URISyntaxException use) {
            log.error(use.getMessage(), use);
        }
        return results;
    }

    /**
     * Returns how long the search took in milliseconds .
     * 
     * @return The time in milliseconds.
     */
    public long getSearchTime() {
        return timer.toValue();
    }

    /**
     * Returns the total number of results obtained by the search.
     * 
     * @return The total number of results.
     */
    public Integer getTotalResults() {
        return null;
    }

    /**
     * Returns if the current search contains any results.
     * 
     * @return True if the search returned any results, false otherwise.
     */
    public boolean hasResults() {
        DOIArticle article = ((DOIArticle) doiLookup.getArticle());
        return ((article != null) && (article.getURL() != null));

    }

    public void setData(ServiceEntry serviceEntry) {
        /* Nothing to do */
    }

    /**
     * Set if this service should glimpse at a URL for data.
     * 
     * @param glimpse
     *            True or False depending on if it should glimpse.
     */
    public void setGlimpse(boolean glimpse) {
        this.glimpse = glimpse;
    }

    /**
     * Set a maximum number of results to return.
     * 
     * @param maxResults
     *            The maximum results to return.
     */
    public void setMaxResults(Integer maxResults) {
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        this.query = query;
    }

}
