package net.brokentrain.ftf.core.services;

public interface WebSearchService extends SearchService {

    /**
     * Returns the query string used.
     * 
     * @return The full query string that was searched.
     */
    public String getQueryString();

}
