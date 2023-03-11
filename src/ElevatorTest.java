import org.junit.*;

import static org.junit.Assert.*;

/**
 * The Elevator Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class ElevatorTest {

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
        floor = new Floor(1, scheduler, floorSubsystem);
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
    public void testIsMoving(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3);
        assertFalse(elevator.isMoving());
        assertEquals(3, elevator.getCurrentFloor());
    }

    @Test
    public void testOpenDoors() {
        int targetFloor = 3;

        elevator.moveToFloor(targetFloor);
        elevator.setCurrentFloor(targetFloor);
        assertTrue(elevator.isDoorOpen());
    }

    @Test
    public void testCloseDoors(){
        elevator.moveToFloor(3);
        assertTrue(elevator.isDoorOpen());
    }

    @Test
    public void testMoveToFloor() {
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3);
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
