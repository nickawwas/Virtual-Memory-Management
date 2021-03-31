public class Command {
    private String commandWord, pageId;
    private int pageValue;

    //Format For Lookup and Release
    Command(String c, String id) {
        commandWord = c;
        pageId = id;

        pageValue = -1;
    }

    //Format For Store
    Command(String c, String id, int val) {
        commandWord = c;
        pageId = id;
        pageValue = val;
    }

    public String getCommand() {
        return commandWord;
    }

    public String getPageId() {
        return pageId;
    }

    public int getPageValue() {
        return pageValue;
    }

    public String toString() {
        String showValue = pageValue != -1 ?  ", Page Value : " + pageValue : "";
        return "Command Word: " + commandWord + ", Page Id: " + pageId + "" + showValue + "\n";
    }
}
