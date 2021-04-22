public class Page {
    //Stores Variables - (id, value) Pairs
    private String id;
    private int value;

    //Time Stamp of Last Access
    // - Can Be Used Instead of Linked List for LRU Implementation
    private int lastAccessed;

    //Default Constructor
    public Page() {
        id = "";
        value = -1;
        lastAccessed = -1;
    }

    //Parameterized Constructor
    public Page(String varName, int varValue) {
        id = varName;
        value = varValue;
        lastAccessed = -1;
    }

    //Set Attributes - Id, Value
    public void setId(String varName) {
        id = varName;
    }

    public void setValue(int varValue) {
        value = varValue;
    }

    public void setLastAccessed(int last) {
        lastAccessed = last;
    }

    //Get Attributes - Id, Value
    public String getId() {
        return id;
    }

    public int getValue() {
        return value;
    }

    public String toString() {
        return "Page Id: " + id + ", Page Value: " + value ;
    }
}
