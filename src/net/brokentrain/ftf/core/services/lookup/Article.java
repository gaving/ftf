package net.brokentrain.ftf.core.services.lookup;

import java.io.Serializable;
import java.util.LinkedHashMap;
import bibtex.dom.BibtexEntry;
import bibtex.dom.BibtexFile;

import com.thoughtworks.xstream.XStream;

/**
 * Represents an Article match. This class cannot be created directly and
 * Articles should be created depending on their specific type using other
 * classes. Once an Article has been constructed elsewhere, data can be
 * retrieved through methods such as {@link #getArticleTitle getArticleTitle()},
 * {@link #getAuthor getAuthor()}, etc. The presentation of an Article can be
 * returned in different formats with use of the {@link #toXML toXML()} and
 * {@link #toBibtex toBibtex()} methods respectively.
 * 
 * @see DOIArticle
 * @see PMIDArticle
 * @see ArXivArticle
 * @see URLArticle
 */
abstract public class Article implements Serializable {

    private String author;

    private String articleTitle;

    private String journalTitle;

    private String issue;

    private String volume;

    private String source;

    private String year;

    /**
     * Return an articles paper title.
     * 
     * @return The articles paper title.
     */
    public String getArticleTitle() {
        return articleTitle;
    }

    /**
     * Return an articles author(s).
     * 
     * @return The articles author(s).
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Return an articles journal issue.
     * 
     * @return The articles journal issue.
     */
    public String getIssue() {
        return issue;
    }

    /**
     * Return an articles journal title.
     * 
     * @return The articles journal title.
     */
    public String getJournalTitle() {
        return journalTitle;
    }

    /**
     * Return an articles journal volume.
     * 
     * @return The articles journal volume.
     */
    public String getSource() {
        return source;
    }

    /**
     * Return a collection of values for this article.
     * 
     * @return An easily digestible map of this articles values.
     */
    public LinkedHashMap<String, String> getValues() {
        LinkedHashMap<String, String> metadataHash = new LinkedHashMap<String, String>();

        metadataHash.put("Title", articleTitle);
        metadataHash.put("Journal", journalTitle);
        metadataHash.put("Author", author);
        metadataHash.put("Issue", issue);
        metadataHash.put("Volume", volume);
        metadataHash.put("Year", year);

        return metadataHash;
    }

    /**
     * Return an articles journal volume.
     * 
     * @return The articles journal volume.
     */
    public String getVolume() {
        return volume;
    }

    /**
     * Return an articles year.
     * 
     * @return The articles year.
     */
    public String getYear() {
        return year;
    }

    /**
     * Set an articles paper title.
     * 
     * @param articleTitle
     *            The articles paper title.
     */
    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    /**
     * Set an articles author(s).
     * 
     * @param author
     *            The articles authors(s).
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Set an articles journal issue.
     * 
     * @param issue
     *            The articles journals issue.
     */
    public void setIssue(String issue) {
        this.issue = issue;
    }

    /**
     * Set an articles journal title.
     * 
     * @param journalTitle
     *            The articles journal title.
     */
    public void setJournalTitle(String journalTitle) {
        this.journalTitle = journalTitle;
    }

    /**
     * Set an articles parent source name.
     * 
     * @param source
     *            The articles source name.
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Set an articles journal volume.
     * 
     * @param volume
     *            The articles journals volume.
     */
    public void setVolume(String volume) {
        this.volume = volume;
    }

    /**
     * Set an articles year.
     * 
     * @param year
     *            The articles year.
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * Return a standard bibtex representation of the article.
     * 
     * @return A standard bibtex entry for the article.
     */
    public String toBibtex() {
        BibtexFile bibtexFile = new BibtexFile();
        BibtexEntry entry = bibtexFile.makeEntry("article", "generated");

        LinkedHashMap<String, String> articleValues = getValues();

        /* Loop through all our article information */
        for (String key : articleValues.keySet()) {

            String value = articleValues.get(key);

            /* Check if we have some meaningful data here */
            if ((value != null) && (!value.equals(""))) {

                /* Set key to new data */
                entry.setField(key, bibtexFile.makeString(value));
            }
        }

        return entry.toString();
    }

    /**
     * Return the string representation of the article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return String.format("Article Title: %s\n" + "Source: %s\n"
                + "Author: %s\n" + "Journal Title, Issue, Volume: %s, %s, %s\n"
                + "Year: %s\n", articleTitle, source, author, journalTitle,
                issue, volume, year);
    }

    /**
     * Return the XML representation of the article.
     * 
     * @return A xml representation suitable for further processing.
     */
    public String toXML() {
        XStream xstream = new XStream();
        xstream.alias("Article", Article.class);
        return xstream.toXML(this);
    }
}
