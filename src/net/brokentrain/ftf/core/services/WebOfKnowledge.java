package net.brokentrain.ftf.core.services;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.WOKArticle;
import net.brokentrain.ftf.core.services.lookup.WOKLinkLookup;
import net.brokentrain.ftf.core.services.lookup.WOKLookup;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Stub;
import org.apache.log4j.Logger;
import org.htmlparser.util.Translate;
import org.xml.sax.InputSource;

import com.isinet.esti.soap.search.SearchResults;
import com.isinet.esti.soap.search.SearchRetrieve;
import com.isinet.esti.soap.search.SearchRetrieveServiceLocator;

/**
 * Implements the Web Of Knowledge service. A search can be created with no
 * parameters {@link #WebOfKnowledge()} which can be set later or created
 * immediately {@link #WebOfKnowledge(String, int)} and started.
 */
public class WebOfKnowledge implements WebSearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new WebOfKnowledge();
        }
    }

    private static final Logger log = Logger.getLogger(WebOfKnowledge.class);

    public static final String RECORD_FIELDS = "get_parent source_title item_title authors abstract ut i_ckey bib_issue";

    public static final String HOMEPAGE_QUERY_STRING = "http://apps.isiknowledge.com/XS/"
            + "CIW.cgi?ServiceName=TransferToWos&PointOfEntry=GeneralSearchSummary&Func=Links"
            + "&xs_limit=5years&CustomersID=Portal&FQ=%s";

    static {
        ServiceFactory.addFactory("WebOfKnowledge", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private String query;

    private String queryString;

    private int maxResults;

    private String username;

    private String password;

    private boolean glimpse;

    /**
     * Construct a new WOK Search.
     */
    public WebOfKnowledge() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        glimpse = true;

        SettingsManager settingsManager = SettingsManager.getSettingsManager();

        /* Set a proxy if any has been specified in the Settings Manager */
        if (settingsManager.getProxySet()) {

            String proxyHost = settingsManager.getProxyHost();
            String proxyPort = settingsManager.getProxyPort();

            log.info("Using proxy information: " + "(" + proxyHost + ", "
                    + proxyPort + ")");

            /* Set proxy host */
            AxisProperties.setProperty("http.proxyHost", proxyHost);

            /* Set proxy port */
            AxisProperties.setProperty("http.proxyPort", String
                    .valueOf(new Integer(proxyPort)));
        }
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {

        /* Start timing */
        timer.start();

        try {

            SearchRetrieveServiceLocator service = new SearchRetrieveServiceLocator();
            SearchRetrieve client = service.getSearchRetrieve();

            /* Set any authentication details to use WOK */
            if ((StringUtil.isset(username)) && (StringUtil.isset(password))) {

                log.info("Using authentication information: " + "(" + username
                        + ", " + password + ")");

                ((Stub) client).setUsername(username);
                ((Stub) client).setPassword(password);
            }

            String query = "Topic=(" + this.query + ")";

            SearchResults WOKresults = client.searchRetrieve("WOS", query, "",
                    "", "", 1, maxResults, WebOfKnowledge.RECORD_FIELDS);

            InputSource response = new InputSource(new StringReader(WOKresults
                    .getRecords()));

            /* Set the content to parse */
            WOKLookup wokLookup = new WOKLookup(response);

            /* Parse the response into articles */
            wokLookup.lookup();

            for (Article article : wokLookup.getArticles()) {

                String cKey = ((WOKArticle) article).getCKey();

                /*
                 * This is debatable, do we really want to just forget any match
                 * that has no hint of a full-text link? Or should we keep the
                 * citation around?
                 */
                if (StringUtil.isset(cKey)) {

                    String rawXML = client.publisherLinks("linksID=" + cKey,
                            "databaseID=WOS");

                    log.debug(rawXML);
                    InputSource rawXMLInputSource = new InputSource(
                            new StringReader(rawXML));

                    WOKLinkLookup wokLinkLookup = new WOKLinkLookup(
                            rawXMLInputSource);

                    if (wokLinkLookup.hasLink()) {

                        URI parserURI = new URI(wokLinkLookup.getLink()
                                .toString());

                        // ((WOKArticle)
                        // article).setFullTextLink(String.format(PubMed.BASE_ID_STRING,
                        // Integer.valueOf(id)));
                        // ((WOKArticle)
                        // article).setAbstractLink(String.format(PubMed.BASE_ABSTRACT_STRING,
                        // Integer.valueOf(id)));

                        Resource resource = new Resource();
                        resource.setArticle(article);
                        resource
                                .setData(new URLData(parserURI.toURL(), glimpse));

                        results.put(parserURI, resource);
                    }
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
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

    public void setData(ServiceEntry serviceEntry) {

        String username = serviceEntry.getProperties().get("username");
        String password = serviceEntry.getProperties().get("password");

        if ((StringUtil.isset(username)) && (StringUtil.isset(password))) {

            /* Set additional information */
            log.debug("Setting username and password");

            this.username = username;
            this.password = password;
        }
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
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {
        this.query = query;
        try {
            String encodedQuery = Translate.encode(URLEncoder.encode(query,
                    "ISO-8859-1"));
            log.debug("Encoded query: " + encodedQuery);
            queryString = WebOfKnowledge.HOMEPAGE_QUERY_STRING.replace("%s",
                    encodedQuery);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }
    }
}
