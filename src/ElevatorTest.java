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
    private Elevator elevator;
    private HashMap<Elevator, ArrayList<Integer>> queue;


    /**
     * Method that sets up the objects before the tests.
     */
    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floorSubsystem = new FloorSubsystem();
        floor = new Floor(1, floorSubsystem);
        elevator = new Elevator(1, Floor.NUM_OF_FLOORS,scheduler);
        queue = new HashMap<>();
    }

    /**
     * Method that closes everything after the tests.
     */
    @After
    public void tearDown() {
        scheduler.closeSocket();
        floorSubsystem.closeSocket();

        scheduler = null;
        floorSubsystem = null;
        floor = null;
        elevator = null;
    }

    ///////////////////////////////////////////ELEVATOR CLASS TESTS/////////////////////////////////////////////////////

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testIsDoorOpen(){
        elevator.openDoors();
        assertTrue(elevator.isDoorOpen());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testIsMoving(){
        assertEquals(1, elevator.getCurrentFloor());
        assertFalse(elevator.isMoving());
        assertEquals(1, elevator.getCurrentFloor());
    }

    @Test
    public void testCheckForStateUpdates(){
        assertTrue(elevator.getStates().equals(States.IDLE));
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testOpenDoors() {
        int targetFloor = 3;

        elevator.isMoving();
        elevator.setCurrentFloor(targetFloor);
        assertFalse(elevator.isDoorOpen());
    }

    /**
     * Test method for the related method in Elevator.
     */
    @Test
    public void testCloseDoors(){
        elevator.isMoving();
        assertFalse(elevator.isDoorOpen());
    }

    @Test
    public void testHandleState(){
        elevator.move();
        States state = States.IDLE;


        assertTrue(elevator.getStates().equals(States.GOING_UP));

//        switch(state) {
//            case GOING_UP -> {
//                elevator.move();
//                assertTrue(elevator.getStates().equals(States.GOING_UP));
//            }
//            case GOING_DOWN -> {
//                elevator.move();
//                assertTrue(elevator.getStates().equals(States.GOING_DOWN));
//            }
//            case OUT_OF_SERVICE -> {
//                assertTrue(elevator.getStates().equals(States.OUT_OF_SERVICE));
//            }
//        }
    }



//    /**
//     * Test method to check if a task is successfully completed and if the state of the elevator is idle.
//     */
//    @Test
//    public void testCheckAllTaskComplete(){
//        assertEquals(1, elevator.getCurrentFloor());
//        elevator.moveToFloor(3);
//        assertEquals(3, elevator.getCurrentFloor());
//        elevator.getStates().equals(States.IDLE);
//    }
//
//    /**
//     * Test method for the moveToFloor method in Elevator.
//     */
//    @Test
//    public void testMove(){
//
//        elevator.move();
//        States state = States.GOING_UP;
//        int direction = 0;
//
//        if(state == States.GOING_UP) {
//            direction = 1;
//            assertEquals(1,direction);
//        }
//
//        if(state == States.GOING_DOWN){
//            direction = 2;
//            assertEquals(2, States.GOING_DOWN);
//        }
//    }

    @Test
    public void testInjectFault() {
        elevator.injectFault(true);
    }


    @Test
    public void testRun() {

    }


}
