package net.brokentrain.ftf.core.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.PMIDArticle;
import net.brokentrain.ftf.core.services.lookup.PMIDLookup;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;
import org.htmlparser.Parser;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

/**
 * Implements the PubMed service, using provided EUtilities. A search can be
 * created with no parameters {@link #PubMed()} which can be set later or
 * created immediately {@link #PubMed(String, int)} and started.
 */
public class PubMed implements CoreSearchService, Scrollable {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new PubMed();
        }
    }

    private static final Logger log = Logger.getLogger(PubMed.class);

    public static final String HOMEPAGE_QUERY_STRING = "http://www.ncbi.nlm.nih.gov/"
            + "sites/entrez?term=%s&search=Find%20Articles&db=pubmed&cmd=search";

    private static final String BASE_ID_STRING = "http://eutils.ncbi.nlm.nih.gov/entrez/"
            + "eutils/elink.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&dbfrom=pubmed&id=%d&retmode=ref&cmd=prlinks";

    private static final String BASE_TEXT_STRING = "http://eutils.ncbi.nlm.nih.gov/entrez/"
            + "eutils/esearch.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&db=pubmed&retmax=%d&retstart=%d&term=%s";

    private static final String BASE_ABSTRACT_STRING = "http://www.ncbi.nlm.nih.gov/"
            + "sites/entrez?db=PubMed&cmd=retrieve"
            + "&dopt=AbstractPlus&list_uids=%d";

    static {
        ServiceFactory.addFactory("PubMed", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    private String queryString;

    private int start;

    private boolean investigate;

    private boolean glimpse;

    private boolean processing;

    private Integer maxProcessingResults;

    /**
     * Construct a new PubMed Search
     */
    public PubMed() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        start = 1;
        glimpse = true;
        processing = false;

        Parser.getConnectionManager().setRedirectionProcessingEnabled(false);
        Parser.getConnectionManager().setCookieProcessingEnabled(true);
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        /* Get id results from ESearch query */
        PubMedESearch pubmedEsearch = new PubMedESearch(PubMed.BASE_TEXT_STRING);

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
            getData(pubmedEsearch.getIds());
        }

        /* Stop timing */
        timer.stop();
    }

    public void getData(ArrayList<String> ids) {

        PMIDLookup pmidLookup = new PMIDLookup(ids);

        pmidLookup.lookup();

        if (pmidLookup.hasResults()) {

            for (Article article : pmidLookup.getArticles()) {

                String id = ((PMIDArticle) article).getPMID();

                URI parserURI = null;

                if (investigate) {

                    /* Create query string for the new PMID */
                    String queryString = String.format(PubMed.BASE_ID_STRING,
                            Integer.valueOf(id));

                    log.info(String.format("Checking %s for link out", id));

                    try {

                        /* Create a new parser for this query string */
                        Parser parser = new Parser(queryString);

                        try {
                            parserURI = new URI(parser.getURL());

                        } catch (URISyntaxException use) {
                            log.error(use.getMessage(), use);
                        }
                    } catch (ParserException pe) {
                        log.error(pe.getMessage(), pe);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                    }
                } else {

                    String normalURL = String.format(
                            PubMed.BASE_ABSTRACT_STRING, Integer.valueOf(id));
                    try {
                        parserURI = new URI(normalURL);
                    } catch (URISyntaxException use) {
                        log.error(use.getMessage(), use);
                    }
                }

                if (parserURI != null) {

                    try {

                        ((PMIDArticle) article).setFullTextLink(String.format(
                                PubMed.BASE_ID_STRING, Integer.valueOf(id)));
                        ((PMIDArticle) article).setAbstractLink(String.format(
                                PubMed.BASE_ABSTRACT_STRING, Integer
                                        .valueOf(id)));

                        Resource resource = new Resource();
                        resource.setArticle(article);
                        resource
                                .setData(new URLData(parserURI.toURL(), glimpse));

                        /* Add the result as we're hoping it is a linkout */
                        results.put(parserURI, resource);
                    } catch (MalformedURLException mue) {
                        log.error(mue.getMessage(), mue);
                    }
                }
            }
        }
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

        /* Set terrier home */
        log.debug("Getting abstract results");

        maxProcessingResults = Integer.valueOf(serviceEntry.getProperties()
                .get("max_processing_results"));
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
        this.investigate = investigate;
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
        queryString = PubMed.HOMEPAGE_QUERY_STRING.replace("%s", this.query);
    }

}
