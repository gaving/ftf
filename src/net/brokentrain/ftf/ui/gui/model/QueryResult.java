package net.brokentrain.ftf.ui.gui.model;

import java.io.Serializable;
import java.util.ArrayList;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.ResultSet;
import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.settings.ServiceEntry;

public class QueryResult implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private transient SearchService service;

    private ArrayList<ResultSet> data;

    private ServiceEntry serviceEntry;

    public QueryResult(ArrayList<ResultSet> data) {
        this.data = data;
    }

    public QueryResult(Dispatcher dispatcher) {
        service = dispatcher.getSearchService();
        serviceEntry = dispatcher.getServiceEntry();
        data = dispatcher.getResults();
    }

    public ArrayList<ResultSet> getData() {
        return data;
    }

    public SearchService getSearchService() {
        return service;
    }

    public ServiceEntry getServiceEntry() {
        return serviceEntry;
    }
}
