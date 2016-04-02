package net.brokentrain.ftf.core.services.lookup;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.htmlparser.util.ParserException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Responsible for looking up an Article's metadata based on DOI. Using the
 * services provided by the crossref.org web service and official doi.org
 * site we can obtain a direct link to an articles full-text based on it's DOI.
 */
public class DOILookup extends DefaultHandler {

    private static final Logger log = Logger.getLogger(DOILookup.class);

    private URL infoString;

    private URL urlString;

    private String query;

    private DOIArticle article;

    private boolean collectArticleTitle;

    private boolean collectAuthor;

    private boolean collectIssue;

    private boolean collectJournalTitle;

    private boolean collectVolume;

    private boolean collectYear;

    /**
     * Construct a new DOI Lookup.
     */
    public DOILookup() {
        try {
            infoString = new URL(
                    "http://www.crossref.org/openurl/?id=doi:%s&noredirect=true");
            urlString = new URL("https://doi.org/%s");
        } catch (MalformedURLException mue) {
            log.error(mue.getMessage());
        }
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if ((collectAuthor) || (collectArticleTitle) || (collectIssue)
                || (collectIssue) || (collectJournalTitle) || (collectVolume)
                || (collectYear)) {

            String elementData = new String(chars, startIndex, endIndex).trim();

            if (collectJournalTitle) {
                article.setJournalTitle(elementData);
            } else if (collectAuthor) {
                article.setAuthor(elementData);
            } else if (collectVolume) {
                article.setVolume(elementData);
            } else if (collectIssue) {
                article.setIssue(elementData);
            } else if (collectYear) {
                article.setYear(elementData);
            } else if (collectArticleTitle) {
                article.setArticleTitle(elementData);
            }

        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        if (localName.equals("journal_title")) {
            collectJournalTitle = false;
        } else if (localName.equals("author")) {
            collectAuthor = false;
        } else if (localName.equals("volume")) {
            collectVolume = false;
        } else if (localName.equals("issue")) {
            collectIssue = false;
        } else if (localName.equals("year")) {
            collectYear = false;
        } else if (localName.equals("article_title")) {
            collectArticleTitle = false;
        }
    }

    /**
     * Return the DOIs full-text link.
     * 
     * @return The full-text link for this DOI.
     */
    public String extractLink() {
        try {

            String resolveURL = urlString.toString().replace("%s", query);
            org.htmlparser.Parser.getConnectionManager()
                    .setRedirectionProcessingEnabled(false);
            org.htmlparser.Parser parser = new org.htmlparser.Parser(resolveURL);
            return parser.getURL();
        } catch (ParserException pe) {
            log.error(pe.getMessage(), pe);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * Return an associated Article structure for this result.
     * 
     * @return An article with associated metadata.
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Indicate if this lookup has yielded a result.
     * 
     * @return True or false depending if this lookup has a result.
     */
    public boolean hasResult() {
        return (article != null);
    }

    /**
     * Lookup the DOI.
     */
    public void lookup() {

        log.info("Trying to resolve " + query);

        String url = infoString.toString().replace("%s", query);
        article = new DOIArticle();
        article.setDOI(query);

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(url);

            article.setURL(extractLink());
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            article = null;
        } catch (SAXException se) {
            log.error(se.getMessage(), se);
            article = null;
        }
    }

    /**
     * Set a new DOI to lookup.
     * 
     * @param query
     *            The new DOI to lookup.
     */
    public void setQuery(String query) {
        try {

            /* Handle a special case if the doi is prefixed */
            if (query.toLowerCase().startsWith("doi:")) {
                query = query.substring(4, query.length());
            }
            this.query = URLEncoder.encode(query, "ISO-8859-1");
            log.debug("Encoded query: " + this.query);
        } catch (UnsupportedEncodingException uee) {
            log.error(uee.getMessage(), uee);
        }
    }

    /**
     * Triggered at the start of an element.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {

        if (localName.equals("journal_title")) {
            collectJournalTitle = true;
        } else if (localName.equals("author")) {
            collectAuthor = true;
        } else if (localName.equals("volume")) {
            collectVolume = true;
        } else if (localName.equals("issue")) {
            collectIssue = true;
        } else if (localName.equals("year")) {
            collectYear = true;
        } else if (localName.equals("article_title")) {
            collectArticleTitle = true;
        }
    }

}
