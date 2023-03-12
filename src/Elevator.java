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
    private static final int PORT = 2200;
    public static final int NUM_OF_ELEVATORS = 2;

    /**
     * Constructor method for Elevator initializing the elevator on the first floor.
     * @param elevatorNum int identifying elevator number.
     * @param numOfFloors int number of floors in the building.
     * @param elevatorQueue ElevatorQueue queue for the elevator object.
     */
    public Elevator(int elevatorNum, int numOfFloors, ElevatorQueue elevatorQueue) {
        this(elevatorNum, numOfFloors, 1, elevatorQueue);
    }

    /**
     * Constructor method for Elevator with custom initializing floor.
     * @param elevatorNum int identifying elevator number.
     * @param numOfFloors int number of floors in the building.
     * @param currentFloor int current floor where the elevator object is initially located.
     * @param elevatorQueue ElevatorQueue queue for the elevator object.
     */
    public Elevator(int elevatorNum, int numOfFloors, int currentFloor, ElevatorQueue elevatorQueue) {
        this.elevatorNum = elevatorNum;
        this.currentFloor = currentFloor;
        this.elevatorQueue = elevatorQueue;
        delayedQueue = new ArrayList<>();

        elevatorQueue.addElevator(this);

        doorOpen = false;
        isMoving = false;
        state = States.IDLE;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);
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
        alertArrival();
        if (!this.isMoving && !doorOpen) {
            try {
                Thread.sleep(3000); // Arbitrary time for doors to open // implement open door using motors
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
                Thread.sleep(3000); // Arbitrary time for doors to close // implement close door using motors
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

    public void callElevator(int callFloor, States state) {
        this.state = States.RECEIVING_TASK;
        moveToFloor(callFloor);
        this.state = state;
    }

    private void handleTask() {
        if(state.equals(States.GOING_UP)) {
            for(int i = currentFloor; i <= Floor.NUM_OF_FLOORS; i++) {
                if(buttonsAndLamps.get(i))
                    moveToFloor(i);
            }
        }
        else if(state.equals(States.GOING_DOWN)) {
            for(int i = currentFloor; i > 0; i--) {
                if(buttonsAndLamps.get(i))
                    moveToFloor(i);
            }
        }
    }

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
            handleTask();
        }
    }

    private void checkAllTaskComplete() {
        boolean done = true;
        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            if(buttonsAndLamps.get(i + 1)) {
                done = false;
                break;
            }
        }
        if((delayedQueue.size() == 0) && done)
            state = States.IDLE;
    }

    /**
     * Moves the current elevator to target floor.
     * @param targetFloor int target floor number
     */
    public void moveToFloor(int targetFloor) {
        System.out.println("ELEVATOR " + elevatorNum + ": " + state.toString().replace('_', ' ').toLowerCase()  + " from floor " + currentFloor + " to " + targetFloor + ".\n");

        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        States direction = States.IDLE;
        if(currentFloor < targetFloor)
            direction = States.GOING_UP;
        else if(currentFloor > targetFloor)
            direction = States.GOING_DOWN;

        while(currentFloor != targetFloor) {
            try {
                sleep(4000);
                if(direction.equals(States.GOING_UP))
                    currentFloor++;
                else
                    currentFloor--;

                if(buttonsAndLamps.get(currentFloor)) {
                    System.out.println("ELEVATOR " + elevatorNum + ": Made a stop on floor " + currentFloor + ".\n");
                    this.isMoving = false;
                    buttonsAndLamps.put(currentFloor, false);
                    openDoors();
                    Thread.sleep(5000);
                    closeDoors();
                    this.isMoving = true;
                }
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

    /**
     * Alerts server once elevator arrives to a floor in its' queue.
     */
    private void alertArrival() {
        byte[] data = new byte[4];
        data[0] = 1;
        data[1] = (byte) currentFloor;
        data[2] = (byte) elevatorNum;

        if(state.equals(States.GOING_UP))
            data[3] = 1;
        else if(state.equals(States.GOING_DOWN))
            data[3] = 2;

        checkAllTaskComplete();
        sendMessage(data);
    }

    private static void sendMessage(byte[] data) {
        DatagramSocket socket = null;
        InetAddress address = null;
        try {
            socket = new DatagramSocket();
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR " + data[2] + ": Sending Packet: " + Arrays.toString(data) + "\n");
            socket.send(sendPacket);
            System.out.println("ELEVATOR " + data[2] + ": Packet Sent!\n");
        } catch (IOException e) {
            System.out.println("ELEVATOR " + data[2] + " Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }

        socket.close();
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
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    elevatorQueue.getFromQueue(elevator);
                    handleDelayedTask();
                }
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true)
                    handleTask();
            }
        });

        thread1.start();
        thread2.start();
    }
}