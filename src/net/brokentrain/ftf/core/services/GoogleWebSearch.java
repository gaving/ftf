package net.brokentrain.ftf.core.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.net.URLEncoder;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.URLArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.SettingsManager;

import org.apache.log4j.Logger;

import com.google.soap.search.GoogleSearch;
import com.google.soap.search.GoogleSearchFault;
import com.google.soap.search.GoogleSearchResult;
import com.google.soap.search.GoogleSearchResultElement;

import org.htmlparser.util.Translate;

/**
 * Implements the Google Web Search service. A search is created with no
 * parameters {@link #GoogleWebSearch()} and is then given data and started.
 */
public class GoogleWebSearch implements WebSearchService, Scrollable {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new GoogleWebSearch();
        }
    }

    private static final Logger log = Logger.getLogger(GoogleWebSearch.class);

    public static final String HOMEPAGE_QUERY_STRING = "http://www.google.com/"
            + "search?q=%s";

    static {
        ServiceFactory.addFactory("GoogleWebSearch", new Factory());
    }

    private GoogleSearch googleClient;

    private GoogleSearchResult googleSearchResult;

    private GoogleSearchResultElement[] googleResults;

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private Integer maxResults;

    private String query;

    private String queryString;

    private boolean glimpse;

    private int retStart;

    /**
     * Construct a new Google Web Search
     */
    public GoogleWebSearch() {
        googleClient = new GoogleSearch();

        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        glimpse = true;
        retStart = 1;

        SettingsManager settingsManager = SettingsManager.getSettingsManager();

        /* Set a proxy if any has been specified in the Settings Manager */
        if (settingsManager.getProxySet()) {

            String proxyHost = settingsManager.getProxyHost();
            String proxyPort = settingsManager.getProxyPort();

            log.info("Using proxy information: " + "(" + proxyHost + ", "
                    + proxyPort + ")");

            /* Set proxy host */
            googleClient.setProxyHost(proxyHost);

            /* Set proxy port */
            googleClient.setProxyPort(new Integer(proxyPort));
        }
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {
        try {

            /* Start timing */
            timer.start();

            googleClient.setStartResult(retStart);
            googleSearchResult = googleClient.doSearch();
            googleResults = googleSearchResult.getResultElements();

            for (GoogleSearchResultElement r : googleResults) {
                try {
                    URI resultURI = new URI(r.getURL());

                    URLArticle article = new URLArticle();
                    article.setArticleTitle(r.getTitle().replaceAll(
                            "\\<.*?\\>", ""));
                    article.setSnippet(r.getSnippet());

                    Resource resource = new Resource();
                    resource.setArticle(article);
                    resource.setData(new URLData(resultURI.toURL(), glimpse));

                    results.put(resultURI, resource);
                } catch (MalformedURLException mue) {
                    log.error(mue.getMessage(), mue);
                } catch (URISyntaxException use) {
                    log.error(use.getMessage(), use);
                }
            }
        } catch (GoogleSearchFault gsf) {
            log.error(gsf.getMessage(), gsf);
        }

        /* Stop timing */
        timer.stop();
    }

    /**
     * Returns the query string used.
     * 
     * @return The full query string that was searched.
     */
    public String getQueryString() {
        return queryString;
    }

    /**
     * Return all search results.
     * 
     * @return A collection of results.
     */
    public HashMap<URI, Resource> getResults() {
        return results;
    }

    /**
     * Returns how long the search took in milliseconds.
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
        return googleSearchResult.getEstimatedTotalResultsCount();
    }

    /**
     * Returns if the current search contains any results.
     * 
     * @return True if the search returned any results, false otherwise.
     */
    public boolean hasResults() {
        return results.size() > 0;
    }

    public void increment() {
        results.clear();
        this.retStart += maxResults;
    }

    public void setData(ServiceEntry serviceEntry) {
        googleClient.setKey(serviceEntry.getProperties().get("key"));
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
        this.maxResults = maxResults;
        googleClient.setMaxResults(maxResults);
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        googleClient.setQueryString(query);
        try {
            this.query = Translate.encode(URLEncoder
                    .encode(query, "ISO-8859-1"));
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }
        queryString = GoogleWebSearch.HOMEPAGE_QUERY_STRING.replace("%s",
                this.query);
    }

}
