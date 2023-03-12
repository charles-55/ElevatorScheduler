import java.util.ArrayList;

/**
 * The Main Class.
 * Runs the program.
 */
public class Main {

    public static void main(String[] args) {
        Scheduler scheduler = new Scheduler();
        FloorSubsystem floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        ElevatorQueue elevatorQueue = new ElevatorQueue();
        ArrayList<Floor> floors = new ArrayList<>();
        ArrayList<Elevator> elevators = new ArrayList<>();

        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            floors.add(new Floor(i + 1));
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevators.add(new Elevator(i + 1, Floor.NUM_OF_FLOORS, elevatorQueue));

        floorSubsystem.start();
        scheduler.start();
        elevatorQueue.start();
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            floors.get(i).start();
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevators.get(i).start();
    }
}
