import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The Elevator Class.
 * Moves between floors based on the data sent from the floor to the scheduler.
 *
 * @author Nicholas Thibault 101172413
 * @version 1.0
 */
public class Elevator extends Thread {

    private final int elevatorNum;
    private int currentFloor;
    private int direction;
    private boolean doorOpen;
    private boolean isMoving;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private States state;
    private DatagramPacket sendPacket, receivePacket, replyPacket;
    private DatagramSocket sendSocket, receiveSocket, sendReplySocket, receiveReplySocket;
    private InetAddress address;

    public static final int MOTOR_TIME = 3000, DOOR_HOLD_TIME = 5000, MAX_DOOR_HOLD_TIME = 10000, TRAVEL_TIME = 4000;
    private static final int SEND_PORT = 2300, RECEIVE_PORT = 2200, SEND_REPLY_PORT = 2500, RECEIVE_REPLY_PORT = 2400;
    public static final int NUM_OF_ELEVATORS = 4;

    /**
     * Constructor method for Elevator initializing the elevator on the first floor.
     * @param elevatorNum int identifying elevator number.
     * @param numOfFloors int number of floors in the building.
     */
    public Elevator(int elevatorNum, int numOfFloors, Scheduler scheduler) {
        this.elevatorNum = elevatorNum;
        this.currentFloor = 1;
        direction = 0;

        doorOpen = false;
        isMoving = false;
        state = States.IDLE;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);

        scheduler.addElevator(this);

        try {
            sendSocket = new DatagramSocket();
            receiveSocket = new DatagramSocket(RECEIVE_PORT + elevatorNum);
            sendReplySocket = new DatagramSocket();
            receiveReplySocket = new DatagramSocket(RECEIVE_REPLY_PORT + elevatorNum);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Get the elevator number.
     * @return int, the elevator number.
     */
    public int getElevatorNum() {
        return elevatorNum;
    }

    /**
     * Getter method for the currentFloor attribute.
     * @return int currentFloor the current floor
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    /**
     * Setter method for the currentFloor attribute.
     * @param currentFloor int the current floor
     */
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    /**
     * Getter method for the current state of the doors
     * @return boolean doorOpen (open if true, closed if false)
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Getter method for the current state of the movement of the elevator
     * @return boolean isMoving (moving if true, stopped if false)
     */
    public boolean isMoving() {
        return isMoving;
    }

    private int getMovingDirection(int targetFloor) {
        if(currentFloor < targetFloor)
            return 1;
        else if(currentFloor > targetFloor)
            return 2;
        else
            return -1;
    }

    public States getStates() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    /**
     * Checks if all the task are complete and updates the state
     */
    private void checkForStateUpdate() {
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            if(buttonsAndLamps.get(i + 1))
                return;
        }
        state = States.IDLE;
    }

    private void checkDirectionalTaskUpdate() {
        switch (direction) {
            case 0 -> {
                for (int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
                    if (buttonsAndLamps.get(i + 1)) {
                        direction = getMovingDirection(i + 1);
                        return;
                    }
                }
            }
            case 1 -> {
                for (int i = currentFloor - 1; i < Floor.NUM_OF_FLOORS; i++) {
                    if(buttonsAndLamps.get(i + 1))
                        return;
                }
                direction = 0;
            }
            case 2 -> {
                for (int i = currentFloor - 1; i > 0; i--) {
                    if(buttonsAndLamps.get(i + 1))
                        return;
                }
                direction = 0;
            }
        }
    }

