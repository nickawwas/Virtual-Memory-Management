import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

public class Memory {
    // Memory Size
    private int memorySize;
    //Current Index in Main Memory
    //private int currIndex; // Useless for now

    //Main Memory and Large Disk
    private LinkedList<Page> mainMemory;
    private List<Page> largeDisk; //TODO implement as storage in vm.txt file

    /**
     * Default Constructor for the Memory class
     */
    public Memory() {
        //currIndex = 0;
        memorySize = 0;
        largeDisk = new ArrayList<>();
        mainMemory = new LinkedList<>();
    }

    /**
     * Parameterized Constructor for the Memory class
     */
    public Memory(int size) {
        //currIndex = 0;
        memorySize = size;
        largeDisk = new ArrayList<>();
        mainMemory = new LinkedList<>();
    }

    /**
     * API of the Memory Class
     * Store: Stores an ID with its corresponding value in the main memory or virtual memory
     * @param varId
     * @param varValue
     */
    public void store(String varId, int varValue) {
        Page v = new Page(varId, varValue);
        //Add Main Memory if Space is Available
        if(!isFull())
            addVariable(v);
        //Add to Large Disk Space Otherwise
        else
            largeDisk.add(v);
        String message = ", Store: Variable " + varId + ", Value: " + varValue;
        // TODO output the message using clock Thread? (or move the message to method call)
    }

    /**
     * API of the Memory Class
     * Release: Releases an ID with its corresponding value from the main memory or virtual memory
     * @param varId
     * @return returns the ID of the removed item if successful, -1 if ID not found
     */
    //TODO: Ask if release only releases from the mainMemory or both
    public int release(String varId) {
        //Attempt 1: Search Id in Main Memory
        int location = searchMemory(varId);
        if(location != -1) {
            //Remove From Main Memory
            removeVariable(location); //TODO store removed value -- IDK what this means?
            String message = ", Release: Variable " + varId;
            // TODO output the message using clock Thread? (or move the message to method call)
            //Return the Id of the removed variable (page)
            return Integer.parseInt(varId);
        }

        //Attempt 2: Search Id in Large Disk
        location = searchDisk(varId);
        if(location != -1) {
            //Remove From Large Disk
            largeDisk.remove(varId);
            String message = ", Release: Variable " + varId;
            // TODO output the message using clock Thread? (or move the message to method call)
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
            // Changing List order to accommodate for LRU
            addVariable(mainMemory.remove(location)); // TODO Make sure this updates List according to LRU
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
                String message = "Memory Manager, SWAP: Variable " + mainMemory.getFirst().getId() + " with Variable " + varId;
                // TODO output the message using clock Thread? (or move the message to method call)
                //Full! - Swap using LRU
                //LRU Algorithm: Swap with Least Recently Used
                largeDisk.add(mainMemory.getFirst());
                mainMemory.removeFirst();
            }
            addVariable(new Page(varId, val));

            return val;
        }

        return -1;
    }

    /**
     * Method used to check If Main Memory is Full
     * @return returns true if Full, false if not
     */
    public Boolean isFull() {
        return memorySize <= mainMemory.size();
    }

    //TODO: confirm that creating a Page counts as accessing it!

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
            if (page.getId() == id)
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
            if (page.getId() == id)
                return largeDisk.indexOf(page);
        }
        //Not Found
        return -1;
    }

    /**
     * Method used to remove a Variable from Main Memory given Index
     * @param index
     */
    //TODO: NEEDS FIXING FOR LRU ALGO -- Fixed to work as LinkedList
    public void removeVariable(int index) {
        mainMemory.remove(index);
    }

    //Get Attribute - CurrIndex -- Useless for now
//    public int getCurrIndex() {
//        return currIndex;
//    }
}
