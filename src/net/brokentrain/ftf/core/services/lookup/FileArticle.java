package net.brokentrain.ftf.core.services.lookup;

import java.net.URI;
import java.util.LinkedHashMap;

/**
 * Represents an Article as returned by a web resource.
 * 
 * @see Article
 */
public class FileArticle extends Article {

    private static final long serialVersionUID = -7201587859914429781L;

    private String uri;

    /**
     * Create an empty article.
     */
    public FileArticle() {

    }

    /**
     * Return the URI for this article.
     * 
     * @return The URI of this article.
     */
    public String getURI() {
        return uri;
    }

    /**
     * Return any extended article properties.
     * 
     * @return Any extended article properties.
     */
    @Override
    public LinkedHashMap<String, String> getValues() {

        LinkedHashMap<String, String> metadataHash = new LinkedHashMap<String, String>();
        metadataHash.put("URI", uri);
        metadataHash.putAll(super.getValues());

        return metadataHash;
    }

    /**
     * Set a specific URI.
     * 
     * @param uri
     *            The URI to set.
     */
    public void setURI(URI uri) {
        this.uri = uri.toString();
    }

}
