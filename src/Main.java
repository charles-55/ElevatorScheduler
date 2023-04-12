import java.util.ArrayList;

/**
 * The Main Class.
 * Runs the program.
 */
public class Main {

    public static void main(String[] args) {

        Scheduler scheduler = new Scheduler();
        new Elevator(1,Floor.NUM_OF_FLOORS,scheduler);
        new FloorSubsystem();

    }
}
