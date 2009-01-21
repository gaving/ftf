package net.brokentrain.ftf.core.services.lookup;

import java.util.LinkedHashMap;

/**
 * Represents an Article as returned by the PMID service.
 * 
 * @see Article
 */
public class PMIDArticle extends Article implements ProcessingArticle {

    private static final long serialVersionUID = 1330001333846955155L;

    private String abstractLink;

    private String fullTextLink;

    private String abstractText;

    private String pmid;

    private boolean hasFullTextLink;

    /**
     * Create an empty PMID Article.
     */
    public PMIDArticle() {

    }

    /**
     * Return a link to the abstract for this article.
     * 
     * @return The link of this articles abstract.
     */
    public String getAbstractLink() {
        return abstractLink;
    }

    /**
     * Return the Abstract for this article.
     * 
     * @return The Abstract of this article.
     */
    public String getAbstractText() {
        return abstractText;
    }

    /**
     * Return a link to the full-text for this article.
     * 
     * @return The link of this articles full-text.
     */
    public String getFullTextLink() {
        return fullTextLink;
    }

    /**
     * Return the PMID for this article.
     * 
     * @return The PMID of this article.
     */
    public String getPMID() {
        return pmid;
    }

    /**
     * Return a collection of values for this article.
     * 
     * @return An easily digestible map of this articles values.
     */
    @Override
    public LinkedHashMap<String, String> getValues() {
        LinkedHashMap<String, String> metadataHash = super.getValues();

        metadataHash.put("PMID", pmid);
        metadataHash.put("Abstract", abstractText);

        return metadataHash;
    }

    public boolean hasFullTextLink() {
        return hasFullTextLink;
    }

    /**
     * Set the link to this articles abstract.
     * 
     * @param abstractLink
     *            The link to set.
     */
    public void setAbstractLink(String abstractLink) {
        this.abstractLink = abstractLink;
    }

    /**
     * Set a specific Abstract.
     * 
     * @param abstractText
     *            The abstractText to set.
     */
    public void setAbstractText(String abstractText) {
        this.abstractText = abstractText;
    }

    /**
     * Set the link to this articles full-text.
     * 
     * @param fullTextLink
     *            The link to set.
     */
    public void setFullTextLink(String fullTextLink) {
        this.fullTextLink = fullTextLink;
    }

    public void setHasFullTextLink(boolean hasFullTextLink) {
        this.hasFullTextLink = hasFullTextLink;
    }

    /**
     * Set a specific PMID.
     * 
     * @param pmid
     *            The pmid to set.
     */
    public void setPMID(String pmid) {
        this.pmid = pmid;
    }

    /**
     * Return the string representation of the PMID article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return "______\nPMID: " + pmid + "\n" + super.toString();
    }

}
