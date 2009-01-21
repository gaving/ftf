package net.brokentrain.ftf.ui.gui.util;

import java.text.NumberFormat;

/**
 * Responsible for providing frequently used operations and methods to reduce
 * replication throughout the system. In an effort to reduce the overhead of
 * this package, it must be created only once and accessed through
 * {@link #getFormatter()} directly.
 * 
 */
public class Formatter {

    private static final NumberFormat numberFormat = NumberFormat.getInstance();

    /**
     * Formats filesize in human readable form.
     * 
     * @param filesize
     *            in bytes
     * @return The formatted filesize.
     */
    public static String formatFilesize(long filesize) {
        String result;

        if (Math.abs(filesize) < 1024) {
            result = "" + filesize + " Bytes";
        } else if (Math.abs(filesize) < 1048576) {
            result = formatFilesizeKB(filesize, 2);
        } else if (Math.abs(filesize) < 1073741824) {
            result = formatFilesizeMB(filesize, 2);
        } else {
            result = formatFilesizeGB(filesize, 2);
        }

        return result;
    }

    /**
     * Formats filesize in bytes to GB
     * 
     * @param filesize
     *            in bytes
     * @param fractionDigits
     *            the fraction digits
     * @return The formatted filesize.
     */
    private static String formatFilesizeGB(long filesize, int fractionDigits) {
        numberFormat.setMaximumFractionDigits(fractionDigits);
        return new StringBuffer(numberFormat.format(filesize / 1073741824.0))
                .append(" GB").toString();
    }

    /**
     * Formats filesize in bytes to KB
     * 
     * @param filesize
     *            in bytes
     * @return The formatted filesize.
     */
    public static String formatFilesizeKB(long filesize) {
        return new StringBuffer().append(filesize / 1024).append(" KB")
                .toString();
    }

    /**
     * Formats filesize in bytes to KB
     * 
     * @param filesize
     *            in bytes
     * @param fractionDigits
     *            the fraction digits
     * @return The formatted filesize.
     */
    private static String formatFilesizeKB(long filesize, int fractionDigits) {
        numberFormat.setMaximumFractionDigits(fractionDigits);
        return new StringBuffer(numberFormat.format(filesize / 1024.0)).append(
                " KB").toString();
    }

    /**
     * Formats filesize in bytes to MB
     * 
     * @param filesize
     *            in bytes
     * @param fractionDigits
     *            the fraction digits
     * @return The formatted filesize.
     */
    private static String formatFilesizeMB(long filesize, int fractionDigits) {
        numberFormat.setMaximumFractionDigits(fractionDigits);
        return new StringBuffer(numberFormat.format(filesize / 1048576.0))
                .append(" MB").toString();
    }

    /**
     * Formats transfer speed in bytes/s to KB/s
     * 
     * @return formatted transfer speed
     * @param filesize
     *            to format in bytes
     * @param fractionDigits
     *            the fraction digits
     */
    public static String formatTransferSpeedKB(long filesize, int fractionDigits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractionDigits);
        return new StringBuffer(nf.format(filesize / 1024.0)).append(" KB/s")
                .toString();
    }

    /**
     * Formats transfer speed in bytes/s to MB/s
     * 
     * @return formatted transfer speed
     * @param filesize
     *            to format in bytes
     * @param fractionDigits
     *            the fraction digits
     */
    public static String formatTransferSpeedMB(long filesize, int fractionDigits) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(fractionDigits);
        return new StringBuffer(nf.format(filesize / 1048576.0))
                .append(" MB/s").toString();
    }

    private Formatter() {
    }
}
