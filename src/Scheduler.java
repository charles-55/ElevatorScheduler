import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The Scheduler Class.
 * Connects the elevators to the floor. It calls an elevator to a floor
 * and adds the elevator to a queue when there is work to be done.
 *
 * @author Osamudiamen Nwoko 101152520
 * @version 1.0
 */
public class Scheduler extends Thread {

    private final HashMap<Elevator, ArrayList<Integer>> queue;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        queue = new HashMap<>();
    }

    /**
     * Gets the queue.
     * @return Hashmap<Elevator, ArrayList<Integer>>, the queue.
     */
    public HashMap<Elevator, ArrayList<Integer>> getQueue() {
        return queue;
    }

    /**
     * Adds an elevator to the controller.
     * @param elevator Elevator, the elevator to add.
     */
    public void addElevator(Elevator elevator) {
        queue.put(elevator, new ArrayList<>());
    }

    /**
     * Adds a stop for an elevator.
     * Calls for an elevator to a floor.
     * @param event ElevatorCallEvent, an event containing details of the elevator call.
     */
    public synchronized void addToQueue(ElevatorCallEvent event) {
        Elevator elevator = null;
        while(elevator == null) {
            for(Elevator e : queue.keySet()) {
                try {
                    if ((Math.abs(e.getCurrentFloor() - event.getFloorNumber())
                            < Math.abs(elevator.getCurrentFloor() - event.getFloorNumber()))
                            && ((e.getDirection().equals(event.getDirection()))
                            || (e.getDirection().equals(ElevatorCallEvent.Direction.STANDBY))))
                        elevator = e;
                } catch (NullPointerException ignored) {}
            }
        }
//        elevator.setDirection(event.getDirection());
//        elevator.moveToFloor(event.getFloorNumber());
//        if(!elevator.isMoving())
//            elevator.setDoorOpen(true);
        queue.get(elevator).add(event.getDestinationFloor());
        Collections.sort(queue.get(elevator));
    }


//    public synchronized ArrayList<Integer> getFromQueue(Elevator elevator) {
//        queue.get(elevator).remove(0);
//        //if(queue.get(elevatorNumber).isEmpty())
//            //elevators.get(elevatorNumber).setDirection(ElevatorCallEvent.Directions.STANDBY);
//    }
}
