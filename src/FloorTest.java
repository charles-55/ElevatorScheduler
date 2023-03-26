import org.junit.*;
import java.util.ArrayList;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

/**
 * The Floor and FloorSubsystem Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class FloorTest {

    private Scheduler scheduler;
    private FloorSubsystem floorSubsystem;
    private Floor floor;
    private ElevatorQueue elevatorQueue;
    private Elevator elevator;
    private static int NUM_OF_FLOORS = 5;

    /**
     * Method that sets up objects before the tests.
     */
    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floor = new Floor(1, floorSubsystem);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, NUM_OF_FLOORS, elevatorQueue, scheduler);
    }

    /**
     * Method that closes up everything after the tests.
     */
    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();
        elevatorQueue.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevatorQueue = null;
        elevator = null;
    }

    @Test
    public void testAddFloor(){
        final ArrayList<Floor> floors = new ArrayList<>();

        assertEquals(0, floors.size());
        floors.add(floor);
        assertEquals(1, floors.size());
    }

    @Test
    public void testSendToScheduler(){
//        byte[] data = new byte[0];
//        LocalTime time = LocalTime.now();
//        States state = States.WAITING_FOR_TASK;
//        assertEquals(state,States.WAITING_FOR_TASK);
//        //floorSubsystem.sendToScheduler(data,time);
//        //assertEquals(state,States.RECEIVING_TASK);

    }

    @Test
    public void testReceiveFromScheduler(){

    }

    /**
     * Test method for the threads.
     */
    @Test
    public void testRun() {
        floorSubsystem.start();
        scheduler.start();
        elevatorQueue.start();
        assertEquals(0, elevatorQueue.getQueue().get(elevator).size());
        floorSubsystem.stop();
    }

    /**
     * Test method for parseData() method in Floor.
     */
    @Test
    public void testParseData() {
        String data = "14:05:15:0 1 Up 4";
        String splitData = Arrays.toString(data.split(","));
        assertEquals(splitData,"[" + data + "]");
    }

}
