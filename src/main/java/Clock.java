/**
 * Clock Using Enum to Implement As Singleton
 * Counts Passing of Time in Units of 10ms
 * Runs as a Separate Thread
 */
public enum Clock implements Runnable {
    INSTANCE();

    private int time;
    private boolean isFinished;

    /**
     * Default Constructor - Initializes the Clock to Paused and Starting at Time 1
     */
    Clock() {
        time = 1000;
        isFinished = false;
    }

    /**
     * Set Current Status to Not Finished/Running (False) or Finished (True)
     */
    public void setStatus(boolean state) {
        isFinished = state;
    }

    /**
     * Get Current Clock Time
     */
    public int getTime() {
        return time;
    }

    /**
     * Log Events
     */
    public void logEvent(String event) {
        main.log.info(event);
    }

    /**
     * Run Thread Until Status is Finished (2)
     * Clock Count Increases by Intervals of 10ms
     */
    @Override
    public void run() {
        logEvent("Clock Started!");

        while(!isFinished) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }

            time += 10;
        }

        logEvent("Clock Stopped!");
    }
}