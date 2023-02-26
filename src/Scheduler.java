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
            floorSocket = new DatagramSocket();
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
        byte[] floorData = new byte[3];
        floorReceivePacket = new DatagramPacket(floorData, floorData.length, floorAddress, FLOOR_PORT);

        try {
            System.out.println("SCHEDULER: ");
            System.out.println("Waiting for Packet from Floor...\n");
            floorSocket.receive(floorReceivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER:");
        System.out.println("Packet received from Floor " + ((int) floorData[0]) + ":");
        System.out.println(Arrays.toString(floorData) + "\n");

        byte[] elevatorData = new byte[4];

        Elevator.Direction direction = Elevator.Direction.STANDBY;
        if(floorData[1] == 1)
            direction = Elevator.Direction.UP;
        else if(floorData[1] == 2)
            direction = Elevator.Direction.DOWN;

        elevatorData[0] = (byte) getClosestElevator((int) floorData[0], direction);
        System.arraycopy(floorData, 0, elevatorData, 1, 3);

        elevatorSendPacket = new DatagramPacket(elevatorData, elevatorData.length, elevatorAddress, ELEVATOR_PORT);

        try {
            System.out.println("Sending Packet to elevator:");
            System.out.println(Arrays.toString(elevatorData) + "\n");
            elevatorSocket.send(elevatorSendPacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Elevator Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Packet sent to elevator.\n");

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
            System.out.println("Waiting for Packet from Elevator...\n");
            elevatorSocket.receive(elevatorReceivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Elevator Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Packet received from Elevator" +""+elevatorSocket.toString()+"\n");

        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_PORT);

        try {
            System.out.println("Sending Packet to floor details ="+""+ floorSendPacket.toString()+"\n");
            floorSocket.send(floorSendPacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Floor Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Packet sent to floor\n");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
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
