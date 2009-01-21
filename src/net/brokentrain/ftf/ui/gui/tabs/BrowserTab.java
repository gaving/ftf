package net.brokentrain.ftf.ui.gui.tabs;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.MenuManager;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;
import net.brokentrain.ftf.ui.gui.util.FileUtil;
import net.brokentrain.ftf.ui.gui.util.MessageBoxUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.StatusTextEvent;
import org.eclipse.swt.browser.StatusTextListener;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class BrowserTab extends Tab {

    private Browser browser;

    private Composite browserArea;

    private Composite controls;

    private CTabFolder tabFolder;

    private Label status;

    private String location;

    private String query;

    private boolean pendingHighlight;

    private Button buttonBackward;

    private Button buttonForward;

    private Button buttonRefresh;

    private Button buttonHome;

    private Button buttonStop;

    private Button buttonHighlight;

    private Button buttonGo;

    public BrowserTab(GUI fetcherGui, CTabItem tabItem, TabManager tabManager,
            String location, boolean firstLoad) {
        super(tabItem, tabManager);

        this.location = location;
        tabFolder = tabItem.getParent();

        initComponents();
    }

    public boolean highlightTerms(boolean highlight) {

        if ((StringUtil.isset(query)) && (!query.matches("^\\s*$"))) {

            /* Get the highlight script */
            StringBuffer script = new StringBuffer(FileUtil.getContent(FileUtil
                    .getResourceAsStream("/misc/highlight.js")));

            /* This is pretty horrible; append the function calls to the script */
            if (highlight) {
                String highlightCall = "highlight('" + query + "', false);";
                script.append(highlightCall);
                GUI.log.debug("Calling: " + highlightCall);
            } else {
                script.append("reset()");
            }

            /* Execute the script */
            if ((browser != null) && (!browser.execute(script.toString()))) {
                MessageBoxUtil.showError(GUI.shell,
                        "Could not execute highlight script!");
                GUI.log.error("Could not execute highlight script!");
                return false;
            }

            return true;
        }
        return false;
    }

    public void initComponents() {

        tabItem.setText("Browser");
        tabItem.setImage(PaintUtil.iconBrowser);
        tabItem.setToolTipText("Web browser");

        /* Create a new browser area with toolbar, pane, etc. */
        browserArea = new Composite(tabFolder, SWT.BORDER);
        browserArea.setLayout(new FormLayout());

        /* Create the Browser widget */
        try {
            browser = new Browser(browserArea, SWT.NONE);
        } catch (SWTError e) {
            MenuManager.notifyState(MenuManager.NO_INTERNAL_BROWSER);
            GUI.log.error("Could not initialise internal browser!", e);
            return;
        }

        /* Create the controls */
        controls = new Composite(browserArea, SWT.NONE);
        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        controls.setLayoutData(data);

        /* Create the status bar */
        status = new Label(browserArea, SWT.NONE);
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        data.bottom = new FormAttachment(100, 0);
        status.setLayoutData(data);

        /* Layout for the controls */
        data = new FormData();
        data.top = new FormAttachment(controls);
        data.bottom = new FormAttachment(status);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);

        browser.setLayoutData(data);

        /* Create the controls and wire them to the browser */
        controls.setLayout(new GridLayout(9, false));

        /* Create the back button */
        buttonBackward = new Button(controls, SWT.FLAT);
        buttonBackward.setImage(PaintUtil.iconBrowserBackward);
        buttonBackward.setToolTipText("Back");
        buttonBackward.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                browser.back();
            }
        });

        /* Create the forward button */
        buttonForward = new Button(controls, SWT.FLAT);
        buttonForward.setImage(PaintUtil.iconBrowserForward);
        buttonForward.setToolTipText("Forward");
        buttonForward.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                browser.forward();
            }
        });

        /* Create the refresh button */
        buttonRefresh = new Button(controls, SWT.FLAT);
        buttonRefresh.setImage(PaintUtil.iconBrowserRefresh);
        buttonRefresh.setToolTipText("Refresh");
        buttonRefresh.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                browser.refresh();
            }
        });

        /* Create the stop button */
        buttonStop = new Button(controls, SWT.FLAT);
        buttonStop.setImage(PaintUtil.iconBrowserStop);
        buttonStop.setToolTipText("Stop");
        buttonStop.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                browser.stop();
            }
        });

        /* Create the home button */
        buttonHome = new Button(controls, SWT.FLAT);
        buttonHome.setImage(PaintUtil.iconBrowserHome);
        buttonHome.setToolTipText("Home");
        buttonHome.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                String homepage = SettingsRegistry.defaultHomepage;
                browser.setUrl(homepage);
            }
        });

        /* Create the highlight button */
        buttonHighlight = new Button(controls, SWT.TOGGLE);
        buttonHighlight.setImage(PaintUtil.iconBrowserHighlight);
        buttonHighlight.setToolTipText("Highlight query terms");
        buttonHighlight.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (highlightTerms(buttonHighlight.getSelection())) {
                    buttonHighlight
                            .setSelection(buttonHighlight.getSelection());
                }
            }
        });

        /* Create the address entry field and set focus to it */
        final Text url = new Text(controls, SWT.BORDER);
        url.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        url.setFocus();

        /* Allow users to hit enter to go to the typed URL */
        url.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                switch (event.keyCode) {
                case SWT.CR:
                    String inputText = url.getText();
                    if ((inputText != null) && (!inputText.matches("^\\s*$"))) {
                        browser.setUrl(inputText);
                    }
                }
            }
        });

        /* Create the go button */
        buttonGo = new Button(controls, SWT.FLAT);
        buttonGo.setImage(PaintUtil.iconBrowserGo);
        buttonGo.setToolTipText("Go to page");
        buttonGo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                browser.setUrl(url.getText());
            }
        });

        /* Update the status text */
        browser.addStatusTextListener(new StatusTextListener() {
            public void changed(StatusTextEvent event) {
                status.setText(event.text);
            }
        });

        /* Handle a location change */
        browser.addLocationListener(new LocationListener() {
            public void changed(LocationEvent event) {
                if (event.top) {
                    url.setText(event.location);
                }
            }

            public void changing(LocationEvent event) {
            }
        });

        /* Handle a location change */
        browser.addProgressListener(new ProgressListener() {
            public void changed(ProgressEvent event) {
            }

            public void completed(ProgressEvent event) {
                /* https://bugs.eclipse.org/bugs/show_bug.cgi?id=107142 :( */
                /* TODO: Implement this when target milestone M4 released ! */
                // GUI.log.debug(browser.getText());
                if (pendingHighlight) {
                    GUI.log.debug("Executing pending highlight request");
                    if (highlightTerms(true)) {
                        buttonHighlight.setSelection(true);
                    }
                    pendingHighlight = false;
                }
            }
        });

        /* Load the default URL if one given */
        setURL(location);

        tabItem.setControl(browserArea);
    }

    public void reset() {

        if ((browser != null) && (!browser.isDisposed())) {

            if (WidgetUtil.isset(buttonHighlight)) {
                buttonHighlight.setSelection(false);
            }
            pendingHighlight = false;
        }
    }

    public void setQuery(String query, boolean highlight) {
        this.query = query;
        pendingHighlight = highlight;
    }

    public void setTabItem(CTabItem tabItem) {
        this.tabItem = tabItem;

        tabItem.setText("Browser");
        tabItem.setImage(PaintUtil.iconBrowser);
        tabItem.setToolTipText("Web browser");

        if (WidgetUtil.isset(browserArea)) {
            this.tabItem.setControl(browserArea);
        }
    }

    public boolean setURL(String location) {

        if (browser != null) {
            browser.setUrl((location != null) ? location
                    : SettingsRegistry.defaultHomepage);
            return true;
        } else {
            MessageBoxUtil.showError(GUI.shell,
                    "Could not initialise internal browser!");
            return false;
        }
    }

}
