package net.brokentrain.ftf.core;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import net.brokentrain.ftf.core.services.CoreSearchService;
import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.services.ServiceFactory;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.core.services.Scrollable;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.apache.log4j.Logger;

public class Dispatcher extends Observable implements Runnable {

    /**
     * Represents the various states that a Dispatcher can ever be in.
     */
    public enum EventType {
        INIT, WORKING, SLEEP, DEAD, READY, ERROR
    }

    private static final Logger log = Logger.getLogger(Dispatcher.class);;

    private EventType status;

    private ArrayList<ResultSet> results;

    private SearchService searchService;

    private ServiceEntry serviceEntry;

    private String query;

    private boolean increment;

    private boolean investigate;

    private boolean glimpse;

    private boolean processing;

    /**
     * Construct a new Dispatcher with a query and configuration section.
     * 
     * @param serviceEntry
     *            The configuration section to read.
     * @param query
     *            The term to fetch.
     */
    public Dispatcher(ServiceEntry serviceEntry, String query) {

        log.debug("Configuring " + serviceEntry.getDescription());

        this.serviceEntry = serviceEntry;

        /* Set new term list */
        this.query = query;

        /* Not continuously incrementing by default */
        increment = false;
        processing = false;

        /* Get the settings manager */
        SettingsManager settingsManager = SettingsManager.getSettingsManager();

        investigate = settingsManager.isInvestigating();
        glimpse = settingsManager.isGlimpsing();

        /* Create new list for the results */
        results = new ArrayList<ResultSet>();

        String controller = serviceEntry.getController();
        Integer maxResults = Integer.valueOf(serviceEntry.getMaxResults());

        if ((StringUtil.isset(controller)) && (maxResults != null)) {

            /* Create the new service */
            searchService = ServiceFactory.createService(controller);

            /* Define its maximum results */
            searchService.setMaxResults(maxResults);

            /* Any additional information that the source needs */
            searchService.setData(serviceEntry);

            /* Tell the service whether or not to glimpse for URL data */
            searchService.setGlimpse(glimpse);

            /* Tell core services if they should bother making things easier */
            if (searchService instanceof CoreSearchService) {
                ((CoreSearchService) searchService).setInvestigate(investigate);
            }

            status = EventType.READY;
            statusChanged(EventType.READY);
        } else {
            status = EventType.ERROR;
            statusChanged(EventType.ERROR);
        }
    }

    /**
     * Return what service this Dispatcher is handling.
     * 
     * @return The service being used by the dispatcher.
     */
    public String getDescription() {
        return serviceEntry.getDescription();
    }

    /**
     * Return the name of the service this Dispatcher is handling.
     * 
     * @return The name of the service.
     */
    public String getName() {
        return serviceEntry.getSection();
    }

    /**
     * Return the name of this source.
     * 
     * @return The name of this source.
     */
    public String getQueryTerm() {
        return query;
    }

    /**
     * Return the results found by this Dispatcher.
     * 
     * @return A list of results found by this Dispatcher.
     */
    public ArrayList<ResultSet> getResults() {
        return results;
    }

    /**
     * Return the search service being used.
     * 
     * @return The search service being used by the Dispatcher.
     */
    public SearchService getSearchService() {
        return searchService;
    }

    /**
     * Return the relevant configuration entry for this Dispatcher.
     * 
     * @return The configuration entry.
     */
    public ServiceEntry getServiceEntry() {
        return serviceEntry;
    }

    /**
     * Return a Dispatchers current status.
     * 
     * @return An EventType explaining the current status.
     */
    public EventType getStatus() {
        return status;
    }

    /**
     * Indicate if this Dispatcher has retrieved any results.
     * 
     * @return True or false depending if this search has returned any results.
     */
    public boolean hasResults() {
        return results.size() > 0;
    }

    /**
     * Informs this dispatcher to attempt incremening the results onwards.
     */
    public void incrementSearch() {
        this.increment = true;
    }

