/**
 * Process class used to store each instance of processes and simulate runtime messages from the Threads
 */
public class ProcessRunnable implements Runnable {
    //Counter used to name the Process instances (counter name does not reset for each user for better output readability)
    static private int nameCounter = 0;

    private int readyTime; // Arrival Time
    private int serviceTime; // Process Duration given from the input file
    private int cpuTime; // Time spent on the CPU
    private String userName; // Name of the user this process is from

    // Starts off at -1, set to 1 if the process just started using CPU,
    // set to 2 if it still uses the CPU (resumed), set to 0 if "paused", then set to 3 to wait.
    // Set to 4 after completion (finished)
    private int processState;

    private String processName;

    /**
     * Parametrized Constructor for the Process Class
     * @param service service time of the process given by the input file
     * @param ready ready time of the process given by the input file
     */
    ProcessRunnable(String name, int ready, int service) {
        userName = name;
        processName = "P" + nameCounter++;
        serviceTime = service;
        readyTime = ready;
        processState = -1;
        cpuTime = 0;
    }

    /**
     *Setter for the Process' ProcessState variable
     */
    public void setProcessState(int state) {processState = state;}

    /**
     * Getters for the readyTime, processState, userName and processName of a Process object
     * @return
     */
    public int getReady() { return readyTime; }
    public int getProcessState() { return processState; }
    public String getUserName() {return userName;}

    /**
     * Run method used to start or "resume" a process thread. The process' state depends on the the processState value as follows:
     * processState = -1 -> The process has been created, but not added to the processQueue of a user yet. (not waiting)
     * processState = 0 -> The process has been paused, changes the state to 3 to avoid printing the message more than once.
     * processState = 1 -> The process has been resumed, changes the state to 2 to avoid printing the message more than once.
     * processState = 2 -> The process has already been resumed, this will increase the cpuTime value to count how much time the process is on the CPU (Active)
     * processState = 3 -> The process has already been paused, this ensure the Thread does not do anything while paused simulating as if it were not using the CPU (Paused)
     * processState = 4 -> The process is considered Finished (Time spent on CPU = Process Burst Time), the thread should also stop. (Finished)
     */
    @Override
    public void run() {
        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Started!");
        do {
            synchronized (this) {
                switch (processState) { // The scheduler will only deal with values 0 and 1
                    case -1:
                        main.loggerObj.error("This process wasn't added to the ready queue yet!");
                        break;
                    case 0: // This message should only be printed once
                        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Paused.");
                        processState = 3;
                        break;
                    case 1: // This message should only be printed once
                        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Resumed.");
                        processState = 2;
                        cpuTime++;
                        break;
                    case 2:
                        //main.loggerObj.info("Time " + main.clockObj.getTime() + ", User "+ userName + ", Process " + processName + " currently running." + " CPU time: " + cpuTime); //DEBUG
                        cpuTime++;
                        break;
                    case 3:
                        //main.loggerObj.info("Time " + main.clockObj.getTime() + ", User "+ userName + ", Process " + processName + " currently idle." + " CPU time: " + cpuTime); //DEBUG
                        break;
                    default:
                        main.loggerObj.error("Input received: " + processState + " is outside of the accepted values 0, 1, 2, 3 or 4!!");
                }
            }

            try {
                Thread.sleep(10); // one unit of cpu time
            } catch (InterruptedException e) {
                main.loggerObj.error(e.getMessage());
            }

        } while (cpuTime < serviceTime);

        processState = 4;
        main.loggerObj.info("Time " + main.clockObj.getTime() + ", User " + userName + ", Process " + processName + " Finished!" + " Process ran " + cpuTime + " units of time!");


    }

    /**
     * Method used to Print the data of the Process class
     * @return String object containing the output message
     */
    public String getData(){
        String data = " Process Name: " + processName + " => Ready Time: "+ readyTime + ", Service Time: " + serviceTime;
        return data;
    }
}
