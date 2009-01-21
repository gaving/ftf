package net.brokentrain.ftf.ui.gui.properties;

import net.brokentrain.ftf.core.Result;
import net.brokentrain.ftf.core.data.URLStore;
import net.brokentrain.ftf.core.services.lookup.Article;
import net.brokentrain.ftf.core.services.lookup.PMIDArticle;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.LayoutDataUtil;
import net.brokentrain.ftf.ui.gui.util.StringUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ResultURLProperties extends ResultPropertyPage {

    public ResultURLProperties(Composite parent, Result result) {
        super(parent, result);
    }

    @Override
    protected void initComponents() {

        URLStore URLresult = (URLStore) resourceData;

        if ((URLresult.getPrices() != null)
                || (URLresult.getResponseCode() != null)) {

            Group urlGroup = new Group(composite, SWT.NONE);
            urlGroup.setText("URL Metadata");
            urlGroup.setLayout(new GridLayout(2, false));
            urlGroup.setLayoutData(LayoutDataUtil.createGridData(
                    GridData.FILL_HORIZONTAL, 2));
            urlGroup.setFont(FontUtil.dialogFont);

            if (URLresult.getResponseCode() != null) {

                Label resultResponseCodeLabel = new Label(urlGroup, SWT.NONE);
                resultResponseCodeLabel.setText("Response Code:");
                resultResponseCodeLabel.setFont(FontUtil.dialogFont);

                final Text resultResponseCode = new Text(urlGroup,
                        SWT.READ_ONLY | SWT.BORDER);
                resultResponseCode.setText(URLresult.getResponseCode());
                resultResponseCode.setFont(FontUtil.dialogFont);
                resultResponseCode.setLayoutData(new GridData(
                        GridData.FILL_HORIZONTAL));

                WidgetUtil.tweakTextWidget(resultResponseCode);
            }

            if (URLresult.getPrices() != null) {

                Label resultPriceLabel = new Label(urlGroup, SWT.NONE);
                resultPriceLabel.setText("Price Snippets:");
                resultPriceLabel.setFont(FontUtil.dialogFont);
                resultPriceLabel.setLayoutData(new GridData(
                        GridData.HORIZONTAL_ALIGN_CENTER
                                | GridData.VERTICAL_ALIGN_BEGINNING));

                final Text resultPrice = new Text(urlGroup, SWT.READ_ONLY
                        | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL
                        | SWT.BORDER);
                resultPrice.setText(URLresult.getPrices().toString());
                resultPrice.setFont(FontUtil.dialogFont);
                resultPrice.setLayoutData(LayoutDataUtil.createGridData(
                        GridData.FILL_BOTH, 1, 100, 100));

                WidgetUtil.tweakTextWidget(resultPrice);
            }

        }

        Article URLarticle = resource.getArticle();

        if (URLarticle != null) {

            Group articleGroup = new Group(composite, SWT.NONE);
            articleGroup.setText("Article Metadata");
            articleGroup.setLayout(new GridLayout(2, false));
            articleGroup.setLayoutData(LayoutDataUtil.createGridData(
                    GridData.FILL_BOTH, 2));
            articleGroup.setFont(FontUtil.dialogFont);

            /* Loop through all fields in an article */
            for (String key : URLarticle.getValues().keySet()) {

                /* Get the value */
                String value = URLarticle.getValues().get(key);

                /* Check it is a suitably entered field and has a value */
                if (StringUtil.isset(key) && (StringUtil.isset(value))) {

                    Label fieldLabel = new Label(articleGroup, SWT.NONE);
                    fieldLabel.setText(key + ":");
                    fieldLabel.setFont(FontUtil.dialogFont);

                    final Text field = new Text(articleGroup, SWT.READ_ONLY
                            | SWT.BORDER);
                    field.setFont(FontUtil.dialogFont);
                    field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                    field.setText((StringUtil.isset(value) ? value : "None"));

                    WidgetUtil.tweakTextWidget(field);
                }
            }

            /* Do the different types of articles, pmids, dois, etc. */
            if (URLarticle instanceof PMIDArticle) {

                Group pmidGroup = new Group(articleGroup, SWT.NONE);
                pmidGroup.setText("PMID Data");
                pmidGroup.setLayout(new GridLayout(2, false));
                pmidGroup.setLayoutData(LayoutDataUtil.createGridData(
                        GridData.FILL_BOTH, 2));
                pmidGroup.setFont(FontUtil.dialogFont);

                PMIDArticle pmidArticle = (PMIDArticle) URLarticle;

                if (pmidArticle.getPMID() != null) {

                    Label resultArticlePMIDLabel = new Label(pmidGroup,
                            SWT.NONE);
                    resultArticlePMIDLabel.setText("PMID:");
                    resultArticlePMIDLabel.setFont(FontUtil.dialogFont);

                    final Text resultArticlePMID = new Text(pmidGroup,
                            SWT.READ_ONLY | SWT.BORDER);
                    resultArticlePMID.setText(pmidArticle.getPMID());
                    resultArticlePMID.setFont(FontUtil.dialogFont);
                    resultArticlePMID.setLayoutData(new GridData(
                            GridData.FILL_HORIZONTAL));

                    WidgetUtil.tweakTextWidget(resultArticlePMID);
                }

                if (pmidArticle.getAbstractText() != null) {

                    Label resultArticleAbstractLabel = new Label(pmidGroup,
                            SWT.NONE);
                    resultArticleAbstractLabel.setText("Abstract:");
                    resultArticleAbstractLabel.setFont(FontUtil.dialogFont);
                    resultArticleAbstractLabel.setLayoutData(new GridData(
                            GridData.HORIZONTAL_ALIGN_CENTER
                                    | GridData.VERTICAL_ALIGN_BEGINNING));

                    final Text resultArticleAbstract = new Text(pmidGroup,
                            SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL
                                    | SWT.H_SCROLL | SWT.BORDER);
                    resultArticleAbstract
                            .setText(pmidArticle.getAbstractText());
                    resultArticleAbstract.setFont(FontUtil.dialogFont);
                    resultArticleAbstract.setLayoutData(LayoutDataUtil
                            .createGridData(GridData.FILL_BOTH, 1, 100, 100));

                    WidgetUtil.tweakTextWidget(resultArticleAbstract);
                }
            }
        }

    }

}
