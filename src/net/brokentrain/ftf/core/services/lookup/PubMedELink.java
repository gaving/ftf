package net.brokentrain.ftf.core.services.lookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PubMedELink extends DefaultHandler {

    private static final Logger log = Logger.getLogger(PubMedELink.class);

    private static final String BASE_QUERY_STRING = "http://www.ncbi.nlm.nih.gov/entrez/eutils/"
            + "elink.fcgi??tool=FTF&email=gavin@brokentrain.net"
            + "dbfrom=pubmed&id=%s&retmode=ref&cmd=lcheck";

    private boolean collectId;

    private boolean hasPublisherLink;

    private HashMap<String, Boolean> articleLinkStore;

    private ArrayList<String> idList;

    /**
     * Construct a new PMID Lookup
     */
    public PubMedELink(ArrayList<String> idList) {

        articleLinkStore = new HashMap<String, Boolean>();
        this.idList = idList;

        for (String id : idList) {

            /* Default to no article having link outs */
            articleLinkStore.put(id, false);
        }
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if (collectId) {

            String elementData = new String(chars, startIndex, endIndex).trim();

            if (articleLinkStore.containsKey(elementData)) {
                articleLinkStore.put(elementData, hasPublisherLink);
            }

        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        if (localName.equals("Id")) {
            collectId = false;
            hasPublisherLink = false;
        }
    }

    /**
     * Return an associated Article structure for this result.
     * 
     * @return An article with associated metadata.
     */
    public HashMap<String, Boolean> getLinkOuts() {
        return articleLinkStore;
    }

    /**
     * Lookup the PMID
     */
    public void lookup() {

        String ids = StringUtil.join(idList, ',');
        String queryString = String.format(PubMedELink.BASE_QUERY_STRING, ids);
        log.info("Finding linkouts " + queryString);

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(queryString);
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (SAXException se) {
            log.error(se.getMessage(), se);
        }
    }

    /**
     * Triggered at the start of an element.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {

        if (localName.equals("Id")) {
            collectId = true;

            if (StringUtil.isset(atts.getValue("HasLinkOut"))) {
                if (atts.getValue("HasLinkOut").equals("Y")) {
                    hasPublisherLink = true;
                }
            }
        }
    }
}
