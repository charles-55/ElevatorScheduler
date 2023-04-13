import javax.swing.*;
import java.awt.*;

public class ElevatorShaftPanel extends JPanel {

    private final ElevatorPanel elevatorPanel;
    private JPanel[] floors;

    public ElevatorShaftPanel(ElevatorPanel elevatorPanel) {
        this.elevatorPanel = elevatorPanel;

        initializeShaft();
        this.setMaximumSize(new Dimension(ElevatorPanel.WIDTH, ElevatorPanel.HEIGHT * Floor.NUM_OF_FLOORS));
    }

    private void initializeShaft() {
        floors = new JPanel[Floor.NUM_OF_FLOORS];

        for(int i = 0; i < Floor.NUM_OF_FLOORS; i++) {
            JPanel panel = new JPanel();
            panel.setBackground(Color.BLACK);
            panel.setMaximumSize(new Dimension(ElevatorPanel.WIDTH, ElevatorPanel.HEIGHT));
            panel.setMinimumSize(new Dimension(ElevatorPanel.WIDTH, ElevatorPanel.HEIGHT));
            panel.setPreferredSize(new Dimension(ElevatorPanel.WIDTH, ElevatorPanel.HEIGHT));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            floors[i] = panel;
        }
        floors[elevatorPanel.getCurrentFloor() - 1] = elevatorPanel;

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        for(int i = Floor.NUM_OF_FLOORS - 1; i >= 0; i--)
            this.add(floors[i]);
    }

    public ElevatorPanel getElevatorPanel() {
        return elevatorPanel;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(floors == null)
            return;

        for(int i = Floor.NUM_OF_FLOORS - 1; i >= 0; i--)
            this.remove(floors[i]);
        elevatorPanel.updateUI();
        initializeShaft();
    }
}
