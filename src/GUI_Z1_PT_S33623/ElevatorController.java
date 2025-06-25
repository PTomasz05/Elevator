package GUI_Z1_PT_S33623;

import javax.swing.*;
import javax.swing.Timer;
import java.util.*;
import java.util.List;
import java.util.stream.IntStream;

public class ElevatorController {

    public final List<Floor> floors = IntStream
            .range(0, 10)
            .mapToObj(Floor::new)
            .toList();
    public final Cabin cabin = new Cabin();

    private ElevatorView view;
    private JButton startBtn;

    private final Timer animTimer, doorTimer, endTimer;
    public double cabinY;
    private int targetFloor;
    private static final int SPEED_PX_PER_SEC = 120;
    private long lastStepTimeNs = 0;
    private boolean simulationRunning = false;
    private Passenger awaitingPassenger = null;

    public ElevatorController() {
        animTimer = new Timer(20, _ -> animateElevator());
        doorTimer = new Timer(5000, _ -> leaveFloor());
        doorTimer.setRepeats(false);
        endTimer = new Timer(10000, _ -> endSimulation() );
        endTimer.setRepeats(false);
        setupFloorCallButtons();
        setupDestButtons();
    }

    public void setView(ElevatorView view) {
        this.view = view;
    }

    public void setStartButton(JButton startBtn) {
        this.startBtn = startBtn;
    }

    public void initAndStart() {
        if (simulationRunning) {
            return;
        }
        simulationRunning = true;
        if (startBtn != null) {
            startBtn.setEnabled(false);
        }

        cabin.reset();
        cabinY = view.yForFloor(0);

        Random rnd = new Random();
        for (Floor f : floors) {
            f.queue.clear();
            int n = rnd.nextInt(6);
            for (int i = 0; i < n; i++) {
                f.queue.add(new Passenger(f.number));
            }
            f.callButton.setEnabled(f.hasWaiting());
        }

        view.repaint();
        doorTimer.start();
    }

    private void endSimulation() {
        animTimer.stop();
        doorTimer.stop();
        cabin.direction = Direction.IDLE;
        simulationRunning = false;
        if (startBtn != null) {
            startBtn.setEnabled(true);
        }
        JOptionPane.showMessageDialog(view, "Symulacja zakończona.");
        view.repaint();
    }

    private void animateElevator() {
        if (cabin.direction == Direction.IDLE) {
            animTimer.stop();
            return;
        }

        long now = System.nanoTime();
        if (lastStepTimeNs == 0) {
            lastStepTimeNs = now;
        }
        double deltaTime = (now - lastStepTimeNs) / 1_000_000_000.0;
        lastStepTimeNs = now;

        double dy = (cabin.direction == Direction.UP ? -1 : 1) * SPEED_PX_PER_SEC * deltaTime;
        double nextY = cabinY + dy;
        double targetY = view.yForFloor(targetFloor);

        boolean reached = (cabin.direction == Direction.UP) ? nextY <= targetY : nextY >= targetY;

        if (reached) {
            cabinY = targetY;
            cabin.currentFloor = targetFloor;
            stopAtFloor(targetFloor);
        } else {
            cabinY = nextY;
        }
        view.repaint();
    }

    private void stopAtFloor(int floor) {
        animTimer.stop();

        List<Passenger> disembarking = new ArrayList<>();
        for (Passenger p : cabin.passengers) {
            if (p.destination == floor) {
                disembarking.add(p);
            }
        }
        cabin.passengers.removeAll(disembarking);

        Floor currentFloor = floors.get(floor);
        List<Passenger> newPassengers = new ArrayList<>();
        while (cabin.hasRoom() && currentFloor.hasWaiting()) {
            Passenger p = currentFloor.queue.remove(0);
            cabin.passengers.add(p);
            newPassengers.add(p);
        }
        currentFloor.callButton.setEnabled(currentFloor.hasWaiting());

        view.repaint();

        for (Passenger p : newPassengers) {
            awaitingPassenger = p;
            for (int i = 0; i < cabin.destButtons.length; i++) {
                cabin.destButtons[i].setEnabled(i != p.origin);
            }
        }
        updateTargetsAndButtons();
        doorTimer.restart();
        checkEndCondition();
    }


