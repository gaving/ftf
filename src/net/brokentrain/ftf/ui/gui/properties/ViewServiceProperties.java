package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.services.SearchService;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ViewServiceProperties extends ServicePropertyPage {

    public ViewServiceProperties(Composite parent, SearchService service) {
        super(parent, service);
    }

    @Override
    protected void initComponents() {

        Label serviceTimeLabel = new Label(composite, SWT.NONE);
        serviceTimeLabel.setText("Total Search Time (ms):");
        serviceTimeLabel.setFont(FontUtil.dialogFont);

        final Text serviceTime = new Text(composite, SWT.READ_ONLY | SWT.BORDER);
        serviceTime.setText(String.valueOf(service.getSearchTime()) + "ms");
        serviceTime.setFont(FontUtil.dialogFont);
        serviceTime.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        WidgetUtil.tweakTextWidget(serviceTime);

    }
}
