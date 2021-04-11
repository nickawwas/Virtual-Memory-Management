import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Scheduler Class - Implementing FIFO Multi-Core Scheduling
 */
public class Scheduler implements Runnable{
    //Number of Cores
    private int coreCount;

    //Lists Containing All Processes and Ready Ones
    private List<Process> processWaitingQ, processReadyQ;

    //Process Threads List
    private List<Thread> threadQueue;

    //Semaphore Representing Number of Cores Available
    public static Semaphore coreCountSem;

    //Parametrized Scheduler Constructor
    Scheduler(ArrayList<Process> allProcesses, int cores){
        coreCount = cores;
        processWaitingQ = allProcesses;
        processReadyQ = new ArrayList<>();
        threadQueue = new ArrayList<>();
        coreCountSem = new Semaphore(cores);
    }

    /**
     * Runs Scheduler Thread Until Waiting and Ready Queue Are Empty
     * - Determine Whether Process is Ready or Waiting and Move them to their Corresponding Queues
     * - Schedule Ready Processes using Semaphore Representing the Number of Cores Available
     */
    @Override
    public void run() {
        main.log.info("Scheduler Started!");

        //Main Scheduler Execution
        while(!processWaitingQ.isEmpty() || !processReadyQ.isEmpty()) {
            //Check Process State for Change From Waiting to Ready
            checkProcessState();

            //Schedule Processes Using FIFO on Multi Core Processors
            fifoScheduling();

            //Sleep Thread
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        //Join All Threads to Terminate Scheduling Properly
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
     * Moves Ready Process to Ready Queue and Removes them from Waiting Queue
     * - Checks if Process Has Arrived/Ready
     */
    public void checkProcessState() {
        for (int i = 0; i < processWaitingQ.size(); i++) {
            Process process = processWaitingQ.get(i);
            if ((1000 * process.getStart()) <= Clock.INSTANCE.getTime()) {
                //Add Process to Ready Queue and Remove from Waiting Queue
                processReadyQ.add(process);
                processWaitingQ.remove(process);

                //Fix Index to Reflect Change
                --i;
            }
        }
    }

    /**
     * Scheduling in FIFO Processes on Multiple Cores
     */
    public void fifoScheduling() {
        for(int i = 0; i < processReadyQ.size(); i++) {
            //Create and Start Process Thread on Multicore Processors
            createProcessThread();

            //Sleep Thread -- Possibly useless? Thread already sleeps in run() after fifoScheduling() call
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                main.log.error(e.getMessage());
//            }
        }
    }

    /**
     * Create, Add to Thread Queue, and Start Threads When a Core is Available
     */
    public void createProcessThread() {
        //Create and Add Thread to Thread Queue
        Thread processT = new Thread(processReadyQ.remove(0));
        threadQueue.add(processT);

        //Start Thread If Semaphore Has Permit
        // Block Otherwise Until Available
        try {
            coreCountSem.acquire();
            processT.start();
        } catch (InterruptedException e) {
            main.log.error(e.getMessage());
        }
    }
}