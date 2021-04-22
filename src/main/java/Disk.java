import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Disk {
    private int diskSize;

    Disk() {
        diskSize = 0;
    }

    /**
     * Store Page in Disk by Writing to Disk
     * @param id
     * @param val
     * @throws Exception
     */
    public void writeDisk(String id, int val) throws Exception {
        FileWriter fw = new FileWriter("vm.txt", true);

        fw.write(id + " " + val + "\n");
        diskSize++;

        fw.close();
    }

    /**
     * Release Page in Disk by Scanning File, Clearing its Content, and Rewriting Pages Without Specified Page
     * @param id
     * @return
     * @throws Exception
     */
    public int removeDisk(String id) throws Exception {
        Scanner sfw = new Scanner(new File("vm.txt"));
        int removedVal = -1;

        //Scan File Content
        String fileContent = "";
        while(sfw.hasNextLine())
            fileContent += sfw.nextLine() + "\n";

        //Clears File
        FileWriter rfw = new FileWriter("vm.txt");
        rfw.write("");

        //Rewrite File & Search for Page
        sfw = new Scanner(fileContent);
        while(sfw.hasNext()) {
            String pageId = sfw.next();
            int pageVal = sfw.nextInt();

            //Write Page to Disk
            if (!pageId.equals(id))
                rfw.append(pageId + " " + pageVal + "\n");
            //Skip Writing Line, Decrement Disk Size and Store Removed Page Value
            else {
                diskSize--;
                removedVal = pageVal;
            }
        }

        sfw.close();
        rfw.close();

        return removedVal;
    }

    /**
     * Lookup Page on Disk by Scanning File
     * @param id
     * @return
     * @throws Exception
     */
    public int readDisk(String id) throws Exception  {
        Scanner sf = new Scanner(new File("vm.txt"));

        //Search for Page
        while(sf.hasNext()) {
            String pageId = sf.next();
            int pageVal = sf.nextInt();

            if(pageId.equals(id))
                return pageVal;
        }

        sf.close();

        //Not Found
        return -1;
    }

    //Prints Disk Content to Check Functionality
    public void printDisk(String m) {
        try {
            Scanner sf = new Scanner(new File("vm.txt"));

            //Search for Page
            while (sf.hasNextLine())
                main.log.info(m + sf.nextLine());

            sf.close();
        } catch(Exception e) {
            Clock.INSTANCE.logEvent(e.getMessage());
        }
    }
}
