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
        while(clockCurrent - startTime < 1000 * pDuration) {
            if(i < main.commandList.size()) {
                try {
                    commandBinarySemaphore.acquire(); // Critical section ahead! only allow ONE thread to access memory at a time!

//                    //Get Random Duration For Command Execution // TODO move this to the commandMethods in Memory --- MOVED!
//                    int commandDuration = (int) (Math.random() * 1000) + 1;
//                    commandDuration = Math.min(1000 * pDuration - clockCurrent + startTime, commandDuration);
//
//                    if (commandDuration == 0) break;
                    // TODO 1.Process Determines the command to be run and sends it as a flag to MemoryClass, Process then WAITS to receive an output from Memory (semaphore)
                    //Perform Command and Log Messages
                    Command nextCommand = main.commandList.get(i);
                    i = (i + 1); // % main.commandList.size();

//                    //Simulate Time for API Call // TODO move this to commandMethods in Memory
//                    int clockStart = Clock.INSTANCE.getTime();
//                    while (clockCurrent - clockStart < commandDuration) {
//                        try {
//                            Thread.sleep(5); // TODO --Done: smaller sleep time for the process class
//                        } catch (Exception e) {
//                            main.log.error(e.getMessage());
//                        }
//
//                        clockCurrent = Clock.INSTANCE.getTime();
//                    }

                    //main.memoryManager.runCommands(nextCommand, pId, clockCurrent); // Using just the ID
                    main.memoryManager.runProcessCommands(nextCommand, this, clockCurrent); // Sending the whole process
                    //Give it some time just in case Process outruns the Memory
                    try {
                        Thread.sleep(5);
                    } catch (Exception e) {
                        main.log.error(e.getMessage());
                    }
                    //Try to acquire the commandLockSemaphore (unpause yourself)
                    main.memoryManager.commandLockSem.acquire();
                    // TODO 2.Memory class then determines which type of command it is and determines a random time for the command to be executed
                    // TODO 3.Memory class then performs the task, waits for time to elapse, and sends the output back to Process
                    //Immediately release as we want the next command to be able to acquire from inside the memory
                    main.memoryManager.commandLockSem.release();
                    // TODO 4.Process then outputs(Logs) the received output and moves on to the next command
                } catch(InterruptedException e) {
                    main.log.error(e.getMessage());
                }

                clockCurrent = Clock.INSTANCE.getTime();
                commandBinarySemaphore.release(); // Release the critical section once
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + clockCurrent + ", " + message + ": Finished");
        Scheduler.coreCountSem.release(); // Releases Permit
    }
}
