import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.*;
import java.time.LocalTime;
import java.util.*;

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
    private States floorMessagingState, elevatorMessagingState;
    private byte[] floorData, elevatorData;
    private final HashMap<int[], Timer> elevatorsInfoAndTimers;
    private static final int FLOOR_RECEIVING_PORT = 2000, FLOOR_SENDING_PORT = 2300, ELEVATOR_SENDING_PORT = 2100, ELEVATOR_RECEIVING_PORT = 2200, ELEVATOR_REPLY_PORT = 2400;

    /**
     * Initializes the controller.
     */
    public Scheduler() {
        floorMessagingState = States.IDLE;
        elevatorMessagingState = States.IDLE;
        floorData = new byte[3];
        elevatorData = new byte[4];
        elevatorsInfoAndTimers = new HashMap<>();
        try {
            floorReceivingSocket = new DatagramSocket(FLOOR_RECEIVING_PORT);
            floorSendingSocket = new DatagramSocket();
            elevatorReceivingSocket = new DatagramSocket(ELEVATOR_RECEIVING_PORT);
            elevatorSendingSocket = new DatagramSocket();
            floorAddress = InetAddress.getLocalHost();
            elevatorAddress = InetAddress.getLocalHost();
        } catch (SocketException| UnknownHostException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            elevatorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    private int getDatagramStateValue(States state) {
        if(state == States.IDLE)
            return 0;
        else if(state == States.GOING_UP)
            return 1;
        else if(state == States.GOING_DOWN)
            return 2;
        else if(state == States.OUT_OF_SERVICE)
            return 503;
        else
            return 404;
    }

    public void addElevator(Elevator elevator) {
        int state = getDatagramStateValue(elevator.getStates());

        elevatorsInfoAndTimers.put(new int[] {elevator.getElevatorNum(), elevator.getCurrentFloor(), state}, new Timer());
    }

    private void updateElevatorInfo(int elevatorNum, int floorNum, int state) {
        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if(arr[0] == elevatorNum) {
                arr[1] = floorNum;
                arr[2] = state;
                break;
            }
        }
    }

    private void handleElevatorMessagingState() {
        switch(elevatorMessagingState) {
            case IDLE -> {
                elevatorData = receiveTask();
            }
            case SCHEDULING -> {
                elevatorData[3] = scheduleElevator(elevatorData);
            }
            case SENDING_TASK -> {
                sendToElevator(elevatorData);
            }
            case OUT_OF_SERVICE -> {
                printAnalyzedState();
            }
        }
    }

    private void handleFloorMessagingState() {
        switch(floorMessagingState) {
            case IDLE -> {
                floorData = receiveMessage();
            }
            case HANDLING_RECEIVED_MESSAGE -> {
                handleElevatorMessage(floorData);
            }
            case SENDING_MESSAGE -> {
                sendToFloor(floorData);
            }
        }
    }

    private void handleElevatorMessage(byte[] data) {
        if(data[1] == (byte) 1) {
            startFaultDetection(data[3], Elevator.MOTOR_TIME, false);
            floorData = Arrays.copyOfRange(data, 2, 6);
            floorMessagingState = States.SENDING_MESSAGE;
            updateElevatorInfo(data[3], data[2], data[0]);
        }
        else if(data[1] == (byte) 2)
            startFaultDetection(data[3], Elevator.MAX_DOOR_HOLD_TIME, false);
        else if(data[1] == 3)
            startFaultDetection(data[3], Elevator.MOTOR_TIME, false);
        else if(data[1] == 4)
            startFaultDetection(data[3], Elevator.TRAVEL_TIME, true);
        else if(data[1] == 0)
            stopTimer(data[3]);
        if((data[0] == getDatagramStateValue(States.GOING_UP)) || (data[0] == getDatagramStateValue(States.GOING_DOWN)))
            startFaultDetection(data[3], Elevator.TRAVEL_TIME, true);
        else if(data[0] == getDatagramStateValue(States.IDLE))
            stopTimer(data[3]);
    }

    private byte[] receiveTask() {
        byte[] data = new byte[4];
        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Floor...\n");
            floorReceivingSocket.receive(floorReceivePacket);
            replyFloorMessage("Correctly received message packet.");
        } catch (IOException e) {
            elevatorMessagingState = States.OUT_OF_SERVICE;
            replyFloorMessage("Received an incorrect packet.");
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Floor " + ((int) data[0]) + ": " + Arrays.toString(data) + ".\n");
        elevatorMessagingState = States.SCHEDULING;

        return data;
    }

    public byte scheduleElevator(byte[] data) {
        int elevatorNum = ((int[]) elevatorsInfoAndTimers.keySet().toArray()[0])[0];
        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if ((Math.abs(arr[1] - ((int) data[0]))
                    < Math.abs(arr[1] - ((int) data[0])))
                    && ((arr[2] == data[1])
                    || (arr[2] == 0)))
                elevatorNum = arr[0];
        }

        elevatorMessagingState = States.SENDING_TASK;
        return (byte) elevatorNum;
    }

    /**
     * Method to send data to the elevator and update the scheduler's state.
     */
    public void sendToElevator(byte[] data) {
        elevatorSendPacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_SENDING_PORT);

        try {
            System.out.println("SCHEDULER: Sending Packet to elevator: " + Arrays.toString(data) + "\n");
            elevatorSendingSocket.send(elevatorSendPacket);
        } catch (IOException e) {
            elevatorMessagingState = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to elevator.\n");
        elevatorMessagingState = States.IDLE;

        updateElevatorInfo(data[3], data[0], data[1]);
    }

    private void startFaultDetection(int elevatorNum, int time, boolean floorOrDoorFault) {
        int[] a = null;

        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if(arr[0] == elevatorNum) {
                a = arr;
                break;
            }
        }
        elevatorsInfoAndTimers.replace(a, new Timer());

        elevatorsInfoAndTimers.get(a).schedule(new TimerTask() {
            @Override
            public void run() {
                timeout(elevatorNum, floorOrDoorFault);
            }
        },   time + 1000);
    }

    private void stopTimer(int elevatorNum) {
        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if(arr[0] == elevatorNum) {
                elevatorsInfoAndTimers.get(arr).cancel();
                break;
            }
        }
    }

    private void timeout(int elevatorNum, boolean floorOrDoorFault) {
        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if(arr[0] == elevatorNum) {
                if(floorOrDoorFault) {
                    arr[2] = 503;
                    sendToElevator(new byte[] {0, (byte) 503, (byte) arr[0]});
                }
                else
                    sendToElevator(new byte[] {(byte) arr[1], (byte) 504, (byte) arr[0]});
                break;
            }
        }
    }

    private byte[] receiveMessage() {
        byte[] data = new byte[5];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Elevator...\n");
            elevatorReceivingSocket.receive(elevatorReceivePacket);
            replyElevatorMessage("Correctly received message packet.");
        } catch (IOException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            replyElevatorMessage("Received an incorrect packet.");
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Elevator: " + Arrays.toString(data) + ".\n");
        floorMessagingState = States.HANDLING_RECEIVED_MESSAGE;

        return data;
    }

    /**
     * Reply to Elevator's message acknowledging its receipt.
     * @param reply String message saying if the packet was correct or not.
     */
    private void replyElevatorMessage(String reply) {
        ByteArrayOutputStream answer = new ByteArrayOutputStream();
        elevatorSendPacket = new DatagramPacket(answer.toByteArray(), answer.toByteArray().length, elevatorAddress, ELEVATOR_SENDING_PORT);

        try {
            System.out.println("SCHEDULER: Sending reply packet to Elevator.");
            elevatorSendingSocket.send(elevatorSendPacket);
            answer.write(reply.getBytes());
        } catch (IOException e) {
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Method to send data to the floor and update the scheduler's state.
     */
    public void sendToFloor(byte[] data) {
        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_SENDING_PORT);

        try {
            System.out.println("SCHEDULER: Sending Packet to Floor: " + Arrays.toString(data)+".\n");
            floorSendingSocket.send(floorSendPacket);
        } catch (IOException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet sent to floor.\n");
        floorMessagingState = States.IDLE;
        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            floorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Reply to Floor's message acknowledging its receipt.
     * @param reply String message saying if the packet was correct or not.
     */
    private void replyFloorMessage(String reply) {
        ByteArrayOutputStream answer = new ByteArrayOutputStream();

        try {
            answer.write(reply.getBytes());
        } catch (IOException e) {
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        floorSendPacket = new DatagramPacket(answer.toByteArray(), answer.toByteArray().length, floorAddress, FLOOR_SENDING_PORT);

        try {
            System.out.println("SCHEDULER: Sending reply packet to Floor.");
            floorSendingSocket.send(floorSendPacket);

        } catch (IOException e) {
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }


    }


    /**
     * Prints the current state of the scheduler.
     */
    public void printAnalyzedState() {
        System.out.print("SCHEDULER: The scheduler-elevator system is " + elevatorMessagingState.toString().toLowerCase().replace('_', ' ') + ".\n");
        System.out.print("SCHEDULER: The scheduler-floor system is " + floorMessagingState.toString().toLowerCase().replace('_', ' ') + ".\n");
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            elevatorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        Thread elevatorHandlingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    handleElevatorMessagingState();
            }
        });

        Thread floorHandlingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true)
                    handleFloorMessagingState();
            }
        });

        elevatorHandlingThread.start();
        floorHandlingThread.start();
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