    /**
     * Getter method for the HashMap of buttons and lamps related to each button.
     * @return HashMap<Integer, Boolean> buttonsAndLamps hashmap of the floor number buttons and state of the lamps (on if true, off if false)
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     * Open and close the doors.
     */
    public void openDoors() {
        if(!this.isMoving && !doorOpen) {
            try {
                Thread.sleep(MOTOR_TIME); // Arbitrary time for doors to open // implement open door using motors
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.doorOpen = true;
        }
    }

    /**
     * Close the door of the elevator.
     */
    public void closeDoors() {
        if(!this.isMoving && doorOpen) {
            try {
                Thread.sleep(MOTOR_TIME); // Arbitrary time for doors to close // implement close door using motors
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.doorOpen = false;
        }

    }

    /**
     * Starts the fault detection for each state the elevator class moves into
     */

    private void handleState() {
        switch(state) {
            case IDLE -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            case GOING_UP, GOING_DOWN -> {
                checkDirectionalTaskUpdate();
                move();
                if(buttonsAndLamps.get(currentFloor)) {
                    if(state == States.GOING_UP)
                        arrivalSequence(1);
                    else if(state == States.GOING_DOWN)
                        arrivalSequence(2);
                }
                checkDirectionalTaskUpdate();
            }
            case OUT_OF_SERVICE -> printAnalyzedState();
        }
    }

    private void processTask(byte[] data) {
        if(data[1] == 103) {
            state = States.OUT_OF_SERVICE;
            return;
        }
        else if(data[1] == 104) {
            closeDoors();
            checkForStateUpdate();

            sendToScheduler(new byte[] {(byte) currentFloor, 0, (byte) elevatorNum, (byte) States.getStateDatagramValue(state), 0}, false);
            receiveReply();
            return;
        }
        buttonsAndLamps.put((int) data[0], true);
        if(direction == 0)
            direction = getMovingDirection(data[0]);

        if(data[1] == 0)
            data[1] = (byte) getMovingDirection(data[0]);
        if(data[1] == 1)
            state = States.GOING_UP;
        else if(data[1] == 2)
            state = States.GOING_DOWN;
        else if(data[1] == -1)
            arrivalSequence(0);
    }

    public void move() {
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        sendToScheduler(new byte[] {(byte) currentFloor, (byte) States.getStateDatagramValue(state), (byte) elevatorNum, (byte) States.getStateDatagramValue(state), 4}, false);

        try {
            Thread.sleep(TRAVEL_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(direction == 1)
            currentFloor++;
        else if(direction == 2)
            currentFloor--;
        System.out.println("ELEVATOR " + elevatorNum + ": At floor " + currentFloor + ".\n");
    }

    private void arrivalSequence(int direction) {
        sendToScheduler(new byte[] {(byte) currentFloor, (byte) direction, (byte) elevatorNum, (byte) States.getStateDatagramValue(state), 1}, false);
        receiveReply();
        openDoors();

        sendToScheduler(new byte[] {(byte) currentFloor, (byte) direction, (byte) elevatorNum, (byte) States.getStateDatagramValue(States.DOOR_OPEN), 2}, false);
        receiveReply();
        try {
            Thread.sleep(DOOR_HOLD_TIME);
        } catch(InterruptedException e){
            return;
        }

        sendToScheduler(new byte[] {(byte) currentFloor, (byte) direction, (byte) elevatorNum, (byte) States.getStateDatagramValue(States.DOOR_OPEN), 3}, false);
        receiveReply();
        closeDoors();

        checkForStateUpdate();
        sendToScheduler(new byte[] {(byte) currentFloor, (byte) direction, (byte) elevatorNum, (byte) States.getStateDatagramValue(state), 0}, false);
        receiveReply();
    }

    /**
     * Creates datagram socket and a packet then using a socket to send and receive messages
     * @param data
     */
    private void sendToScheduler(byte[] data, boolean reply) {
        sendPacket = new DatagramPacket(data, data.length, address, (reply ? SEND_REPLY_PORT + elevatorNum : SEND_PORT));

        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            System.out.println("ELEVATOR " + data[3] + " Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        if(reply)
            System.out.println("ELEVATOR " + elevatorNum + ": Reply sent to scheduler.\n");
        else {
            System.out.println("ELEVATOR " + elevatorNum + ": Packet Sent: " + Arrays.toString(data) + "\n");
            receiveReply();
        }
    }

    public byte[] receiveFromScheduler() {
        byte[] data = new byte[2];
        receivePacket = new DatagramPacket(data, data.length, address, RECEIVE_PORT + elevatorNum);

        try {
            System.out.println("ELEVATOR " + elevatorNum + ": Waiting for Packet...\n");
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("ELEVATOR " + elevatorNum + ": Error Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR " + elevatorNum + ": Packet received: " + Arrays.toString(data) + "\n");

        sendToScheduler(new byte[5], true); // send reply

        return data;
    }

    /**
     * Receive the response from Scheduler.
     */
    private void receiveReply() {
        byte[] data = new byte[4];
        replyPacket = new DatagramPacket(data, data.length, address, RECEIVE_REPLY_PORT + elevatorNum);

        try {
            receiveReplySocket.receive(replyPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR " + elevatorNum + ": Reply received from scheduler.\n");
    }

    /**
     * Tries to cause a fault in the elevator for testing purposes
     * @param floorOrDoorFault Decides which fault it should be either floor fault or door fault
     */
    public void injectFault(boolean floorOrDoorFault) {
//        if(floorOrDoorFault) {
//            sendToScheduler(new byte[] {(byte) States.getDatagramStateValue(state), (byte) 4, (byte) currentFloor, (byte) elevatorNum, -1});
//        }
//        else {
//            openDoors();
//            sendMessageReceiveReply(new byte[] {(byte) States.getDatagramStateValue(state), 2, (byte) currentFloor, (byte) elevatorNum, -1});
//        }
    }

    /**
     * Prints the state of movement of the elevator and the state of its doors.
     */
    public void printAnalyzedState() {
        System.out.print("ELEVATOR " + elevatorNum + ": ");

        if(this.isDoorOpen()) {
            System.out.println("The elevator's doors are open.");
        } else {
            System.out.println("The elevator's doors are closed.");
        }

        if(isMoving) {
            if(state.equals(States.GOING_UP)) {
                System.out.println("The elevator is moving up.\n");
            } else if(state.equals(States.GOING_DOWN)) {
                System.out.println("The elevator is moving down.\n");
            } else if(state.equals(States.RECEIVING_TASK)) {
                System.out.println("The elevator is receiving a task.\n");
            } else {
                throw new RuntimeException("Error: The elevator is moving when it is not supposed to.\n");
            }
        } else {
            if(state.equals(States.OUT_OF_SERVICE)) {
                System.out.println("The elevator is out of service!");
            } else if(state.equals(States.RECEIVING_TASK)) {
                System.out.println("The elevator is receiving a task.\n");
            } else if(state.equals(States.GOING_UP)) {
                System.out.println("The elevator is stopped and is about to go up.\n");
            } else if(state.equals(States.GOING_DOWN)) {
                System.out.println("The elevator is stopped and is about to go down.\n");
            } else if(state.equals(States.IDLE)) {
                System.out.println("The elevator is stopped and is idle.\n");
            }
        }
    }

    /**
     * Run method.
     */
    @Override
    public void run() {
        Thread getTaskThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(state != States.OUT_OF_SERVICE)
                    processTask(receiveFromScheduler());
            }
        });

        Thread handleStateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(state != States.OUT_OF_SERVICE)
                    handleState();
                handleState();
            }
        });

        getTaskThread.start();
        handleStateThread.start();
    }
}