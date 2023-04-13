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

        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevators.add(new Elevator(i + 1, Floor.NUM_OF_FLOORS, scheduler));

        scheduler.start();
        floorSubsystem.start();
        for(Elevator elevator : elevators)
            elevator.start();

        new Frame(floorSubsystem);

//        Scheduler scheduler = new Scheduler();
//        new Elevator(1, Floor.NUM_OF_FLOORS, scheduler);
//        new Frame(new FloorSubsystem());
    }
}
