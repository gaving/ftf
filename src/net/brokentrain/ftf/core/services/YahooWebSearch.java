package net.brokentrain.ftf.core.services;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.math.BigInteger;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.URLArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;

import com.yahoo.search.SearchClient;
import com.yahoo.search.SearchException;
import com.yahoo.search.WebSearchRequest;
import com.yahoo.search.WebSearchResult;
import com.yahoo.search.WebSearchResults;

import org.htmlparser.util.Translate;

/**
 * Implements the Yahoo! Web Search service. A search can be created with no
 * parameters {@link #YahooWebSearch()} which can be set later or created
 * immediately {@link #YahooWebSearch(String, String, int)} and started.
 */
public class YahooWebSearch implements WebSearchService, Scrollable {

    private static class Factory extends ServiceFactory {
        @Override
        protected SearchService create() {
            return new YahooWebSearch();
        }
    }

    private static final Logger log = Logger.getLogger(YahooWebSearch.class);

    public static final String HOMEPAGE_QUERY_STRING = "http://search.yahoo.com/"
            + "search?p=%s";

    static {
        ServiceFactory.addFactory("YahooWebSearch", new Factory());
    }

    private SearchClient yahooClient;

    private WebSearchRequest yahooRequest;

    private WebSearchResults yahooResults;

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private String query;

    private String queryString;

    private boolean glimpse;

    private int retStart;

    private int maxResults;

    /**
     * Construct a new Yahoo! Web Search
     */
    public YahooWebSearch() {
        yahooRequest = new WebSearchRequest("");

        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        retStart = 1;
        glimpse = true;

        /*
         * http://www.realtimeart.com/switchboard/doc/com/yahoo/search/WebSearchRequest.html#setFormat(java.lang.String)
         * Specifies the kind of web document to search for. At the time of
         * writing, the following options are available: all (default), html,
         * msword, pdf, ppt, rss, txt, xls.
         * 
         * yahooRequest.setFormat("pdf");
         */
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        try {

            yahooRequest.setStart(BigInteger.valueOf(retStart));
            yahooResults = yahooClient.webSearch(yahooRequest);
            for (WebSearchResult r : yahooResults.listResults()) {
                try {
                    URI resultURI = new URI(r.getUrl());

                    URLArticle article = new URLArticle();
                    article.setArticleTitle(r.getTitle());
                    article.setSnippet(r.getSummary());

                    Resource resource = new Resource();
                    resource.setArticle(article);
                    resource.setData(new URLData(resultURI.toURL(), glimpse));

                    results.put(resultURI, resource);
                } catch (MalformedURLException ue) {
                    log.error(ue.getMessage(), ue);
                } catch (URISyntaxException use) {
                    log.error(use.getMessage(), use);
                }
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (SearchException se) {
            log.error(se.getMessage(), se);
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
        return yahooResults.getTotalResultsAvailable().intValue();
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
        yahooClient = new SearchClient(serviceEntry.getProperties().get("key"));
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
        yahooRequest.setResults(maxResults);
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        yahooRequest.setQuery(query);
        try {
            this.query = Translate.encode(URLEncoder
                    .encode(query, "ISO-8859-1"));
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }
        queryString = YahooWebSearch.HOMEPAGE_QUERY_STRING.replace("%s",
                this.query);
    }
}
