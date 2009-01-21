package net.brokentrain.ftf.core.data;

import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.StringFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.http.ConnectionMonitor;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.DefaultParserFeedback;
import org.htmlparser.util.NodeIterator;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * Represents a match from a URL based hit and implements the URLStore
 * functionality accordingly. A url based match is created by providing a
 * designated URL {@link #URLData(URL)} and then extracting data out of it.
 */
public class URLData implements URLStore, ConnectionMonitor, Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private static final Logger log = Logger.getLogger(URLData.class);

    private transient Parser parser;

    private ArrayList<String> prices;

    private DataStore.ContentType contentType;

    private URL url;

    private boolean reachable;

    private Date lastModified;

    private String contentEncoding;

    private String responseCode;

    private String size;

    /**
     * Construct a new data object for a particular web resource.
     * 
     * @param url
     *            The URL to open.
     */
    public URLData(URL url, boolean investigate) {
        this.url = url;

        /* Don't bother if we've been told not to! */
        if (!investigate) {
            return;
        }

        reachable = true;

        try {
            ConnectionManager manager = Parser.getConnectionManager();
            manager.setRedirectionProcessingEnabled(true);
            manager.setMonitor(this);
            parser = new Parser(url.toString(), new DefaultParserFeedback(
                    DefaultParserFeedback.QUIET));
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
            parser = null;
            reachable = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            parser = null;
            reachable = false;
        }

        if ((!reachable) || (parser == null)) {
            return;
        }

        URLConnection connection = parser.getConnection();

        contentEncoding = connection.getContentEncoding();
        size = String.valueOf(connection.getContentLength());
        lastModified = new Date(connection.getLastModified());

        String contentType = connection.getContentType();

        if (contentType != null) {
            if (contentType.startsWith("application/pdf")) {
                this.contentType = ContentType.PDF;
                log.debug("PDF detected.. not checking for prices!");
                return;
            } else if (contentType.startsWith("text/html")) {
                this.contentType = ContentType.HTML;
            } else if (contentType.startsWith("text/plain")) {
                this.contentType = ContentType.TEXT;
            }
        }

        /*
         * Define price symbol filters for restricted sites (from:
         * http://www.w3schools.com/tags/ref_entities.asp) 'Vague' filters since
         * we need the CONTEXT of the prices, this is further narrowed down to
         * specific matches later on.
         */
        NodeFilter[] filters = new NodeFilter[] { new StringFilter("$"),
                new StringFilter("&#036;"), new StringFilter("&#36;"),
                new StringFilter("&dollar;"), new StringFilter("USD"),
                new StringFilter("£"), new StringFilter("&pound;"),
                new StringFilter("&#163;"), new StringFilter("GBP"),
                new StringFilter("€"), new StringFilter("&euro;"),
                new StringFilter("&#8364;"),

        /*
         * REMOVED: Causes a lot of false positives new StringFilter("EUR"),
         */

        /* TODO: Further currencies? */
        };

        prices = new ArrayList<String>();
        NodeFilter priceSymbols = new OrFilter(filters);

        /*
         * Ignore the 'preamble' (potential javascript and stuff) and just
         * accept everything below the body tag
         */
        NodeFilter acceptedTags = new HasParentFilter(
                new TagNameFilter("body"), true);
        NodeFilter finalFilter = new AndFilter(priceSymbols, acceptedTags);

        try {
            NodeList nodeList = parser.extractAllNodesThatMatch(finalFilter);

            /* Loop through nodes and add matching links to list */
            for (NodeIterator e = nodeList.elements(); e.hasMoreNodes();) {

                TextNode currentNode = (TextNode) e.nextNode();
                String priceInformation = currentNode.getText();

                /* Ignore empty nodes and strings */
                if ((priceInformation != null)
                        && (!priceInformation.matches("^\\s*$"))) {
                    prices.add(priceInformation.trim());
                }
            }
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
        }
    }

    /**
     * Return the content type for this web resource.
     * 
     * @return The content type.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Return the encoding type for this web resource.
     * 
     * @return The encoding type.
     */
    public String getEncoding() {
        return contentEncoding;
    }

    /**
     * Return the filename for this web resource.
     * 
     * @return The filename.
     */
    public String getFilename() {
        return url.getFile();
    }

    /**
     * Return the last modification time for this web resource.
     * 
     * @return The last modification time.
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * Return the prices associated with this web resource.
     * 
     * @return A list of extracted prices for this web resource.
     */
    public ArrayList<String> getPrices() {
        return (((prices != null) && (!prices.isEmpty())) ? prices : null);
    }

    /**
     * Return the HTTP response code when attempting to contact this web
     * resource. http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
     * 
     * @return The response code.
     */
    public String getResponseCode() {
        return responseCode;
    }

    /**
     * Return the size for this web resource.
     * 
     * @return The size.
     */
    public String getSize() {
        return size;
    }

    /**
     * Return the URL for this web resource.
     * 
     * @return The URL.
     */
    public String getURL() {
        return url.toString();
    }

    /**
     * Obtain any information after connecting.
     * 
     * @param conn
     *            A HttpURLConnection to read from.
     */
    public void postConnect(HttpURLConnection conn) {
        try {
            responseCode = String.valueOf(conn.getResponseCode());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }

    /**
     * Obtain any information before connecting.
     * 
     * @param conn
     *            A HttpURLConnection to read from.
     */
    public void preConnect(HttpURLConnection conn) {
    }

}
