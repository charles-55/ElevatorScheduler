import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ArrayList<Elevator> elevators;
    private DatagramPacket floorSendPacket, floorReceivePacket, elevatorSendPacket, elevatorReceivePacket;
    private InetAddress floorAddress, elevatorAddress;
    private DatagramSocket floorSocket, elevatorSocket;
    private static final int FLOOR_PORT = 23, ELEVATOR_PORT = 69;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        queue = new HashMap<>();
        elevators = new ArrayList<>();
        try {
            floorSocket = new DatagramSocket(FLOOR_PORT);
            elevatorSocket = new DatagramSocket();
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
        elevators.add(elevator);
    }

    /**
     * Get the closest elevator to a floor and moving in the same direction or on standby.
     * @param floorNum int, the floor the elevator is bing called to.
     * @param direction Direction, direction of the elevator.
     * @return int, the elevator number for the closest elevator.
     */
    public synchronized int getClosestElevator(int floorNum, Elevator.Direction direction) {
        Elevator elevator = (Elevator) queue.keySet().toArray()[0];
        for(Elevator e : queue.keySet()) {
            if ((Math.abs(e.getCurrentFloor() - floorNum)
                    < Math.abs(elevator.getCurrentFloor() - floorNum))
                    && ((e.getDirection().equals(direction))
                    || (e.getDirection().equals(Elevator.Direction.STANDBY))))
                elevator = e;
        }
        return elevator.getElevatorNum();
        /*
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

         */
    }

    public void sendToElevator() {
        byte[] data = new byte[3];
        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Floor...\n");
            floorSocket.receive(floorReceivePacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Floor Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Floor " + ((int) data[0]) + ": " + Arrays.toString(data) + ".\n");

        elevatorSendPacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_PORT);

        try {
            System.out.println("SCHEDULER: Sending Packet to elevator: " + Arrays.toString(data) + "\n");
            elevatorSocket.send(elevatorSendPacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Elevator Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to elevator.\n");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendToFloor() {
        byte[] data = new byte[3];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Elevator...\n");
            elevatorSocket.receive(elevatorReceivePacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Elevator Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Elevator: " + Arrays.toString(data) + ".\n");

        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_PORT);

        try {
            System.out.println("SCHEDULER: Sending Packet to Floor: " + Arrays.toString(data)+".\n");
            floorSocket.send(floorSendPacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Floor Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to floor\n");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }

        while(true) {
            sendToElevator();
            sendToFloor();
        }
    }
}
