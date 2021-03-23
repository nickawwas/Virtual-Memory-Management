import java.util.List;
import java.util.ArrayList;

public class Memory {
    //Current Index in Main Memory
    private int currIndex;

    //Main Memory and Large Disk
    private Variable[] mainMemory;
    private List<Variable> largeDisk;

    //Default Constructor
    public Memory() {
        currIndex = 0;

        largeDisk = new ArrayList<>();
        mainMemory = new Variable[10];
    }

    //Parameterized Constructor
    public Memory(int size) {
        currIndex = 0;

        largeDisk = new ArrayList<>();
        mainMemory = new Variable[size];
    }

    //Three APIs - Store, Release, Lookup
    public void store(String varId, int varValue) {
        Variable v = new Variable(varId, varValue);
        //Add Main Memory if Space is Available
        if(!isFull())
            addVariable(v);
        //Add to Large Disk Space Otherwise
        else
            largeDisk.add(v);
    }

    public int release(String varId) {
        //Attempt 1: Search Id in Main Memory
        int location = searchMemory(varId);
        if(location != -1) {
            //Remove From Main Memory
            removeVariable(location);
            return mainMemory[location].getValue();
        }

        //Attempt 2: Search Id in Large Disk
        location = searchDisk(varId);
        if(location != -1) {
            //Remove From Large Disk
            largeDisk.remove(varId);
            return largeDisk.get(location).getValue();
        }

        //No Id Found
        return -1;
    }

    public int lookup(String varId) {
        int location = searchMemory(varId);

        //Search Id in Main Memory
        if(location != -1)
            return mainMemory[location].getValue();

        //Search Id in Disk Space
        location = searchDisk(varId);
        if(location != -1) {
            int val = largeDisk.get(location).getValue();

            //Release Id From Virtual Memory (Large Disk)
            largeDisk.remove(varId);

            //Move Variable Into Main Memory
            //Not Full - Page Fault Occurs
            if(!isFull())
                addVariable(new Variable(varId, val));
            //Full - Swap with LRU
            else {
                //TODO: Implement LRU Alg
                //Swap with Least Recently Used
            }

            return val;
        }

        return -1;
    }

    //Check If Main Memory is Full
    public Boolean isFull() {
        return currIndex >= mainMemory.length;
    }

    //Add Variable After Checking isFull
    public void addVariable(Variable var) {
        mainMemory[currIndex++] = var;
    }

    //Search for Variable in Main Memory and Return Index
    public int searchMemory(String id) {
        for(int i = 0; i < currIndex; i++)
            //Check if Variable Id Matches Searched Id
            if (mainMemory[i].getId() == id)
                return i;

        //Not Found
        return -1;
    }

    //Search for Variable in Large Disk and Return Index
    public int searchDisk(String id) {
        for(int i = 0; i < largeDisk.size(); i++)
            //Check if Variable Id Matches Searched Id
            if (largeDisk.get(i).getId() == id)
                return i;

        //Not Found
        return -1;
    }

    //TODO: NEEDS FIXING FOR LRU ALGO
    //Remove Variable Given Index
    public void removeVariable(int index) {
        //Swap Last Element with Removed Index
        mainMemory[index] = mainMemory[--currIndex];
        //Set Last Element to Null
        mainMemory[currIndex] = null;
    }

    //Get Attribute - CurrIndex
    public int getCurrIndex() {
        return currIndex;
    }
}
