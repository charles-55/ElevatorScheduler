import org.junit.*;
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


    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floor = new Floor(1);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, NUM_OF_FLOORS, elevatorQueue);
    }

    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();
        floor.closeSocket();
        elevatorQueue.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevatorQueue = null;
        elevator = null;
    }

    @Test
    public void testRun(){
        floorSubsystem.start();
        floor.start();
        scheduler.start();
        elevatorQueue.start();
        assertEquals(0, elevatorQueue.getQueue().get(elevator).size());
        floorSubsystem.stop();
        floor.stop();
    }

    @Test
    public void testParseData(){
        String data = "14:05:15:0 1 Up 4";
        String splitData = Arrays.toString(data.split(","));
        assertEquals(splitData,"[" + data + "]");
    }
}
