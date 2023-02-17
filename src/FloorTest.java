import org.junit.*;
import static org.junit.Assert.assertEquals;

/**
 * The Floor Test Class.
 *
 * @author Oyindamola Taiwo-Olupeka 101155729
 * @version 1.0
 */
public class FloorTest {
    private Scheduler scheduler;
    private Floor floor;
    private FloorSubsystem floorSubsystem;
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
    public void testRun(){
        scheduler = new Scheduler();
        elevator = new Elevator(NUM_OF_FLOORS, scheduler);

        //////////////******FIX
    }

    @Test
    public void testParseData(){

        //////////////******FIX
    }
}
