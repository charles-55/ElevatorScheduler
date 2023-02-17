import org.junit.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import static org.junit.Assert.assertEquals;

/**
 * The Scheduler Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class SchedulerTest {
    private Scheduler scheduler;
    private Floor floor;
    private FloorSubsystem floorSubsystem;
    private Elevator elevator;
    private ElevatorCallEvent event;
    private static int NUM_OF_FLOORS = 5;
    private HashMap<Elevator, ArrayList<Integer>> queue;


    @Before
    public void setUp() {
        scheduler = new Scheduler();
        floor = new Floor(1, scheduler);
        elevator = new Elevator(NUM_OF_FLOORS, scheduler);
        queue = new HashMap<>();
        event = new ElevatorCallEvent(LocalTime.now(),1, ElevatorCallEvent.Direction.UP,3);
    }

    @After
    public void tearDown() {
//        scheduler = null;
//        floor = null;
//        elevator = null;
    }

    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

    @Test
    public void testAddToQueue(){
        assertEquals(0,queue.size());
        queue.get(elevator).add(event.getDestinationFloor());
        assertEquals(1,queue.size());

        //////////////******FIX

    }

    @Test
    public void testGetFromQueue(){
        queue.get(elevator).add(event.getFloorNumber());
        assertEquals(1,queue.size());
        //queue.remove(elevator);
        queue.get(elevator).remove(0);
        assertEquals(0,queue.size());

        //////////////******FIX
    }



}
