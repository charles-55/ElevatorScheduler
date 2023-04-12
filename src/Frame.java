import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Frame extends JFrame {
    private final FloorSubsystem floorSubsystem;
    private final ArrayList<FloorPanel> floorPanels;
    private final ArrayList<ElevatorShaftPanel> shaftPanels;
    public static final Color ON = Color.GREEN;
    public static final Color OFF = Color.GRAY;

    public Frame(FloorSubsystem floorSubsystem, ArrayList<Floor> floors) {
        super("Elevator Simulation");
        this.floorSubsystem = floorSubsystem;
        floorPanels = new ArrayList<>();
        shaftPanels = new ArrayList<>();

        for(Floor floor : floors)
            floorPanels.add(new FloorPanel(floor));

        generateElevatorShaftPanels();

        JScrollPane scrollPane = new JScrollPane(generateFrameContent());
        JScrollBar scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, 10, 0, 100);
        scrollPane.setVerticalScrollBar(scrollBar);

        this.add(scrollPane);
        this.addWindowListener(new Controller(floorSubsystem));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(1050, 800);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    private void generateElevatorShaftPanels() {
        for(Elevator elevator : floorSubsystem.getElevators())
            shaftPanels.add(new ElevatorShaftPanel(new ElevatorPanel(elevator)));
    }

    private JPanel generateFrameContent() {
        JPanel mainFramePanel = new JPanel();
        JPanel panel = new JPanel();
        mainFramePanel.setLayout(new BoxLayout(mainFramePanel, BoxLayout.X_AXIS));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for(int i = floorPanels.size() - 1; i >= 0; i--)
            panel.add(floorPanels.get(i));
        for(ElevatorShaftPanel shaftPanel : shaftPanels)
            mainFramePanel.add(shaftPanel);
        mainFramePanel.add(panel);

        return mainFramePanel;
    }

    public void updateUI() {
        for(FloorPanel floorPanel : floorPanels)
            floorPanel.updateUI();
        for(ElevatorShaftPanel shaftPanel : shaftPanels)
            shaftPanel.updateUI();
    }
}