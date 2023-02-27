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
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket socket;
    private static InetAddress address;
    private static final int PORT = 22;
    public enum Direction {UP, DOWN, STANDBY}

    public Elevator(int elevatorNum, int numOfFloors, ElevatorQueue elevatorQueue) {
        this(elevatorNum, numOfFloors, 1, elevatorQueue);
    }

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

        try {
            socket = new DatagramSocket();
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
     * method for the current state of the doors
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
     * method for the current state of the movement of the elevator
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
     * method to open, disembark / embark passengers, then close the doors.
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

    public void addToDelayedQueue(int floorNum, int destinationFloor) {
        delayedQueue.add(new int[] {floorNum, destinationFloor});
    }

    /**
     * Moves the current elevator to target floor.
     * @param targetFloor int target floor number
     * @param direction Direction direction of target floor
     */
    public void moveToFloor(int targetFloor, Direction direction) {
        System.out.println("ELEVATOR " + elevatorNum + ": Moving from floor " + currentFloor + " to " + targetFloor + ".");
        this.direction = direction;
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); // Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        buttonsAndLamps.put(targetFloor, false);
        this.isMoving = false;
        this.currentFloor = targetFloor;
        System.out.println("ELEVATOR " + elevatorNum + ": At floor " + currentFloor + ".\n");
        this.openDoors();
    }

    public void put(int i, boolean b) {
        buttonsAndLamps.put(i, b);
    }

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

        sendPacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR " + elevatorNum + ": Sending Packet: " + Arrays.toString(data) + "\n");
            socket.send(sendPacket);
            System.out.println("ELEVATOR " + elevatorNum + ": Packet Sent!\n");
        } catch (IOException e) {
            System.out.println("ELEVATOR " + elevatorNum + " Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void alertDelay() {
        byte[] data = new byte[4];
        data[0] = 2;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

         DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR: Alerting Delay: " + Arrays.toString(data) + "\n");
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            System.out.println("ELEVATOR: Packet Sent!\n");
        } catch (IOException e) {
            System.out.println("ELEVATOR Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void alertDelayResolved() {
        byte[] data = new byte[4];
        data[0] = 3;
        data[1] = 0;
        data[2] = 0;
        data[3] = 0;

        DatagramPacket sendPacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR: Alerting Delay Resolved: " + Arrays.toString(data) + "\n");
            DatagramSocket socket = new DatagramSocket();
            socket.send(sendPacket);
            System.out.println("ELEVATOR: Packet Sent!\n");
        } catch (IOException e) {
            System.out.println("ELEVATOR Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * method that prints the state of movement of the elevator and the state of its doors.
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
            if (this.getDirection() == Direction.STANDBY) {
                System.out.println("The elevator is stopped.");
            } else {
                throw new Exception("Error: The elevator is not moving but its direction is not STANDBY.");
            }

        }
    }

    /**
     * This is the section for running with threads.
     */
    @Override
    public void run() {
        while(true) {
            elevatorQueue.getFromQueue(this);
            for(int destinationFloor : buttonsAndLamps.keySet()) {
                if(buttonsAndLamps.get(destinationFloor)) {
                    moveToFloor(destinationFloor, direction);
                }
            }
        }
    }
}
