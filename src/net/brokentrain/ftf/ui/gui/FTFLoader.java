package net.brokentrain.ftf.ui.gui;

import org.eclipse.swt.widgets.Display;

public class FTFLoader {

    public static void main(String[] argv) {

        /* Start the loader */
        new FTFLoader();
    }

    private Display display;

    private FTFLoader() {

        /* Set the application name */
        Display.setAppName("FTF");

        /* Create a new display */
        display = new Display();

        /* Start the GUI */
        new GUI(display).showGui();
    }
}
