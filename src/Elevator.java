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
    private Direction direction;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private static final int PORT = 2200;
    public enum Direction {UP, DOWN, STANDBY}

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
        direction = Direction.STANDBY;

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
     * Setter method to close the doors
     * @param doorOpen boolean value for state of the doors
     */
    public void setDoorOpen(boolean doorOpen) {
        if(!isMoving) {
            this.doorOpen = doorOpen;
        }
    }

    /**
     * Getter method for the current state of the movement of the elevator
     * @return boolean isMoving (moving if true, stopped if false)
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Getter method for the current direction of the elevator.
     * @return Direction direction of the elevator (up, down, standby)
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Setter method for the current direction of the elevator.
     * @param direction Direction direction of the elevator
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
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
        delayedQueue.add(new int[] {floorNum, destinationFloor});
    }

    /**
     * Moves the current elevator to target floor.
     * @param targetFloor int target floor number
     * @param direction Direction direction of target floor
     */
    public void moveToFloor(int targetFloor, Direction direction) {
        System.out.println("ELEVATOR " + elevatorNum + ": Moving from floor " + currentFloor + " to " + targetFloor + ".\n");
        this.direction = direction;
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        while(currentFloor != targetFloor) {
            try {
                Thread.sleep(4000);
                if(direction.equals(Direction.UP))
                    currentFloor++;
                else if(direction.equals(Direction.DOWN))
                    currentFloor--;

                if(checkForStop()) {
                    System.out.println("ELEVATOR " + elevatorNum + ": Made a stop on floor " + currentFloor + ".\n");
                    this.isMoving = false;
                    buttonsAndLamps.put(currentFloor, false);
                    System.out.println();
                    openDoors();
                    Thread.sleep(5000);
                    closeDoors();
                    this.isMoving = true;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        this.isMoving = false;
        buttonsAndLamps.put(targetFloor, false);
        System.out.println("ELEVATOR " + elevatorNum + ": At floor " + currentFloor + ".\n");
        for(int[] arr : delayedQueue)
            System.out.print(Arrays.toString(arr) + ", ");
        System.out.println();

        this.openDoors();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeDoors();
    }

    private boolean checkForStop() {
        return buttonsAndLamps.get(currentFloor);
    }

    /**
     * Puts a new value for a button on the elevator. (on if true, off if false)
     * @param i int button index.
     * @param b boolean light state (On if true, Off if false)
     */
    public void put(int i, boolean b) {
        buttonsAndLamps.put(i, b);
    }

    /**
     * Alerts server once elevator arrives to a floor in its' queue.
     */
    public void alertArrival() {
        byte[] data = new byte[4];
        data[0] = 1;
        data[1] = (byte) currentFloor;
        data[2] = (byte) elevatorNum;

        if(direction.equals(Direction.UP))
            data[3] = 1;
        else if(direction.equals(Direction.DOWN))
            data[3] = 2;

        boolean done = true;
        for(int destinationFloor : buttonsAndLamps.keySet()) {
            if(buttonsAndLamps.get(destinationFloor)) {
                done = false;
                break;
            }
        }
        if(done) {
            direction = Direction.STANDBY;
            if(elevatorQueue.isWaiting())
                elevatorQueue.notify();
        }

        sendMessage(data);
    }

    /**
     * Alerts server that an elevator entered a delay.
     */
    public static void alertDelay() {
        byte[] data = new byte[4];
        data[0] = 2;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

        sendMessage(data);
    }

    /**
     * Alerts server that the elevator finished its delay.
     */
    public static void alertDelayResolved() {
        byte[] data = new byte[4];
        data[0] = 3;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

        sendMessage(data);
    }

    public static void sendMessage(byte[] data) {
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
    public void printState() throws Exception {
        //TODO: Once we do multiple instances of elevators, identify which elevator it is in the print.
        // Also, send error when doors are open but elevator is moving.
        if (this.isDoorOpen()) {
            System.out.println("The elevator's doors are open.");
        } else {
            System.out.println("The elevator's doors are closed.");
        }

        if (this.isMoving()) {
            if (this.getDirection() == Direction.UP) {
                System.out.println("The elevator is moving up.");
            } else if (this.getDirection() == Direction.DOWN) {
                System.out.println("The elevator is moving down.");
            } else {
                throw new Exception("Error: The elevator is moving but its direction is STANDBY.");
            }
        } else {
            if (this.getDirection() != Direction.STANDBY) {
                System.out.println("The elevator is stopped and is about to go " + this.getDirection().toString() + ".");
            } else {
                System.out.println("The elevator is stopped and is on STANDBY.");
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
                while (true)
                    elevatorQueue.getFromQueue(elevator);
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int destinationFloor : buttonsAndLamps.keySet()) {
                        if (buttonsAndLamps.get(destinationFloor)) {
                            if(direction.equals(Direction.STANDBY)) {
                                if(currentFloor > destinationFloor)
                                    direction = Direction.DOWN;
                                else if(currentFloor < destinationFloor)
                                    direction = Direction.UP;
                                elevator.moveToFloor(destinationFloor, direction);
                            }
                            else if(direction.equals(Direction.UP)) {
                                if(destinationFloor > currentFloor)
                                    put(destinationFloor, true);
                            }
                            else if(direction.equals(Direction.DOWN)) {
                                if(destinationFloor < currentFloor)
                                    put(destinationFloor, true);
                            }
                        }
                    }
                }
            }
        });
        Thread thread3 = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for(int[] arr : delayedQueue) {
                        if((currentFloor > arr[0]) && (direction == Direction.DOWN))
                            buttonsAndLamps.put(arr[0], true);
                        else if((currentFloor < arr[0]) && (direction == Direction.UP))
                            buttonsAndLamps.put(arr[0], true);
                        else if(currentFloor == arr[0]) {
                            if((direction == Direction.DOWN) && (arr[0] > arr[1])) {
                                buttonsAndLamps.put(arr[1], true);
                                delayedQueue.remove(arr);
                            }
                            else if((direction == Direction.UP) && (arr[0] < arr[1])) {
                                buttonsAndLamps.put(arr[1], true);
                                delayedQueue.remove(arr);
                            }
                        }
                    }
                }
            }
        });

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
