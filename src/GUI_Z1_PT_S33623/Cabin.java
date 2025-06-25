package GUI_Z1_PT_S33623;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Cabin {

    public static final int MAX_CAPACITY = 5;
    public int currentFloor = 0;
    public Direction direction = Direction.IDLE;
    public final List<Passenger> passengers = new CopyOnWriteArrayList<>();
    public final JToggleButton[] destButtons = new JToggleButton[10];
    public final boolean[] targets = new boolean[10];

    public Cabin() {
        for (int i = 0; i < 10; i++) {
            destButtons[i] = new JToggleButton(String.valueOf(i));
            destButtons[i].setEnabled(false);
        }
    }

    public void reset() {
        currentFloor = 0;
        direction = Direction.IDLE;
        passengers.clear();
        Arrays.fill(targets, false);
        for (JToggleButton button : destButtons) {
            button.setSelected(false);
        }
    }

    public boolean hasRoom() {
        return passengers.size() < MAX_CAPACITY;
    }

    public boolean hasTargets() {
        for (boolean target : targets) {
            if (target) {
                return true;
            }
        }
        return false;
    }

    public OptionalInt nextTargetInDirection() {
        if (direction == Direction.UP) {
            for (int f = currentFloor + 1; f < 10; f++) {
                if (targets[f]) {
                    return OptionalInt.of(f);
                }
            }
        } else if (direction == Direction.DOWN) {
            for (int f = currentFloor - 1; f >= 0; f--) {
                if (targets[f]) {
                    return OptionalInt.of(f);
                }
            }
        }
        return OptionalInt.empty();
    }
}