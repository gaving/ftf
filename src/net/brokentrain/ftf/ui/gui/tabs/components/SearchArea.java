package net.brokentrain.ftf.ui.gui.tabs.components;

import net.brokentrain.ftf.core.settings.SettingsManager;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.EventManager;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.tabs.SearchTab;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class SearchArea {

    /**
     * Default text for the query combo
     */
    public static final String NO_QUERY_TEXT = "Enter search terms";

    private EventManager eventManager;

    private Composite parent;

    private SearchTab searchTab;

    private CLabel searchLabel;

    private Composite buttonComposite;

    private StackLayout stackLayout;

    private Button fetchButton;

    private Button stopButton;

    private Combo queryInputCombo;

    public SearchArea(Composite parent, GUI fetcherGui, CTabFolder tabFolder,
            EventManager eventManager, SearchTab searchTab) {

        this.parent = parent;
        this.eventManager = eventManager;
        this.searchTab = searchTab;

        SettingsManager.getSettingsManager();

        initComponents();
    }

    public String[] getQueryInputContents() {

        if (WidgetUtil.isset(queryInputCombo)) {
            return queryInputCombo.getItems();
        }

        return null;
    }

    public String getQueryInputText() {
        return queryInputCombo.getText();
    }

    public void initComponents() {

        parent = new Composite(parent, SWT.BORDER);
        parent.setLayout(LayoutUtil.createGridLayout(4, 0, 0, 4));
        parent
                .setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true,
                        false));

        /* Search label */
        searchLabel = new CLabel(parent, SWT.SHADOW_NONE);
        searchLabel.setText("&Search:");
        searchLabel
                .setToolTipText("Enter desired search terms into the query box");

        /* Query input */
        queryInputCombo = new Combo(parent, SWT.BORDER);
        queryInputCombo.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL_HORIZONTAL, 1));
        queryInputCombo.setText(SearchArea.NO_QUERY_TEXT);
        queryInputCombo.setToolTipText("Enter terms to search for");
        queryInputCombo.setVisibleItemCount(5);
        queryInputCombo.setFocus();
        queryInputCombo.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent event) {

                /* If the text hasn't changed then set the box to empty */
                if (queryInputCombo.getText().equals(SearchArea.NO_QUERY_TEXT)) {
                    queryInputCombo.setText("");
                    queryInputCombo.setForeground(ColourUtil.black);
                }
            }

            public void focusLost(FocusEvent event) {

                /* If no text was entered just replace with the dummy text */
                if (queryInputCombo.getText().equals("")) {
                    queryInputCombo.setText(SearchArea.NO_QUERY_TEXT);
                    queryInputCombo.setForeground(ColourUtil.gray);
                }
            }
        });

        /* Handle hitting enter instead of pressing the button */
        queryInputCombo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                switch (event.keyCode) {
                case SWT.CR:

                    String inputText = queryInputCombo.getText();

                    /* Only work if the button has been enabled! */
                    if ((fetchButton.isEnabled())
                            && (!inputText.matches("^\\s*$"))) {

                        /* Fetch if the text isn't blank */
                        eventManager.actionDoFetch(searchTab, inputText);
                    }
                }
            }
        });

        /* Tweak the widget to update the edit menu state */
        WidgetUtil.tweakComboWidget(queryInputCombo);

        buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayoutData(LayoutDataUtil.createGridData(
                GridData.FILL, 1));
        stackLayout = new StackLayout();
        buttonComposite.setLayout(stackLayout);

        /* Fetch button */
        fetchButton = new Button(buttonComposite, SWT.PUSH);
        fetchButton.setLayoutData(LayoutDataUtil.createGridData(GridData.FILL,
                1));
        fetchButton.setText("Search!");
        fetchButton.setImage(PaintUtil.iconFetch);
        fetchButton.setToolTipText("Start searching for the entered terms!");
        fetchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String inputText = queryInputCombo.getText();

                /* No blank terms or if the text is the default template text */
                if ((!inputText.matches("^\\s*$"))
                        && (!inputText.equals(SearchArea.NO_QUERY_TEXT))) {

                    GUI.log.debug("Handling the query");

                    /* Start fetching the terms */
                    eventManager.actionDoFetch(searchTab, inputText);
                }
            }
        });

        /* Fetch button */
        stopButton = new Button(buttonComposite, SWT.PUSH);
        stopButton.setLayoutData(LayoutDataUtil
                .createGridData(GridData.FILL, 1));
        stopButton.setText("Stop!");
        stopButton.setImage(PaintUtil.iconStop);
        stopButton.setToolTipText("Stop the current fetch");
        stopButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                GUI.log.debug("Stopping the current fetch");

                /* Start fetching the terms */
                eventManager.actionStopFetch(searchTab);
            }
        });

        /* Firefox-style quick query focus */
        /* TODO: Add alt+d for IE style? */
        GUI.display.addFilter(SWT.KeyDown, new Listener() {
            public void handleEvent(Event e) {
                if (((e.stateMask & SWT.CTRL) != 0)
                        && (((char) e.keyCode) == 'l')) {

                    if (WidgetUtil.isset(queryInputCombo)) {
                        queryInputCombo.setFocus();
                    }
                }
            }
        });

        stackLayout.topControl = fetchButton;

        if (SettingsRegistry.queryHistory != null) {
            loadQueryInputContents(SettingsRegistry.queryHistory);
        }
    }

    public void loadQueryInputContents(String[] contents) {
        if (WidgetUtil.isset(queryInputCombo)) {
            queryInputCombo.setItems(contents);
        }
    }

    public void setBusy(boolean busy) {

        if (busy) {
            if (queryInputCombo.getItemCount() > 0) {

                /* Ensure that we're not saving the previous item */
                if (!queryInputCombo.getText().equals(
                        queryInputCombo.getItems()[0])) {

                    /* Store the term at the top of the history combo */
                    queryInputCombo.add(queryInputCombo.getText(), 0);
                }
            } else {
                queryInputCombo.add(queryInputCombo.getText(), 0);
            }

            /* Bring the stop button to the front */
            stackLayout.topControl = stopButton;
        } else {

            /* Bring the fetch button to the front */
            stackLayout.topControl = fetchButton;
        }

        /* Force the button to change */
        buttonComposite.layout();
    }

    public void setFetchButtonEnabled(boolean enabled) {
        if (WidgetUtil.isset(fetchButton)) {
            fetchButton.setEnabled(enabled);
        }
    }

    public void setQueryInputFocus() {
        queryInputCombo.setFocus();
    }

    public void setQueryInputText(String text) {
        queryInputCombo.setText(text);
    }
}
