import java.io.IOException;
import java.net.*;
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

    private DatagramPacket floorSendPacket, floorReceivePacket, floorReplyPacket, elevatorSendPacket, elevatorReceivePacket, elevatorReplyPacket;
    private InetAddress floorAddress, elevatorAddress;
    private DatagramSocket floorReceivingSocket, floorSendingSocket, floorReceiveReplySocket, floorSendReplySocket, elevatorReceivingSocket, elevatorSendingSocket, elevatorReceiveReplySocket, elevatorSendReplySocket;
    private States floorMessagingState, elevatorMessagingState;
    private byte[] floorData, elevatorData;
    private final HashMap<int[], Timer> elevatorsInfoAndTimers; // map [elevatorNum, floorNum, state] to timers
    private static final int FLOOR_RECEIVING_PORT = 2000, FLOOR_SENDING_PORT = 2100, FLOOR_RECEIVE_REPLY_PORT = 2110, FLOOR_SEND_REPLY_PORT = 2120, ELEVATOR_SENDING_PORT = 2200, ELEVATOR_RECEIVING_PORT = 2300, ELEVATOR_SEND_REPLY_PORT = 2400, ELEVATOR_RECEIVE_REPLY_PORT = 2500;

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
            floorReceiveReplySocket = new DatagramSocket(FLOOR_RECEIVE_REPLY_PORT);
            floorSendReplySocket = new DatagramSocket();

            elevatorReceivingSocket = new DatagramSocket(ELEVATOR_RECEIVING_PORT);
            elevatorSendingSocket = new DatagramSocket();
            elevatorReceiveReplySocket = new DatagramSocket(ELEVATOR_RECEIVE_REPLY_PORT);
            elevatorSendReplySocket = new DatagramSocket();

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

    public void addElevator(Elevator elevator) {
        int state = States.getStateDatagramValue(elevator.getStates());

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
                elevatorData = receiveFromFloorSubsystem();
            }
            case SENDING_TASK -> {
                if(elevatorData[1] != 0)
                    sendToElevator(elevatorData, scheduleElevator(elevatorData), false);
                else
                    sendToElevator(elevatorData, elevatorData[2], false);
            }
            case OUT_OF_SERVICE -> {
                printAnalyzedState();
            }
        }
    }

    /**
     * Decides what to do with the message gotten from the floorsubsystem
     */
    private void handleFloorMessagingState() {
        switch(floorMessagingState) {
            case IDLE -> {
                floorData = receiveFromElevator();
            }
            case HANDLING_RECEIVED_MESSAGE -> {
                handleElevatorMessage(floorData);
            }
            case SENDING_MESSAGE -> {
                sendToFloor(floorData, false);
            }
            case OUT_OF_SERVICE -> {
                printAnalyzedState();
            }
        }
    }

    /**
     * Assigns task to each elevator accordingly
     * @param data
     * @return
     */
    public int scheduleElevator(byte[] data) {
        int elevatorNum = ((int[]) elevatorsInfoAndTimers.keySet().toArray()[0])[0];
        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if ((Math.abs(arr[1] - ((int) data[0]))
                    < Math.abs(arr[1] - ((int) data[0])))
                    && ((arr[2] == data[1])
                    || (arr[2] == 0)))
                elevatorNum = arr[0];
        }

        elevatorMessagingState = States.SENDING_TASK;
        return elevatorNum;
    }

    /**
     * Decides what to do with the message gotten from the elevator
     * @param data takes in a parameter to decrypt the message received
     */
    private void handleElevatorMessage(byte[] data) {
        if(data[4] == (byte) 1) { // if opening door
            startFaultDetection(data[2], Elevator.MOTOR_TIME, false);
            floorData = new byte[] {data[0], data[1], data[2], (byte) States.getStateDatagramValue(States.DOOR_OPEN)}; // update the floor subsystem
            floorMessagingState = States.SENDING_MESSAGE;
            updateElevatorInfo(data[2], data[0], data[3]);
            return;
        }
        else if(data[4] == (byte) 2) // if holding door
            startFaultDetection(data[2], Elevator.MAX_DOOR_HOLD_TIME, false);
        else if(data[4] == 3) // if closing door
            startFaultDetection(data[2], Elevator.MOTOR_TIME, false);
        else if(data[4] == 4) { // if elevator is moving
            startFaultDetection(data[2], Elevator.TRAVEL_TIME, true);
            floorData = new byte[] {data[0], data[1], data[2], data[3]}; // update floor subsystem
            floorMessagingState = States.SENDING_MESSAGE;
            return;
        }
        else if(data[4] == 0) { // if door closed and all task complete
            stopTimer(data[3]);
            floorData = new byte[] {data[0], data[1], data[2], data[3]}; // update floor subsystem
            floorMessagingState = States.SENDING_MESSAGE;
            return;
        }
//        if((data[3] == States.getStateDatagramValue(States.GOING_UP)) || (data[3] == States.getStateDatagramValue(States.GOING_DOWN)))
//            startFaultDetection(data[2], Elevator.TRAVEL_TIME, true);
//        else if(data[3] == States.getStateDatagramValue(States.IDLE))
//            stopTimer(data[2]);
        floorMessagingState = States.IDLE;
    }

    /**
     * Method to send data to the elevator and update the scheduler's state.
     */
    public void sendToElevator(byte[] data, int elevatorNum, boolean reply) {
        data = Arrays.copyOfRange(data, 0, data.length - 1);
        elevatorSendPacket = new DatagramPacket(data, data.length, elevatorAddress, (reply ? ELEVATOR_SEND_REPLY_PORT : ELEVATOR_SENDING_PORT) + elevatorNum);

        try {
            elevatorSendingSocket.send(elevatorSendPacket);
        } catch (IOException e) {
            elevatorMessagingState = States.OUT_OF_SERVICE;
            e.printStackTrace();
            System.exit(1);
        }

        if(reply)
            System.out.println("SCHEDULER: Reply sent to elevator " + elevatorNum + ".\n");
        else {
            System.out.println("SCHEDULER: Packet sent to elevator " + elevatorNum + ": " + Arrays.toString(data) + "\n");
            receiveElevatorReply(elevatorNum);
            elevatorMessagingState = States.IDLE;
            updateElevatorInfo(elevatorNum, data[0], data[1]);
        }
    }

    /**
     * Method to send data to the floor and update the scheduler's state.
     */
    public void sendToFloor(byte[] data, boolean reply) {
        floorSendPacket = new DatagramPacket(data, data.length, floorAddress, reply ? FLOOR_SEND_REPLY_PORT : FLOOR_SENDING_PORT);

        try {
            floorSendingSocket.send(floorSendPacket);
        } catch (IOException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        if(reply)
            System.out.println("SCHEDULER: Reply sent to floor " + data[0] + ".\n");
        else {
            System.out.println("SCHEDULER: Packet sent to floor: " + Arrays.toString(data) + "\n");
            receiveFloorSubsystemReply();
            floorMessagingState = States.IDLE;
        }
    }

    private byte[] receiveFromElevator() {
        byte[] data = new byte[5];
        elevatorReceivePacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Elevator...\n");
            elevatorReceivingSocket.receive(elevatorReceivePacket);
        } catch (IOException e) {
            floorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet Received from Elevator: " + Arrays.toString(data) + ".\n");

        sendToElevator(new byte[2], data[2], true); // send reply

        floorMessagingState = States.HANDLING_RECEIVED_MESSAGE;

        return data;
    }

    /**
     * Waits to receive packet then passes the packet to the reply method to respond
     * @return
     */
    private byte[] receiveFromFloorSubsystem() {
        byte[] data = new byte[3];
        floorReceivePacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_RECEIVING_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for Packet from Floor...\n");
            floorReceivingSocket.receive(floorReceivePacket);
        } catch (IOException e) {
            elevatorMessagingState = States.OUT_OF_SERVICE;
            printAnalyzedState();
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Packet received from floor " + ((int) data[0]) + ": " + Arrays.toString(data) + ".\n");

        sendToFloor(new byte[] {data[0], 0, 0, 0}, true); // reply floor
        elevatorMessagingState = States.SENDING_TASK;
        return data;
    }

    private void receiveElevatorReply(int elevatorNum) {
        byte[] data = new byte[5];
        elevatorReplyPacket = new DatagramPacket(data, data.length, elevatorAddress, ELEVATOR_RECEIVE_REPLY_PORT + elevatorNum);

        try {
            elevatorReceiveReplySocket.receive(elevatorReplyPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //System.out.println("Test");

        System.out.println("SCHEDULER: Received reply from elevator " + elevatorNum + ".\n");
    }

    private void receiveFloorSubsystemReply() {
        byte[] data = new byte[2];
        floorReplyPacket = new DatagramPacket(data, data.length, floorAddress, FLOOR_RECEIVE_REPLY_PORT);

        try {
            System.out.println("SCHEDULER: Waiting for reply from Floor Subsystem...\n");
            floorReceiveReplySocket.receive(floorReplyPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("SCHEDULER: Received reply from floor " + data[0] + ".\n");
    }

    private void startFaultDetection(int elevatorNum, int time, boolean floorOrDoorFault) {
        int[] a = null;

        for(int[] arr : elevatorsInfoAndTimers.keySet()) {
            if(arr[0] == elevatorNum) {
                elevatorsInfoAndTimers.get(arr).cancel();
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
                    arr[2] = 103;
                    sendToElevator(new byte[] {0, (byte) 103}, arr[0], false);
                }
                else
                    sendToElevator(new byte[] {(byte) arr[1], (byte) 104}, arr[0], false);
                break;
            }
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
        floorSendReplySocket.close();
        floorReceiveReplySocket.close();

        elevatorSendingSocket.close();
        elevatorReceivingSocket.close();
        elevatorSendReplySocket.close();
        elevatorReceiveReplySocket.close();
    }
}
