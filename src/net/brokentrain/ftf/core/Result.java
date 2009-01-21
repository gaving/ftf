package net.brokentrain.ftf.core;

import java.io.Serializable;
import java.net.URI;

/**
 * Represents a direct link to a full-text resource, regardless of the type of
 * result. A result expects to be constructed and assigned a specific Resource
 * object to represent the resource that it will hold. The Resource object
 * represents any type of match from URI matches to File objects on disk, and
 * contains any associated Article information.
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private URI uri;

    private Resource resource;

    /**
     * Construct a new result.
     */
    public Result() {
    }

    /**
     * Return the resource store of this object.
     * 
     * @return The objects resource.
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Return the URI of this result.
     * 
     * @return The direct URI.
     */
    public URI getURI() {
        return uri;
    }

    /**
     * Set the specific resource this result holds.
     * 
     * @param resource
     *            The resource to hold.
     */
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /**
     * Set the URI of this result.
     * 
     * @param uri
     *            The URI to use.
     */
    public void setURI(URI uri) {
        this.uri = uri;
    }
}
