package GUI_Z1_PT_S33623;

import javax.swing.*;

public class ElevatorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ElevatorGUI gui = new ElevatorGUI();
            gui.setVisible(true);
        });
    }
}
