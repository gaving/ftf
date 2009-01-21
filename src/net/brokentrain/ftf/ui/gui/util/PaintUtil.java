package net.brokentrain.ftf.ui.gui.util;

import java.io.IOException;
import java.io.InputStream;

import net.brokentrain.ftf.core.data.DataStore;
import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class PaintUtil {

    private static final Class<PaintUtil> clazz = PaintUtil.class;

    /* Application */
    public static Image iconFetcher;

    public static Image iconFetch;

    public static Image iconStop;

    /* Menu */
    public static Image iconNew;

    public static Image iconNewTab;

    public static Image iconNewDOI;

    public static Image iconNewLocal;

    public static Image iconNewProcessing;

    public static Image iconNewText;

    public static Image iconNewArxiv;

    public static Image iconImported;

    public static Image iconClose;

    public static Image iconSave;

    public static Image iconSaveAs;

    public static Image iconExport;

    public static Image iconQuit;

    public static Image iconCut;

    public static Image iconCopy;

    public static Image iconDelete;

    public static Image iconPaste;

    public static Image iconSelectAll;

    public static Image iconBackward;

    public static Image iconForward;

    public static Image iconProperties;

    public static Image iconPrefs;

    public static Image iconTutorial;

    public static Image iconFAQ;

    public static Image iconAbout;

    public static Image iconBlueStripes;

    /* Main interface */
    public static Image iconBrowser;

    public static Image iconStatus;

    public static Image iconLog;

    public static Image iconTransfer;

    public static Image iconQuery;

    public static Image iconBrowserBackward;

    public static Image iconBrowserForward;

    public static Image iconBrowserRefresh;

    public static Image iconBrowserStop;

    public static Image iconBrowserHome;

    public static Image iconBrowserHighlight;

    public static Image iconBrowserGo;

    public static Image iconPDF;

    public static Image iconHTML;

    public static Image iconText;

    public static Image iconUnknown;

    public static Image iconPDFBig;

    public static Image iconHTMLBig;

    public static Image iconTextBig;

    public static Image iconUnknownBig;

    public static Image iconDebug;

    public static Image iconError;

    public static Image iconInformation;

    public static Image iconWarning;

    public static Image iconException;

    public static Image iconService;

    public static Image iconMetadataClose;

    /* Context menu */
    public static Image iconOpen;

    public static Image iconLaunch;

    public static Image iconDownload;

    public static Image iconCopyBibtex;

    public static Image iconCheckAll;

    public static Image iconRelated;

    public static Image iconHasFullText;

    public static Image iconHasNoFullText;

    /* Message Area */
    public static Image iconPlain;

    public static Image iconSearching;

    public static Image iconNoResults;

    public static Image iconSmile;

    public static void disposeIcons() {
        iconFetcher.dispose();
        iconFetch.dispose();
        iconStop.dispose();

        iconNewTab.dispose();
        iconNew.dispose();
        iconNewDOI.dispose();
        iconNewLocal.dispose();
        iconNewProcessing.dispose();
        iconNewText.dispose();
        iconNewArxiv.dispose();
        iconImported.dispose();
        iconClose.dispose();
        iconSave.dispose();
        iconSaveAs.dispose();

        iconCut.dispose();
        iconCopy.dispose();
        iconDelete.dispose();
        iconPaste.dispose();
        iconSelectAll.dispose();

        iconBackward.dispose();
        iconForward.dispose();

        iconProperties.dispose();

        iconExport.dispose();
        iconQuit.dispose();
        iconPrefs.dispose();
        // iconTutorial.dispose();
        // iconFAQ.dispose();
        iconAbout.dispose();

        iconBlueStripes.dispose();

        iconBrowser.dispose();
        iconStatus.dispose();
        iconLog.dispose();
        iconTransfer.dispose();
        iconQuery.dispose();

        iconBrowserBackward.dispose();
        iconBrowserForward.dispose();
        iconBrowserRefresh.dispose();
        iconBrowserStop.dispose();
        iconBrowserHome.dispose();
        iconBrowserHighlight.dispose();
        iconBrowserGo.dispose();

        iconPDF.dispose();
        iconHTML.dispose();
        iconText.dispose();
        iconUnknown.dispose();

        iconPDFBig.dispose();
        iconHTMLBig.dispose();
        iconTextBig.dispose();
        iconUnknownBig.dispose();

        iconDebug.dispose();
        iconError.dispose();
        iconInformation.dispose();
        iconError.dispose();
        iconWarning.dispose();
        iconException.dispose();

        iconService.dispose();
        iconMetadataClose.dispose();

        iconOpen.dispose();
        iconLaunch.dispose();
        iconDownload.dispose();
        iconCopyBibtex.dispose();
        iconCheckAll.dispose();
        iconRelated.dispose();

        iconHasFullText.dispose();
        iconHasNoFullText.dispose();

        iconPlain.dispose();
        iconSearching.dispose();
        iconNoResults.dispose();

        iconSmile.dispose();
    }

    public static Image getBigIcon(DataStore.ContentType contentType) {
        if (contentType != null) {
            switch (contentType) {

            case PDF:
                return PaintUtil.iconPDFBig;

            case HTML:
                return PaintUtil.iconHTMLBig;

            case TEXT:
                return PaintUtil.iconTextBig;

            default:
                return PaintUtil.iconUnknownBig;
            }
        } else {
            return PaintUtil.iconUnknownBig;
        }
    }

    public static Image getFilledImage(Display display, RGB color, int width,
            int height) {
        Image filledImage = new Image(display, width, height);
        Color selectedColor = new Color(display, color);

        GC gc = new GC(filledImage);
        gc.setBackground(selectedColor);
        gc.fillRectangle(0, 0, width, height);
        gc.setBackground(display.getSystemColor(SWT.COLOR_BLACK));
        gc.drawRectangle(0, 0, width - 1, height - 1);

        gc.dispose();
        selectedColor.dispose();

        return filledImage;
    }

    public static Image getServiceIcon(String serviceName) {
        return loadImage("/img/services/" + serviceName.concat(".png"));
    }

    public static Image getSmallIcon(DataStore.ContentType contentType) {
        if (contentType != null) {
            switch (contentType) {

            case PDF:
                return PaintUtil.iconPDF;

            case HTML:
                return PaintUtil.iconHTML;

            case TEXT:
                return PaintUtil.iconText;

            default:
                return PaintUtil.iconUnknown;
            }
        } else {
            return PaintUtil.iconUnknown;
        }
    }

    public static void initIcons() {

        iconFetcher = loadImage("/img/core/fetcher.png");
        iconFetch = loadImage("/img/core/fetch.png");
        iconStop = loadImage("/img/core/stop.png");

        iconNewTab = loadImage("/img/core/menu/new_tab.png");
        iconNew = loadImage("/img/core/menu/new.png");
        iconNewDOI = loadImage("/img/services/DOILookup.png");
        iconNewLocal = loadImage("/img/core/menu/local.png");
        iconNewProcessing = loadImage("/img/core/menu/processing.png");
        iconNewText = loadImage("/img/core/menu/textual.png");
        iconNewArxiv = loadImage("/img/services/ArXivLookup.png");
        iconImported = loadImage("/img/core/menu/imported.png");

        iconClose = loadImage("/img/core/menu/close.png");
        iconSave = loadImage("/img/core/menu/save.png");
        iconSaveAs = loadImage("/img/core/menu/saveas.png");

        iconCopy = loadImage("/img/core/menu/copy.png");
        iconCut = loadImage("/img/core/menu/cut.png");
        iconDelete = loadImage("/img/core/menu/delete.png");
        iconPaste = loadImage("/img/core/menu/paste.png");
        iconSelectAll = loadImage("/img/core/menu/select-all.png");

        iconBackward = loadImage("/img/core/menu/backward.png");
        iconForward = loadImage("/img/core/menu/forward.png");

        iconProperties = loadImage("/img/core/menu/properties.png");

        iconExport = loadImage("/img/core/menu/export.png");
        iconQuit = loadImage("/img/core/menu/quit.png");
        iconPrefs = loadImage("/img/core/menu/prefs.png");
        // iconTutorial = loadImage("/img/core/menu/tutorial.png");
        // iconFAQ = loadImage("/img/core/menu/faq.png");
        iconAbout = loadImage("/img/core/menu/help.png");

        iconBlueStripes = loadImage("/img/core/stripes.png");

        iconBrowser = loadImage("/img/core/browser.png");
        iconStatus = loadImage("/img/core/status.png");
        iconLog = loadImage("/img/core/log.png");
        iconTransfer = loadImage("/img/core/transfer.png");
        iconQuery = loadImage("/img/core/query.png");

        iconBrowserBackward = loadImage("/img/browser/back.png");
        iconBrowserForward = loadImage("/img/browser/forward.png");
        iconBrowserRefresh = loadImage("/img/browser/refresh.png");
        iconBrowserStop = loadImage("/img/browser/stop.png");
        iconBrowserHome = loadImage("/img/browser/home.png");
        iconBrowserHighlight = loadImage("/img/browser/highlight.png");
        iconBrowserGo = loadImage("/img/browser/go.png");

        iconPDF = loadImage("/img/mimetypes/pdf_small.png");
        iconHTML = loadImage("/img/mimetypes/html_small.png");
        iconText = loadImage("/img/mimetypes/text_small.png");

        /* Default to this since it looks nicer with no glimpsing */
        iconUnknown = loadImage("/img/mimetypes/html_small.png");

        iconPDFBig = loadImage("/img/mimetypes/pdf.png");
        iconHTMLBig = loadImage("/img/mimetypes/html.png");
        iconTextBig = loadImage("/img/mimetypes/text.png");
        iconUnknownBig = loadImage("/img/mimetypes/unknown.png");

        iconDebug = loadImage("/img/core/log/debug.png");
        iconError = loadImage("/img/core/log/error.png");
        iconInformation = loadImage("/img/core/log/information.png");
        iconWarning = loadImage("/img/core/log/warning.png");
        iconException = loadImage("/img/core/log/exception.png");

        iconService = loadImage("/img/core/service.png");
        iconMetadataClose = loadImage("/img/core/metadatapane/close.png");

        iconOpen = loadImage("/img/core/menu/open.png");
        iconLaunch = loadImage("/img/core/menu/launch.png");
        iconDownload = loadImage("/img/core/menu/download.png");
        iconCopyBibtex = loadImage("/img/core/menu/bibtex.png");
        iconCheckAll = loadImage("/img/core/menu/checkall.png");
        iconRelated = loadImage("/img/core/menu/related.png");

        iconHasFullText = loadImage("/img/core/search/hasfulltext.png");
        iconHasNoFullText = loadImage("/img/core/search/hasnofulltext.png");

        iconPlain = loadImage("/img/core/messagearea/plain.png");
        iconSearching = loadImage("/img/core/messagearea/searching.png");
        iconNoResults = loadImage("/img/core/messagearea/noresults.png");

        iconSmile = loadImage("/img/core/smile.png");
    }

    public static boolean isset(Image image) {
        return ((image != null) && !image.isDisposed());
    }

    public static Image loadImage(String path) {

        Image image;
        InputStream inS = null;

        try {
            inS = clazz.getResourceAsStream(path);
            image = new Image(GUI.display, inS);
            inS.close();
        } catch (IOException ioe) {
            image = null;
            GUI.log.error("Problems loading icon!", ioe);
        } catch (Exception e) {
            image = null;
            GUI.log.error("Problems loading icon!", e);
        } finally {
            if (inS != null) {
                try {
                    inS.close();
                } catch (IOException ioe) {
                    GUI.log.error("Problems loading icon!", ioe);
                }
            }
        }

        return image;
    }

    private PaintUtil() {
    }

}
