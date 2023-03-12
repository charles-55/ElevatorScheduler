import java.io.IOException;
import java.net.*;
import java.util.Arrays;

/**
 * The Scheduler Class.
 * Connects the elevators to the floor. It calls an elevator to a floor
 * and adds the elevator to a queue when there is work to be done.
 *
 * @author Osamudiamen Nwoko 101152520
 * @author Leslie Ejeh 101161386
 * @author Nicholas Thibault
 * @version 1.0
 */
public class Scheduler extends Thread {

    private DatagramPacket floorSendPacket, floorReceivePacket, elevatorSendPacket, elevatorReceivePacket;
    private InetAddress floorAddress, elevatorAddress;
    private DatagramSocket floorReceivingSocket, floorSendingSocket, elevatorReceivingSocket, elevatorSendingSocket;
    private States state;
    private static final int FLOOR_RECEIVING_PORT = 2000, FLOOR_SENDING_PORT = 2300, ELEVATOR_SENDING_PORT = 2100, ELEVATOR_RECEIVING_PORT = 2200;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        try {
            state = States.IDLE;
            floorReceivingSocket = new DatagramSocket(FLOOR_RECEIVING_PORT);
            floorSendingSocket = new DatagramSocket();
            elevatorReceivingSocket = new DatagramSocket(ELEVATOR_RECEIVING_PORT);
            elevatorSendingSocket = new DatagramSocket();
            floorAddress = InetAddress.getLocalHost();
            elevatorAddress = InetAddress.getLocalHost();
        } catch (SocketException| UnknownHostException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Method to send data to the elevator and update the scheduler's state.
     */
    public void sendToElevator() {
        byte[] data = new byte[3];
        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Floor...\n");
            floorReceivingSocket.receive(floorReceivePacket);
            state = States.RECEIVING_TASK;
        } catch (IOException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Floor " + ((int) data[0]) + ": " + Arrays.toString(data) + ".\n");

        elevatorSendPacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_SENDING_PORT);

        try {

            System.out.println("SCHEDULER: Sending Packet to elevator: " + Arrays.toString(data) + "\n");
            elevatorSendingSocket.send(elevatorSendPacket);
            state = States.SENDING_TASK;
        } catch (IOException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to elevator.\n");
        state = States.IDLE;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Method to send data to the floor and update the scheduler's state.
     */
    public void sendToFloor() {
        byte[] data = new byte[4];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Elevator...\n");
            elevatorReceivingSocket.receive(elevatorReceivePacket);
            state = States.RECEIVING_MESSAGE;
        } catch (IOException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Elevator: " + Arrays.toString(data) + ".\n");

        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_SENDING_PORT + ((int) data[1]));

        try {
            System.out.println("SCHEDULER: Sending Packet to Floor: " + Arrays.toString(data)+".\n");
            floorSendingSocket.send(floorSendPacket);
            state = States.SENDING_MESSAGE;
        } catch (IOException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to floor.\n");
        state = States.IDLE;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prints the current state of the scheduler.
     */
    public void printAnalyzedState() {
        System.out.print("SCHEDULER: ");

        if(state.equals(States.IDLE)) {
            System.out.println("The scheduler is idle.\n");
        } else if(state.equals(States.RECEIVING_TASK)) {
            System.out.println("The scheduler is receiving a task.\n");
        } else if(state.equals(States.SENDING_TASK)) {
            System.out.println("The scheduler is sending a task.\n");
        } else if(state.equals(States.RECEIVING_MESSAGE)) {
            System.out.println("The scheduler is receiving a message.\n");
        } else if(state.equals(States.SENDING_MESSAGE)) {
            System.out.println("The scheduler is sending a message.\n");
        } else {
            throw new RuntimeException("Error: The scheduler is in an unknown state.\n");
        }
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            state = States.OUT_OF_SERVICE;
            printAnalyzedState();
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
     * Closes the socket.
     */
    public void closeSocket() {
        floorSendingSocket.close();
        floorReceivingSocket.close();
        elevatorSendingSocket.close();
        elevatorReceivingSocket.close();
    }

}
