import java.util.concurrent.Semaphore;

//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 1;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;
    //Index used to loop through the command list given (shared by ALL processes)
    private static int index = 0;

    private static Semaphore commandBinarySemaphore;

    /**
     * Parameterized Constructor
     * @param start
     * @param duration
     */
    public Process(int start, int duration) {
        pId = pNum++;
        pStart = start;
        pDuration = duration;
        commandBinarySemaphore = new Semaphore(1);
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
        while(clockCurrent - startTime < 1000 * pDuration) {
            try {
                // Critical section ahead!
                // Requires Permit to Access CS
                commandBinarySemaphore.acquire();

                //Perform Command and Log Messages
                Command nextCommand = main.commandList.get(index);
                index = (index + 1) % main.commandList.size();

                clockCurrent = Clock.INSTANCE.getTime();

                main.memoryManager.runCommands(nextCommand, this, clockCurrent);

                synchronized (this) {
                    this.wait();
                }

                // Release the Semaphore to Access Command
                commandBinarySemaphore.release();
            } catch(InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");
        // Releases Permit to Schedule Another Process
        Scheduler.coreCountSem.release();
    }
}
