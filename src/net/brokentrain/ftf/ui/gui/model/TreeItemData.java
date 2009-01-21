package net.brokentrain.ftf.ui.gui.model;

import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.services.SearchService;

public class TreeItemData {

    private static final int TYPE_RESULT = 0;

    private static final int TYPE_SERVICE = 1;

    public static TreeItemData createResult(String name, Result result) {
        TreeItemData treeItemData = new TreeItemData(name, TYPE_RESULT);
        treeItemData.setResult(result);
        return treeItemData;
    }

    public static TreeItemData createService(String name, SearchService service) {
        TreeItemData treeItemData = new TreeItemData(name, TYPE_SERVICE);
        treeItemData.setService(service);
        return treeItemData;
    }

    private int type;

    private String name;

    private Result result;

    private SearchService service;

    private TreeItemData(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Result getResult() {
        return result;
    }

    public SearchService getSearchService() {
        return service;
    }

    public boolean isResult() {
        return (type == TYPE_RESULT);
    }

    public boolean isService() {
        return (type == TYPE_SERVICE);
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public void setService(SearchService service) {
        this.service = service;
    }

}
