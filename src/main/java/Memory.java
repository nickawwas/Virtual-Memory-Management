import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;

public class Memory implements Runnable{
    // Memory Size
    private int memorySize;

    //Main Memory and Large Disk
    private LinkedList<Page> mainMemory;
    //TODO implement as storage in vm.txt file
    private List<Page> largeDisk;

    private boolean terminate;

    /**
     * Parameterized Constructor - Initialize Main Memory and Large Disk Given Memory Size
     */
    public Memory(int size) {
        memorySize = size;
        largeDisk = new ArrayList<>();
        mainMemory = new LinkedList<>();
        terminate = false;
    }

    /**
     * API of the Memory Class
     * Store: Stores an ID with its corresponding value in the main memory or virtual memory
     *        If a page with the same Id already exists, the old one will be deleted and replaced by the new one
     * @param varId
     * @param varValue
     */
    public void store(String varId, int varValue) {

        int location = searchMemory(varId);
        if(location != -1) {
            //Remove From Main Memory
            removeVariable(location);
        }
        location = searchDisk(varId);
        if (location != -1){
            //Remove From Large Disk
            largeDisk.remove(location);
        }
        Page v = new Page(varId, varValue);
        //Add Main Memory if Space is Available
        if (!isFull())
            addVariable(v);
            //Add to Large Disk Space Otherwise
        else
            largeDisk.add(v);

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
            removeVariable(location);

            //Return the Id of the removed variable (page)
            return Integer.parseInt(varId);
        }

        //Attempt 2: Search Id in Large Disk
        location = searchDisk(varId);
        if(location != -1) {
            //Remove From Large Disk
            largeDisk.remove(varId);

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
            // TODO Make sure this updates List according to LRU
            addVariable(mainMemory.remove(location));
            return mainMemory.getLast().getValue();
        }

        //Search Id in Disk Space
        location = searchDisk(varId);

        if(location != -1) {
            //Found in Large Disk! - Page Fault Occurs
            int val = largeDisk.get(location).getValue();

            //Release Id From Virtual Memory (Large Disk)
            largeDisk.remove(varId);

            //Move Variable Into Main Memory
            if(isFull()) {
                String swappedId = mainMemory.getFirst().getId();

                //Full! - Swap using Least Recently Used (LRU) Page
                largeDisk.add(mainMemory.getFirst());
                mainMemory.removeFirst();

                String message = ", Memory Manager, SWAP: Variable " + swappedId + " with Variable " + varId;
                Clock.INSTANCE.logEvent("Clock: " + Clock.INSTANCE.getTime() + message);
            }

            //Add Variable to Main Memory
            addVariable(new Page(varId, val));
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
    public void addVariable(Page var) {
        mainMemory.addLast(var);
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
        for(Page page: largeDisk) {
            //Check if Variable Id Matches Searched Id
            if (page.getId().equals(id))
                return largeDisk.indexOf(page);
        }
        //Not Found
        return -1;
    }

    /**
     * Method used to remove a Page/Variable from Main Memory given Index
     * @param index
     */
    public void removeVariable(int index) {
        mainMemory.remove(index);
    }

    public void setStatus(boolean x){ terminate = x; }

    @Override
    public void run() {
        while(!terminate) {
            //RELEASE, LOOKUP, STORE

            try {
                Thread.sleep(10);
            } catch (Exception e) {
                main.log.error(e.getMessage());
            }
        }
    }
}
