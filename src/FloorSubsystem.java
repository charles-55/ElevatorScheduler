import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    private Frame frame;
    private final ArrayList<Floor> floors;
    private final HashMap<Integer, int[]> elevatorInfo; // map elevatorNum to [currentFloor, direction, state]
    private DatagramPacket sendPacket, receivePacket, replyPacket;
    private DatagramSocket sendSocket, receiveSocket, sendReplySocket, receiveReplySocket;
    private InetAddress address;
    private static final int SEND_PORT = 2000, RECEIVE_PORT = 2100, SEND_REPLY_PORT = 2110, RECEIVE_REPLY_PORT = 2120;

    /**
     * Initialize the FloorSubsystem.
     */
    public FloorSubsystem() {
        floors = new ArrayList<>();
        elevatorInfo = new HashMap<>();

        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++)
            new Floor(i + 1, this);
        for(int i = 0; i < Elevator.NUM_OF_ELEVATORS; i++)
            elevatorInfo.put(i + 1, new int[] {1, 0, 0});

        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(RECEIVE_PORT);
            sendReplySocket = new DatagramSocket();
            receiveReplySocket = new DatagramSocket(RECEIVE_REPLY_PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void setFrame(Frame frame) {
        this.frame = frame;

        Thread refreshThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    frame.refresh();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public HashMap<Integer, int[]> getElevatorInfo() {
        return elevatorInfo;
    }

    public void callElevator(int floorNum, int direction) {
        sendToScheduler(new byte[] {(byte) floorNum, (byte) direction, 0}, false);
    }

    public void elevatorPress(int floorNum, int elevatorNum) {
        sendToScheduler(new byte[] {(byte) floorNum, 0, (byte) elevatorNum}, false);
    }

    private void sendToScheduler(byte[] data, boolean reply) {
        sendPacket = new DatagramPacket(data, data.length, address, reply ? SEND_REPLY_PORT : SEND_PORT);

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("FLOOR SUBSYSTEM: Send Packet Error: " + Arrays.toString(data) + "\n");
            throw new RuntimeException(e);
        }

        if(reply)
            System.out.println("FLOOR SUBSYSTEM: Reply sent to scheduler.\n");
        else {
            System.out.println("FLOOR SUBSYSTEM: Sent Packet: " + Arrays.toString(data) + "\n");
            receiveReply();
        }
    }

    private void receiveFromScheduler() {
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

        sendToScheduler(new byte[] {data[0], 0, 0}, true); // send reply

        updateElevatorInfo(data);
        updateFrame(data);

        if(!updateFloor(data[0], data[1]))
            System.out.println("FLOOR SUBSYSTEM: Failed to update floor!\n");
    }

    private void receiveReply() {
        byte[] data = new byte[4];
        replyPacket = new DatagramPacket(data, data.length, address, RECEIVE_REPLY_PORT);

        try {
            receiveReplySocket.receive(replyPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("FLOOR SUBSYSTEM: Reply received for floor " + data[0] + ".\n");
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

    private void updateFrame(byte[] data) {
        frame.updateUI(data[2], data[0], States.getDatagramValueState(data[3]));
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
        sendSocket.close();
        receiveSocket.close();
        sendReplySocket.close();
        receiveReplySocket.close();
    }
}
