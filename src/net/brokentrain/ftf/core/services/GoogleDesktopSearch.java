package net.brokentrain.ftf.core.services;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import jgd.JGDQuery;
import jgd.jaxb.Results;
import jgd.schemas.GoogleDesktopFile;
import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.FileData;
import net.brokentrain.ftf.core.services.lookup.FileArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;

/**
 * Implements the GoogleDesktopSearch file service. A search can be created with
 * no parameters {@link #GoogleDesktopSearch()} which can be set later or
 * created immediately {@link #GoogleDesktopSearch(String, int)} and started.
 */
public class GoogleDesktopSearch implements SearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected SearchService create() {
            return new GoogleDesktopSearch();
        }
    }

    private static final Logger log = Logger
            .getLogger(GoogleDesktopSearch.class);

    static {
        ServiceFactory.addFactory("GoogleDesktopSearch", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    /**
     * Construct a new GoogleDesktopSearch Search.
     */
    public GoogleDesktopSearch() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        try {

            /* Start timing */
            timer.start();

            /* Start a new desktop query */
            JGDQuery jgdQuery = new JGDQuery(query);

            /* Limit the number of results */
            jgdQuery.setNum(maxResults);

            /* Filter by files only */
            jgdQuery.setFilterByFiles();

            /* Execute the jgdQuery */
            Results jgdResults = jgdQuery.execute();

            /* Get the result */
            List<?> l = jgdResults.getResult();

            for (Object jdgResult : l) {

                GoogleDesktopFile googleDesktopFile = (GoogleDesktopFile) jdgResult;

                /* Create a new file object from the result */
                File file = new File(googleDesktopFile.get_uri());

                /* If this fails, skip it */
                if (file == null) {
                    continue;
                }

                log.info("Local result found: " + file.toString());

                Resource resource = new Resource();
                resource.setArticle(new FileArticle());
                resource.setData(new FileData(file));

                /* Add to total results with extended data */
                results.put(file.toURI(), resource);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } catch (UnsatisfiedLinkError ule) {
            log.error(ule.getMessage(), ule);
        } catch (NoClassDefFoundError ncdefe) {
            log.error(ncdefe.getMessage(), ncdefe);
        }

        /* Stop timing */
        timer.stop();
    }

    /**
     * Return all search metadata.
     * 
     * @return A collection of metadata.
     */
    public HashMap<URI, Resource> getResults() {
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
        return results.size();
    }

    /**
     * Returns if the current net.brokentrain.ftf contains any results.
     * 
     * @return True if the search returned any results, false otherwise.
     */
    public boolean hasResults() {
        return results.size() > 0;
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
    }

    /**
     * Set a maximum number of results to return.
     * 
     * @param maxResults
     *            The maximum results to return.
     */
    public void setMaxResults(Integer maxResults) {
        this.maxResults = maxResults;
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
