import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ProjectFrameListener implements WindowListener {

    private final ProjectModel model;

    public ProjectFrameListener(ProjectModel model) {
        this.model = model;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        model.end();
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
}
