import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 1;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;

    //Stores Index Of Current Command from Command List (Shared Among All Processes)
    private static int index = 0;

    // Locks Critical Section
    private static Semaphore commandSem = new Semaphore(1);

    /**
     * Parameterized Constructor
     * @param start
     * @param duration
     */
    public Process(int start, int duration) {
        pId = pNum++;
        pStart = start;
        pDuration = duration;
    }

    //Get Attributes - Id, Start, Duration
    public int getId() {
        return pId;
    }

    public int getStart() {
        return pStart;
    }

    public int getDuration() {
        return pDuration;
    }

    public String toString() {
        return "Process Id: " + pId + ", Start Time: " + pStart + ", Duration: " + pDuration + "\n";
    }

    /**
     * Run Method - Start a process thread
     */
    @Override
    public void run() {
        int startTime = Clock.INSTANCE.getTime();
        int clockCurrent = Clock.INSTANCE.getTime();

        String message = "Process " + pId;
        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", "  + message + ": Started");

        //Run Until Process Finishes its Execution
        while(clockCurrent - startTime < (1000 * pDuration)) {
            try {
                // Use Try Acquire to Acquire Semaphore Permit And Assign Process a Command
                boolean acquired = false;
                while(!acquired && (clockCurrent - startTime < (1000 * pDuration))) {
                    // Check if Process is Waiting for Too Long
                    // Returns True = Acquired a Permit within Timeout, False = Timed out
                    acquired = commandSem.tryAcquire(10, TimeUnit.MILLISECONDS);

                    //Update Clock Value
                    clockCurrent = Clock.INSTANCE.getTime();
                }

                //Update Clock Value
                clockCurrent = Clock.INSTANCE.getTime();

                // Ensure that after waiting at the .acquire() command, the process is still in operating time bounds
                if (clockCurrent - startTime < (1000 * pDuration)) {
                    //Select Command to Perform
                    Command nextCommand = main.commandList.get(index);
                    index = (index + 1) % main.commandList.size();

                    // MMU Maps Logical to Physical Addresses, Runs Commands
                    main.memoryManager.runCommands(nextCommand, this, clockCurrent, startTime);

                    // Wait For MMU Thread
                    synchronized (this) {
                        this.wait();
                    }
                }

                // Release the Semaphore to Access Command
                commandSem.release();

                //Update Current Clock
                clockCurrent = Clock.INSTANCE.getTime();
            } catch(InterruptedException e) {
                main.log.error(e.getMessage());
            }

            // Sleep Process to Give Other Process an Opportunity to Access Memory
            // Otherwise, Only One Process Would be Running Until It Terminates
            // Note: Clock Delay Must Be Larger Than The Delay of the Process
            try {
                Thread.sleep(8);
            } catch (Exception e) {
                main.log.error(e.getMessage());
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");

        // Releases Permit to Schedule Next Process
        Scheduler.coreCountSem.release();
    }
}
