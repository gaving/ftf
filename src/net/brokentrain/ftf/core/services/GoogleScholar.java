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
import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.htmlparser.util.Translate;

/**
 * Implements the Google Scholar service. A search can be created with no
 * parameters {@link #GoogleScholar()} which can be set later or created
 * immediately {@link #GoogleScholar(String, int)} and started.
 */
public class GoogleScholar implements WebSearchService {

    private static class Factory extends ServiceFactory {
        @Override
        protected WebSearchService create() {
            return new GoogleScholar();
        }
    }

    private static final Logger log = Logger.getLogger(GoogleScholar.class);

    private static final String BASE_QUERY_STRING = "http://scholar.google.com/scholar?q=%s&num=100";
    static {
        ServiceFactory.addFactory("GoogleScholar", new Factory());
    }

    private ExecutionTimer timer;

    private HashMap<URI, Resource> results;

    private int maxResults;

    private String query;

    private String queryString;

    private boolean glimpse;

    /**
     * Construct a new GS Search.
     */
    public GoogleScholar() {
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

            NodeFilter spanAndClass = new AndFilter(new TagNameFilter("p"),
                    new HasAttributeFilter("class", "g"));

            int resultCount = 0;
            NodeList list = parser.extractAllNodesThatMatch(spanAndClass);
            for (NodeIterator node = list.elements(); node.hasMoreNodes();) {

                if (resultCount >= maxResults) {
                    log.info("Limiting matches..");
                    break;
                }

                Node currentNode = node.nextNode();
                NodeList rootChildren = currentNode.getChildren();

                if (rootChildren == null) {
                    return;
                }

                String authors = "";
                String title = "";
                String link = "";

                NodeList children = rootChildren
                        .extractAllNodesThatMatch(new HasParentFilter(
                                new AndFilter(new TagNameFilter("span"),
                                        new HasAttributeFilter("class", "a"))),
                                true);

                StringBuffer tmpText = new StringBuffer();
                for (NodeIterator targetNode = children.elements(); targetNode
                        .hasMoreNodes();) {
                    Node textNode = targetNode.nextNode();
                    if (textNode instanceof TextNode) {
                        TextNode text = (TextNode) textNode;
                        tmpText = tmpText.append(text.getText());
                    }
                }
                authors = tmpText.toString();

                children = rootChildren
                        .extractAllNodesThatMatch(new HasParentFilter(
                                new AndFilter(new TagNameFilter("span"),
                                        new HasAttributeFilter("class", "w"))),
                                true);

                for (NodeIterator targetNode = children.elements(); targetNode
                        .hasMoreNodes();) {
                    Node linkNode = targetNode.nextNode();
                    if (linkNode instanceof LinkTag) {
                        LinkTag tag = (LinkTag) linkNode;
                        link = tag.getLink();
                        title = tag.getLinkText();
                    }
                }

                if (!link.equals("")) {
                    String result = link;

                    log.info("Full-text result found: " + result);

                    try {

                        URI resultURI = new URI(Translate.decode(result));

                        URLArticle article = new URLArticle();
                        article.setArticleTitle(Translate.decode(title));
                        article.setAuthor(Translate.decode(authors));

                        Resource resource = new Resource();
                        resource.setArticle(article);
                        resource
                                .setData(new URLData(resultURI.toURL(), glimpse));

                        results.put(resultURI, resource);
                        resultCount++;
                    } catch (MalformedURLException mue) {
                        log.error(mue.getMessage(), mue);
                    } catch (URISyntaxException use) {
                        log.error(use.getMessage(), use);
                    }
                }
            }
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
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

    /*
     * Set a new query term to search for. @param query The new query to search
     * for.
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
        queryString = String
                .format(GoogleScholar.BASE_QUERY_STRING, this.query);
    }
}
