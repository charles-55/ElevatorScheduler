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

    private final FloorSubsystem floorSubsystem;
    private final int floorNumber;
    private final HashMap<Elevator.Direction, Boolean> buttonsAndLamps;
    private final Scheduler scheduler;
    private DatagramPacket receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int PORT = 22;

    /**
     * Constructor for the floor class.
     */
    public Floor(int floorNumber, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.floorNumber = floorNumber;
        buttonsAndLamps = new HashMap<>();
        buttonsAndLamps.put(Elevator.Direction.UP, false);
        buttonsAndLamps.put(Elevator.Direction.DOWN, false);

        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        floorSubsystem = new FloorSubsystem(this, address, "src/InputTable.txt");
    }

    public HashMap<Elevator.Direction, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setButtonDirection(Elevator.Direction direction, boolean state) {
        buttonsAndLamps.put(direction, state);
    }

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
            System.out.println("FLOOR " + data[1] + ": Elevator " + data[2] + " arrived.\n");
            if (data[1] == (byte) floorNumber) {
                if (data[3] == 1)
                    setButtonDirection(Elevator.Direction.UP, false);
                else if (data[3] == 2)
                    setButtonDirection(Elevator.Direction.DOWN, false);
            }
        }
        else if(data[0] == 2) {
            System.out.println("FLOOR " + floorNumber + ":  A delay occurred!\n");
            try {
                floorSubsystem.wait();
            } catch (InterruptedException e) {
                System.out.println("FLOOR " + floorNumber + ":  A error occurred!\n");
                e.printStackTrace();
            }
        }
        else if(data[0] == 3) {
            floorSubsystem.notify();
            System.out.println("FLOOR " + floorNumber + ":  Delay resolved!\n");
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
    public void run() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(1);
        }
        floorSubsystem.start();
        while(true) {
            readMessage();
        }
    }
}
