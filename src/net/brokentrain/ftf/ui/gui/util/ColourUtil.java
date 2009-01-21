package net.brokentrain.ftf.ui.gui.util;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColourUtil {

    public static Color black = GUI.display.getSystemColor(SWT.COLOR_BLACK);

    public static Color blue = GUI.display.getSystemColor(SWT.COLOR_BLUE);

    public static Color darkGray = GUI.display
            .getSystemColor(SWT.COLOR_DARK_GRAY);

    public static Color darkGreen = GUI.display
            .getSystemColor(SWT.COLOR_DARK_GREEN);

    public static Color darkRed = GUI.display
            .getSystemColor(SWT.COLOR_DARK_RED);

    public static Color darkYellow = GUI.display
            .getSystemColor(SWT.COLOR_DARK_YELLOW);

    public static Color gray = GUI.display.getSystemColor(SWT.COLOR_GRAY);

    public static Color lightRed = new Color(GUI.display, 240, 128, 128);

    public static Color lightPink = new Color(GUI.display, 255, 228, 225);

    public static Color lightYellow = new Color(GUI.display, 255, 255, 192);

    public static Color red = GUI.display.getSystemColor(SWT.COLOR_RED);

    public static Color white = GUI.display.getSystemColor(SWT.COLOR_WHITE);

    public static Color grayToolBarColor;

    public static Color grayViewFormColor;

    public static Color viewFormBorderInsideColor;

    public static Color viewFormBorderMiddleColor;

    public static Color viewFormBorderOutsideColor;

    public static void initColours() {

        RGB grayViewFormRGB;
        RGB grayToolBarRGB;

        if (SettingsRegistry.isWindows()) {
            RGB widgetBackground = GUI.display.getSystemColor(
                    SWT.COLOR_WIDGET_BACKGROUND).getRGB();
            RGB listSelection = GUI.display.getSystemColor(
                    SWT.COLOR_LIST_SELECTION).getRGB();

            /** Theme: Windows Blue */
            if (widgetBackground.equals(new RGB(236, 233, 216))
                    && listSelection.equals(new RGB(49, 106, 197))) {
                grayViewFormRGB = new RGB(241, 240, 234);
                grayToolBarRGB = new RGB(236, 231, 220);
            }

            /** Theme: Windows Classic */
            else if (widgetBackground.equals(new RGB(212, 208, 200))
                    && listSelection.equals(new RGB(10, 36, 106))) {
                grayViewFormRGB = new RGB(227, 223, 215);
                grayToolBarRGB = new RGB(237, 233, 225);
            }

            /** Theme: Windows Silver */
            else if (widgetBackground.equals(new RGB(224, 223, 227))
                    && listSelection.equals(new RGB(178, 180, 191))) {
                grayViewFormRGB = new RGB(239, 238, 242);
                grayToolBarRGB = new RGB(234, 233, 237);
            }

            /** Theme: Windows Olive */
            else if (widgetBackground.equals(new RGB(236, 233, 216))
                    && listSelection.equals(new RGB(147, 160, 112))) {
                grayViewFormRGB = new RGB(244, 243, 226);
                grayToolBarRGB = new RGB(239, 238, 221);
            }

            /** Theme: Windows Royale */
            else if (widgetBackground.equals(new RGB(235, 233, 237))
                    && listSelection.equals(new RGB(51, 94, 168))) {
                grayViewFormRGB = new RGB(245, 243, 247);
                grayToolBarRGB = new RGB(240, 238, 242);
            }

            /** Any other Theme on Windows */
            else {
                grayViewFormRGB = widgetBackground;
                grayToolBarRGB = widgetBackground;
            }
        }

        /** On any other OS use Widget-Background everywhere */
        else {
            RGB widgetBackground = GUI.display.getSystemColor(
                    SWT.COLOR_WIDGET_BACKGROUND).getRGB();
            grayViewFormRGB = widgetBackground;
            grayToolBarRGB = widgetBackground;
        }

        grayViewFormColor = new Color(GUI.display, grayViewFormRGB);
        grayToolBarColor = new Color(GUI.display, grayToolBarRGB);

        /** ViewForm Border Colors */
        viewFormBorderInsideColor = new Color(GUI.display, 162, 160, 162);
        viewFormBorderMiddleColor = new Color(GUI.display, 173, 171, 168);
        viewFormBorderOutsideColor = new Color(GUI.display, 201, 198, 195);
    }

    private ColourUtil() {
    }

}
