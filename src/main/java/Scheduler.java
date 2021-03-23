import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler class used to schedule the inputted processes
 */
public class Scheduler implements Runnable{
    private int quantum;
    //User List - Lists all users
    private List<User> userList;
    //Arrived, Ready user Queue
    private List<User> userQueue;
    //Process Threads List
    private List<Thread> threadQueue;



    /**
     * Parametrized constructor of the Scheduler class
     * @param input character array of the parsed input file
     */
    Scheduler(String[] input){
        // Assume the Quantum will ALWAYS be the first value (as mentioned in the instructions)
        quantum = Integer.parseInt(input[0]);
        userList = new ArrayList<>();
        userQueue = new ArrayList<>();
        threadQueue = new ArrayList<>();
        parseInput(input);
    }

    /**
     * Method used by the Scheduler class in order to parse the input String array into its proper User and Process classes
     * @param input String array of the parsed input file
     */
    public void parseInput(String[] input){
        User user = new User();
        for(int i = 1; i < input.length-1; i++) {
            if(!input[i].matches("[0-9]+")) {
                user = new User(input[i++], Integer.parseInt(input[i]));
                userList.add(user);
            }
            else if (input[i].matches("[0-9]+") && (user.getName() != " ")){ //Ensures that these values are being added to an existing user
                user.addProcess(Integer.parseInt(input[i++]), Integer.parseInt(input[i]));
            }
            else {
                main.loggerObj.error("String " + input[i] + " is neither a Letter or a Number, Unexpected input!");
                break;
            }
        }
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
        main.loggerObj.info("Scheduler Started!");
        main.clockT.start();

        do {
            aliveCheck();
            quantumSeparator();
            executingMethod();
            deadCheck();
        } while(!userList.isEmpty());

        //Joining all threads that have been started previously to ensure they all terminate properly before the Scheduler terminates
        for(Thread thread: threadQueue){
            try{
                thread.join();
            } catch (InterruptedException e) {
                main.loggerObj.error(e.getMessage());
            }
        }

        main.clockObj.setStatus(2); // Clock stopped

        // joining the Clock Thread to ensure that it finishes before the Scheduler one
        try{
            main.clockT.join();
        } catch (InterruptedException e) {
            main.loggerObj.error(e.getMessage());
        }

        main.loggerObj.info("Scheduler Stopped!");
    }

    /**
     * Method used to check if there are any Processes ready to be added to the waiting Queue (and add them)
     */
    public void aliveCheck() {
        for (User user : userList) {
            for (ProcessRunnable process : user.getProcessList()) {
                if (process.getReady() <= main.clockObj.getTime() && process.getProcessState() == -1 && !user.getProcessQueue().contains(process)) {
                    // process's arrival time has been met/exceeded, the process hasn't been put in the queue yet (-1 is the default starting state)
                    user.addToQueue(process);

                    if (!userQueue.contains(user)) { // checks if the user is already in the queue because of past processes
                        userQueue.add(user); //add user in userQueue in scheduler if not present
                    }
                }
            }
        }
    }

    /**
     * Method used to separate the Quantum according to the amount of users in waiting and their respective processes in waiting
     */
    public void quantumSeparator(){
        for(User user: userQueue){ // For each user in the user queue
            user.setUserQuantum (quantum/userQueue.size()); // calculate the quantum for the process by dividing total quantum by user qty and process qty (process qty is user specific)
        }
    }


    /**
     * Method used to create process threads and simulate process execution (one process on the CPU at a time)
     */
    public void executingMethod(){

        List<ProcessRunnable> processIteration = new ArrayList<>();
        List<Integer> quantumIteration = new ArrayList<>();

        // Storing into temp arrayList
        for (User user : userQueue) {
            for (ProcessRunnable process : user.getProcessQueue()) {
                processIteration.add(process);
                quantumIteration.add(user.getUserQuantum());
            }
        }

        //Started
        startCheck(processIteration);

        for(int i = 0; i < processIteration.size(); i++){
            if(processIteration.get(i).getProcessState() != 4 ) {
                processIteration.get(i).setProcessState(1); // Resume the process
            }

            int cpuTime = quantumIteration.get(i); // Quantum slice for the process

            //We opted to keep the clock paused and resume its counting ONLY when the clock time is supposed to increment to avoid losing sync with other threads.
            //main.clockObj.setStatus(0);

            while (cpuTime > 0) {

                main.clockObj.setStatus(0);

                try{
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    main.loggerObj.error(e.getMessage());
                }

                cpuTime--;

                main.clockObj.setStatus(1);
            }

            //main.clockObj.setStatus(1);

            if(processIteration.get(i).getProcessState() != 4 ) {
                processIteration.get(i).setProcessState(0); // Pause the process
                try{
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    main.loggerObj.error(e.getMessage());
                }
            }
        }
    }

    /**
     * Method used to check if any Process can be STARTED
     * @param processIteration
     */public void startCheck(List<ProcessRunnable> processIteration){
        //Checks all processes at each beginning of quantum to see if any can be started
        for(ProcessRunnable process: processIteration){
            if(process.getProcessState() == -1) {
                Thread processT = new Thread(process);
                threadQueue.add(processT); // Add the Thread to the Thread Queue
                process.setProcessState(3); // Set process in waiting state
                processT.start();
            }
        }
    }

    /**
     * Method used to check if any of the processes in the waiting queue are Finished (and remove them from both queue and original list)
     */
    public void deadCheck() {
        //main.loggerObj.info("DEADCHECK"); //DEBUG
        while(removeFromQueue())
            removeFromQueue();

        while(removeFromList())
            removeFromList();
    }

    /**
     * Method used to remove the objects from the Queue
     * @return
     */
    public boolean removeFromQueue(){
        boolean redo = false;
        for(User user : userQueue){
            for(ProcessRunnable process: user.getProcessQueue()){
                if(process.getProcessState() == 4){ // Process is considered finished
                    user.getProcessQueue().remove(process);
                    //main.loggerObj.info("Process " + process.getProcessName() + " removed from queue!"); //DEBUG
                    redo = true;
                    break;
                }
            }

            if(user.getProcessQueue().isEmpty()){
                userQueue.remove(user);
                //main.loggerObj.info("User " + user.getName() + " removed from queue!"); //DEBUG
                redo = true;
                break;
            }
        }
        return redo;
    }

    /**
     * Method used to remove the objects from the List
     * @return
     */
    public boolean removeFromList(){
        boolean redo = false;
        for (User user : userList){
            for(ProcessRunnable process: user.getProcessList()){
                if(process.getProcessState() == 4){ // Process is considered finished
                    user.getProcessList().remove(process);
                    //main.loggerObj.info("Process " + process.getProcessName() + " removed from list!"); //DEBUG
                    redo = true;
                    break;

                }
            }
            if(user.getProcessList().isEmpty()){
                userList.remove(user);
                //main.loggerObj.info("User " + user.getName() + " removed from list!"); //DEBUG
                redo = true;
                break;
            }
        }
        return redo;
    }

    /**
     * Method used to print the data of each Process acquired from the input file
     */
    public void printData(){
        String name = " ";
        for(User user : userList){
            for (ProcessRunnable process: user.getProcessList()) {
                if(name == process.getUserName()) {
                    main.loggerObj.info(process.getData());
                } else {
                    name = process.getUserName();
                    main.loggerObj.info(" ");
                    main.loggerObj.info("Printing Processes from User: " + process.getUserName() + ", with Processes:");
                    main.loggerObj.info(process.getData());
                }
            }
        }
    }
}