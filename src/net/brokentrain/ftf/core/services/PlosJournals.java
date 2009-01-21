package net.brokentrain.ftf.core.services;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;

import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.data.URLData;
import net.brokentrain.ftf.core.services.lookup.URLArticle;
import net.brokentrain.ftf.core.settings.ServiceEntry;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

/**
 * Implements the Public Library of Science Journals service. A search can be
 * created with no parameters {@link #PlosJournals()} which can be set later or
 * created immediately {@link #PlosJournals(String, int)} and started.
 */
public class PlosJournals implements WebSearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new PlosJournals();
        }
    }

    private static final Logger log = Logger.getLogger(PlosJournals.class);

    private static final String BASE_QUERY_STRING = "http://biology.plosjournals.org/perlserv/"
            + "?request=advanced-search&search_fulltext=1&anywhere=%s"
            + "&limit=10&order=score";

    static {
        ServiceFactory.addFactory("PlosJournals", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    private String queryString;

    private boolean glimpse;

    /**
     * Construct a new PJ Search.
     */
    public PlosJournals() {
        timer = new ExecutionTimer();
        results = new HashMap<URI, Resource>();
        glimpse = true;
    }

    /**
     * Start the search with the loaded terms.
     */
    public void doSearch() {
        try {

            /* Start timing */
            timer.start();

            Parser parser = new Parser(queryString, new DefaultParserFeedback(
                    DefaultParserFeedback.QUIET));

            NodeFilter spanParentAndAnchor = new AndFilter(new TagNameFilter(
                    "span"), new HasChildFilter(new TagNameFilter("a")));

            NodeList list = parser
                    .extractAllNodesThatMatch(spanParentAndAnchor);

            int resultCount = 0;
            for (NodeIterator node = list.elements(); node.hasMoreNodes();) {

                if (resultCount >= maxResults) {
                    log.info("Limiting matches..");
                    break;
                }

                NodeList children = node.nextNode().getChildren()
                        .extractAllNodesThatMatch(new TagNameFilter("a"));

                for (NodeIterator targetNode = children.elements(); targetNode
                        .hasMoreNodes();) {

                    LinkTag linkTag = (LinkTag) targetNode.nextNode();

                    String result = linkTag.extractLink();

                    /* Watch out for case-specific license match here :( */
                    if (!result
                            .equals("http://journals.plos.org/plosbiology/license.php")) {
                        log.info("Full-text result found: " + result);

                        String title = linkTag.getLinkText();

                        try {

                            URI resultURI = new URI(Translate.decode(result));

                            URLArticle article = new URLArticle();
                            article.setArticleTitle(title);
                            // article.setAuthor(authors);

                            Resource resource = new Resource();
                            resource.setArticle(article);
                            resource.setData(new URLData(resultURI.toURL(),
                                    glimpse));

                            results.put(resultURI, resource);
                            resultCount++;
                        } catch (MalformedURLException mue) {
                            log.error(mue.getMessage(), mue);
                        } catch (URISyntaxException use) {
                            log.error(use.getMessage(), use);
                        }
                    }
                }
            }
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
            results.clear();
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
        try {
            this.query = Translate.encode(URLEncoder
                    .encode(query, "ISO-8859-1"));
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }

        /* Create the full query string */
        queryString = String.format(PlosJournals.BASE_QUERY_STRING, this.query);
    }
}
