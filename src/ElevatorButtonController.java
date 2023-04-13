import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ElevatorButtonController implements ActionListener {

    private final int elevatorNum;
    private FloorSubsystem floorSubsystem;

    public ElevatorButtonController(int elevatorNum, FloorSubsystem floorSubsystem){
        this.elevatorNum = elevatorNum;
        this.floorSubsystem = floorSubsystem;

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        floorSubsystem.elevatorPress(Integer.parseInt(e.getActionCommand()), elevatorNum);
    }
}
