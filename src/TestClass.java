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
    private Elevator elevator;
    private ElevatorCallEvent event;
    private static int NUM_OF_FLOORS = 5;

    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floor = new Floor(1, scheduler);
        elevator = new Elevator(1, 5, scheduler);
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
        elevator = new Elevator(1, 5, scheduler);

        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, Elevator.Direction.UP);
        assertEquals(3, elevator.getCurrentFloor());
    }
}
