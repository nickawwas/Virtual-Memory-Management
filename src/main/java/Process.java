import java.util.concurrent.Semaphore;

//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 1;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;
    //Index used to loop through the command list given (shared by ALL processes)
    private static int index = 0;

    private Semaphore commandBinarySemaphore;

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
                commandBinarySemaphore.acquire(); // Critical section ahead! only allow ONE thread to access memory at a time!

                //Get Random Duration For Command Execution -- TODO send this to Memory
                int commandDuration = (int) (Math.random() * 1000) + 1;
                commandDuration = Math.min(1000 * pDuration - clockCurrent + startTime, commandDuration);

                //Perform Command and Log Messages
                Command nextCommand = main.commandList.get(index);
                index = (index + 1) % main.commandList.size();

                main.memoryManager.runCommands(nextCommand, pId, clockCurrent);

                //Simulate Time for API Call -- TODO send this to Memory
                int clockStart = Clock.INSTANCE.getTime();
                while (clockCurrent - clockStart < commandDuration) {
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        main.log.error(e.getMessage());
                    }

                    clockCurrent = Clock.INSTANCE.getTime();
                }

                commandBinarySemaphore.release(); // Release the critical
            } catch(InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");
        Scheduler.coreCountSem.release(); // Releases Permit
    }
}
