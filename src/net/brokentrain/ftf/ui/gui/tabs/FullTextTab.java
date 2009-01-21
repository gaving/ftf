package net.brokentrain.ftf.ui.gui.tabs;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import net.brokentrain.ftf.core.Dispatcher;
import net.brokentrain.ftf.core.Resource;
import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.ResultSet;
import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.services.WebSearchService;
import net.brokentrain.ftf.core.settings.ServiceEntry;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.model.QueryResult;
import net.brokentrain.ftf.ui.gui.model.SavedQuery;
import net.brokentrain.ftf.ui.gui.model.TreeItemData;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class FullTextTab extends SearchTab {

    private EventManager eventManager;

    public FullTextTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager,
            boolean showMessageArea) {
        super(fetcherGui, tabItem, tabManager, showMessageArea);

        tabItem.getParent();
        eventManager = fetcherGui.getEventManager();
        fetcherGui.getStatusbar();

        SearchTab.TAB_TYPE = SearchTab.TAB_TYPE_QUERY;
    }

    public void addServiceResult(QueryResult queryResult) {

        ServiceEntry serviceEntry = queryResult.getServiceEntry();

        String description = serviceEntry.getDescription();

        /* Hide the message area if it's the first result */
        hideMessageArea();

        /* Populate the new tree with the results */
        for (ResultSet resultSet : queryResult.getData()) {

            TreeItem serviceParent = getServiceParent(queryResult);

            Result originalResult = resultSet.getOriginalLink();

            TreeItem originalParent = insertParentItem(description,
                    serviceParent, originalResult, null);

            ArrayList<Result> childResults = resultSet.getData();

            /* Some things might not have any child results! */
            if ((childResults != null) && (!childResults.isEmpty())) {

                /* Create a new item under this parent for each */
                for (Result result : childResults) {
                    insertChildItem(description, originalParent, result);
                }
            }

        }
    }

    public void buildResultMenu(Menu menu, final Result result,
            final TreeItem[] selection) {

        /* Open in internal browser */
        MenuItem openItem = new MenuItem(menu, SWT.NONE);
        openItem.setText("Open");
        openItem.setImage(PaintUtil.iconOpen);
        openItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (selection.length > 0) {
                    String downloadURL = result.getURI().toString();
                    if (StringUtil.isset(downloadURL)) {
                        eventManager.actionOpenInInternalBrowser(downloadURL,
                                query);
                    }
                }
            }
        });

        /* Launch in external browser */
        MenuItem launchItem = new MenuItem(menu, SWT.NONE);
        launchItem.setText("Launch");
        launchItem.setImage(PaintUtil.iconLaunch);
        launchItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (selection.length > 0) {
                    for (TreeItem selectedItem : selection) {
                        TreeItemData treeItemData = (TreeItemData) selectedItem
                                .getData();
                        Result result = treeItemData.getResult();
                        String downloadURL = result.getURI().toString();
                        if (StringUtil.isset(downloadURL)) {
                            eventManager.actionLaunchURL(downloadURL);
                        }
                    }
                }
            }
        });

        new MenuItem(menu, SWT.SEPARATOR);

        /* Download */
        MenuItem downloadItem = new MenuItem(menu, SWT.NONE);
        downloadItem.setText("Download");
        downloadItem.setImage(PaintUtil.iconDownload);
        downloadItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (selection.length > 0) {
                    for (TreeItem selectedItem : selection) {
                        TreeItemData treeItemData = (TreeItemData) selectedItem
                                .getData();
                        Result result = treeItemData.getResult();
                        String downloadURL = result.getURI().toString();
                        if (StringUtil.isset(downloadURL)) {
                            eventManager.actionAddDownload(downloadURL);
                        }
                    }
                }
            }
        });

        MenuItem copyItem = new MenuItem(menu, SWT.NONE);
        copyItem.setText("Copy Address");
        copyItem.setImage(PaintUtil.iconCopy);
        copyItem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                TreeItem selectedItem = selection[0];
                if (selectedItem != null) {
                    String copyURL = result.getURI().toString();
                    if (StringUtil.isset(copyURL)) {
                        eventManager.actionCopyToClipboard(copyURL);
                    }
                }
            }
        });

    }

    @Override
    public void createTree() {

        /* Create actual results tree */
        tree = new Tree(getQueryArea(), SWT.BORDER | SWT.MULTI
                | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.H_SCROLL);
        tree.setLayoutData(new GridData(GridData.FILL_BOTH));
        tree.setHeaderVisible(false);
        tree.setLinesVisible(true);

        tree.addListener(SWT.MouseDoubleClick, new Listener() {
            public void handleEvent(Event e) {
                onMouseDoubleClick(e);
            }
        });

        tree.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                onSelection();
            }
        });

        final Menu menu = new Menu(tree);
        tree.setMenu(menu);
        menu.addMenuListener(new MenuAdapter() {
            @Override
            public void menuShown(MenuEvent e) {

                /* Get rid of existing menu items */
                MenuItem[] items = menu.getItems();
                for (MenuItem element : items) {
                    (element).dispose();
                }

                final TreeItem[] selectedItems = tree.getSelection();

                /* Return on empty selection */
                if (selectedItems.length < 1) {
                    return;
                }

                /* Get the data of the first selection */
                TreeItemData treeItemData = (TreeItemData) selectedItems[0]
                        .getData();

                if (treeItemData == null) {

                    /* Exit immediately for items without data */
                    return;
                }

                if (treeItemData.isResult()) {

                    Result treeItemResult = treeItemData.getResult();

                    /* Build the context menu for a result */
                    buildResultMenu(menu, treeItemResult, selectedItems);

                    /* Append seperator */
                    new MenuItem(menu, SWT.SEPARATOR);
                }

                MenuItem propertiesItem = new MenuItem(menu, SWT.NONE);
                propertiesItem.setText("Properties");
                propertiesItem.setImage(PaintUtil.iconProperties);
                propertiesItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        TreeItem selectedItem = selectedItems[0];
                        Object treeData = selectedItem.getData();
                        if (treeData != null) {

                            /* View the properties of a service or result */
                            eventManager
                                    .actionViewProperties((TreeItemData) treeData);
                        }
                    }
                });

                /* Disable the properties item if there is no data available */
                if (((treeItemData.isService()) && (treeItemData
                        .getSearchService() == null))
                        || (treeItemData.isResult() && (treeItemData
                                .getResult() == null))) {
                    propertiesItem.setEnabled(false);
                }
            }
        });

        tree.pack();

    }

    @Override
    public void decorateItem(TreeItem item, ResultSet resultSet) {

        TreeItemData itemData = (TreeItemData) item.getData();

        LinkedHashMap<String, String> metadata = null;

        /* Decorate an ordinary result */
        if (itemData.isResult()) {

            Result itemResult = itemData.getResult();
            Resource itemResultResource = itemResult.getResource();
            DataStore itemResultData = itemResultResource.getData();

            /* Get content type */
            DataStore.ContentType contentType = itemResultData.getContentType();

            /* Set the tree icon for this particular content type */
            item.setImage(PaintUtil.getSmallIcon(contentType));

            /* Set the bigger tooltip image */
            item.setData("TIP_IMAGE", PaintUtil.getBigIcon(contentType));

            /* Insert metadata into tooltip */
            metadata = getResultMetadata(itemResultData);

        } else if (itemData.isService()
                && (itemData.getSearchService() != null)) {

            SearchService itemService = itemData.getSearchService();

            /* Set different type of font */
            item.setFont(FontUtil.treeFont);

            /* Set service icon */
            item.setData("TIP_IMAGE", PaintUtil.iconService);

            metadata = getServiceMetadata(itemService);
        }

        if (metadata != null) {
            setMetadata(item, metadata);
        }
    }

    private LinkedHashMap<String, String> getServiceMetadata(
            SearchService service) {
        LinkedHashMap<String, String> metadata = new LinkedHashMap<String, String>();

        /* Set filename */
        if (service.getTotalResults() != null) {
            metadata.put("Results", String.valueOf(service.getTotalResults()));
        }

        /* Set total time */
        metadata.put("Time", String.valueOf(service.getSearchTime()) + "ms");

        /* Properties specific to web services only */
        if (service instanceof WebSearchService) {

            WebSearchService webService = (WebSearchService) service;

            /* Set query string */
            if (StringUtil.isset(webService.getQueryString())) {
                String queryString = webService.getQueryString();
                metadata.put("Query", StringUtil.ellipsize(queryString, 15));
            }

        }

        return metadata;
    }

    private TreeItem getServiceParent(QueryResult queryResult) {
        ServiceEntry serviceEntry = queryResult.getServiceEntry();

        String description = serviceEntry.getDescription();
        String controller = serviceEntry.getController();

        TreeItem parent;
        int rowIndex = getServiceRowIndex(description);

        /* Check for existing parent */
        if (rowIndex == -1) {

            /* No parent, so create a new one */
            parent = new TreeItem(tree, SWT.NONE);

            /* Set the parents text with number of results */
            parent.setText(description);

            /* Give it the services particular icon */
            parent.setImage(PaintUtil.getServiceIcon(controller));

            /* Associate the service name with the parent */
            parent.setData(TreeItemData.createService(description, queryResult
                    .getSearchService()));

            decorateItem(parent, null);
        } else {

            /* Set parent to be existing treeItem */
            parent = tree.getItem(rowIndex);
        }

        parent.setExpanded(true);
        return parent;
    }

    @Override
    public void populateTree(Dispatcher service) {

        QueryResult queryResult = new QueryResult(service);

        /* Add service to our collection */
        results.add(queryResult);

        /* Populate the tree with the new results */
        addServiceResult(queryResult);
    }

    @Override
    public void populateTree(Object loadedResults) {

        if (loadedResults instanceof SavedQuery) {

            SavedQuery savedQuery = (SavedQuery) loadedResults;

            ArrayList<QueryResult> queryResults = savedQuery.getData();
            results = queryResults;
            setQuery(savedQuery.getQuery());

            for (QueryResult result : queryResults) {
                addServiceResult(result);
            }
        }
    }

}
