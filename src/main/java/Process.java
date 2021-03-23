public class Process implements Runnable{
    //Process Identifier
    static private int pNum = 0;

    //Stores Process - (Start, Duration) Pairs
    private int pId, pStart, pDuration;

    //Default Constructor
    public Process() {
        pStart = -1;
        pDuration = -1;
    }

    //Parameterized Constructor
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
     * Run method used to start or "resume" a process thread. The process' state depends on the the processState value as follows:
     */
    @Override
    public void run() {
        main.log.info("running...");
//        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Started!");
//        do {
//            synchronized (this) {
//                //if() //TODO implement starting and finishing process?
//            }
//
//        } while (cpuTime < serviceTime);
//
//        processState = 4;
//        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Finished!" + " Process ran " + cpuTime + " units of time!");
    }

}
