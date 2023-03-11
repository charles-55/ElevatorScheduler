import javax.swing.*;
import java.awt.*;

public class FloorPanel extends JPanel {

    private final Floor floor;
    private final JButton[] buttons;
    public static final int WIDTH = 500, HEIGHT = 200;

    public FloorPanel(Floor floor) {
        this.floor = floor;
        buttons = new JButton[] {generateOffButton("UP"), generateOffButton("DOWN")};

        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(buttons[0]);
        panel.add(buttons[1]);
        panel.setMaximumSize(new Dimension(50, 50));

        this.setLayout(new BorderLayout());
        this.add(panel, BorderLayout.LINE_START);
        this.setMaximumSize(new Dimension(WIDTH, HEIGHT));
    }

    private JButton generateOffButton(String name) {
        JButton button = new JButton(name);
        button.setBackground(ProjectFrame.OFF);
        return button;
    }

    @Override
    public void updateUI() {
        super.updateUI();

        if(floor == null)
            return;

        if(floor.getButtonsAndLamps().get(1))
            buttons[0].setBackground(ProjectFrame.ON);
        else
            buttons[0].setBackground(ProjectFrame.ON);
        if(floor.getButtonsAndLamps().get(2))
            buttons[1].setBackground(ProjectFrame.ON);
        else
            buttons[1].setBackground(ProjectFrame.ON);
    }
}
