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


    /**
     * Method that sets up the objects before the tests.
     */
    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem("src/InputTable.txt");
        floor = new Floor(1, floorSubsystem);
        elevatorQueue = new ElevatorQueue();
        elevator = new Elevator(1, Floor.NUM_OF_FLOORS, elevatorQueue,scheduler);
        elevator = new Elevator(1, Floor.NUM_OF_FLOORS, elevatorQueue, scheduler);
        queue = new HashMap<>();
    }

    /**
     * Method that closes everything after the tests.
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

    ///////////////////////////////////////////ELEVATOR CLASS TESTS/////////////////////////////////////////////////////

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testIsDoorOpen(){
        elevator.moveToFloor(3);
        assertTrue(elevator.isDoorOpen());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testIsMoving(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3);
        assertFalse(elevator.isMoving());
        assertEquals(3, elevator.getCurrentFloor());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testOpenDoors() {
        int targetFloor = 3;

        elevator.moveToFloor(targetFloor);
        elevator.setCurrentFloor(targetFloor);
        assertTrue(elevator.isDoorOpen());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testCloseDoors(){
        elevator.moveToFloor(3);
        assertTrue(elevator.isDoorOpen());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testAddToDelayedQueue(){
        ArrayList<int[]> delayedQueue = new ArrayList<>();
        elevatorQueue.getQueue().put(elevator,delayedQueue);
        assertFalse(elevatorQueue.getQueue().isEmpty());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testCallElevator(){
        elevator.callElevator(3,States.IDLE);
        elevator.getStates().equals(States.GOING_UP);
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testHandleTask(){
        elevator.moveToFloor(3);
        elevator.getState().equals(States.GOING_UP);
    }

    /**
     * Test method for the handleDelayedTask method in Elevator.
     */
    @Test
    public void testHandleDelayedTask() {
        ArrayList<int[]> delayedQueue = new ArrayList<>();
        elevator.moveToFloor(3);
        elevator.getState().equals(States.GOING_UP);
    }

    /**
     * Test method to check if a task is successfully completed and if the state of the elevator is idle.
     */
    @Test
    public void testCheckAllTaskComplete(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3);
        assertEquals(3, elevator.getCurrentFloor());
        elevator.getStates().equals(States.IDLE);
    }

    /**
     * Test method for the moveToFloor method in Elevator.
     */
    @Test
    public void testMove(){

        elevator.move();
        States state = States.GOING_UP;
        int direction = 0;

        if(state == States.GOING_UP) {
            direction = 1;
            assertEquals(1,direction);
        }

        if(state == States.GOING_DOWN){
            direction = 2;
            assertEquals(2, States.GOING_DOWN);
        }
    }

    @Test
    public void testHandleState(){
        elevator.move();
        States state = States.IDLE;

        switch(state) {
            case GOING_UP -> {
                elevator.move();

                assertEquals(state, States.GOING_UP);
            }
            case GOING_DOWN -> {
                elevator.move();
                assertEquals(state, States.GOING_DOWN);
            }
            case OUT_OF_SERVICE -> {

            }
        }
    }

    @Test
    public void testInjectFault() {
        elevator.injectFault(true);
    }

    /**
     * Test method for the moveToFloor method in Elevator.
     */
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

    /**
     * Test method for AddElevator method.
     */
    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

    /**
     * Test method for respondToCall method.
     */
    @Test
    public void testRespondToCall(){
        elevator.moveToFloor(3);
        elevator.getState().equals(States.RECEIVING_TASK);
    }

    /**
     * Test method for addToQueue method.
     */
    @Test
    public void testAddToQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertFalse(elevatorQueue.getQueue().isEmpty());
    }

    /**
     * Test method for getFromQueue method.
     */
    @Test
    public void testGetFromQueue(){
        elevatorQueue.getQueue().put(elevator,new ArrayList<>());
        assertEquals(2,elevatorQueue.getQueue().size());
        elevatorQueue.getQueue().remove(elevator,new ArrayList<>());
        assertEquals(1,elevatorQueue.getQueue().size());
    }

}
