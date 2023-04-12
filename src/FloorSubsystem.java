import java.io.File;
import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

/**
 * The FloorSubsystem Class.
 * Client in system
 * To read events in the format: Time, floor, floor direction, and elevator button,
 * Each line of input is to be sent to the Scheduler
 *
 * @author Sabah Samwatin
 * @version 1.0
 * */

public class FloorSubsystem extends Thread {

    private final ArrayList<Floor> floors;
    private final HashMap<Integer, int[]> elevatorInfo; // map elevatorNum to [currentFloor, direction, state]
    private States state, parseState, receiveState;
    private DatagramPacket sendPacket, receivePacket, replyPacket;
    private DatagramSocket sendSocket, receiveSocket, replySocket;
    private InetAddress address;
    private static final int SEND_PORT = 2000, RECEIVE_PORT = 2100, REPLY_PORT = 2110;

    /**
     * Initialize the FloorSubsystem.
     */
    public FloorSubsystem() {
        floors = new ArrayList<>();
        elevatorInfo = new HashMap<>();

        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevatorInfo.put(i + 1, new int[3]);

        state = States.IDLE;
        parseState = States.IDLE;
        receiveState = States.IDLE;

        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket();
            replySocket = new DatagramSocket(REPLY_PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void addFloor(Floor floor){
        floors.add(floor);
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public HashMap<Integer, int[]> getElevatorInfo() {
        return elevatorInfo;
    }

    public void callElevator(int floorNum, Integer direction) {
        sendToScheduler(new byte[] {(byte) floorNum, direction.byteValue()});
        receiveReply();
    }

    public synchronized void sendToScheduler(byte[] data) {
        sendPacket = new DatagramPacket(data, data.length, address, SEND_PORT);

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Send Packet Error: " + Arrays.toString(data) + "\n");
            throw new RuntimeException(e);
        }
        System.out.println("FLOOR SUBSYSTEM: Sent Packet: " + Arrays.toString(data) + "\n");

        receiveReply();
    }

    private synchronized void receiveFromScheduler() {
        byte[] data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length, address, RECEIVE_PORT);

        try {
            System.out.println("FLOOR SUBSYSTEM: Waiting for Packet...\n");
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Error Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("FLOOR SUBSYSTEM: Packet received: " + Arrays.toString(data) + "\n");
        System.out.println("FLOOR " + data[0] + ": Elevator " + data[2] + " arrived.\n");

        updateElevatorInfo(data);
        // update frame

        if(!updateFloor(data[0], data[2]))
            System.out.println("FLOOR SUBSYSTEM: Failed to update floor!\n");
    }

    private synchronized void receiveReply() {
        byte[] data = new byte[3];
        replyPacket = new DatagramPacket(data, data.length, address, REPLY_PORT);

        try {
            replySocket.receive(replyPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(data[1] == 0) {
            String direction = "";
            if(data[2] == 1)
                direction = "up";
            else if(data[2] == 2)
                direction = "down";
            System.out.println("FLOOR SUBSYSTEM: Reply received to floor " + data[0] + " going " + direction + ".\n");
        }

        state = States.IDLE;
    }

    private boolean updateFloor(int floorNumber, int direction) {
        if(!((direction == 1) || (direction == 2)))
            return false;

        for(Floor floor : floors) {
            if(floor.getFloorNumber() == floorNumber) {
                floor.setButtonDirection(direction, false);
                return true;
            }
        }

        return false;
    }

    private void updateElevatorInfo(byte[] data) {
        for(Integer elevatorNum : elevatorInfo.keySet()) {
            if(elevatorNum == data[2]) {
                elevatorInfo.put(elevatorNum, new int[] {data[0], data[1], data[3]});
                return;
            }
        }
    }

    private void updateFrame() {}

    /**
     * Run method.
     */
    @Override
    public void run() {
        Thread receiveMessageThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    receiveFromScheduler();
            }
        });

        receiveMessageThread.start();
    }

    /**
     * Close the Datagram Sockets.
     */
    public void closeSocket() {
        sendSocket.close();
        receiveSocket.close();
        replySocket.close();
    }
}
