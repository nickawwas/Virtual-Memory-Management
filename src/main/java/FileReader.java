import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Class used to Open and Parse an Input file's contents into a String array
 */
public class FileReader {
    private Scanner sf;
    /**
     * Method used to open and parse the file
     * @param fileName Name of the input file to be opened
     * @return the tokens String array from the input file
     */
    public ArrayList<String> readFile(String fileName) throws Exception {
        sf = new Scanner(new File(fileName));

        ArrayList<String> tokens = new ArrayList<>();
        while(sf.hasNextLine())
            tokens.add(sf.nextLine());

        return tokens;
    }

    public ArrayList<Integer> readIntFile(String fileName) throws Exception {
        Scanner sf = new Scanner(new File(fileName));

        ArrayList<Integer> tokens = new ArrayList<>();
        while(sf.hasNext())
            tokens.add(Integer.parseInt(sf.next()));

        return tokens;
    }
}