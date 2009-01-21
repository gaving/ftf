package net.brokentrain.ftf.core.services.lookup;

import java.util.LinkedHashMap;

/**
 * Represents an Article as returned by the WOK service.
 * 
 * @see Article
 */
public class WOKArticle extends Article implements ProcessingArticle {

    private static final long serialVersionUID = 1330001333846955155L;

    private String abstractLink;

    private String fullTextLink;

    private String abstractText;

    private String cKey;

    private String ut;

    private boolean hasFullTextLink;

    /**
     * Create an empty WOK Article.
     */
    public WOKArticle() {

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
     * Return the CKey for this article.
     * 
     * @return The CKey of this article.
     */
    public String getCKey() {
        return cKey;
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
     * Return the UT for this article.
     * 
     * @return The UT of this article.
     */
    public String getUT() {
        return ut;
    }

    /**
     * Return a collection of values for this article.
     * 
     * @return An easily digestible map of this articles values.
     */
    @Override
    public LinkedHashMap<String, String> getValues() {
        LinkedHashMap<String, String> metadataHash = super.getValues();

        metadataHash.put("CKey", cKey);
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
     * Set a specific CKey.
     * 
     * @param cKey
     *            The cKey to set.
     */
    public void setCKey(String cKey) {
        this.cKey = cKey;
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
     * Set a specific UT.
     * 
     * @param ut
     *            The ut to set.
     */
    public void setUT(String ut) {
        this.ut = ut;
    }

    /**
     * Return the string representation of the CKey article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return "______\nCKey: " + cKey + "\n" + "UT: " + ut + "\n"
                + super.toString();
    }

}
