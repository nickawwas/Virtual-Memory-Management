import org.apache.log4j.Logger;
import org.apache.log4j.LogManager;

import java.io.IOException;
import java.util.ArrayList;

public class main {
    public static Logger loggerObj, log;
    private static FileReader fr;

    public static Clock clockObj;
    public static Thread clockT;

    /**
     * Main method used to:
     * call the FileReader Method to obtain the input from an input.txt file
     * Create a scheduler object and output its contents (to confirm parsing of input was done correctly)
     * Create Clock and scheduler Threads and start the scheduler thread
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //Create File Reader Object
        fr = new FileReader();

        //Create Logger Object
        loggerObj = LogManager.getLogger("");
        log = LogManager.getLogger("1");

        //Files
        String configFile = "memconfig.txt";
        String processFile = "processes.txt";
        String commandFile = "commands.txt";

        //Read Config File - Contains Number of Pages in Main Memory
        int numPages = fr.readIntFile(configFile).get(0);

        //Read Process File - Contains Number of Cores, Processes and Lines
        ArrayList<Integer> processContent = fr.readIntFile(processFile);
        int numCores = processContent.remove(0);
        int numProcesses = processContent.remove(0);

        //Initialize Processes List (Start, Duration)
        ArrayList<Process> processList = new ArrayList<>();
        while(!processContent.isEmpty())
            processList.add(new Process(processContent.remove(0), processContent.remove(0)));

        //Number of Processes Must Match Num Processes Specified
        if(processList.size() != numProcesses)
            log.info("Error: Number of Process Don't Match!");
        log.info(processList.toString());

        //Read Command File - Contains Commands
        ArrayList<String> commandsList = fr.readFile(commandFile);
        log.info(commandsList);

        /*
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

         */
    }
}