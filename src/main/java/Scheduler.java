import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Scheduler class used to schedule the inputted processes
 */
public class Scheduler implements Runnable{
    //Number of Cores
    private int coreCount;

    //Lists Containing All Processes and Ready Ones
    private List<Process> processWaitingQ, processReadyQ;

    //Process Threads List
    private List<Thread> threadQueue;

    // Semaphore used to limit the amount of Threads (Processes) using commandCall method. Limited by amount of cores specified at construction
    public static Semaphore coreCountSem;

    /**
     * Parametrized constructor of the Scheduler class
     * character array of the parsed input file
     */
    Scheduler(ArrayList<Process> allProcesses, int cores){
        coreCount = cores;
        processWaitingQ = allProcesses;
        processReadyQ = new ArrayList<>();
        threadQueue = new ArrayList<>();
        coreCountSem = new Semaphore(cores); // Initialize the semaphore with the amount of cores available
    }

    /**
     * Method used to run the whole Runnable Scheduling algorithm within a Thread. Performs 4 tasks in each itteration:
     * 1. Check if there are any Processes ready to be added to the waiting Queue (and add them)
     * 2. Separate the Quantum according to the amount of users in waiting and their respective processes in waiting
     * 3. Preform the execution, which simulates allocating CPU time to one specific process at a time
     * 4. Check if any of the processes in the waiting queue are Finished (and remove them)
     */
    @Override
    public void run() {
        main.log.info("Scheduler Started!");

        //Main Scheduler Execution
        while(!processWaitingQ.isEmpty() || !processReadyQ.isEmpty()) {
            readyCheck();
            executingMethod();

            //Sleep Thread
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        //Join All Threads to Terminate Memory Manager Properly
        for(Thread thread: threadQueue) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        main.log.info("Scheduler Stopped!");
    }

    /**
     * Method - Check if Process Has Arrived/Is Ready to Start Execution
     * Moves Process From Waiting to Ready Queue
     */
    public void readyCheck() {
        for (int i = 0; i < processWaitingQ.size(); i++) {
            Process process = processWaitingQ.get(i);
            if (process.getStart() <= Clock.INSTANCE.getTime()/1000) {
                processReadyQ.add(process);
                processWaitingQ.remove(process);

                //Reset Queue
                i = 0;
            }
        }
    }

    /**
     * Method used to create process threads and simulate process execution (one process on the CPU at a time)
     */
    public void executingMethod() {
        for(int i = 0; i < processReadyQ.size(); i++) {
            //Start Process Thread
            startCheck();

            //Sleep Thread
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }
    }

    /**
     * Method used to check if any Process can be STARTED
     */
    public void startCheck() {
        // Add the Thread to the Thread Queue and Start Thread
        Thread processT = new Thread(processReadyQ.remove(0));
        threadQueue.add(processT);

        try {
            coreCountSem.acquire(); // If no permits, block until available
            processT.start();
        } catch(InterruptedException e) {
            main.log.error(e.getMessage());
        }

        //commandCallSemaphore.release();
    }

    /**
     * Method used by Processes to call Commands until they terminate
     */
    //TODO: Check how to implement a semaphore to restrict Thread access to number of cores
    public void commandCall() {
        try {
            coreCountSem.acquire();
            // Method that runs commands
        } catch (InterruptedException e) {
           main.log.error(e.getMessage());
        }
        finally {

        }
    }


    /**
     * Method used to print the data of each Process acquired from the input file
     */
    public void printData() {
        for (Process process: processReadyQ)
                main.log.info(process.toString());
    }
}