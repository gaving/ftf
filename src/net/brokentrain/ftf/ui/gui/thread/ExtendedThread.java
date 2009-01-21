package net.brokentrain.ftf.ui.gui.thread;

public class ExtendedThread extends Thread {

    protected boolean stopped = false;

    public ExtendedThread() {
        super();
        setDaemon(true);
    }

    public ExtendedThread(Runnable runnable) {
        super(runnable);
        setDaemon(true);
    }

    public ExtendedThread(String name) {
        super(name);
        setDaemon(true);
    }

    public boolean isStopped() {
        return stopped;
    }

    public void startThread() {
        start();
    }

    public synchronized void stopThread() {
        stopped = true;
    }
}
