package net.brokentrain.ftf.ui.gui.components;

import java.io.File;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.HotkeyUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class MainToolbar {

    private EventManager eventManager;

    private Shell shell;

    private Composite topContainer;

    private MenuItem newArxiv;

    private MenuItem newDOI;

    private MenuItem newLocal;

    private MenuItem newProcessing;

    private MenuItem newTextual;

    private MenuItem toolBarState;

    private Menu newMenu;

    private Menu openMenu;

    private Menu toolbarMenu;

    private ToolBar toolBar;

    private ToolBar processingToolBar;

    private ToolItem newItem;

    private ToolItem add;

    private ToolItem open;

    private ToolItem save;

    private ToolItem load;

    private ToolItem saveModel;

    private ToolItem train;

    private ToolItem next;

    private ToolItem previous;

    private ToolItem predict;

    private ViewForm toolbarViewForm;

    public MainToolbar(GUI fetcherGui, Shell shell, EventManager eventManager) {
        this.shell = shell;
        this.eventManager = eventManager;
        initComponents();
    }

    public void buildOpenMenu(Menu openMenu) {

        for (MenuItem openItem : openMenu.getItems()) {
            openItem.dispose();
        }

        if ((SettingsRegistry.openHistory != null)
                && (!SettingsRegistry.openHistory.isEmpty())) {

            for (final String fullPath : SettingsRegistry.openHistory) {

                /* Create a new menu item for each service */
                final MenuItem recentItem = new MenuItem(openMenu, SWT.NONE);
                recentItem.setText(new File(fullPath).getName());
                recentItem.setImage(PaintUtil.iconImported);
                recentItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        eventManager.actionOpen(fullPath, true);
                    }
                });
            }
        }

    }

    public void createToolBar() {

        if (toolBar != null) {
            toolBar.dispose();
        }

        if (!WidgetUtil.isset(toolbarMenu)) {
            initContextMenu(toolbarViewForm);
        }

        toolBar = new ToolBar(topContainer, SWT.FLAT);
        toolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true));
        toolBar.setMenu(toolbarMenu);

        String toolImgPath = "/img/toolbar/";

        newItem = new ToolItem(toolBar, SWT.DROP_DOWN);
        newItem.setImage(PaintUtil.loadImage(toolImgPath + "new.png"));
        newItem.setText("New");
        newItem.setToolTipText("Create a new search");
        newItem.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                newItem.getImage().dispose();
            }
        });

        newMenu = new Menu(toolBar);
        newItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {

                if (event.detail == SWT.ARROW) {
                    Rectangle rect = newItem.getBounds();
                    Point pt = new Point(rect.x, rect.y + rect.height);
                    pt = newItem.getParent().toDisplay(pt);
                    newMenu.setLocation(pt.x, pt.y);
                    newMenu.setVisible(true);
                } else {
                    eventManager.actionNewTab(EventManager.SEARCH_TYPE_TEXT);
                }
            }
        });

        newTextual = new MenuItem(newMenu, SWT.PUSH);
        newTextual.setImage(PaintUtil.iconNewText);
        newTextual.setText("&Full-text query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_TEXT"));
        newTextual.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_TEXT);
            }
        });

        newLocal = new MenuItem(newMenu, SWT.NONE);
        newLocal.setText("&Local query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_LOCAL"));
        newLocal.setImage(PaintUtil.iconNewLocal);
        newLocal.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_LOCAL);
            }
        });

        newProcessing = new MenuItem(newMenu, SWT.NONE);
        newProcessing.setText("&Processing query" + "\t"
                + HotkeyUtil.getHotkeyName("MENU_NEW_PROCESSING"));
        newProcessing.setImage(PaintUtil.iconNewProcessing);
        newProcessing.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_PROCESSING);
            }
        });

        new MenuItem(newMenu, SWT.SEPARATOR);

        newArxiv = new MenuItem(newMenu, SWT.NONE);
        newArxiv.setText("&Arxiv Identifier");
        newArxiv.setImage(PaintUtil.iconNewArxiv);
        newArxiv.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_ARXIV);
            }
        });

        newDOI = new MenuItem(newMenu, SWT.PUSH);
        newDOI.setText("&Digital Object Identifier");
        newDOI.setImage(PaintUtil.iconNewDOI);
        newDOI.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionNewTab(EventManager.SEARCH_TYPE_DOI);
            }
        });

        open = new ToolItem(toolBar, SWT.DROP_DOWN);
        open.setText("Open");
        open.setToolTipText("Open a previous queries results");
        open.setImage(PaintUtil.loadImage(toolImgPath + "open.png"));
        open.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                open.getImage().dispose();
            }
        });

        openMenu = new Menu(toolBar);
        open.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {

                if (event.detail == SWT.ARROW) {
                    Rectangle rect = open.getBounds();
                    Point pt = new Point(rect.x, rect.y + rect.height);
                    pt = open.getParent().toDisplay(pt);
                    openMenu.setLocation(pt.x, pt.y);
                    buildOpenMenu(openMenu);
                    openMenu.setVisible(true);
                } else {
                    eventManager.actionOpen();
                }
            }
        });

        save = new ToolItem(toolBar, SWT.NONE);
        save.setText("Save");
        save.setToolTipText("Save the current results");
        save.setImage(PaintUtil.loadImage(toolImgPath + "save.png"));
        save.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                save.getImage().dispose();
            }
        });
        save.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionSave();
            }
        });

        MenuManager.registerSaveToolItem(save);

        processingToolBar = new ToolBar(topContainer, SWT.FLAT);
        processingToolBar.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
                true, true));
        processingToolBar.setMenu(toolbarMenu);

        new ToolItem(processingToolBar, SWT.SEPARATOR);

        add = new ToolItem(processingToolBar, SWT.NONE);
        add.setText("Add");
        add.setToolTipText("Add results to this query");
        add.setImage(PaintUtil.loadImage(toolImgPath + "add.png"));
        add.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                add.getImage().dispose();
            }
        });
        add.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionAdd();
            }
        });

        train = new ToolItem(processingToolBar, SWT.NONE);
        train.setText("Train");
        train.setToolTipText("Mark the selected abstracts as positive");
        train.setImage(PaintUtil.loadImage(toolImgPath + "train.png"));
        train.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                train.getImage().dispose();
            }
        });
        train.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionTrain();
            }
        });

        predict = new ToolItem(processingToolBar, SWT.NONE);
        predict.setText("Predict");
        predict
                .setToolTipText("Show predictions for results based on the current model");
        predict.setImage(PaintUtil.loadImage(toolImgPath + "predict.png"));
        predict.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                predict.getImage().dispose();
            }
        });
        predict.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionPredict();
            }
        });

        previous = new ToolItem(processingToolBar, SWT.NONE);
        previous.setText("Previous");
        previous.setToolTipText("Show previous results");
        previous.setImage(PaintUtil.loadImage(toolImgPath + "previous.png"));
        previous.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                previous.getImage().dispose();
            }
        });
        previous.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionPrevious();
            }
        });

        next = new ToolItem(processingToolBar, SWT.NONE);
        next.setText("Next");
        next.setToolTipText("Show next results");
        next.setImage(PaintUtil.loadImage(toolImgPath + "next.png"));
        next.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                next.getImage().dispose();
            }
        });
        next.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionNext();
            }
        });

        new ToolItem(processingToolBar, SWT.SEPARATOR);

        load = new ToolItem(processingToolBar, SWT.NONE);
        load.setText("Load");
        load.setToolTipText("Load a new model");
        load.setImage(PaintUtil.loadImage(toolImgPath + "load.png"));
        load.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                load.getImage().dispose();
            }
        });
        load.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionOpenModel();
            }
        });

        saveModel = new ToolItem(processingToolBar, SWT.NONE);
        saveModel.setText("Save");
        saveModel.setToolTipText("Save the current model");
        saveModel.setImage(PaintUtil.loadImage(toolImgPath + "savemodel.png"));
        saveModel.addDisposeListener(new DisposeListener() {
            public void widgetDisposed(DisposeEvent e) {
                saveModel.getImage().dispose();
            }
        });
        saveModel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionSaveModel();
            }
        });

        toolBar.pack();
        processingToolBar.pack();

        topContainer.setMenu(toolbarMenu);
        topContainer.layout();

        toolbarViewForm.layout();
        toolbarViewForm.redraw();
        toolbarViewForm.update();

        shell.layout();
    }

    private void initComponents() {

        toolbarViewForm = new ViewForm(shell, SWT.BORDER | SWT.FLAT);
        toolbarViewForm.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING,
                true, false));

        initContextMenu(toolbarViewForm);

        topContainer = new Composite(toolbarViewForm, SWT.NONE);
        topContainer.setLayout(LayoutUtil.createGridLayout(2, 0, 0));
        topContainer.setMenu(toolbarMenu);

        toolbarViewForm.setTopLeft(topContainer);
    }

    private void initContextMenu(Control parent) {

        toolbarMenu = new Menu(parent);

        toolBarState = new MenuItem(toolbarMenu, SWT.CHECK);
        toolBarState.setText("Toolbar");
        toolBarState.setSelection(SettingsRegistry.showToolbar);
        toolBarState.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                eventManager.actionShowToolbar(toolBarState.getSelection());
            }
        });
    }

    public void setProcessingToolbarVisible(boolean show) {

        if (WidgetUtil.isset(processingToolBar)) {

            processingToolBar.setVisible(show);
            processingToolBar.layout();
        }
    }

    public void setShowToolBar(boolean show) {

        if (!show && toolbarViewForm.getVisible()) {
            setShowViewForm(false);
        } else if (show && !toolbarViewForm.getVisible()) {
            setShowViewForm(true);
        }

        if (WidgetUtil.isset(shell) && GUI.isAlive()) {
            shell.layout();
        }

        SettingsRegistry.showToolbar = show;
        toolBarState.setSelection(show);
    }

    public void setShowViewForm(boolean show) {

        toolbarViewForm.setVisible(show);

        ((GridData) toolbarViewForm.getLayoutData()).exclude = !show;
    }

}
