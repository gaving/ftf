package net.brokentrain.ftf.core.services;

/**
 * Responsible for timing the period taken to fetch results for a specific
 * Source. The timer can be started by use of {@link #start start()} and
 * similarly stopped by use of {@link #stop stop()}. The textual representation
 * of this particular class is the time formatted in milliseconds.
 */
public final class ExecutionTimer {

    private long tStarted;

    private long tStopped;

    private boolean fIsRunning;

    /**
     * Create a new timer.
     */
    public ExecutionTimer() {
        tStarted = 0;
        tStopped = 0;
        fIsRunning = false;
    }

    /**
     * Start the timer.
     */
    public void start() {
        if (fIsRunning) {
            throw new IllegalStateException("Timer has not been stopped!");
        }

        /* Reset start and stop values */
        tStarted = System.currentTimeMillis();
        tStopped = 0;
        fIsRunning = true;
    }

    /**
     * Stop the timer.
     */
    public void stop() {

        /* Ensure that we have a running timer */
        if (!fIsRunning) {
            throw new IllegalStateException("Timer is not running!");
        }
        tStopped = System.currentTimeMillis();
        fIsRunning = false;
    }

    /**
     * Return the string representation of the timer.
     * 
     * @return A string representation suitable for printing.
     */
    @Override
    public String toString() {
        validateIsReadable();
        StringBuffer result = new StringBuffer();
        result.append(tStopped - tStarted);
        result.append(" ms");
        return result.toString();
    }

    /**
     * Return the raw time in milliseconds.
     * 
     * @return A long integer representing the time elapsed.
     */
    public long toValue() {
        validateIsReadable();
        return tStopped - tStarted;
    }

    /**
     * Check that the timer is in a suitable state for reading.
     * 
     * @exception IllegalStateException
     *                if the timer is either still running or has not even been
     *                started.
     */
    private void validateIsReadable() {
        if (fIsRunning) {
            throw new IllegalStateException(
                    (fIsRunning) ? "Timer is still running!"
                            : "Timer has not been started!");
        }
    }
}
