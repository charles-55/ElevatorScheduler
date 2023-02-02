import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The Controller (Scheduler) Class.
 * Connects the elevators to the floor. It calls an elevator to a floor
 * and adds the elevator to a queue when there is work to be done.
 *
 * @author Osamudiamen Nwoko 101152520
 * @version 1.0
 */
public class Controller extends Thread {

    private final ArrayList<Elevator> elevators;
    private final ArrayList<Floor> floors;
    private final HashMap<Integer, ArrayList<Integer>> queue;

    /**
     * Initializes the controller.
     */
    public Controller() {
        elevators = new ArrayList<>();
        floors = new ArrayList<>();
        queue = new HashMap<>();
    }

    /**
     * Gets the list of elevators.
     * @return ArrayList<Elevator>, the list of elevators.
     */
    public ArrayList<Elevator> getElevators() {
        return elevators;
    }

    /**
     * Gets the list of floors.
     * @return ArrayList<Floor>, the list of floors.
     */
    public ArrayList<Floor> getFloors() {
        return floors;
    }

    /**
     * Gets the queue.
     * @return Hashmap<Integer, ArrayList<Integer>>, the queue.
     */
    public HashMap<Integer, ArrayList<Integer>> getQueue() {
        return queue;
    }

    /**
     * Adds an elevator to the controller.
     * @param elevator Elevator, the elevator to add.
     */
    public void addElevator(Elevator elevator) {
        elevators.add(elevator);
    }

    /**
     * Adds a floor to the controller.
     * @param floor Floor, the floor to add.
     */
    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    /**
     * Calls for an elevator to a floor.
     * @param event ElevatorCallEvent, an event containing details of the elevator call.
     */
    public synchronized void callElevator(ElevatorCallEvent event) {
        //elevators.get(event.getElevatorNumber()).goToFloor(event.getFloorNumber);
        //elevators.get(event.getElevatorNumber()).setDirection(event.getDirection());
    }

    /**
     * Adds a stop for an elevator.
     * @param elevatorNumber int, the elevator number.
     * @param floorDestination int, the floor number.
     */
    public synchronized void addToQueue(int elevatorNumber, int floorDestination) {
        queue.get(elevatorNumber).add(floorDestination);
        Collections.sort(queue.get(elevatorNumber));
    }

    /**
     * Removes a stop from an elevator and sets it to standby when there is no more task to be done.
     * @param elevatorNumber int, the elevator number.
     * @param floorDestination int, the floor number.
     */
    public synchronized void removeFromQueue(int elevatorNumber, int floorDestination) {
        queue.get(elevatorNumber).remove(floorDestination);
        //if(queue.get(elevatorNumber).isEmpty())
            //elevators.get(elevatorNumber).setDirection(ElevatorCallEvent.Directions.STANDBY);
    }
}
