import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Scanner;

/*
 * @author Sabah Samwatin
 * Client in system
 * To read events in the format: Time, floor, floor direction, and elevator button,
 * Each line of input is to be sent to the Scheduler
 * */

public class FloorSubsystem {
    private ArrayList<ButtonPress> info = new ArrayList<ButtonPress>();
    private String fileName = " (put location for txt file) "; //current location for the file

    public FloorSubsystem() {
    }

    /**
     * Parse the data received in the input file
     * @param fileName - the name of the input file assuming it is .txt
     */
    public void parseData(String fileName) {
        this.fileName = fileName;
        File file = new File(fileName);

        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String data = sc.nextLine();
                String[] splited = data.split(" ");

                // create a new ArrayList with the data separated
                boolean direction = false;
                if(splited[2].equals("Up")) {
                    direction = true;
                }
                this.info.add(new ButtonPress(direction,Integer.parseInt(splited[1]), Integer.parseInt(splited[3]), LocalTime.parse(splited[0])));
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * Adds a ButtonPress to the info arraylist
     */
    public void addIn(ButtonPress buttonpress) {
        this.info.add(buttonpress);
    }

    /**
     * Removes a ButtonPress from the info arraylist
     */
    public void removeOut(ArrayList<Object> removee) {
        info.remove(removee);
    }

    /**
     * Removes a ButtonPress to the info arraylist using an index
     * @param index - the index of the removee
     */
    public void removeOut(int index) {
        info.remove(index);
    }

    /**
     * Gets info arraylist
     */
    public ArrayList<ButtonPress> getInfo(){
        return info;
    }

}

