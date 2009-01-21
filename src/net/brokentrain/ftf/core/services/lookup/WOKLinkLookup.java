package net.brokentrain.ftf.core.services.lookup;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WOKLinkLookup extends DefaultHandler {

    private static final Logger log = Logger.getLogger(WOKLinkLookup.class);

    private boolean collectPublisherLink;

    private URL fullTextLink;

    private StringBuffer fullTextBuffer;

    /**
     * Construct a new PMID Lookup
     */
    public WOKLinkLookup(InputSource content) {

        log.info("Looking for full-text link");

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(content);
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (SAXException se) {
            log.error(se.getMessage(), se);
        }
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if (collectPublisherLink) {
            String elementData = new String(chars, startIndex, endIndex).trim();
            fullTextBuffer.append(elementData);
        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        if (localName.equals("nval") && collectPublisherLink) {
            try {
                log.debug("Found link:" + fullTextBuffer.toString());
                fullTextLink = new URL(fullTextBuffer.toString());
            } catch (MalformedURLException mue) {
                log.error(mue.getMessage(), mue);
            }
            collectPublisherLink = false;
        }
    }

    public URL getLink() {
        return fullTextLink;
    }

    /**
     * Indicate if this lookup has yielded a result.
     * 
     * @return True or false depending if this lookup has a full-text result.
     */
    public boolean hasLink() {
        return (fullTextLink != null);
    }

    /**
     * Triggered at the start of an element.
     */
    @Override
    public void startElement(String namespaceURL, String localName,
            String qualifiedName, Attributes atts) throws SAXException {

        if (localName.equals("nval")) {

            if (StringUtil.isset(atts.getValue("name"))) {

                if (atts.getValue("name").equals("publisher_url")) {

                    collectPublisherLink = true;
                    fullTextBuffer = new StringBuffer();
                }
            }

        }
    }
}
