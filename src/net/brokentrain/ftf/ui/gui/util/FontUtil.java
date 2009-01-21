package net.brokentrain.ftf.ui.gui.util;

import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontUtil {

    public static Font dialogBoldFont = null;

    public static Font dialogFont = createFont();

    public static Font headerBoldFont = null;

    public static Font headerFont = createFont(getFontData()[0].getHeight() + 1);

    public static Font tableBoldFont = null;

    public static Font tableFont = createFont();

    public static Font textBoldFont = null;

    public static Font textFont = createFont(getFontData()[0].getHeight() + 1);

    public static Font treeBoldFont = null;

    public static Font treeFont = createFont();

    private static Font createBoldFont(Font sourceFont) {
        FontData[] fontData = sourceFont.getFontData();

        for (FontData element : fontData) {
            element.setStyle(SWT.BOLD);
        }
        return new Font(GUI.display, fontData);
    }

    public static Font createFont() {
        FontData[] fontData = getFontData();
        return createFont(fontData[0].getName(), fontData[0].getHeight(), 0);
    }

    public static Font createFont(int height) {
        FontData[] fontData = getFontData();
        return createFont(fontData[0].getName(), height, 0);
    }

    public static Font createFont(int height, int style) {
        FontData[] fontData = getFontData();
        return createFont(fontData[0].getName(), height, style);
    }

    public static Font createFont(String name, int height, int style) {
        FontData[] fontData = getFontData();
        for (FontData element : fontData) {
            element.setName(name);
            element.setHeight(height);

            if (style != SWT.NORMAL) {
                element.setStyle(style);
            }
        }
        return new Font(GUI.display, fontData);
    }

    public static void disposeFonts() {
        if (isset(dialogBoldFont)) {
            dialogBoldFont.dispose();
        }

        if (isset(dialogFont)) {
            dialogFont.dispose();
        }

        if (isset(headerFont)) {
            headerFont.dispose();
        }

        if (isset(tableFont)) {
            tableFont.dispose();
        }

        if (isset(tableBoldFont)) {
            tableBoldFont.dispose();
        }

        if (isset(textBoldFont)) {
            textBoldFont.dispose();
        }

        if (isset(textFont)) {
            textFont.dispose();
        }

        if (isset(treeFont)) {
            treeFont.dispose();
        }

        if (isset(treeBoldFont)) {
            treeBoldFont.dispose();
        }

        if (isset(headerBoldFont)) {
            headerBoldFont.dispose();
        }
    }

    public static FontData[] getFontData() {
        return GUI.display.getSystemFont().getFontData();
    }

    public static FontData[] getFontData(int height) {
        FontData[] fontData = getFontData();
        return getFontData(fontData[0].getName(), height, 0);
    }

    public static FontData[] getFontData(String name, int height, int style) {
        FontData[] fontData = getFontData();
        for (FontData element : fontData) {
            element.setName(name);
            element.setHeight(height);

            if (style != SWT.NORMAL) {
                element.setStyle(style);
            }
        }
        return fontData;
    }

    private static void initStyledFonts() {
        dialogBoldFont = createBoldFont(dialogFont);
        tableBoldFont = createBoldFont(tableFont);
        textBoldFont = createBoldFont(textFont);
        treeBoldFont = createBoldFont(treeFont);
        headerBoldFont = createBoldFont(headerFont);
    }

    public static boolean isset(Font font) {
        return ((font != null) && !font.isDisposed());
    }

    public static void setFontForAll(GUI fetcherGui) {

        LayoutUtil.setLayoutForAll(GUI.shell);
    }

    public static void updateFonts() {
        initStyledFonts();
        JFaceResources.getFontRegistry().put(JFaceResources.DIALOG_FONT,
                FontUtil.dialogFont.getFontData());
    }

    private FontUtil() {
    }
}
