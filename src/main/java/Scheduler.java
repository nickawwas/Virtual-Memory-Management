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
    private Semaphore commandCallSemaphore;

    /**
     * Parametrized constructor of the Scheduler class
     * character array of the parsed input file
     */
    Scheduler(ArrayList<Process> ls, int cores){
        coreCount = cores;
        processWaitingQ = ls;
        processReadyQ = new ArrayList<>();
        threadQueue = new ArrayList<>();
        commandCallSemaphore = new Semaphore(cores);
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
        main.log.info("Memory Manager Started!");

        // TODO add method calls depending on changes
        do {
            readyCheck();
            executingMethod();
        } while(!processWaitingQ.isEmpty()); //TODO probably change

        //Joining all threads that have been started previously to ensure they all terminate properly before the Scheduler terminates
        for(Thread thread: threadQueue){
            try{
                thread.join();
            } catch (InterruptedException e) {
                main.log.error(e.getMessage());
            }
        }

        /** joining the Clock Thread to ensure that it finishes before the Scheduler one
        try{
            main.clockT.join();
        } catch (InterruptedException e) {
            main.log.error(e.getMessage());
        }
         */

        main.log.info("Memory Manager Stopped!");
    }

    /**
     * Method used to check if there are any Processes ready to be added to the waiting Queue (and add them)
     */
    public void readyCheck() {
            for (Process process : processWaitingQ) {
                if (process.getStart() <= Clock.INSTANCE.getTime()) {
                    // process's arrival time has been met/exceeded, the process hasn't been put in the queue yet (-1 is the default starting state)
                    processReadyQ.add(process);
                    processWaitingQ.remove(process);
                }
            }
    }

    /**
     * Method used to create process threads and simulate process execution (one process on the CPU at a time)
     */
    public void executingMethod(){

        for(int i = 0; i < processReadyQ.size(); i++){

            //while (cpuTime > 0) {

                Clock.INSTANCE.setStatus(0);

                try{
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    main.log.error(e.getMessage());
                }

                //cpuTime--;

                Clock.INSTANCE.setStatus(1);
            //}

            main.log.info("Process finished");
        }
    }

    /**
     * Method used to check if any Process can be STARTED
     * @param processIteration
     */public void startCheck(List<Process> processIteration){
        //Checks all processes at each beginning of quantum to see if any can be started
        for(Process process: processIteration){
            Thread processT = new Thread(process);
            threadQueue.add(processT); // Add the Thread to the Thread Queue
            processT.start();
        }
    }

    /**
     * Method used by Processes to call Commands until they terminate
     */
    //TODO: Check how to implement a semaphore to restrict Thread access to number of cores
    public void commandCall(){
        try{
            commandCallSemaphore.acquire();
        } catch (InterruptedException e){
            String errorMessage = e.getMessage();
            // TODO output the error using clock Thread?
        }

    }


    /**
     * Method used to print the data of each Process acquired from the input file
     */
    public void printData(){
            for (Process process: processReadyQ) {
                    main.log.info(process.toString());
            }
    }
}