package GUI_Z1_PT_S33623;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Floor {

    public final int number;
    public final List<Passenger> queue = new CopyOnWriteArrayList<>();
    public final JButton callButton = new JButton("▲  ▼");

    public Floor(int number) {
        this.number = number;
        callButton.setFocusable(false);
        callButton.setEnabled(false);
    }

    public boolean hasWaiting() {
        return !queue.isEmpty();
    }
}