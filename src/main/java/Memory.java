import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Memory implements Runnable{
    // Memory Size
    private int memorySize;
    private int currentProcess, currentClock;
    private Command currentCommand;
    private boolean commandFinished;

    //Main Memory and Large Disk
    private LinkedList<Page> mainMemory;
    //TODO implement as storage in vm.txt file
    //private List<Page> largeDisk;
    private Disk largeDisk; //TODO -- changed!

    private boolean terminate;

    /**
     * Parameterized Constructor - Initialize Main Memory and Large Disk Given Memory Size
     */
    public Memory(int size) {
        memorySize = size;
        currentProcess = -1;
        currentClock = -1;
        //largeDisk = new ArrayList<>();
        largeDisk = new Disk(); //TODO -- changed!
        mainMemory = new LinkedList<>();
        terminate = false;
        currentCommand = null;
        commandFinished = false;
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
            removeDiskVariable(varId); //TODO -- changed!
        }

        Page v = new Page(varId, varValue);
        //Add Main Memory if Space is Available
        if (!isFull())
            addMemoryVariable(v);
        //Add to Large Disk Space Otherwise
        else
            addDiskVariable(v); //TODO -- changed!

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
            removeDiskVariable(varId); //TODO -- changed!

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

        //Search Id in Disk Space
        location = searchDisk(varId); // This actually represents the value, not the location!

        if(location != -1) {
            //Found in Large Disk! - Page Fault Occurs
            int val = location; //TODO -- changed!

            //Release Id From Virtual Memory (Large Disk)
            removeDiskVariable(varId); //TODO -- changed!

            //Move Variable Into Main Memory
            if(isFull()) {
                //Full! - Swap using Least Recently Used (LRU) Page
                String swappedId = mainMemory.getFirst().getId();

                //Add the least accessed Page in Main Memory to the Large Disk
                addDiskVariable(mainMemory.getFirst()); //TODO -- changed!

                //Remove the least accessed Page from Main Memory
                mainMemory.removeFirst();

                String message = ", Memory Manager, SWAP: Variable " + swappedId + " with Variable " + varId;
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
            largeDisk.writeDisk(var.getId(), var.getValue()); //TODO -- changed!
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
        for(Page page: mainMemory) {
            //Check if Variable Id Matches Searched Id
            if (page.getId().equals(id))
                return mainMemory.indexOf(page);
        }
        //Not Found
        return -1;
    }

    /**
     * Method used to search for Variable in Large Disk
     * @param id
     * @return returns Variable's index if successful, -1 if not found
     */
    public int searchDisk(String id) {
        int value = -1;
        try{
             value = largeDisk.readDisk(id); //TODO -- changed!
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
        return value;
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
            largeDisk.removeDisk(id); //TODO -- changed!
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Method used to Terminate the Memory Thread from an outside thread (Main)
     */
    public void setStatus(boolean x) { terminate = x; }

    // Flag used to tell the process thread that the command has completed running
    public boolean getCommandFinished(){return commandFinished;}
    public void setCommandFinished(boolean value){commandFinished = value;}


    public void runCommands(Command command, int processID, int clockCurrent) {
        currentCommand = command;
        currentProcess = processID;
        currentClock = clockCurrent;
    }

    @Override
    public void run() {
        main.log.info("Memory Started!");
        while(!terminate) {

            if (currentCommand != null) {
                switch (currentCommand.getCommand()) {
                    //Run Command For Duration Calculated Above
                    case "Release":
                        int r = release(currentCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess + ", Release: Variable " + currentCommand.getPageId());
                        break;
                    case "Lookup":
                        int l = lookup(currentCommand.getPageId());
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess + ", Lookup: Variable " + currentCommand.getPageId() + ", Value: " + l);
                        break;
                    case "Store":
                        Clock.INSTANCE.logEvent("Clock: " + currentClock + ", " + "Process " + currentProcess + ", Store: Variable " + currentCommand.getPageId() + ", Value: " + currentCommand.getPageValue());
                        store(currentCommand.getPageId(), currentCommand.getPageValue());
                        break;
                    default:
                        Clock.INSTANCE.logEvent("Invalid Command");
                }

                currentCommand = null;
                commandFinished = true;
            }

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                main.log.error(e.getMessage());
            }
        }

        main.log.info("Memory Stopped!");
    }
}
