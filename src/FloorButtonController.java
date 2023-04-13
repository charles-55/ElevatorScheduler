import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FloorButtonController implements ActionListener {

    private final int floorNum;
    private FloorSubsystem floorSubsystem;

    public FloorButtonController(int floorNum, FloorSubsystem floorSubsystem) {
        this.floorNum = floorNum;
        this.floorSubsystem = floorSubsystem;
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        floorSubsystem.callElevator(floorNum, Integer.parseInt(e.getActionCommand()));
    }
}
