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
    private DatagramPacket floorSendPacket, floorReceivePacket, elevatorSendPacket, elevatorReceivePacket;
    private InetAddress floorAddress, elevatorAddress;
    private DatagramSocket floorSocket, elevatorSocket;
    private static final int FLOOR_PORT = 20, ELEVATOR_PORT = 21;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        queue = new HashMap<>();

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
