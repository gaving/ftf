package net.brokentrain.ftf.ui.gui.util;

import org.eclipse.swt.program.Program;

public class BrowserUtil {

    public static void openLink(String location) {
        Program.launch(location);
    }

    private BrowserUtil() {
    }

}
