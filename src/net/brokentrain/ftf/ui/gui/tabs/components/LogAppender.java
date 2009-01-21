package net.brokentrain.ftf.ui.gui.tabs.components;

import net.brokentrain.ftf.ui.gui.GUI;
import net.brokentrain.ftf.ui.gui.util.ColourUtil;
import net.brokentrain.ftf.ui.gui.util.FontUtil;
import net.brokentrain.ftf.ui.gui.util.PaintUtil;
import net.brokentrain.ftf.ui.gui.util.WidgetUtil;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public class LogAppender extends AppenderSkeleton {

    private Table table;

    public LogAppender(Table table) {
        this.table = table;

        /* Listen for logj sending log events */
        Logger.getRootLogger().addAppender(this);
    }

    @Override
    protected void append(final LoggingEvent event) {

        /* Make sure the display is reachable */
        if (GUI.isAlive()) {
            GUI.display.asyncExec(new Runnable() {
                public void run() {

                    /* Append the logging event to the log tab */
                    appendLog(event);
                }
            });
        }
    }

    private void appendLog(LoggingEvent loggingEvent) {

        /* Ensure we have a logging event and the table is reachable */
        if ((loggingEvent == null) || (!WidgetUtil.isset(table))) {
            return;
        }

        if (loggingEvent.getMessage() != null) {

            /* Get the level and location of the message */
            Level level = loggingEvent.getLevel();
            String location = loggingEvent.getLocationInformation()
                    .getFileName();

            /* Extract message from the log entry */
            String message = loggingEvent.getMessage().toString();

            TableItem tableItem = new TableItem(table, SWT.NONE);

            switch (level.toInt()) {

            /* Debug level */
            case Priority.DEBUG_INT:
                tableItem.setForeground(ColourUtil.darkGreen);
                tableItem.setImage(PaintUtil.iconDebug);
                break;

            /* Info level */
            case Priority.INFO_INT:
                tableItem.setForeground(ColourUtil.blue);
                tableItem.setImage(PaintUtil.iconInformation);
                break;

            /* Warn level */
            case Priority.WARN_INT:
                tableItem.setForeground(ColourUtil.darkYellow);
                tableItem.setImage(PaintUtil.iconWarning);
                break;

            /* Error level */
            case Priority.ERROR_INT:
                tableItem.setForeground(ColourUtil.red);
                tableItem.setImage(PaintUtil.iconError);
                break;

            /* Fatal level */
            case Priority.FATAL_INT:
                tableItem.setForeground(ColourUtil.darkRed);
                tableItem.setImage(PaintUtil.iconError);
                break;
            }

            /* Set table tableItem data */
            tableItem.setText(0, level.toString());
            tableItem.setText(1, location);
            tableItem.setText(2, message);

            /* Bold the items with additional throwable information */
            if (loggingEvent.getThrowableInformation() != null) {
                tableItem.setFont(FontUtil.tableBoldFont);
            }

            /* Assign the message to the tableItem */
            tableItem.setData(loggingEvent);

            /* Scroll the table automatically */
            table.showItem(tableItem);
        }
    }

    @Override
    public void close() {

        /* Required to be implemented! */
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }

}
