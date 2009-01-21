package net.brokentrain.ftf.core.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.PMCIDArticle;
import net.brokentrain.ftf.core.services.lookup.PMCIDLookup;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;
import org.htmlparser.util.Translate;

/**
 * Implements the PubMed Central service, using provided EUtilities. A search
 * can be created with no parameters {@link #PubMedCentral()} which can be set
 * later or created immediately {@link #PubMedCentral(String, int)} and started.
 */
public class PubMedCentral implements CoreSearchService, Scrollable {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new PubMedCentral();
        }
    }

    private static final Logger log = Logger.getLogger(PubMedCentral.class);

    public static final String HOMEPAGE_QUERY_STRING = "http://www.ncbi.nlm.nih.gov/"
            + "sites/entrez?term=%s&search=Find%20Articles&db=pmc&cmd=search";

    private static final String BASE_ID_STRING = "http://www.pubmedcentral.nih.gov/"
            + "picrender.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&artid=%d&blobtype=pdf";

    private static final String BASE_TEXT_STRING = "http://eutils.ncbi.nlm.nih.gov/"
            + "entrez/eutils/esearch.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&db=pmc&retmax=%d&retstart=%d&term=%s+AND+free+fulltext[filter]";

    private static final String BASE_ABSTRACT_STRING = "http://www.pubmedcentral.nih.gov/"
            + "articlerender.fcgi?tool=pmcentrez&artid=%d&rendertype=abstract";

    static {
        ServiceFactory.addFactory("PubMedCentral", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    private String queryString;

    private int start;

    private boolean glimpse;

    private boolean processing;

    private Integer maxProcessingResults;

    /**
     * Construct a new PubMed Central Search
     */
    public PubMedCentral() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        start = 1;
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        /* Get id results from ESearch query */
        PubMedESearch pubmedEsearch = new PubMedESearch(
                PubMedCentral.BASE_TEXT_STRING);

        /* Set the query */
        pubmedEsearch.setQuery(query);

        /* Set the start */
        pubmedEsearch.setStart(start);

        /* Set the maximum number of results to return */
        pubmedEsearch.setMaxResults(processing ? maxProcessingResults
                : maxResults);

        /* Start fetching */
        pubmedEsearch.fetch();

        if (pubmedEsearch.hasResults()) {

            PMCIDLookup pmcidLookup = new PMCIDLookup(pubmedEsearch.getIds());

            pmcidLookup.lookup();

            if (pmcidLookup.hasResults()) {

                for (Article article : pmcidLookup.getArticles()) {

                    String id = ((PMCIDArticle) article).getPMCID();

                    log.info(String.format("Checking %s for link out", id));

                    /* Create query string for the new PMCID */
                    String queryString = String.format(
                            PubMedCentral.BASE_ID_STRING, Integer.valueOf(id));

                    try {
                        URI parserURI = new URI(queryString);

                        /* PubMed Central by its very nature contains full-text */
                        ((PMCIDArticle) article).setHasFullTextLink(true);
                        ((PMCIDArticle) article).setFullTextLink(String.format(
                                PubMedCentral.BASE_ID_STRING, Integer
                                        .valueOf(id)));
                        ((PMCIDArticle) article).setAbstractLink(String.format(
                                PubMedCentral.BASE_ABSTRACT_STRING, Integer
                                        .valueOf(id)));

                        Resource resource = new Resource();
                        resource.setArticle(article);
                        resource
                                .setData(new URLData(parserURI.toURL(), glimpse));

                        /* Add the result as we're hoping it is a linkout */
                        results.put(parserURI, resource);
                    } catch (MalformedURLException mue) {
                        log.error(mue.getMessage(), mue);
                    } catch (URISyntaxException use) {
                        log.error(use.getMessage(), use);
                    }
                }
            }
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
        return results.size();
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
        this.start += processing ? maxProcessingResults : maxResults;
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

    public void setInvestigate(boolean investigate) {
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

    public void setProcessingAbstract(boolean processing) {
        this.processing = processing;
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        try {
            this.query = Translate.encode(URLEncoder
                    .encode(query, "ISO-8859-1"));
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }

        /* Create the full query string */
        queryString = PubMedCentral.HOMEPAGE_QUERY_STRING.replace("%s",
                this.query);
    }

}
