package net.brokentrain.ftf.ui.gui.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import net.brokentrain.ftf.core.settings.Const;
import net.brokentrain.ftf.ui.gui.GUI;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

public class FileUtil extends SimpleFileUtil {

    private static final Class<FileUtil> clazz = FileUtil.class;

    public static final String GUI_FILE = Const.CONFIG_DIRECTORY + "gui.ini";

    public static void dumpCrashLog(Exception e) {

        String currentDate = StringUtil.dateToFileName(StringUtil.formatDate());

        String filename = "crash_" + currentDate + ".log";

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
            writer.write(getStackTrace(e));
            writer.close();
        } catch (IOException ioe) {
            GUI.log.error("Couldn't save crash log to file!", ioe);
        }
    }

    public static boolean exists(String path) {
        if (!StringUtil.isset(path)) {
            return false;
        }

        return new File(path).exists();
    }

    public static String getBasename(String name) {
        String base = new File(name).getName();

        int index = base.lastIndexOf('.');

        if (index != -1) {
            base = base.substring(0, index);
        }

        return base;
    }

    public static String getContent(InputStream inS) {
        String line;
        StringBuffer content = new StringBuffer();

        if (inS == null) {
            return "";
        }

        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inS));

            while ((line = in.readLine()) != null) {
                content.append(line).append('\n');
            }

            in.close();
        } catch (IOException ioe) {
            GUI.log
                    .error("An error occurred processing the input stream!",
                            ioe);
            return "";
        }

        return content.toString();
    }

    public static String getDirectory(Shell parent, String path, String title) {
        DirectoryDialog directoryDialog = new DirectoryDialog(parent);

        if (path != null) {
            directoryDialog.setFilterPath(path);
        }

        if (StringUtil.isset(title)) {
            directoryDialog.setText(title);
        }

        return directoryDialog.open();
    }

    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getFilePath(Shell parent, String[] formats,
            String fileName, int style, String path, String title) {
        FileDialog fileDialog = new FileDialog(parent, style);

        if (formats != null) {
            fileDialog.setFilterExtensions(formats);
        }

        if (path != null) {
            fileDialog.setFilterPath(path);
        }

        if (fileName != null) {
            fileDialog.setFileName(fileName);
        }

        if (StringUtil.isset(title)) {
            fileDialog.setText(title);
        }

        return fileDialog.open();
    }

    public static String getFilePath(String[] formats, String fileName,
            int style, String path, String title) {
        return getFilePath(GUI.shell, formats, fileName, style, path, title);
    }

    public static InputStream getMisc(String path) {
        try {
            FileInputStream fis = new FileInputStream("misc/" + path);
            return fis;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    public static String getResource(String path) {
        String userFile = "cfg/".concat(path);
        return userFile;
    }

    public static InputStream getResourceAsStream(String path) {
        return clazz.getResourceAsStream(path);
    }

    public static String getSavePath(String fileName, String format,
            String title) {
        String selectedFileName = getFilePath(new String[] { "*." + format,
                "*.*" }, fileName, SWT.SAVE, null, title);

        if (selectedFileName != null) {

            if (!new File(selectedFileName).exists() || overwrite()) {
                return selectedFileName;
            }
        }
        return null;
    }

    public static String getStackTrace(Throwable throwable) {

        /* Convert the exception to a query string friendly format */
        StringWriter sWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sWriter));

        return sWriter.getBuffer().toString();
    }

    public static boolean isDirectory(String path) {
        if (!StringUtil.isset(path)) {
            return false;
        }

        return new File(path).isDirectory();
    }

    public static boolean isMediumWriteable(File file) {
        try {
            file.createNewFile();
            file.delete();
        } catch (IOException ioe) {
            return false;
        }
        return true;
    }

    public static void makeDirectory(String path) {
        try {
            new File(path).mkdir();
        } catch (Exception e) {
            GUI.log.error("Couldn't create directory!", e);
        }
    }

    public static void makeFile(String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.close();
        } catch (IOException ioe) {
            GUI.log.error("Couldn't write to file!", ioe);
        }
    }

    private static boolean overwrite() {
        return MessageBoxUtil.showMessage(GUI.shell, SWT.ICON_WARNING | SWT.YES
                | SWT.NO, "Warning",
                "The target file already exists! Do you wish to overwrite?") == SWT.YES;
    }

    public static Object readObject(File path) {

        /* Check path exists */
        if (path.exists()) {

            Object x = null;

            try {
                FileInputStream fis = new FileInputStream(path);
                ObjectInputStream ois = new ObjectInputStream(fis);
                x = ois.readObject();
                fis.close();
            } catch (Exception e) {
                GUI.log.error("Failed to read incoming object!", e);
                return null;
            }

            return x;
        }
        return null;
    }

    public static boolean writeObject(Object object, File path) {

        try {
            FileOutputStream fos = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
            oos.flush();
            fos.close();
        } catch (Exception e) {
            GUI.log.error("Failed to write outgoing object!", e);
            return false;
        }

        return true;
    }

    private FileUtil() {
    }

}
