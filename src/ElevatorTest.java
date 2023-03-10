import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * The Elevator Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class ElevatorTest {
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
        elevator = new Elevator(1, NUM_OF_FLOORS, elevatorQueue);
    }

    @After
    public void tearDown() {
        scheduler.closeSocket();
        floor.closeSocket();
        elevatorQueue.closeSocket();

        scheduler = null;
        floor = null;
        elevatorQueue = null;
        elevator = null;
    }

    @Test
    public void testIsMoving(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, Elevator.Direction.UP);
        assertEquals(false,elevator.isMoving());
        assertEquals(3, elevator.getCurrentFloor());
    }

    @Test
    public void testOpenDoors() {
        int targetFloor = 3;

        elevator.moveToFloor(targetFloor, Elevator.Direction.UP);
        elevator.setCurrentFloor(targetFloor);
        assertTrue(elevator.isDoorOpen() ==true);
    }

    @Test
    public void testCloseDoors(){
        elevator.moveToFloor(3, Elevator.Direction.UP);
        assertTrue(elevator.isDoorOpen() != false);
    }

    @Test
    public void testMoveToFloor() {
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, Elevator.Direction.UP);
        assertEquals(3, elevator.getCurrentFloor());
    }

//    @Test
//    public void testRun() {
//        floor.start();
//        scheduler.start();
//        elevatorQueue.start();
//        assertEquals(0, elevatorQueue.getQueue().get(elevator).size());
//        floor.stop();
//    }


}
