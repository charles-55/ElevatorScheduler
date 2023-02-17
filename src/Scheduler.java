import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * The Scheduler Class.
 * Connects the elevators to the floor. It calls an elevator to a floor
 * and adds the elevator to a queue when there is work to be done.
 *
 * @author Osamudiamen Nwoko 101152520
 * @author Leslie Ejeh 101161386
 * @version 1.0
 */
public class Scheduler extends Thread {

    private final HashMap<Elevator, ArrayList<Integer>> queue;
    private DatagramPacket floorSendPacket, floorReceivePacket, elevatorSendPacket, elevatorReceivePacket;
    private DatagramSocket floorSocket, elevatorSocket;
    private static final int FLOOR_PORT = 23, ELEVATOR_PORT = 69;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        queue = new HashMap<>();
        try {
            floorSocket = new DatagramSocket(FLOOR_PORT);
            elevatorSocket = new DatagramSocket(ELEVATOR_PORT);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
     * Adds a stop for an elevator to the queue.
     * Calls for an elevator to a floor.
     * @param event ElevatorCallEvent, an event containing details of the elevator call.
     */
    public synchronized void addToQueue(ElevatorCallEvent event) {
        Elevator elevator = (Elevator) queue.keySet().toArray()[0];
        for(Elevator e : queue.keySet()) {
            if ((Math.abs(e.getCurrentFloor() - event.getFloorNumber())
                    < Math.abs(elevator.getCurrentFloor() - event.getFloorNumber()))
                    && ((e.getDirection().equals(event.getDirection()))
                    || (e.getDirection().equals(ElevatorCallEvent.Direction.STANDBY))))
                elevator = e;
        }
        queue.get(elevator).add(event.getDestinationFloor());
        Collections.sort(queue.get(elevator));

        if(elevator.getDirection().equals(ElevatorCallEvent.Direction.STANDBY))
            elevator.moveToFloor(event.getFloorNumber(), event.getDirection());
        else if(elevator.getDirection().equals(ElevatorCallEvent.Direction.DOWN) && (elevator.getCurrentFloor() - event.getFloorNumber() >= 0))
            elevator.put(event.getFloorNumber(), true);
        else if(elevator.getDirection().equals(ElevatorCallEvent.Direction.UP) && (elevator.getCurrentFloor() - event.getFloorNumber() <= 0))
            elevator.put(event.getFloorNumber(), true);
        else {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Added to queue.");

        notifyAll();
    }

    /**
     * Removes a stop for the elevator from the queue.
     * @param elevator Elevator, the elevator
     */
    public synchronized void getFromQueue(Elevator elevator) {
        while(queue.get(elevator).size() == 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        for(int i = 0; i < queue.get(elevator).size();) {
            elevator.put(queue.get(elevator).get(i), true);
            queue.get(elevator).remove(i);
        }
        System.out.println("Got from queue.");
    }
}
