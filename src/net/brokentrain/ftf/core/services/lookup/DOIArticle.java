package net.brokentrain.ftf.core.services.lookup;

/**
 * Represents an Article as returned by the DOI service.
 * 
 * @see Article
 */
public class DOIArticle extends Article {

    private static final long serialVersionUID = 3751527003405794786L;

    private String doi;

    private String url;

    /**
     * Create an empty DOI Article.
     */
    public DOIArticle() {

    }

    /**
     * Return the DOI for this article.
     * 
     * @return The DOI of this article.
     */
    public String getDOI() {
        return doi;
    }

    /**
     * Return the URL for this article.
     * 
     * @return The URL of this article.
     */
    public String getURL() {
        return url;
    }

    /**
     * Set a specific DOI.
     * 
     * @param doi
     *            The doi to set.
     */
    public void setDOI(String doi) {
        this.doi = doi;
    }

    /**
     * Set a specific URL.
     * 
     * @param url
     *            The url to set.
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Return the string representation of the DOI article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return "______\nDOI: " + doi + "\n" + "Full text URL: " + url + "\n"
                + super.toString();
    }

}