    /**
     * Determines if a Dispatcher is still busy searching.
     * 
     * @return True or false depending on if this Dispatcher is still busy.
     */
    public boolean isDead() {
        return (status.equals(EventType.DEAD));
    }

    /**
     * Start executing this Dispatcher.
     */
    public void run() {

        if ((searchService == null) || (query == null)) {

            /* No search service configured! */
            statusChanged(EventType.ERROR);
            return;
        }

        results.clear();

        log.info(getName() + ": Querying search service for " + query);

        /* Change the sources status to initialising */
        statusChanged(EventType.INIT);

        /* Give the search service the current query */
        searchService.setQuery(query);

        if (increment) {
            if (searchService instanceof Scrollable) {
                ((Scrollable) searchService).increment();
            } else {
                log.warn("Non-scrollable service given a start value!");
            }
        }

        if (processing) {
            if (searchService instanceof CoreSearchService) {
                log.debug("Telling service we're processing abstracts");
                ((CoreSearchService) searchService).setProcessingAbstract(true);
            }
        }

        statusChanged(EventType.WORKING);

        /* Tell the service to do the search */
        searchService.doSearch();

        if (!searchService.hasResults()) {

            /* The search service didn't return anything! */
            statusChanged(EventType.DEAD);
            return;
        }

        HashMap<URI, Resource> serviceResults = searchService.getResults();

        /* 'Crawl' each one of the results returned by the service */
        for (URI uri : serviceResults.keySet()) {

            /* Busy processing! */
            log.debug(getName() + ": Processing " + uri);

            try {

                Result result = new Result();
                result.setURI(new URI(uri.toString()));
                result.setResource(serviceResults.get(uri));

                ResultSet resultSetEntry = new ResultSet();

                /* Set the original link returned by the service */
                resultSetEntry.setOriginalLink(result);

                /*
                 * TODO: This should probably be an optional setting somewhere,
                 * there are cases where you might want to investigate local
                 * matches for PDFs and stuff, but for now we'll only make that
                 * for remote (see Web) based services.
                 */
                if ((!uri.getScheme().equals("file")) && (investigate)) {

                    /* Investigate any results that lie in this page */
                    PageHandler pageHandler = new PageHandler();
                    pageHandler.setName(serviceEntry.getSection());
                    pageHandler.setURL(uri.toURL());
                    pageHandler.doInvestigate();

                    /* Set additional results */
                    resultSetEntry.setData(pageHandler.getResults());
                }

                results.add(resultSetEntry);
            } catch (URISyntaxException use) {
                log.error(getName() + ": " + use.getMessage(), use);
            } catch (MalformedURLException mue) {
                log.error(getName() + ": " + mue.getMessage(), mue);
            }
        }

        /* Parser has finally exhausted all input and is finished */
        statusChanged(EventType.DEAD);
    }

    /**
     * Determines if a Dispatcher will pull HTTP data from each URL.
     * 
     * @param True
     *            or False depending on if it should glimpse at each URL.
     */
    public void setGlimpse(boolean glimpse) {
        this.glimpse = glimpse;
    }

    /**
     * Determines if a Dispatcher will investigate each result for papers.
     * 
     * @param True
     *            or False depending on if it should investigate.
     */
    public void setInvestigate(boolean investigate) {
        this.investigate = investigate;
    }

    public void setProcessingAbstract(boolean processing) {
        this.processing = processing;
    }

    private void statusChanged(EventType e) {

        if (!status.equals(e)) {

            /* Update the new status */
            status = e;

            if (status.equals(EventType.INIT)
                    || status.equals(EventType.WORKING)
                    || status.equals(EventType.SLEEP)
                    || status.equals(EventType.READY)
                    || status.equals(EventType.ERROR)
                    || status.equals(EventType.DEAD)) {

                /* Indicate our state has changed */
                setChanged();

                /* Notify any listening observers */
                notifyObservers();
            }
        }
    }
}
