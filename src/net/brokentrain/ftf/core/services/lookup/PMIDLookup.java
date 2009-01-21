package net.brokentrain.ftf.core.services.lookup;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import java.io.IOException;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Responsible for looking up an Article's metadata based on PMID. Using the
 * official EUtility services provided by PubMed can obtain a direct link to an
 * articles full-text based on it's PMID.
 */
public class PMIDLookup extends DefaultHandler implements LookupService {

    private static final Logger log = Logger.getLogger(PMIDLookup.class);

    private static final String BASE_QUERY_STRING = "http://www.ncbi.nlm.nih.gov/entrez/eutils/"
            + "efetch.fcgi?tool=FTF&email=gavin@brokentrain.net"
            + "&id=%s&db=pubmed&retmode=xml";

    private List<Article> articles;

    private PMIDArticle article;

    private ArrayList<String> idList;

    private HashMap<String, Boolean> articleLinkStore;

    private boolean collectArticleTitle;

    private boolean collectAuthors;

    private boolean collectAuthorLastname;

    private boolean collectAuthorForename;

    private boolean collectAbstractText;

    private boolean collectDateCreated;

    private boolean collectYear;

    private boolean collectJournal;

    private boolean collectPMID;

    private boolean collectJournalTitle;

    private boolean collectJournalIssue;

    private boolean collectVolume;

    private boolean collectIssue;

    private StringBuffer abstractBuffer;

    private StringBuffer articleBuffer;

    private StringBuffer authorBuffer;

    /**
     * Construct a new PMID Lookup
     */
    public PMIDLookup(ArrayList<String> idList) {
        this.idList = idList;
    }

    /**
     * Triggered at the traversal of an XML elements value.
     */
    @Override
    public void characters(char[] chars, int startIndex, int endIndex) {

        if ((collectArticleTitle) || (collectPMID) || (collectAbstractText)
                || (collectYear) || (collectVolume) || (collectIssue)
                || (collectJournalTitle) || (collectAuthors)) {

            String elementData = new String(chars, startIndex, endIndex).trim();

            if (collectArticleTitle) {
                articleBuffer.append(elementData);
            } else if (collectPMID) {
                article.setPMID(elementData);

                if (articleLinkStore != null) {

                    Boolean hasLink = articleLinkStore.get(elementData);

                    if (hasLink != null) {
                        article.setHasFullTextLink(Boolean.valueOf(hasLink));
                    } else {
                        article.setHasFullTextLink(false);
                    }
                } else {
                    article.setHasFullTextLink(false);
                }
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
            } else if (collectAuthorForename) {
                authorBuffer.append(", ");
                authorBuffer.append(elementData);
            } else if (collectAuthorLastname) {
                authorBuffer.append(elementData);
            }
        }
    }

    /**
     * Triggered at the end of an element.
     */
    @Override
    public void endElement(String namespaceUri, String localName,
            String qualifiedName) throws SAXException {

        if (localName.equals("PubmedArticle")) {
            articles.add(article);
        } else if (localName.equals("PMID")) {
            collectPMID = false;
        } else if (localName.equals("ArticleTitle")) {
            collectArticleTitle = false;
            article.setArticleTitle(articleBuffer.toString());
            articleBuffer = null;
        } else if (localName.equals("AbstractText")) {
            collectAbstractText = false;
            article.setAbstractText(abstractBuffer.toString());
            abstractBuffer = null;
        } else if (localName.equals("DateCreated")) {
            collectDateCreated = false;
        } else if (localName.equals("Year") && collectDateCreated) {
            collectYear = false;
        } else if (localName.equals("Journal")) {
            collectJournal = false;
        } else if (localName.equals("JournalIssue") && collectJournal) {
            collectJournalIssue = false;
        } else if (localName.equals("Volume") && collectJournalIssue) {
            collectVolume = false;
        } else if (localName.equals("Issue") && collectJournalIssue) {
            collectIssue = false;
        } else if (localName.equals("Title") && collectJournal) {
            collectJournalTitle = false;
        } else if (localName.equals("AuthorList")) {
            collectAuthors = false;
            article.setAuthor(authorBuffer.toString());
            authorBuffer = null;
        } else if (localName.equals("LastName") && collectAuthors) {
            collectAuthorLastname = false;
        } else if (localName.equals("ForeName") && collectAuthors) {
            collectAuthorForename = false;
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
        String queryString = String.format(PMIDLookup.BASE_QUERY_STRING, ids);
        log.info("Looking up " + queryString);

        PubMedELink pubMedELink = new PubMedELink(idList);
        pubMedELink.lookup();
        articleLinkStore = pubMedELink.getLinkOuts();

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

        if (localName.equals("PubmedArticle")) {
            article = new PMIDArticle();
        } else if (localName.equals("PMID")) {
            collectPMID = true;
        } else if (localName.equals("ArticleTitle")) {
            collectArticleTitle = true;
            articleBuffer = new StringBuffer();
        } else if (localName.equals("AbstractText")) {
            collectAbstractText = true;
            abstractBuffer = new StringBuffer();
        } else if (localName.equals("DateCreated")) {
            collectDateCreated = true;
        } else if (localName.equals("Year") && collectDateCreated) {
            collectYear = true;
        } else if (localName.equals("Journal")) {
            collectJournal = true;
        } else if (localName.equals("JournalIssue") && collectJournal) {
            collectJournalIssue = true;
        } else if (localName.equals("Volume") && collectJournalIssue) {
            collectVolume = true;
        } else if (localName.equals("Issue") && collectJournalIssue) {
            collectIssue = true;
        } else if (localName.equals("Title") && collectJournal) {
            collectJournalTitle = true;
        } else if (localName.equals("AuthorList")) {
            collectAuthors = true;
            authorBuffer = new StringBuffer();
        } else if (localName.equals("LastName") && collectAuthors) {
            collectAuthorLastname = true;
        } else if (localName.equals("ForeName") && collectAuthors) {
            if (authorBuffer == null) {
                authorBuffer = new StringBuffer();
            }
            collectAuthorForename = true;
        }
    }
}
