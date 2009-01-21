package net.brokentrain.ftf.ui.gui.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.brokentrain.ftf.ui.gui.GUI;

public class SimpleFileUtil {

    public static final int OP_ABORTED = -1;

    public static final int OP_FAILED = 1;

    public static final int OP_SUCCESSFULL = 0;

    public static void copy(File src, File dest) throws IOException {
        copy(new BufferedInputStream(new FileInputStream(src)),
                new BufferedOutputStream(new FileOutputStream(dest)));
    }

    public static void copy(InputStream fis, OutputStream fos) {
        try {
            byte buffer[] = new byte[0xffff];
            int nbytes;

            while ((nbytes = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, nbytes);
            }
        } catch (IOException e) {
            GUI.log.debug("copy");
        } finally {

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    GUI.log.debug("copy");
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    GUI.log.debug("copy");
                }
            }
        }
    }

    public static void copy(String src, String dest) throws IOException {
        copy(new BufferedInputStream(new FileInputStream(src)),
                new BufferedOutputStream(new FileOutputStream(dest)));
    }

    protected SimpleFileUtil() {
    }
}
