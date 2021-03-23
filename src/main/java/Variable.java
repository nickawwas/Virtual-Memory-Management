public class Variable {
    //Stores Variables - (id, value) Pairs
    private String id;
    private int value;

    //Default Constructor
    public Variable() {
        id = "";
        value = -1;
    }

    //Parameterized Constructor
    public Variable(String varName, int varValue) {
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
}
