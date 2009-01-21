package net.brokentrain.ftf.core.services.lookup;

import java.net.URL;
import java.util.LinkedHashMap;

/**
 * Represents an Article as returned by a web resource.
 * 
 * @see Article
 */
public class URLArticle extends Article {

    private static final long serialVersionUID = -7201587859914429781L;

    private String url;

    private String snippet;

    /**
     * Create an empty article.
     */
    public URLArticle() {

    }

    /**
     * Return the context snippet of this article.
     * 
     * @return The context snippet of this article.
     */
    public String getSnippet() {
        return snippet;
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
     * Return any extended article properties.
     * 
     * @return Any extended article properties.
     */
    @Override
    public LinkedHashMap<String, String> getValues() {

        LinkedHashMap<String, String> metadataHash = new LinkedHashMap<String, String>();
        metadataHash.put("Snippet", this.snippet);
        metadataHash.putAll(super.getValues());

        return metadataHash;
    }

    /**
     * Set a specific article snippet.
     * 
     * @param snippet
     *            The snippet to set.
     */
    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    /**
     * Set a specific URL.
     * 
     * @param url
     *            The URL to set.
     */
    public void setURL(URL url) {
        this.url = url.toString();
    }

}
