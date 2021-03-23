import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

public class main {
    public static final Logger loggerObj = LogManager.getLogger("");
    public static Clock clockObj;
    public static Thread clockT;

    /**
     * Main method used to:
     * call the FileReader Method to obtain the input from an input.txt file
     * Create a scheduler object and output its contents (to confirm parsing of input was done correctly)
     * Create Clock and scheduler Threads and start the scheduler thread
     * @param args
     */
    public static void main(String[] args) {
        String fileName = "Input";
        FileReader fileReader = new FileReader();
        String[] input = fileReader.openFile(fileName);

        Scheduler scheduler = new Scheduler(input);
        scheduler.printData();

        //creates the Scheduler and Clock Threads
        Thread schedulerT = new Thread(scheduler);
        clockObj = new Clock();
        clockT = new Thread(clockObj);

        schedulerT.start();

        //Ensuring the Scheduler thread terminates properly before the main thread terminates
        try{
            schedulerT.join();
        } catch (InterruptedException e) {
            main.loggerObj.error(e.getMessage());
        }

        loggerObj.info("Process Scheduling Complete!");
    }
}