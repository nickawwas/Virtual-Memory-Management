import java.util.ArrayList;
import org.apache.log4j.Logger;

public class main {
    public static Logger log;
    private static FileReader fr;

    public static ArrayList<Process> processList;
    public static ArrayList<Command> commandList;

    public static Memory memoryManager;

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
        log = Logger.getLogger("MMU");

        //Files
        String configFile = "memconfig.txt";
        String processFile = "processes.txt";
        String commandFile = "commands.txt";

        //Read Config File - Contains Number of Pages in Main Memory
        int numPages = fr.readIntFile(configFile).get(0);
        memoryManager = new Memory(numPages);

        //Read Process File - Contains Number of Cores, Processes and Lines
        ArrayList<Integer> processContent = fr.readIntFile(processFile);
        int numCores = processContent.remove(0);
        int numProcesses = processContent.remove(0);

        //Initialize Processes List (Start, Duration)
        processList = new ArrayList<>();
        while(!processContent.isEmpty())
            processList.add(new Process(processContent.remove(0), processContent.remove(0)));

        //Number of Processes Must Match Num Processes Specified
        if(processList.size() != numProcesses)
            log.info("Error: Number of Process Don't Match!");
        //log.info(processList.toString());

        //Read Command File - Contains Commands
        ArrayList<String> commandContent = fr.readFile(commandFile);
        commandList = new ArrayList<>();
        while(!commandContent.isEmpty()) {
          String[] temp = commandContent.remove(0).split("\\s+");

          switch(temp.length) {
              case 0:
                  break;
              case 1:
                  log.info("Error: Command is Missing a Parameter");
                  break;
              case 2:
                  commandList.add(new Command(temp[0], temp[1]));
                  break;
              case 3:
                  commandList.add(new Command(temp[0], temp[1], Integer.parseInt(temp[2])));
                  break;
              default:
                  log.info("Error: Command Has Too Many Parameters");
          }
        }
        //log.info(commandList.toString());

        //Initialize Scheduler Object
        Scheduler scheduler = new Scheduler(processList, numCores);
        log.info("Memory Management Started!");

        //Create & Start Scheduler and Clock Threads
        Thread schedulerT = new Thread(scheduler);
        schedulerT.start();
        Thread clockT = new Thread(Clock.INSTANCE);
        clockT.start();

        // Join Scheduler and Clock Threads
        // Terminate Clock by Setting Status 2, Finished
        try {
            schedulerT.join();
            Clock.INSTANCE.setStatus(true);
            clockT.join();
        } catch (InterruptedException e) {
            main.log.error(e.getMessage());
        }

        log.info("Memory Management Complete!");
    }
}