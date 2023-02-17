import java.io.IOException;
import java.net.*;
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
    private InetAddress floorAddress, elevatorAddress;
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
            floorAddress = InetAddress.getLocalHost();
            elevatorAddress = InetAddress.getLocalHost();
        } catch (SocketException| UnknownHostException e) {
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

    public void doYourJob() {
        byte[] data = new byte[1024];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_PORT);

        //Receive packet from the elevator
        try {
            System.out.println("Waiting for Packet from elevator...\n");
            elevatorSocket.receive(elevatorReceivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Packet received from Client\n");
        // find a way to get the messages from the packet for accuracy


        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_PORT);

        //Receive packet from the elevator
        try {
            System.out.println("Waiting for Packet from elevator...\n");
            floorSocket.receive(floorReceivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Packet received from floor\n");
        // find a way to get the exact  messages sent to the packet for accuracy

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }

        // sending packet to elevator
        elevatorSendPacket = new DatagramPacket(data, elevatorReceivePacket.getLength(), elevatorAddress, ELEVATOR_PORT);

        try {
            elevatorSocket.send(elevatorSendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Packet sent to Elevator!\n");


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
