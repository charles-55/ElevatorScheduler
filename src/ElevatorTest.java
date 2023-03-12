import org.junit.*;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

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
    private HashMap<Elevator, ArrayList<Integer>> queue;


    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floor = new Floor(1);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, Floor.NUM_OF_FLOORS, elevatorQueue);
        queue = new HashMap<>();
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

    ///////////////////////////////////////////ELEVATOR CLASS TESTS/////////////////////////////////////////////////////

    @Test
    public void isDoorOpen(){
        elevator.moveToFloor(3);
        assertTrue(elevator.isDoorOpen());
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
    public void testAddToDelayedQueue(){
        ArrayList<int[]> delayedQueue = new ArrayList<>();
        elevatorQueue.getQueue().put(elevator,delayedQueue);
        assertFalse(elevatorQueue.getQueue().isEmpty());
    }

    @Test
    public void testCallElevator(){
        elevator.callElevator(3,States.IDLE);
        elevator.getStates().equals(States.GOING_UP);
    }

    @Test
    public void testHandleTask(){
        elevator.moveToFloor(3);
        elevator.getState().equals(States.GOING_UP);
    }

    @Test
    public void testHandleDelayedTask() {
        ArrayList<int[]> delayedQueue = new ArrayList<>();
        elevator.moveToFloor(3);
        elevator.getState().equals(States.GOING_UP);
    }

    @Test
    public void testCheckAllTaskComplete(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3);
        assertEquals(3, elevator.getCurrentFloor());
        elevator.getStates().equals(States.IDLE);
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


///////////////////////////////////////////ELEVATOR QUEUE CLASS TESTS///////////////////////////////////////////////////

    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

    @Test
    public void testRespondToCall(){
        elevator.moveToFloor(3);
        elevator.getState().equals(States.RECEIVING_TASK);
    }

    @Test
    public void testAddToQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertFalse(elevatorQueue.getQueue().isEmpty());
    }

    @Test
    public void testGetFromQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertEquals(1,elevatorQueue.getQueue().size());
        elevatorQueue.getQueue().remove(elevator,new ArrayList<>());
        assertTrue(elevatorQueue.getQueue().isEmpty());
    }

}
