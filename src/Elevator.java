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
    private boolean doorOpen;
    private boolean isMoving;
    private final Scheduler scheduler;
    private Direction direction;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int PORT = 69;
    public enum Direction {UP, DOWN, STANDBY}

    /**
     * Initialize the elevator.
     * @param elevatorNum int, the elevator number.
     * @param numOfFloors int, the number of floors to move between.
     * @param scheduler Scheduler, the scheduler to get the data from.
     */
    public Elevator(int elevatorNum, int numOfFloors, Scheduler scheduler) {
        this(elevatorNum, numOfFloors, 1, scheduler);
    }

    /**
     * Initialize the elevator.
     * @param elevatorNum int, the elevator number.
     * @param numOfFloors int, the number of floors to move between.
     * @param currentFloor int, the current floor the elevator is on.
     * @param scheduler Scheduler, the scheduler to get the data from.
     */
    public Elevator(int elevatorNum, int numOfFloors, int currentFloor, Scheduler scheduler) {
        this.elevatorNum = elevatorNum;
        this.currentFloor = currentFloor;
        this.scheduler = scheduler;

        scheduler.addElevator(this);

        doorOpen = false;
        isMoving = false;
        direction = Direction.STANDBY;

        buttonsAndLamps = new HashMap<>();
        for(int i = 1; i <= numOfFloors; i++)
            buttonsAndLamps.put(i, false);

        try {
            socket = new DatagramSocket(PORT);
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
     * Get the current floor.
     * @return int, the current floor.
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    /**
     * Get the state of the elevator door.
     * @return boolean, true if open, false otherwise.
     */
    public boolean isDoorOpen() {
        return doorOpen;
    }

    /**
     * Set the state of the elevator door.
     * @param doorOpen boolean, true if open, false otherwise.
     */
    public void setDoorOpen(boolean doorOpen) {
        if(!isMoving) {
            this.doorOpen = doorOpen;
        }
    }

    /**
     * Get the state of the elevator.
     * @return boolean, true if moving, false otherwise.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Get the direction of the elevator.
     * @return Direction, direction of the elevator.
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Get the buttons and the lamps mapped to them.
     * @return HashMap<Integer, Boolean>, the buttons and the lamps mapped to them.
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     * Open the door of the elevator.
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
     * Move the elevator to a particular floor.
     * @param targetFloor int, the floor to move to.
     */
    public void moveToFloor(int targetFloor, Direction direction) {
        System.out.println("Moving from floor " + currentFloor + " to " + targetFloor + ".");
        this.direction = direction;
        if(doorOpen)
            closeDoors();
        this.isMoving = true;

        try {
            Thread.sleep((long) Math.abs(targetFloor - this.currentFloor) * 4000); // Arbitrary time for the elevator to move up X floors (X * 4 seconds)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("At floor " + currentFloor + ".");

        buttonsAndLamps.put(targetFloor, false);
        this.isMoving = false;
        this.currentFloor = targetFloor;
        this.openDoors();
    }

    public void put(int i, boolean b) {
        buttonsAndLamps.put(i, b);
    }

    public void respondToCall() {
        byte[] data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR " + elevatorNum + ": Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println("ELEVATOR " + elevatorNum + " Error: Socket Timed Out.");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("ELEVATOR " + elevatorNum + ": Packet Received: " + Arrays.toString(data) + ".\n");

        if(elevatorNum == (int) data[0]) {
            Direction direction = Direction.STANDBY;
            if(data[2] == 1)
                direction = Elevator.Direction.UP;
            else if(data[2] == 2)
                direction = Elevator.Direction.DOWN;

            if(this.direction.equals(Direction.STANDBY))
                moveToFloor(data[1], direction);
            else if(direction.equals(Direction.DOWN) && (currentFloor - (int) data[1] >= 0))
                buttonsAndLamps.put((int) data[1], true);
            else if(direction.equals(Direction.UP) && (currentFloor - (int) data[1] <= 0))
                buttonsAndLamps.put((int) data[1], true);
            else {
                try {
                    wait();
                    buttonsAndLamps.put((int) data[1], true);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            // wait till arrived at floor

            buttonsAndLamps.put((int) data[3], true);
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void alertArrival() {
        byte[] data = new byte[3];
        data[0] = (byte) currentFloor;
        data[1] = (byte) elevatorNum;

        if(direction.equals(Direction.UP))
            data[2] = 1;
        else if(direction.equals(Direction.DOWN))
            data[2] = 2;

        boolean stillMoving = false;
        for(Integer integer : buttonsAndLamps.keySet()) {
            if(buttonsAndLamps.get(integer))
                stillMoving = true;
        }

        if(!stillMoving)
            direction = Direction.STANDBY;

        sendPacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("ELEVATOR " + elevatorNum + ": Sending packet: " + Arrays.toString(data) + "\n");
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

    /**
     * This is the section for running with threads.
     */
    @Override
    public void run() {
        while(true) {
            respondToCall();
            for(int destinationFloor : buttonsAndLamps.keySet()) {
                if(buttonsAndLamps.get(destinationFloor)) {
                    moveToFloor(destinationFloor, direction);
                }
            }
        }
    }
}
