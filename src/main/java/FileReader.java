import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Class used to Open and Parse Contents of Input File
 */
public class FileReader {
    /**
     * Open and Parse Content of File, Line by Line
     * @param fileName
     * @return ArrayList of Each Line As a String from Input File
     */
    public ArrayList<String> readFile(String fileName) throws Exception {
        Scanner sf = new Scanner(new File(fileName));

        ArrayList<String> tokens = new ArrayList<>();
        while(sf.hasNextLine())
            tokens.add(sf.nextLine());

        return tokens;
    }

    /**
     * Open and Parse Content of File, Token by Token
     * @param fileName
     * @return ArrayList of Each Token As an Integer from Input File
     */
    public ArrayList<Integer> readIntFile(String fileName) throws Exception {
        Scanner sf = new Scanner(new File(fileName));

        ArrayList<Integer> tokens = new ArrayList<>();
        while(sf.hasNext())
            tokens.add(Integer.parseInt(sf.next()));

        return tokens;
    }
}