    private void updateTargetsAndButtons() {
        Arrays.fill(cabin.targets, false);
        for (Passenger p : cabin.passengers) {
            if (p.destination != -1) {
                cabin.targets[p.destination] = true;
            }
        }
        for (int i = 0; i < 10; i++) {
            if (!floors.get(i).callButton.isEnabled() && floors.get(i).hasWaiting()) {
                cabin.targets[i] = true;
            }
        }

        for (int i = 0; i < 10; i++) {
            cabin.destButtons[i].setSelected(cabin.targets[i]);
        }
    }


    private void leaveFloor() {
        chooseNextTarget();
        if (cabin.direction != Direction.IDLE) {
            lastStepTimeNs = 0;
            animTimer.start();
        }
        checkEndCondition();
    }

    private void addCall(int floor) {
        floors.get(floor).callButton.setEnabled(false);
        cabin.targets[floor] = true;
        updateTargetsAndButtons();

        if (!animTimer.isRunning() && !doorTimer.isRunning()) {
            attemptToStart();
        }
    }

    private void chooseNextTarget() {
        OptionalInt next = cabin.nextTargetInDirection();
        if (next.isPresent()) {
            targetFloor = next.getAsInt();
            return;
        }

        cabin.direction = cabin.direction.opposite();
        next = cabin.nextTargetInDirection();
        if (next.isPresent()) {
            targetFloor = next.getAsInt();
            return;
        }

        int nearest = findNearestTarget();
        if (nearest != -1) {
            cabin.direction = (nearest > cabin.currentFloor) ? Direction.UP : Direction.DOWN;
            targetFloor = nearest;
        } else {
            cabin.direction = Direction.IDLE;
        }
    }

    private void attemptToStart() {
        if (cabin.direction == Direction.IDLE && cabin.hasTargets()) {
            chooseNextTarget();
            if (cabin.direction != Direction.IDLE) {
                lastStepTimeNs = 0;
                animTimer.start();
            }
        }
    }

    private int findNearestTarget() {
        int bestFloor = -1;
        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            if (cabin.targets[i]) {
                int distance = Math.abs(i - cabin.currentFloor);
                if (distance < minDistance) {
                    minDistance = distance;
                    bestFloor = i;
                }
            }
        }
        return bestFloor;
    }

    private void checkEndCondition() {
        boolean noCalls      = floors.stream().noneMatch(Floor::hasWaiting);
        boolean cabinIdle    = cabin.passengers.isEmpty() && cabin.direction == Direction.IDLE;

        boolean noTargets = true;
        for (boolean t : cabin.targets) {
            if (t) {
                noTargets = false;
                break;
            }
        }

        if (simulationRunning && noCalls && noTargets && cabinIdle) {
            if (!endTimer.isRunning()) {
                endTimer.restart();
            }
        } else {
            endTimer.stop();
        }
    }

    private void setupFloorCallButtons() {
        for (Floor f : floors) {
            f.callButton.addActionListener(e -> addCall(f.number));
        }
    }

    private void setupDestButtons() {
        for (int i = 0; i < cabin.destButtons.length; i++) {
            int dest = i;
            JToggleButton b = cabin.destButtons[i];
            b.setEnabled(false);
            b.addActionListener(e -> {
                for ( Passenger p : cabin.passengers) {
                    if ( p.destination == -1 ) {
                        p.destination = dest;
                    }
                }
                updateTargetsAndButtons();

                Arrays.stream(cabin.destButtons).forEach(btn -> btn.setEnabled(false));

                if (!animTimer.isRunning() && !doorTimer.isRunning()) {
                        attemptToStart();
                    }
                checkEndCondition();
            });
        }
    }

}