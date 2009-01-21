package net.brokentrain.ftf.core.services.lookup;

/**
 * Represents an Article as returned by the ArXiv service.
 * 
 * @see Article
 */
public class ArXivArticle extends Article {

    private static final long serialVersionUID = 3751527003405794786L;

    private String id;

    private String url;

    /**
     * Create an empty ArXiv Article.
     */
    public ArXivArticle() {

    }

    /**
     * Return the ArXiv ID for this article.
     * 
     * @return The ID of this article.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the URL for this article.
     * 
     * @return The URL of this article.
     */
    public String getURL() {
        return this.url;
    }

    /**
     * Set a specific ArXiv ID.
     * 
     * @param id
     *            The ID to set.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set a specific URL.
     * 
     * @param url
     *            The URL to set.
     */
    public void setURL(String url) {
        this.url = url;
    }

    /**
     * Return the string representation of the ArXiv article.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        return "______\nId: " + this.id + "\n" + "Full text: " + this.url
                + "\n" + super.toString();
    }

}
