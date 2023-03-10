import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
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

    private DatagramPacket floorSendPacket, floorReceivePacket, elevatorSendPacket, elevatorReceivePacket;
    private InetAddress floorAddress, elevatorAddress;
    private DatagramSocket floorReceivingSocket, floorSendingSocket, elevatorReceivingSocket, elevatorSendingSocket;
    private static final int FLOOR_RECEIVING_PORT = 2000, FLOOR_SENDING_PORT = 2300, ELEVATOR_SENDING_PORT = 2100, ELEVATOR_RECEIVING_PORT = 2200;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        try {
            floorReceivingSocket = new DatagramSocket(FLOOR_RECEIVING_PORT);
            floorSendingSocket = new DatagramSocket();
            elevatorReceivingSocket = new DatagramSocket(ELEVATOR_RECEIVING_PORT);
            elevatorSendingSocket = new DatagramSocket();
            floorAddress = InetAddress.getLocalHost();
            elevatorAddress = InetAddress.getLocalHost();
        } catch (SocketException| UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Method to send data to the elevator
     */
    public void sendToElevator() {
        byte[] data = new byte[3];
        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Floor...\n");
            floorReceivingSocket.receive(floorReceivePacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Floor Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Floor " + ((int) data[0]) + ": " + Arrays.toString(data) + ".\n");

        elevatorSendPacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_SENDING_PORT);

        try {
            System.out.println("SCHEDULER: Sending Packet to elevator: " + Arrays.toString(data) + "\n");
            elevatorSendingSocket.send(elevatorSendPacket);
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

    /**
     * Method to send data to the floor.
     */
    public void sendToFloor() {
        byte[] data = new byte[4];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Elevator...\n");
            elevatorReceivingSocket.receive(elevatorReceivePacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Elevator Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Elevator: " + Arrays.toString(data) + ".\n");

        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_SENDING_PORT + ((int) data[1]));

        try {
            System.out.println("SCHEDULER: Sending Packet to Floor: " + Arrays.toString(data)+".\n");
            floorSendingSocket.send(floorSendPacket);
        } catch (IOException e) {
            System.out.println("SCHEDULER Error: Floor Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to floor.\n");

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

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    sendToElevator();
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    sendToFloor();
            }
        });

        thread1.start();
        thread2.start();
    }

    /**
     * Closes the socket
     */
    public void closeSocket() {
        floorSendingSocket.close();
        floorReceivingSocket.close();
        elevatorSendingSocket.close();
        elevatorReceivingSocket.close();
    }
}
