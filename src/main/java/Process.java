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

        //Run Until Process Finishes its Execution
        while(Clock.INSTANCE.getTime() - startTime < pDuration) {
            //TODO: ADD LOGIC HERE

           try {
                Thread.sleep(10);
           } catch(Exception e) {
                main.log.error(e.getMessage());
           }
        }
    }
}
