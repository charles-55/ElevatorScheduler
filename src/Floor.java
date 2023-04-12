import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The Floor Class.
 * Represents current status of floor.
 * Takes the information in table and processes it.
 *
 * @author Sabah Samwatin
 * @version 1.0
 */
public class Floor {

    private final int floorNumber;
    private final FloorSubsystem floorSubsystem;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    public static final int NUM_OF_FLOORS = 22;

    /**
     * Constructor for the floor class.
     */
    public Floor(int floorNumber, FloorSubsystem floorSubsystem) {
        this.floorNumber = floorNumber;
        this.floorSubsystem = floorSubsystem;
        this.floorSubsystem.addFloor(this);

        buttonsAndLamps = new HashMap<>();
        buttonsAndLamps.put(1, false);
        buttonsAndLamps.put(2, false);
    }

    /**
     * Accessor method for the buttons and lamps
     * @return buttonsAndLamps
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     * Returns the floor number
     * @return floorNumber
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Mutator method for the button direction.
     * @param direction
     * @param state
     */
    public void setButtonDirection(Integer direction, boolean state) {
        buttonsAndLamps.put(direction, state);
    }

    public void callElevator(Integer direction) {
        buttonsAndLamps.put(direction, true);
        floorSubsystem.callElevator(floorNumber);
    }

    /**
     * Method to print the state of the floor.
     */
    public void printAnalyzedState() {
        boolean up = this.buttonsAndLamps.get(1);
        boolean down = this.buttonsAndLamps.get(2);

        if (up)
            System.out.println("Calling an elevator to go up.");
        if (down)
            System.out.println("Calling an elevator to go down.");
        if (!(up || down))
            System.out.println("Idle.");
    }
}
