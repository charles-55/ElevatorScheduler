import org.junit.*;
import java.time.LocalTime;
import static org.junit.Assert.assertEquals;

/**
 * The Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka
 * @version 1.0
 */
public class TestClass {

    private Scheduler scheduler;
    private Floor floor;
    private ElevatorQueue elevatorQueue;
    private Elevator elevator;
    private static int NUM_OF_FLOORS = 5;

    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floor = new Floor(1, scheduler);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, 5, elevatorQueue);
    }

    @After
    public void tearDown() {
        scheduler = null;
        floor = null;
        elevator = null;
    }

    @Test
    public void testInputFile() {
        floor.start();
        assertEquals(11, scheduler.getQueue().get(elevator).size());
        floor.stop();
    }

    @Test
    public void testMoveFloor() {
        scheduler = new Scheduler();
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, 5, elevatorQueue);

        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, Elevator.Direction.UP);
        assertEquals(3, elevator.getCurrentFloor());
    }
}
