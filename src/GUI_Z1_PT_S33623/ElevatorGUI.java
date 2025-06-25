package GUI_Z1_PT_S33623;

import javax.swing.*;
import java.awt.*;

public class ElevatorGUI extends JFrame {

    public ElevatorGUI() {
        super("Symulator windy");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        ElevatorController controller = new ElevatorController();
        ElevatorView view = new ElevatorView(controller);
        controller.setView(view);

        JButton startBtn = new JButton("START");
        startBtn.addActionListener(_ -> controller.initAndStart());
        controller.setStartButton(startBtn);

        JPanel floorButtonsPanel = new JPanel(new GridLayout(10, 1, 5, 5));
        floorButtonsPanel.setBorder(BorderFactory.createTitledBorder("Wezwania"));
        for (int i = 9; i >= 0; i--) {
            floorButtonsPanel.add(controller.floors.get(i).callButton);
        }

        JPanel cabinButtonsPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        cabinButtonsPanel.setBorder(BorderFactory.createTitledBorder("Cele (Wskaźniki)"));
        for (JToggleButton b : controller.cabin.destButtons) {
            cabinButtonsPanel.add(b);
        }

        add(view, BorderLayout.CENTER);
        add(floorButtonsPanel, BorderLayout.EAST);
        add(cabinButtonsPanel, BorderLayout.WEST);
        add(startBtn, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(600, 750));
    }
}