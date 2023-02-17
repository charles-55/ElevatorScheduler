import java.io.IOException;
import java.net.*;
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
    private final HashMap<ElevatorCallEvent.Direction, Boolean> buttonsAndLamps;
    private final Scheduler scheduler;
    private DatagramPacket receivePacket;
    private DatagramSocket socket;
    private InetAddress address;
    private static final int PORT = 23;

    /**
     * Constructor for the floor class.
     */
    public Floor(int floorNumber, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.floorNumber = floorNumber;
        buttonsAndLamps = new HashMap<>();
        buttonsAndLamps.put(ElevatorCallEvent.Direction.UP, false);
        buttonsAndLamps.put(ElevatorCallEvent.Direction.DOWN, false);

        try {
            socket = new DatagramSocket(PORT);
            address = InetAddress.getLocalHost();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public HashMap<ElevatorCallEvent.Direction, Boolean> getButtonsAndLamps() {
        return buttonsAndLamps;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setButtonDirection(ElevatorCallEvent.Direction direction, boolean state) {
        buttonsAndLamps.put(direction, state);
    }

    public void readMessage() {
        byte[] info = new byte[3];
        receivePacket = new DatagramPacket(info, info.length, address, PORT);

        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        if(info[0] == (byte) floorNumber) {
            if(info[1] == 1) {
                if(info[2] == 1)
                    setButtonDirection(ElevatorCallEvent.Direction.UP, false);
                else if(info[2] == 2)
                    setButtonDirection(ElevatorCallEvent.Direction.DOWN, false);
            }
        }
    }

    /**
     * This is the section for running with threads.
     */
    public void run() {
        FloorSubsystem floorSubsystem = new FloorSubsystem(this, scheduler, address, PORT);
        floorSubsystem.parseData("src/InputTable.txt"); // edit this to specify the file to read
        while(true) {
            readMessage();
        }
    }
}
