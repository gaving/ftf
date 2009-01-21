package net.brokentrain.ftf.core.services.lookup;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PMCIDLookup extends DefaultHandler implements LookupService {

    private static final Logger log = Logger.getLogger(PMCIDLookup.class);

    private static final String BASE_QUERY_STRING = "http://www.ncbi.nlm.nih.gov/entrez/eutils/"
            + "efetch.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&db=pmc&id=%s&retmode=xml";

    private List<Article> articles;

    private PMCIDArticle article;

    private ArrayList<String> idList;

    private boolean collectArticleTitle;

    private boolean collectAbstractText;

    private boolean collectYear;

    private boolean collectJournalTitle;

    private boolean collectPMCID;

    private boolean collectVolume;

    private boolean collectIssue;

    private StringBuffer abstractBuffer;

    private StringBuffer articleBuffer;

    /**
     * Construct a new PMID Lookup
     */
    public PMCIDLookup(ArrayList<String> idList) {
        this.idList = idList;
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if ((collectArticleTitle) || (collectPMCID) || (collectAbstractText)
                || (collectYear) || (collectVolume) || (collectIssue)
                || (collectJournalTitle)) {

            String elementData = new String(chars, startIndex, endIndex).trim();

            if (collectArticleTitle) {
                articleBuffer.append(elementData);
            } else if (collectPMCID) {
                article.setPMCID(elementData);
            } else if (collectAbstractText) {
                abstractBuffer.append(elementData);
            } else if (collectYear) {
                article.setYear(elementData);
            } else if (collectVolume) {
                article.setVolume(elementData);
            } else if (collectIssue) {
                article.setIssue(elementData);
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

        if (localName.equals("article")) {
            articles.add(article);
        } else if (localName.equals("article-id")) {
            collectPMCID = false;
        } else if (localName.equals("article-title")) {
            collectArticleTitle = false;
            article.setArticleTitle(articleBuffer.toString());
        } else if (localName.equals("abstract")) {
            collectAbstractText = false;
            article.setAbstractText(abstractBuffer.toString());
        } else if (localName.equals("year")) {
            collectYear = false;
        } else if (localName.equals("volume")) {
            collectVolume = false;
        } else if (localName.equals("issue")) {
            collectIssue = false;
        } else if (localName.equals("journal-title")) {
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

        String ids = StringUtil.join(idList, ',');
        String queryString = String.format(PMCIDLookup.BASE_QUERY_STRING, ids);
        log.info("Looking up " + queryString);

        articles = new ArrayList<Article>();

        try {
            SAXParser p = new SAXParser();
            p.setContentHandler(this);
            p.parse(queryString);
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

        if (localName.equals("article")) {
            article = new PMCIDArticle();
        } else if (localName.equals("article-id")) {
            if (StringUtil.isset(atts.getValue("pub-id-type"))) {
                String pubIdType = atts.getValue("pub-id-type");
                if (pubIdType.equals("pmc")) {
                    collectPMCID = true;
                }
            }
        } else if (localName.equals("article-title")) {
            collectArticleTitle = true;
            articleBuffer = new StringBuffer();
        } else if (localName.equals("abstract")) {
            collectAbstractText = true;
            abstractBuffer = new StringBuffer();
        } else if (localName.equals("year")) {
            collectYear = true;
        } else if (localName.equals("volume")) {
            collectVolume = true;
        } else if (localName.equals("issue")) {
            collectIssue = true;
        } else if (localName.equals("journal-title")) {
            collectJournalTitle = true;
        }
    }
}
