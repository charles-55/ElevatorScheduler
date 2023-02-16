import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
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
    private boolean lampOn; // checks if floor is ready to receive an elevator
    private final Scheduler scheduler;
    private DatagramPacket sendPacket, receivePacket;
    private DatagramSocket socket;
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
        } catch (SocketException e) {
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

    public boolean isLampOn() {
        return lampOn;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void setButtonDirection(ElevatorCallEvent.Direction direction, boolean state) {
        buttonsAndLamps.put(direction, state);
    }

    public void setLampOn(boolean lampOn) {
        this.lampOn = lampOn;
    }

    /**
     * This is the section for running with threads.
     */
    public void run() {
        FloorSubsystem floorSubsystem = new FloorSubsystem(this, scheduler);
        floorSubsystem.parseData("src/InputTable.txt"); // edit this to specify the file to read
    }
}
