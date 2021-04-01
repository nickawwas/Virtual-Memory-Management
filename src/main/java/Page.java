public class Page {
    //Stores Variables - (id, value) Pairs
    private String id;
    private int value;

    //private int lastAccessed = Clock.INSTANCE.getTime();

    //Default Constructor
    public Page() {
        id = "";
        value = -1;
    }

    //Parameterized Constructor
    public Page(String varName, int varValue) {
        id = varName;
        value = varValue;
    }

    //Set Attributes - Id, Value
    public void setId(String varName) {
        id = varName;
    }

    public void setValue(int varValue) {
        value = varValue;
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
