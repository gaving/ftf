package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.core.services.WebSearchService;
import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ViewWebServiceProperties extends ServicePropertyPage {

    public ViewWebServiceProperties(Composite parent, SearchService service) {
        super(parent, service);
    }

    @Override
    protected void initComponents() {

        final String queryString = ((WebSearchService) service)
                .getQueryString();

        Label serviceQueryStringLabel = new Label(composite, SWT.NONE);
        serviceQueryStringLabel.setText("Query string");
        serviceQueryStringLabel.setFont(FontUtil.dialogFont);

        final Text serviceQueryString = new Text(composite, SWT.READ_ONLY
                | SWT.BORDER);
        serviceQueryString.setText(queryString);
        serviceQueryString.setFont(FontUtil.dialogFont);
        serviceQueryString
                .setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        if (StringUtil.isset(queryString)) {
            serviceQueryString.setCursor(GUI.display
                    .getSystemCursor(SWT.CURSOR_HAND));
            serviceQueryString.setToolTipText("Open this URL");
            serviceQueryString.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseUp(MouseEvent e) {

                    if ((e.button == 1)
                            && (serviceQueryString.getSelectionCount() == 0)) {
                        GUI.fetcherGui.getEventManager().actionLaunchURL(
                                queryString);
                    }
                }
            });
        }

        WidgetUtil.tweakTextWidget(serviceQueryString);

    }
}
