package net.brokentrain.ftf.core;

import java.io.Serializable;

import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.core.services.lookup.Article;

public class Resource implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private Article article;

    private DataStore data;

    /**
     * Construct a new resource.
     */
    public Resource() {
    }

    /**
     * Return the Article of this resource.
     * 
     * @return The direct Article.
     */
    public Article getArticle() {
        return article;
    }

    /**
     * Return the data store of this object.
     * 
     * @return The objects data.
     */
    public DataStore getData() {
        return data;
    }

    /**
     * Set the Article of this resource.
     * 
     * @param article
     *            The Article to use.
     */
    public void setArticle(Article article) {
        this.article = article;
    }

    /**
     * Set the specific data this resource holds.
     * 
     * @param data
     *            The data to hold.
     */
    public void setData(DataStore data) {
        this.data = data;
    }
}
