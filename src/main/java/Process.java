//Process Class - Implements Runnable Threads
public class Process implements Runnable {
    //Process Identifier
    static private int pNum = 0;

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
        return "Process Id: " + pId + ", Start Time: " + pStart + ", Duration: " + pDuration;
    }

    /**
     * Run Method - Start a process thread
     */
    @Override
    public void run() {
        int startTime = Clock.INSTANCE.getTime();

        String message = "Process " + pId;
        Clock.INSTANCE.logEvent(message + ": Started");

        //Run Until Process Finishes its Execution
        while(Clock.INSTANCE.getTime() - startTime < pDuration) {
            //TODO: Add Command Logic HERE
            int commandDuration = (int) (Math.random() * Clock.INSTANCE.getTime());
            //To get a multiple of 10 duration: commandDuration - (commandDuration % 10)

            //TODO: Run Next Command For Duration Found Above
            //Log Message Should Probably Happen Here
            /*
             String nextCommand = main.commandList.remove(0);
             switch(nextCommand.substring(0, nextCommand.indexOf(" ")) {
                case "Release":
                    Memory.release(val1);
                    break;
                case "Lookup":
                    Memory.lookup(val1);
                    break;
                case "Store":
                    Memory.store(val1, val2);
                    break;
                default:
                    Clock.INSTANCE.logEvent("Invalid Command");
             }
             */

            try {
                Thread.sleep(10);
           } catch(Exception e) {
                main.log.error(e.getMessage());
           }
        }

        Clock.INSTANCE.logEvent(message + ": Finished");
    }
}
