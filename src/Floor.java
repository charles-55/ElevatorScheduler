import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * The Floor Class.
 * Represents current status of floor.
 * Takes the information in table and processes it.
 *
 * @author Sabah Samwatin
 * @version 1.0
 */
public class Floor extends Thread {

    private final int floorNumber;
    private final HashMap<Integer, Boolean> buttonsAndLamps;
    private DatagramPacket receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private final int PORT;
    public static final int NUM_OF_FLOORS = 5;

    /**
     * Constructor for the floor class.
     */
    public Floor(int floorNumber) {
        this.floorNumber = floorNumber;
        buttonsAndLamps = new HashMap<>();
        buttonsAndLamps.put(1, false);
        buttonsAndLamps.put(2, false);
        PORT = 2300 + floorNumber;

        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Accessor method for the buttons and lamps
     * @return buttonsAndLamps
     */
    public HashMap<Integer, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    /**
     * Returns the floor number
     * @return floorNumber
     */
    public int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Mutator method for the button direction.
     * @param direction
     * @param state
     */
    public void setButtonDirection(Integer direction, boolean state) {
        buttonsAndLamps.put(direction, state);
    }

    /**
     * Method to read the message from the datagram
     */
    public void readMessage() {
        byte[] data = new byte[4];
        receivePacket = new DatagramPacket(data, data.length, address, PORT);

        try {
            System.out.println("FLOOR " + floorNumber + ": Waiting for Packet...\n");
            socket.receive(receivePacket);
        } catch (IOException e) {
            System.out.println(" FLOOR Error: Socket Timed Out.\n");
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("FLOOR: Packet received: " + Arrays.toString(data) + "\n");

        if(data[0] == 1) {
             // to be moved when other floors are created
            if (data[1] == (byte) floorNumber) {
                System.out.println("FLOOR " + data[1] + ": Elevator " + data[2] + " arrived.\n");
                if (data[3] == 1)
                    setButtonDirection(1, false);
                else if (data[3] == 2)
                    setButtonDirection(2, false);
            }
        }
        else if(data[0] == 2) {
            System.out.println("FLOOR " + data[1] + ":  A delay occurred in elevator " + data[2] + "!\n");
        }
        else if(data[0] == 3) {
            System.out.println("FLOOR " + data[1] + ":  Delay in elevator " + data[2] + " resolved!\n");
        }

        try {
            Thread.sleep(50);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Method to print the state of the floor.
     */
    public void printAnalyzedState() {
        boolean up = this.buttonsAndLamps.get(1);
        boolean down = this.buttonsAndLamps.get(2);
        //If light is up
        if (up && down) {
            throw new RuntimeException("Error: The floor lights are both up and down!");
        } else if (up) {
            System.out.println("Calling an elevator to go up.");
        } else if (down) {
            System.out.println("Calling an elevator to go down.");
        } else {
            System.out.println("Idle.");
        }
    }

    /**
     * This is the section for running with threads.
     */
    public void run() {
        try {
            Thread.sleep(750);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        while(true) {
            readMessage();
        }
    }

    /**
     * Method to close the socket
     */
    public void closeSocket() {
        socket.close();
    }
}
