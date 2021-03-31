/**
 * Clock Using Enum to Implement As Singleton
 * Counts Passing of Time in Units of 10ms
 * Runs as a Separate Thread
 */
public enum Clock implements Runnable {
    INSTANCE();
    private int time, status;

    /**
     * Default Constructor - Initializes the Clock to Paused and Starting at Time 1
     */
    Clock() {
        time = 1;
        status = 0;
    }

    /**
     * Set Current Status to Paused (0), Running (1), or Finished (2)
     */
    public void setStatus(int state) {
        status = state;
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
        main.log.info("Clock Started!");

        while(status != 2) {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }

            time++;
        }

        main.log.info("Clock Stopped!");
    }
}