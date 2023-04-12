import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class Controller implements WindowListener, ActionListener {

    private final FloorSubsystem floorSubsystem;

    public Controller(FloorSubsystem floorSubsystem) {
        this.floorSubsystem = floorSubsystem;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
       // floorSubsystem.closeSocket();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        //e.getActionCommand() =
    }
}
