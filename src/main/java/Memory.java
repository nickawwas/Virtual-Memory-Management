import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class Memory implements Runnable{
    // Memory Size
    private final int memorySize;

    //Main Memory and Large Disk
    private LinkedList<Page> mainMemory;
    private Disk largeDisk;

    //Memory Management Unit (MMU) Attributes
    private int currentClock, startClock;
    private Command currentCommand;
    private Process currentProcess;

    private Semaphore lockSem;
    private boolean terminate, commandAvailable;

    /**
     * Parameterized Constructor - Initialize Main Memory and Large Disk Given Memory Size
     */
    public Memory(int size) {
        memorySize = size;
        largeDisk = new Disk();
        mainMemory = new LinkedList<>();

        lockSem = new Semaphore(1);

        currentProcess = null;
        currentCommand = null;
        currentClock = -1;

        terminate = false;
        commandAvailable = false;
    }

    /**
     * API of the Memory Class
     * Store: Stores an ID with its corresponding value in the main memory or virtual memory
     *        If a page with the same Id already exists, the old one will be deleted and replaced by the new one
     * @param varId
     * @param varValue
     */
    public void store(String varId, int varValue) {
        //Update LRU Main Memory -> Move to Back by Removing Then Adding Back
        int location = searchMemory(varId);
        if(location != -1) {
            //Remove From Main Memory
            removeMemoryVariable(location);
        }
        location = searchDisk(varId);
        if (location != -1) {
            //Remove From Large Disk
            removeDiskVariable(varId);
        }

        Page v = new Page(varId, varValue);
        //Add Main Memory if Space is Available
        if (!isFull())
            addMemoryVariable(v);
        //Add to Large Disk Space Otherwise
        else
            addDiskVariable(v);
    }

    /**
     * API of the Memory Class
     * Release: Releases an ID with its corresponding value from the main memory or virtual memory
     * @param varId
     * @return returns the ID of the removed item if successful, -1 if ID not found
     */
    public int release(String varId) {
        //Attempt 1: Search Id in Main Memory
        int location = searchMemory(varId);
        if(location != -1) {
            //Remove From Main Memory
            removeMemoryVariable(location);

            //Return the Id of the removed variable (page)
            return Integer.parseInt(varId);
        }

        //Attempt 2: Search Id in Large Disk
        location = searchDisk(varId);
        if(location != -1) {
            //Remove From Large Disk
            removeDiskVariable(varId);

            //Return the Id of the removed variable (page)
            return Integer.parseInt(varId);
        }

        //Return - No Id Found
        return -1;
    }

    /**
     * API of the Memory Class
     * Lookup: Looks up an ID's value from the main memory or virtual memory
     *         If the ID is found in the VM, it is swapped with a value in MM using LRU
     * @param varId
     * @return returns the value of the ID if successful, -1 if not found
     */
    public int lookup(String varId) {
        int location = searchMemory(varId);

        //Search Id in Main Memory
        if(location != -1) {
            // Changing List Order to Accommodate for LRU
            addMemoryVariable(mainMemory.remove(location));
            return mainMemory.getLast().getValue();
        }

        //Search Id in Disk Space for Value
        location = searchDisk(varId);

        if(location != -1) {
            //Found in Large Disk! - Page Fault Occurs
            int val = location;

            //Release Id From Virtual Memory (Large Disk)
            removeDiskVariable(varId);

            //Move Variable Into Main Memory
            if(isFull()) {
                //Full! - Swap using Least Recently Used (LRU) Page
                String swappedId = mainMemory.getFirst().getId();

                //Add the least accessed Page in Main Memory to the Large Disk
                addDiskVariable(mainMemory.getFirst());

                //Remove the least accessed Page from Main Memory
                mainMemory.removeFirst();

                String message = ", Memory Manager, SWAP: Variable " + varId + " with Variable " + swappedId;
                Clock.INSTANCE.logEvent("Clock: " + currentClock + message);
            }

            //Add Variable to Main Memory
            addMemoryVariable(new Page(varId, val));
            return val;
        }

        return location;
    }

    /**
     * Method used to check If Main Memory is Full
     * @return returns true if Full, false if not
     */
    public Boolean isFull() {
        return memorySize <= mainMemory.size();
    }

    /**
     * Method to Add Variable to the end of the List (most recently accessed) After Checking isFull
     * @param var
     */
    public void addMemoryVariable(Page var) {
        mainMemory.addLast(var);
    }

    /**
     * Method to Add Variable to the Disk (vm.txt)
     * @param var
     */
    public void addDiskVariable(Page var) {
        try {
            largeDisk.writeDisk(var.getId(), var.getValue());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method used to search for Variable in Main Memory
     * @param id
     * @return returns Variable's index if successful, -1 if not found
     */
    public int searchMemory(String id) {
        //Check if Variable Id Matches Searched Id
        for(Page page: mainMemory)
            if (page.getId().equals(id))
                return mainMemory.indexOf(page);

        //Not Found
        return -1;
    }

    /**
     * Method used to search for Variable in Large Disk
     * @param id
     * @return returns Variable's index if successful, -1 if not found
     */
    public int searchDisk(String id) {
        int val = -1;

        try {
            val = largeDisk.readDisk(id);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        return val;
    }

    /**
     * Method used to remove a Page/Variable from Main Memory given Index
     * @param index
     */
    public void removeMemoryVariable(int index) {
        mainMemory.remove(index);
    }

    /**
     * Method used to remove a Page/Variable from Disk Memory given Index
     * @param id
     */
    public void removeDiskVariable(String id) {
        try{
            largeDisk.removeDisk(id);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method used to Terminate the Memory Thread from an outside thread (Main)
     */
    public void setStatus(boolean mmuDone) { terminate = mmuDone; }


    public void printMem(String m) {
        for(Page page: mainMemory)
            Clock.INSTANCE.logEvent(m + page.toString());
    }

    /**
     *
     * @param command
     * @param currentP
     * @param clockCurrent
     */
    public void runCommands(Command command, Process currentP , int clockCurrent) {
        // Allow Only ONE Thread to Access/Modify Memory at a Time!
        // Semaphore Used to Deal with Reader-Writer Problem
        try {
            lockSem.acquire();
        } catch(InterruptedException e){
            main.log.error(e.getMessage());
        }

        currentCommand = command;
        currentProcess = currentP;
        currentClock = clockCurrent;
        startClock = currentClock;
        commandAvailable = true;
    }

    @Override
    public void run() {
        main.log.info("Memory Started!");

        while(!terminate) {
            //Run Command Once Available, Else Sleep Thread
            if (commandAvailable) {
                switch (currentCommand.getCommand()) {
                    case "Release":
                        int r = release(currentCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess.getId() + ", Release: Variable " + currentCommand.getPageId());
                        break;
                    case "Lookup":
                        int l = lookup(currentCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess.getId() + ", Lookup: Variable " + currentCommand.getPageId() + ", Value: " + l);
                        break;
                    case "Store":
                        store(currentCommand.getPageId(), currentCommand.getPageValue());
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess.getId() + ", Store: Variable " + currentCommand.getPageId() + ", Value: " + currentCommand.getPageValue());
                        break;
                    default:
                        Clock.INSTANCE.logEvent("Invalid Command");
                }

                //printMem("After");
                //largeDisk.printDisk("After");

                //Simulate API Call
                commandSleeper();

                //Notify Process Thread
                synchronized (currentProcess) {
                    currentProcess.notify();
                }

                //Command Completed, None Available
                commandAvailable = false;

                lockSem.release();
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                main.log.error(e.getMessage());
            }
        }

        main.log.info("Memory Stopped!");
    }

    /**
     * Simulate API Call for Command
     */
    public void commandSleeper() {
        //Update Current Clock
        currentClock = Clock.INSTANCE.getTime();

        //Get Random Duration For Command Execution
        int commandDuration = (int) (Math.random() * 1000) + 1;
        commandDuration = Math.min((1000 * currentProcess.getDuration()) - currentClock + startClock, commandDuration);

        //Simulate Time for API Call
        int startClock = Clock.INSTANCE.getTime();
        while (currentClock - startClock - commandDuration < 10) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                main.log.error(e.getMessage());
            }

            currentClock = Clock.INSTANCE.getTime();
        }
    }
}
