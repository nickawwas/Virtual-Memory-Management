/**
 * Clock class made to count passing of time in units of 10ms. To be run as a separate thread originating from the Main class
 */
public class Clock implements Runnable{
    int time, status;

    /**
     * Constructor initializes the Clock's settings
     */
    Clock(){
        status = 0; // Clock is paused
        time = 1;
    }

    /**
     * Getter for the clock's time in units
     */
    public int getTime() {
        return time;
    }

    /**
     * Setter used to control the status (paused/running) of the clock
     */
    public void setStatus(int state) {
        status = state;
    }

    /**
     * Run method used when starting a thread using a clock object.
     * Method waits for 10ms and increments the time value if setting is set to 0 (on / resumed). Method skips counting for setting 1 (Paused) and ends the thread for setting 2 (Finished)
     */
    @Override
    public void run() {
        main.loggerObj.info("Clock Started!");

        do {
            try {
                Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                main.loggerObj.error(e.getMessage());
            }
                time++;
        } while (status != 2);

        main.loggerObj.info("Clock Stopped!");
    }
}