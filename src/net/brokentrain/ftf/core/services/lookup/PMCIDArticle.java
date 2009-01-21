package net.brokentrain.ftf.core.services.lookup;

import java.util.LinkedHashMap;

/**
 * Represents an Article as returned by the PMCID service.
 * 
 * @see Article
 */
public class PMCIDArticle extends Article implements ProcessingArticle {

    private static final long serialVersionUID = 1330001333846955155L;

    private String abstractLink;

    private String fullTextLink;

    private String abstractText;

    private String pmcid;

    private boolean hasFullTextLink;

    /**
     * Create an empty PMCID Article.
     */
    public PMCIDArticle() {

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
     * Return the PMCID for this article.
     * 
     * @return The PMCID of this article.
     */
    public String getPMCID() {
        return pmcid;
    }

    /**
     * Return a collection of values for this article.
     * 
     * @return An easily digestible map of this articles values.
     */
    @Override
    public LinkedHashMap<String, String> getValues() {
        LinkedHashMap<String, String> metadataHash = super.getValues();

        metadataHash.put("PMCID", pmcid);
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
     * Set a specific PMCID.
     * 
     * @param pmcid
     *            The pmcid to set.
     */
    public void setPMCID(String pmcid) {
        this.pmcid = pmcid;
    }

    /**
     * Return the string representation of the PMCID article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return "______\nPMCID: " + pmcid + "\n" + super.toString();
    }

}
