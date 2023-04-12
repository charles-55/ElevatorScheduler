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
    private HashMap<Integer, int[]> elevatorInfo;
    private States state, parseState, receiveState;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int SEND_PORT = 2000, RECEIVE_PORT = 2100, REPLY_PORT = 2110;

    /**
     * Initialize the FloorSubsystem.
     */
    public FloorSubsystem() {
        floors = new ArrayList<>();
        elevatorInfo = new HashMap<>();

        state = States.IDLE;
        parseState = States.IDLE;
        receiveState = States.IDLE;

        try {
            socket = new DatagramSocket();
            this.address = InetAddress.getLocalHost();
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

    public void callElevator(int floorNum) {
        sendToScheduler(new byte[] {});
    }

    public synchronized void sendToScheduler(byte[] data) {
        sendPacket = new DatagramPacket(data, data.length, address, SEND_PORT);

        try {
            socket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Send Packet Error: " + Arrays.toString(data) + "\n");
            throw new RuntimeException(e);
        }
        System.out.println("FLOOR SUBSYSTEM: Sent Packet: " + Arrays.toString(data) + "\n");

        receiveReply();
    }

    private synchronized void receiveReply() {
        byte[] data = new byte[3];
        DatagramPacket replyPacket = new DatagramPacket(data, data.length, address, REPLY_PORT);
        DatagramSocket replySocket;

        try {
            replySocket = new DatagramSocket(REPLY_PORT);
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

        replySocket.close();
        state = States.IDLE;
    }

    private synchronized void receiveFromScheduler() {
        byte[] data = new byte[3];
        receivePacket = new DatagramPacket(data, data.length, address, RECEIVE_PORT);

        try {
            System.out.println("FLOOR SUBSYSTEM: Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Error Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("FLOOR SUBSYSTEM: Packet received: " + Arrays.toString(data) + "\n");

        System.out.println("FLOOR " + data[0] + ": Elevator " + data[1] + " arrived.\n");
        if (data[2] == 1)
            updateFloor(data[0], 1);
        else if (data[2] == 2)
            updateFloor(data[0], 2);

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void updateFloor(int floorNumber, int direction) {
        for(Floor floor:floors) {
            if(floor.getFloorNumber() == floorNumber) {
                floor.setButtonDirection(direction, false);
                break;
            }
        }
    }

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
        socket.close();
    }
}
