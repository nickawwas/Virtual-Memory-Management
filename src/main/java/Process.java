import java.util.concurrent.Semaphore;

//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 1;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;

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

        int i = 0;
        //Run Until Process Finishes its Execution
        while(clockCurrent/1000 - startTime/1000 < pDuration) {
            if(i < main.commandList.size()) {
                try {
                    commandBinarySemaphore.acquire(); // Critical section ahead! only allow ONE thread to access memory at a time!

                //Get Random Duration For Command Execution -- //TODO: Maybe this should be determined somewhere else? in Memory Manager
                int commandDuration = (int) (Math.random() * 1000) + 1;
                commandDuration = Math.min(1000 * pDuration - clockCurrent + startTime, commandDuration);

                if (commandDuration == 0) break;

                //Perform Command and Log Messages
                Command nextCommand = main.commandList.get(i);
                i = (i + 1); // % main.commandList.size();

                //Simulate Time for API Call
                int clockStart = Clock.INSTANCE.getTime();
                while (clockCurrent - clockStart < commandDuration) {
                    try {
                        Thread.sleep(10);
                    } catch (Exception e) {
                        main.log.error(e.getMessage());
                    }

                    clockCurrent = Clock.INSTANCE.getTime();
                }

                switch (nextCommand.getCommand()) {
                    //Run Command For Duration Calculated Above
                    case "Release":
                        int r = main.memoryManager.release(nextCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ", Release: Variable " + nextCommand.getPageId());
                        break;
                    case "Lookup":
                        int l = main.memoryManager.lookup(nextCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ", Lookup: Variable " + nextCommand.getPageId() + ", Value: " + l);
                        break;
                    case "Store":
                        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ", Store: Variable " + nextCommand.getPageId() + ", Value: " + nextCommand.getPageValue());
                        main.memoryManager.store(nextCommand.getPageId(), nextCommand.getPageValue());
                        break;
                    default:
                        Clock.INSTANCE.logEvent("Invalid Command");
                }
            } catch(InterruptedException e) {
                main.log.error(e.getMessage());
            }
                commandBinarySemaphore.release(); // Release the critical section once
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");
        Scheduler.coreCountSem.release(); // Releases Permit
    }
}
