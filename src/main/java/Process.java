public class Process {
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

}
