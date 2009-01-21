package net.brokentrain.ftf.ui.gui.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.List;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.components.MenuManager;
import net.brokentrain.ftf.ui.gui.settings.SettingsRegistry;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;

public class WidgetUtil {

    public static ArrayList<StyleRange> calculateStyleRanges(
            StyledText textField, List<String> words, Color foreground,
            Color background, int fontstyle, boolean caseSensitive,
            boolean underline) {

        ArrayList<StyleRange> styleRanges = new ArrayList<StyleRange>();

        String text = textField.getText();

        if (!caseSensitive) {
            text = textField.getText().toLowerCase();
        }

        for (int a = 0; a < words.size(); a++) {
            int start = 0;
            String curWord = words.get(a);

            if (!caseSensitive) {
                curWord = curWord.toLowerCase();
            }

            int pos;

            while ((pos = text.indexOf(curWord, start)) > -1) {

                StyleRange styleRange = new StyleRange();
                styleRange.start = pos;
                styleRange.length = (curWord.length());
                styleRange.fontStyle = fontstyle;
                styleRange.foreground = foreground;
                styleRange.background = background;
                styleRange.underline = underline;
                styleRanges.add(styleRange);

                start = styleRange.start + styleRange.length;
            }
        }

        return styleRanges;
    }

    private static boolean collides(TreeSet<StyleRange> styleRanges,
            StyleRange newRange) {
        for (StyleRange range : styleRanges) {

            if ((range.start < newRange.start + newRange.length)
                    && (range.start + range.length > newRange.start)) {
                return true;
            }
        }
        return false;
    }

    public static void createWildCardMenu(final Text text, String[] wildcards) {
        Menu wildCardMenu = new Menu(text);

        for (String element : wildcards) {
            final String wildcard = element;
            MenuItem menuItem = new MenuItem(wildCardMenu, SWT.POP_UP);
            menuItem.setText(element);
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    text.insert(wildcard);
                }
            });
        }
        text.setMenu(wildCardMenu);
    }

    public static String getShellTitle() {

        return "Full-text Fetcher (" + SettingsRegistry._MAJOR_VERSION + ")";
    }

    public static void highlightText(StyledText textField,
            ArrayList<StyleRange> styleRanges) {

        TreeSet<StyleRange> ranges = new TreeSet<StyleRange>(
                new Comparator<Object>() {
                    public int compare(final Object arg0, final Object arg1) {
                        final StyleRange range1 = (StyleRange) arg0;
                        final StyleRange range2 = (StyleRange) arg1;

                        if (range1.start < range2.start) {
                            return -1;
                        } else if (range1.start > range2.start) {
                            return 1;
                        }

                        return 0;
                    }
                });

        for (int a = 0; a < styleRanges.size(); a++) {
            StyleRange range = styleRanges.get(a);
            if (!collides(ranges, range)) {
                ranges.add(range);
            }
        }

        try {
            textField.setStyleRanges(ranges.toArray(new StyleRange[ranges
                    .size()]));
        } catch (IllegalArgumentException e) {
            GUI.log.error("Error setting range!", e);
        }
    }

    public static void highlightText(StyledText textField, List<String> words,
            Color foreground, Color background, int fontstyle,
            boolean caseSensitive, boolean underline) {
        highlightText(textField, calculateStyleRanges(textField, words,
                foreground, background, fontstyle, caseSensitive, underline));
    }

    public static void initMnemonics(Button buttons[]) {

        ArrayList<String> chars = new ArrayList<String>();

        for (Button element : buttons) {
            String name = element.getText();

            name = name.replaceAll("&", "");

            for (int b = 0; b < name.length(); b++) {

                if ((name.substring(b, b + 1) != null)
                        && !name.substring(b, b + 1).equals(" ")) {

                    if (!chars.contains(name.substring(b, b + 1).toLowerCase())) {

                        StringBuffer buttonText = new StringBuffer(name
                                .substring(0, b));
                        buttonText.append("&").append(
                                name.substring(b, name.length()));

                        element.setText(buttonText.toString());

                        chars.add(name.substring(b, b + 1).toLowerCase());
                        break;
                    }
                }
            }
        }
    }

    public static void initMnemonics(ToolItem items[]) {

        ArrayList<String> chars = new ArrayList<String>();

        for (ToolItem element : items) {
            String name = element.getText();

            name = name.replaceAll("&", "");

            for (int b = 0; b < name.length(); b++) {

                if ((name.substring(b, b + 1) != null)
                        && !name.substring(b, b + 1).equals(" ")) {

                    if (!chars.contains(name.substring(b, b + 1).toLowerCase())) {

                        StringBuffer itemText = new StringBuffer(name
                                .substring(0, b));
                        itemText.append("&").append(
                                name.substring(b, name.length()));

                        element.setText(itemText.toString());

                        chars.add(name.substring(b, b + 1).toLowerCase());
                        break;
                    }
                }
            }
        }
    }

    public static boolean isset(Widget widget) {
        return ((widget != null) && !widget.isDisposed());
    }

    public static boolean isWidget(Object obj) {
        return ((obj != null) && (obj instanceof Widget) && isset((Widget) obj));
    }

    public static void tweakComboWidget(final Combo combo) {

        if (!isset(combo)) {
            return;
        }

        combo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                MenuManager.handleEditMenuState();
            }
        });
    }

    public static void tweakTextWidget(final Text text) {

        if (!isset(text)) {
            return;
        }

        text.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                text.selectAll();
            }
        });

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if ((((e.stateMask & SWT.CTRL) != 0) || ((e.stateMask & SWT.COMMAND) != 0))
                        && ((e.keyCode == 'a') || (e.keyCode == 'A'))) {
                    text.selectAll();
                }
            }
        });

        text.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                MenuManager.handleEditMenuState();
            }
        });
    }

    private WidgetUtil() {
    }
}
