import java.util.ArrayList;

/**
 * The Main Class.
 * Runs the program.
 */
public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        FloorSubsystem floorSubsystem = new FloorSubsystem();
        ArrayList<Elevator> elevators = new ArrayList<>();

        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            new Floor(i + 1, floorSubsystem);
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevators.add(new Elevator(i + 1, Floor.NUM_OF_FLOORS, scheduler));

        floorSubsystem.start();
        scheduler.start();
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevators.get(i).start();
    }
}
