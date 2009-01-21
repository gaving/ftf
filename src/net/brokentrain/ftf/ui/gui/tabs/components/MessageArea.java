package net.brokentrain.ftf.ui.gui.tabs.components;

import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class MessageArea {

    /**
     * Represents the different messages we can get back from a query.
     */
    public enum MessageType {
        SEARCHING, NO_RESULTS
    };

    private Color colour;

    private Composite messageArea;

    private Composite parent;

    private Image image;

    private Label imageLabel;

    private Label messageLabel;

    private MessageType messageType;

    private String messageText;

    public MessageArea(Composite parent, CTabItem tabItem,
            MessageType messageType) {
        this.parent = parent;
        this.messageType = messageType;

        initComponents();
    }

    public MessageArea(Composite parent, CTabItem tabItem,
            MessageType messageType, String messageText) {
        this.parent = parent;
        this.messageType = messageType;
        this.messageText = messageText;

        initComponents();
    }

    public void hide() {
        GridData messageData = (GridData) messageArea.getLayoutData();
        messageArea.setVisible(false);
        messageData.exclude = true;
        messageArea.getParent().layout();
    }

    public void initComponents() {

        messageArea = new Composite(parent, SWT.BORDER);
        messageArea.setLayout(LayoutUtil
                .createGridLayout(2, 7, 11, 4, 8, false));

        GridData messageAreaData = new GridData(GridData.FILL_HORIZONTAL);
        messageAreaData.exclude = true;
        messageArea.setLayoutData(messageAreaData);

        imageLabel = new Label(messageArea, SWT.NULL);
        imageLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER
                | GridData.VERTICAL_ALIGN_BEGINNING));

        messageLabel = new Label(messageArea, SWT.WRAP);

        setupMessageState();

        GridData data = new GridData(GridData.GRAB_HORIZONTAL
                | GridData.HORIZONTAL_ALIGN_FILL
                | GridData.VERTICAL_ALIGN_BEGINNING);
        data.widthHint = 0;
        messageLabel.setLayoutData(data);

        /* Hide by default */
        messageArea.setVisible(false);
    }

    public boolean isVisible() {
        return messageArea.isVisible();
    }

    public void setMessageState(MessageType messageType) {
        this.messageType = messageType;
        setupMessageState();
    }

    public void setupMessageState() {

        switch (messageType) {

        /* Searching */
        case SEARCHING:
            colour = ColourUtil.lightYellow;
            image = PaintUtil.iconSearching;
            messageText = "Please wait!\nFetcher is currently in the "
                    + "process of crawling your selected "
                    + "sources, this could " + "take a few minutes!";
            break;

        /* No results */
        case NO_RESULTS:
            colour = ColourUtil.lightPink;
            image = PaintUtil.iconNoResults;
            messageText = "Nothing found!\nNo results found for the terms "
                    + "that you entered! Try using different services or using different "
                    + "keywords.";
            break;

        default:
            colour = ColourUtil.gray;
            image = PaintUtil.iconPlain;
        }

        /* Set messagearea background */
        messageArea.setBackground(colour);

        /* Make image look transparent */
        image.setBackground(imageLabel.getBackground());
        imageLabel.setImage(image);
        imageLabel.setBackground(colour);

        /* Set message text */
        messageLabel.setText(messageText);
        messageLabel.setBackground(colour);
    }

    public void show() {
        GridData messageData = (GridData) messageArea.getLayoutData();
        messageArea.setVisible(true);
        messageData.exclude = false;
        messageArea.getParent().layout();
    }

    public void toggle() {
        GridData messageData = (GridData) messageArea.getLayoutData();
        messageArea.setVisible((messageData.exclude));
        messageData.exclude = !(messageData.exclude);
        messageArea.getParent().layout();
    }

}
