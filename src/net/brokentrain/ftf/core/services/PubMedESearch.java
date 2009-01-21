package net.brokentrain.ftf.core.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Responsible for extracting Id's out of a formatted ESearch response. Takes
 * both a base address (ESearch url) from a configuration file and a list of
 * terms to search for {@link #PubMedESearch(String)}. Once a PubMedESearch has
 * been created, it can be started with {@link #fetch fetch()}. Upon finishing
 * execution, the matching ids for the queries can be obtained with
 * {@link #getIds getIds()}.
 */
public class PubMedESearch extends DefaultHandler {

    private static final Logger log = Logger.getLogger(PubMedESearch.class);

    private ArrayList<String> collectedIds;

    private boolean collectIds;

    private int maxResults;

    private int start;

    private String query;

    private URL url;

    /**
     * Construct a new id extractor by creating a new extractor object for a
     * specific string.
     * 
     * @param textURL
     *            The ESearch URL to try.
     */
    public PubMedESearch(String textURL) {
        collectIds = false;
        collectedIds = new ArrayList<String>();

        try {
            url = new URL(textURL);
        } catch (MalformedURLException mue) {
            log.error(mue.getMessage(), mue);
        }
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {
        if (collectIds) {

            /* Extract the digits (PMID) we're interested in */
            String id = new String(chars, startIndex, endIndex).trim();
            try {

                /* Add to list */
                log.debug("Found " + id + ", adding to list to try");
                collectedIds.add(id);
            } catch (NumberFormatException nfe) {
                log.error("Ignoring malformed Id. (" + id + ")", nfe);
            }
        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        /* Not interested in any other xml elements */
        if (qualifiedName.equals("IdList")) {
            collectIds = false;
        }
    }

    /**
     * Start the fetch process with the loaded terms.
     */
    public void fetch() {

        /* For each term given, create an ESearch query */
        String url = String.format(this.url.toString(), maxResults, start,
                query);
        log.info("Attempting to open " + url);

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(url);
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (SAXException se) {
            log.error(se.getMessage(), se);
        }
    }

    /**
     * Return all found ids.
     * 
     * @return A collection of ids.
     */
    public ArrayList<String> getIds() {
        return collectedIds;
    }

    public boolean hasResults() {
        return (collectedIds.size() > 0);
    }

    /**
     * Set a maximum number of results to return.
     * 
     * @param maxResults
     *            The maximum results to return.
     */
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    /**
     * Set a new query term to search for.
     * 
     * @param query
     *            The new query to search for.
     */
    public void setQuery(String query) {

        /* Assume the query is already properly encoded */
        this.query = query;
    }

    /**
     * Set the starting result for the query.
     * 
     * @param start
     *            The article index for the start.
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * Triggered at the start of an element.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {

        /* Start recording the ids if we hit the list */
        if (localName.equals("IdList")) {
            collectIds = true;
        }
    }

}
