import java.util.concurrent.Semaphore;

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
                // Acquire Semaphore Permit to Access Command
                commandSem.acquire();

                //Select Command to Perform
                Command nextCommand = main.commandList.get(index);
                index = (index + 1) % main.commandList.size();
                //Clock.INSTANCE.logEvent(nextCommand.toString());

                //Update Clock Value
                clockCurrent = Clock.INSTANCE.getTime();

                // MMU Maps Logical to Physical Addresses, Runs Commands
                main.memoryManager.runCommands(nextCommand, this, clockCurrent);

                // Wait For MMU Thread
                synchronized (this) {
                    this.wait();
                }

                // Release the Semaphore to Access Command
                commandSem.release();

                clockCurrent = Clock.INSTANCE.getTime();
                //Clock.INSTANCE.logEvent("Check : " + (clockCurrent - startTime) + ", Given Clock: " + Clock.INSTANCE.getTime() + ", P duration: " + (pDuration * 1000));

            } catch(InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");

        // Releases Permit to Schedule Next Process
        Scheduler.coreCountSem.release();
    }
}
