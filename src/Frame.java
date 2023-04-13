import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Frame extends JFrame {
    private final FloorSubsystem floorSubsystem;
    private final ArrayList<FloorPanel> floorPanels;
    private final ArrayList<ElevatorShaftPanel> shaftPanels;
    public static final Color ON = Color.GREEN;
    public static final Color OFF = Color.GRAY;

    public Frame(FloorSubsystem floorSubsystem) {
        super("Elevator Simulation");
        this.floorSubsystem = floorSubsystem;
        this.floorSubsystem.setFrame(this);
        floorPanels = new ArrayList<>();
        shaftPanels = new ArrayList<>();

        for(Floor floor : floorSubsystem.getFloors())
            floorPanels.add(new FloorPanel(floor, floorSubsystem));

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
        for(int elevatorNum : floorSubsystem.getElevatorInfo().keySet()) {
            int[] arr = floorSubsystem.getElevatorInfo().get(elevatorNum);
            shaftPanels.add(new ElevatorShaftPanel(new ElevatorPanel(elevatorNum, arr[0], States.getDatagramValueState(arr[2]),floorSubsystem)));
        }
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

    public void updateUI(int elevatorNum, int currentFloor, States state) {
        for(FloorPanel floorPanel : floorPanels)
            floorPanel.updateUI();
        for(ElevatorShaftPanel shaftPanel : shaftPanels) {
            if(shaftPanel.getElevatorPanel().getElevatorNum() == elevatorNum) {
                shaftPanel.getElevatorPanel().setCurrentFloor(currentFloor);
                shaftPanel.getElevatorPanel().setElevatorState(state);
            }
            shaftPanel.updateUI();
        }
    }

    public void refresh() {
        for(FloorPanel floorPanel : floorPanels)
            floorPanel.updateUI();
        for(ElevatorShaftPanel shaftPanel : shaftPanels)
            shaftPanel.updateUI();
    }
}
