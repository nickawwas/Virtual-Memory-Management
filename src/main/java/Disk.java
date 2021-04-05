import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Disk {
    private int diskSize;

    Disk() {
        diskSize = 0;
    }

    public static void main(String[] args) throws Exception {
        Disk d = new Disk();
        d.writeDisk("1", 2);
        d.writeDisk("2", 3);
        System.out.println(d.readDisk("3"));
    }

    public void writeDisk(String id, int val) throws Exception {
        FileWriter fw = new FileWriter("vm.txt", true);

        fw.write(id + " " + val + "\n");
        diskSize++;

        fw.close();
    }

    public void removeDisk(String id) throws Exception {
        Scanner sfw = new Scanner(new File("vm.txt"));
        FileWriter rfw = new FileWriter("vm.txt");

        //Search for Page
        while(sfw.hasNext()) {
            String pageId = sfw.next();
            int pageVal = sfw.nextInt();

            if(pageId.equals(id)) {
                //Skip Write And Read Next Line
            }
        }

        rfw.write("");
        diskSize--;

        rfw.close();
    }

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
}
