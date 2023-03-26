import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
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
    private boolean doorOpen;
    private boolean isMoving;
    private final ElevatorQueue elevatorQueue;
    private final ArrayList<int[]> delayedQueue;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private States state;
    public static final int MOTOR_TIME = 3000, DOOR_HOLD_TIME = 5000, MAX_DOOR_HOLD_TIME = 10000, TRAVEL_TIME = 4000;
    private static final int SEND_PORT = 2200, RECEIVE_PORT = 2400;
    public static final int NUM_OF_ELEVATORS = 2;

    /**
     * Constructor method for Elevator initializing the elevator on the first floor.
     * @param elevatorNum int identifying elevator number.
     * @param numOfFloors int number of floors in the building.
     * @param elevatorQueue ElevatorQueue queue for the elevator object.
     */
    public Elevator(int elevatorNum, int numOfFloors, ElevatorQueue elevatorQueue, Scheduler scheduler) {
        this.elevatorNum = elevatorNum;
        this.currentFloor = 1;
        this.elevatorQueue = elevatorQueue;

        delayedQueue = new ArrayList<>();

        elevatorQueue.addElevator(this);

        doorOpen = false;
        isMoving = false;
        state = States.IDLE;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);

        scheduler.addElevator(this);
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

    public States getStates() {
        return state;
    }

    public void setState(States state) {
        this.state = state;
    }

    /**
     *
     */
    private int getDatagramStateValue() {
        if(state == States.IDLE) {
            return 0;
        } else if (state==States.GOING_UP) {
            return  1;
        } else if (state==States.GOING_DOWN) {
            return 2;
        } else if (state==States.OUT_OF_SERVICE) {
            return 503;
        }else{
            return 404;
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
     * Adds a new destination for the elevator in the delayed queue (different direction than current queue)
     * @param floorNum int origin floor.
     * @param destinationFloor int destination floor.
     */
    public void addToDelayedQueue(int floorNum, int destinationFloor) {
        synchronized (delayedQueue) {
            delayedQueue.add(new int[]{floorNum, destinationFloor});
        }
    }

    public void injectFault(){

    }

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
                move();
                if(buttonsAndLamps.get(currentFloor)) {

                    sendMessageReceiveReply(new byte[] {(byte) getDatagramStateValue(), 1, (byte) currentFloor, (byte) elevatorNum, -1});
                    openDoors();
                    sendMessageReceiveReply(new byte[] {(byte) getDatagramStateValue(), 2, (byte) currentFloor, (byte) elevatorNum, -1});

                    try {
                        Thread.sleep(DOOR_HOLD_TIME);
                    } catch(InterruptedException e){
                        return;
                    }

                    sendMessageReceiveReply(new byte[] {(byte) getDatagramStateValue(), 3, (byte) currentFloor, (byte) elevatorNum, -1});
                    closeDoors();
                    sendMessage(new byte[] {(byte) getDatagramStateValue(), (byte) (checkAllTaskComplete() ? 0 : 4), (byte) currentFloor, (byte) elevatorNum, -1});
                }
            }
            case OUT_OF_SERVICE -> printAnalyzedState();
        }
    }

    /**
     * Checks for delayed task to complete
     */
    private void handleDelayedTask() {
        if((delayedQueue.size() != 0) && state.equals(States.IDLE)) {
            if(delayedQueue.get(0)[0] < delayedQueue.get(0)[1]) {
                if(currentFloor > delayedQueue.get(0)[0])
                    callElevator(delayedQueue.get(0)[0], States.GOING_UP);
            }
            else if(delayedQueue.get(0)[0] > delayedQueue.get(0)[1]) {
                if(currentFloor < delayedQueue.get(0)[0])
                    callElevator(delayedQueue.get(0)[0], States.GOING_DOWN);
            }
        }
        synchronized (delayedQueue) {
            ArrayList<int[]> arrToRemove = new ArrayList<>();
            for(int[] arr : delayedQueue) {
                if((currentFloor > arr[0]) && (state.equals(States.GOING_DOWN))) {
                    buttonsAndLamps.put(arr[0], true);
                    buttonsAndLamps.put(arr[1], true);
                    arrToRemove.add(arr);
                }
                else if ((currentFloor < arr[0]) && (state.equals(States.GOING_UP))) {
                    buttonsAndLamps.put(arr[0], true);
                    buttonsAndLamps.put(arr[1], true);
                    arrToRemove.add(arr);
                }
                else if (currentFloor == arr[0]) {
                    if((state.equals(States.GOING_DOWN)) && (arr[0] > arr[1])) {
                        buttonsAndLamps.put(arr[1], true);
                        arrToRemove.add(arr);
                    } else if ((state.equals(States.GOING_UP)) && (arr[0] < arr[1])) {
                        buttonsAndLamps.put(arr[1], true);
                        arrToRemove.add(arr);
                    }
                }
            }
            for(int[] arr : arrToRemove)
                delayedQueue.remove(arr);
        }
    }

    /**
     * Checks if all the task are complete and updates the state
     */

    private boolean checkAllTaskComplete() {
        boolean done = true;
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            if(buttonsAndLamps.get(i + 1)) {
                done = false;
                break;
            }
        }
        if((delayedQueue.size() == 0) && done) {
            state = States.IDLE;
            return true;
        }

        return false;
    }

    /**
     * Calls the elevator and sets the state after receiving task.
     * @param callFloor int, the floor calling the elevator.
     * @param state States, the next state after receiving the call.
     */
    public void callElevator(int callFloor, States state) {
        moveToFloor(callFloor);
        this.state = state;
    }

    public void move() {
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        sendMessageReceiveReply(new byte[] {(byte) getDatagramStateValue(), (byte) 4, (byte) currentFloor, (byte) elevatorNum, -1});

        try {
            sleep(TRAVEL_TIME);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        int direction = 0;
        if(state == States.GOING_UP) {
            currentFloor++;
            direction = 1;
        }
        else if(state == States.GOING_DOWN) {
            currentFloor--;
            direction = 2;
        }
        System.out.println("ELEVATOR " + elevatorNum + ": At floor " + currentFloor + ".\n");

        sendMessageReceiveReply(new byte[] {(byte) getDatagramStateValue(), (byte) (buttonsAndLamps.get(currentFloor) ? 1 : 0), (byte) currentFloor, (byte) elevatorNum, (byte) direction});
    }

    /**
     * Moves the current elevator to target floor.
     * @param targetFloor int target floor number
     */
    public void moveToFloor(int targetFloor) {
        States direction = States.IDLE;
        if(currentFloor < targetFloor)
            direction = States.GOING_UP;
        else if(currentFloor > targetFloor)
            direction = States.GOING_DOWN;

        System.out.println("ELEVATOR " + elevatorNum + ": Receiving call from floor " + targetFloor + ".\n");

        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        while(currentFloor != targetFloor) {
            try {
                sleep(TRAVEL_TIME);
                if(direction.equals(States.GOING_UP))
                    currentFloor++;
                else
                    currentFloor--;
            } catch (InterruptedException e) {
                state = States.OUT_OF_SERVICE;
                printAnalyzedState();
                e.printStackTrace();
                System.exit(1);
            }
        }

        buttonsAndLamps.put(targetFloor, false);
        this.isMoving = false;
        System.out.println("ELEVATOR " + elevatorNum + ": At floor " + currentFloor + ".\n");
        this.openDoors();
        if(state == States.RECEIVING_TASK)
            state = States.IDLE;
    }

    private void sendMessageReceiveReply(byte[] data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                sendMessage(data);
                receiveReply();
            }
        });
        thread.start();
    }

    /**
     * Creates datagram socket and a packet then using a socket to send and receive messages
     * @param data
     */
    private synchronized static void sendMessage(byte[] data) {
        InetAddress address = null;
        DatagramSocket sendReceiveSocket = null;
        try {
            //socket = new DatagramSocket();
            sendReceiveSocket = new DatagramSocket();
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException | SocketException e) {//(SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, SEND_PORT);

        try {
            System.out.println("ELEVATOR " + data[3] + ": Sending Packet: " + Arrays.toString(data) + "\n");
            sendReceiveSocket.send(sendPacket);
            System.out.println("ELEVATOR " + data[3] + ": Packet Sent!\n");
        } catch (IOException e) {
            System.out.println("ELEVATOR " + data[3] + " Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }
        sendReceiveSocket.close();
    }

    /**
     * Receive the response from Scheduler.
     */
    private synchronized static void receiveReply() {
        byte[] data = new byte[4];
        InetAddress address = null;
        DatagramSocket sendReceiveSocket = null;

        try {
            System.out.println("ELEVATOR: Waiting for reply packet from Scheduler...\n");
            sendReceiveSocket = new DatagramSocket(RECEIVE_PORT);
            address = InetAddress.getLocalHost();



        } catch (IOException e) {//(SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        DatagramPacket receivePacket = new DatagramPacket(data, data.length, address, RECEIVE_PORT);

        try {
            sendReceiveSocket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR: Reply packet received.\n");

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
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
        Elevator elevator = this;
        Thread taskUpdateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(state != States.OUT_OF_SERVICE) {
                    elevatorQueue.getFromQueue(elevator);
                    handleDelayedTask();
                }
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

        taskUpdateThread.start();
        handleStateThread.start();
    }
}