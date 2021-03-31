//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 1;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;

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
        int startTime = Clock.INSTANCE.getTime()/1000;

        String message = "Process " + pId;
        Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + ", "  + message + ": Started");

        //Run Until Process Finishes its Execution
        while(Clock.INSTANCE.getTime()/1000 - startTime < pDuration) {
            if(!main.commandList.isEmpty()) {
                //Get Random Duration For Command Execution
                int commandDuration = (int) (Math.random() * Math.min(Clock.INSTANCE.getTime() - startTime, 1000));
                commandDuration -= commandDuration % 10;

                //Perform Command and Log Messages
                Command nextCommand = main.commandList.remove(0);

                //Simulate Time for API Call
                try {
                    Thread.sleep(commandDuration);
                } catch (Exception e) {
                    main.log.error(e.getMessage());
                }

                switch (nextCommand.getCommand()) {
                    //Run Command For Duration Calculated Above
                    case "Release":
                        Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + ", " + message + " RELEASE");
                        int r = main.memoryManager.release(nextCommand.getPageId());
                        break;
                    case "Lookup":
                        Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + ", " + message + " LOOKUP");
                        int l = main.memoryManager.lookup(nextCommand.getPageId());
                        break;
                    case "Store":
                        Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + ", " + message + " STORE");
                        main.memoryManager.store(nextCommand.getPageId(), nextCommand.getPageValue());
                        break;
                    default:
                        Clock.INSTANCE.logEvent("Invalid Command");
                }
            } else {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    main.log.error(e.getMessage());
                }
            }
        }

        Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + ", " + message + ": Finished");
    }
}
