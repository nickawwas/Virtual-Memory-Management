public class Command {
    private String commandWord;
    private int pageId, pageValue;

    //Format For Lookup and Release
    Command(String c, int id) {
        commandWord = c;
        pageId = id;

        pageValue = -1;
    }

    //Format For Store
    Command(String c, int id, int val) {
        commandWord = c;
        pageId = id;
        pageValue = val;
    }

    public String getCommand() {
        return commandWord;
    }

    public int getPageId() {
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
