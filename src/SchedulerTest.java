import org.junit.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * The Scheduler Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class SchedulerTest {
    private Scheduler scheduler;
    private Floor floor;
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
        scheduler = null;
        floor = null;
        elevator = null;
    }

    @Test
    public void testAddElevator(){
        assertEquals(0, queue.size());
        queue.put(elevator, new ArrayList<>());
        assertEquals(1, queue.size());
    }

    @Test
    public void testAddToQueue(){
        scheduler.getQueue().put(elevator,new ArrayList<>());
        assertFalse(scheduler.getQueue().isEmpty());
    }

    @Test
    public void testGetFromQueue(){
        scheduler.getQueue().put(elevator,new ArrayList<>());
        assertEquals(1,scheduler.getQueue().size());
        scheduler.getQueue().remove(elevator,new ArrayList<>());
        assertTrue(scheduler.getQueue().isEmpty());
    }



}
