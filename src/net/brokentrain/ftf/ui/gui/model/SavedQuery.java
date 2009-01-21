package net.brokentrain.ftf.ui.gui.model;

import java.io.Serializable;
import java.util.ArrayList;

public class SavedQuery implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private ArrayList<QueryResult> data;

    private String query;

    private int tabType;

    public SavedQuery(ArrayList<QueryResult> data, String query, int tabType) {
        this.data = data;
        this.query = query;
        this.tabType = tabType;
    }

    public ArrayList<QueryResult> getData() {
        return data;
    }

    public String getQuery() {
        return query;
    }

    public int getTabType() {
        return tabType;
    }

}
