import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * User class used to store each instance of users as well as their respective Processes from an input file
 * This class is an extension of the Scheduler class and used as an intermediate storage between the scheduler and its processes to avoid complicated Hashmaps
 */
public class User {
    private String userName;
    private int processQty;
    private int userQuantum;

    //Process List
    private List<ProcessRunnable> processList;
    //Arrived, Ready Process Queue
    private List<ProcessRunnable> processQueue;


    /**
     * Base Constructor used to initialize an empty User
     */
    User(){ userName = " "; processQty = 0;}

    /**
     * Parametrized Constructor of the User class
     * @param name name of the user from the input file
     * @param qty amount of precesses of the user from the input file
     */
    User(String name, int qty) {
        userName = name;
        processQty = qty;
        processList = new ArrayList<>();
        processQueue = new ArrayList<>();
    }

    /**
     * Getters for the userName, userQuantum, processQty processList and processQueue objects
     * @return the objects wanted from each getter
     */
    public String getName() {return userName;}
    public int getUserQuantum() {return userQuantum;}
    public int getProcessQty() {return processQty;}
    public List<ProcessRunnable> getProcessList() {return processList;}
    public List<ProcessRunnable> getProcessQueue() {return processQueue;}

    /**
     * Method used to assign a new Processes to a User (add to the userList)
     * @param service service time of the process given by the input file
     * @param ready ready time of the process given by the input file
     */
    public void addProcess(int ready, int service){
        if (processList.size() >= processQty)
            main.loggerObj.error("Can't add more processes than the value given from the input file!");
        else
            processList.add(new ProcessRunnable(userName, ready, service));
    }

    /**
     * Method used to assign an existing process to the processQueue of a User
     * @param process
     */
    public void addToQueue(ProcessRunnable process) {
        processQueue.add(process);
    }

    /**
     * Method used to assign the user object with its respective quantum division depending on the amount of (waiting) processes in the processQueue
     */
    public void setUserQuantum(int quantum) {
        for (ProcessRunnable process: processQueue) {
            userQuantum = (quantum / processQueue.size());
        }
    }

    /**
     * Method used to Print the data of each Process in the User Class
     */
    public void getProcessData() {
        for(int i = 0; i < processList.size(); i++)
            main.loggerObj.info(processList.get(i).getData());
    }
}