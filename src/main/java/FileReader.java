import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class used to Open and Parse an Input file's contents into a String array
 */
public class FileReader {
    /**
     * Method used to open and parse the file
     * @param fileName Name of the input file to be opened
     * @return the tokens String array from the input file
     */
    public String[] openFile(String fileName) {
        String input = new String();

        try {
            Scanner sf = new Scanner(new File(fileName + ".txt"));
            while(sf.hasNextLine()) {
                input += sf.nextLine() + " ";

            }
        } catch (IOException e){
            main.loggerObj.error(e);
        }


        String[] tokens = input.split("\\s+");
        return tokens;
    }
}