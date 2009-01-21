package net.brokentrain.ftf.core.services.lookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WOKLookup extends DefaultHandler implements LookupService {

    private static final Logger log = Logger.getLogger(WOKLookup.class);

    private List<Article> articles;

    private WOKArticle article;

    private InputSource content;

    private boolean collectArticleTitle;

    private boolean collectAbstractText;

    private boolean collectCKey;

    private boolean collectYear;

    private boolean collectJournalTitle;

    private boolean collectUT;

    private StringBuffer abstractBuffer;

    private StringBuffer articleBuffer;

    /**
     * Construct a new PMID Lookup
     */
    public WOKLookup(InputSource content) {
        this.content = content;
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if ((collectArticleTitle) || (collectCKey) || (collectUT)
                || (collectAbstractText) || (collectYear)
                || (collectJournalTitle)) {

            String elementData = new String(chars, startIndex, endIndex).trim();

            if (collectArticleTitle) {
                articleBuffer.append(elementData);
            } else if (collectAbstractText) {
                abstractBuffer.append(elementData);
            } else if (collectCKey) {
                article.setCKey(elementData);
            } else if (collectUT) {
                article.setUT(elementData);
            } else if (collectJournalTitle) {
                article.setJournalTitle(elementData);
            }
        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        if (localName.equals("REC")) {
            articles.add(article);
        } else if (localName.equals("ut")) {
            collectUT = false;
        } else if (localName.equals("i_ckey")) {
            collectCKey = false;
        } else if (localName.equals("item_title")) {
            collectArticleTitle = false;
            article.setArticleTitle(articleBuffer.toString());
        } else if (localName.equals("abstract")) {
            collectAbstractText = false;
            article.setAbstractText(abstractBuffer.toString());
        } else if (localName.equals("bib_issue")) {
        } else if (localName.equals("source_title")) {
            collectJournalTitle = false;
        }
    }

    /**
     * Return an associated Article structure for this result.
     * 
     * @return An article with associated metadata.
     */
    public List<Article> getArticles() {
        return articles;
    }

    /**
     * Indicate if this lookup has yielded a result.
     * 
     * @return True or false depending if this lookup has a result.
     */
    public boolean hasResults() {
        return (articles.size() > 0);
    }

    /**
     * Lookup the PMID
     */
    public void lookup() {

        log.info("Trying to resolve");

        articles = new ArrayList<Article>();

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(content);
        } catch (IOException ioe) {
            article = null;
            log.error(ioe.getMessage(), ioe);
        } catch (SAXException se) {
            article = null;
            log.error(se.getMessage(), se);
        }
    }

    /**
     * Triggered at the start of an element.
     */
    @Override
    public void startElement(String namespaceURI, String localName,
            String qualifiedName, Attributes atts) throws SAXException {

        if (localName.equals("REC")) {
            article = new WOKArticle();
        } else if (localName.equals("ut")) {
            collectUT = true;
        } else if (localName.equals("i_ckey")) {
            collectCKey = true;
        } else if (localName.equals("item_title")) {
            collectArticleTitle = true;
            articleBuffer = new StringBuffer();
        } else if (localName.equals("bib_issue")) {
            article.setYear(atts.getValue("year"));
            article.setVolume(atts.getValue("vol"));
        } else if (localName.equals("abstract")) {
            collectAbstractText = true;
            abstractBuffer = new StringBuffer();
        } else if (localName.equals("source_title")) {
            collectJournalTitle = true;
        }
    }

}
