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
    private Elevator elevator;
    private ElevatorCallEvent event;
    private static int NUM_OF_FLOORS = 5;


    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floor = new Floor(1, scheduler);
        elevator = new Elevator(NUM_OF_FLOORS, scheduler);
    }

    @After
    public void tearDown() {
        scheduler = null;
        floor = null;
        elevator = null;
    }

    @Test
    public void testIsMoving(){
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, ElevatorCallEvent.Direction.UP);
        assertEquals(false,elevator.isMoving());
        assertEquals(3, elevator.getCurrentFloor());
    }

    @Test
    public void testOpenDoors() {
        int targetFloor = 3;

        elevator.moveToFloor(targetFloor, ElevatorCallEvent.Direction.UP);
        elevator.setCurrentFloor(targetFloor);
        assertTrue(elevator.isDoorOpen() ==true);
    }

    @Test
    public void testCloseDoors(){
        elevator.moveToFloor(3, ElevatorCallEvent.Direction.UP);
        assertTrue(elevator.isDoorOpen() != false);
    }

    @Test
    public void testMoveToFloor() {
        assertEquals(1, elevator.getCurrentFloor());
        elevator.moveToFloor(3, ElevatorCallEvent.Direction.UP);
        assertEquals(3, elevator.getCurrentFloor());
    }

    @Test
    public void testRun(){
        scheduler = new Scheduler();
        elevator = new Elevator(NUM_OF_FLOORS, scheduler);

        scheduler.getFromQueue(elevator);



        //////////////******FIX

    }
//    public void run() {
//        while(true) {
//            scheduler.getFromQueue(this);
//            for(int destinationFloor : buttonsAndLamps.keySet()) {
//                if(buttonsAndLamps.get(destinationFloor)) {
//                    moveToFloor(destinationFloor, direction);
//                    buttonsAndLamps.put(destinationFloor, false);
//                }
//            }
//        }
//    }
}
