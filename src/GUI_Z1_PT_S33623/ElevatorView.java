package GUI_Z1_PT_S33623;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ElevatorView extends JPanel {

    private final ElevatorController controller;
    private static final int FLOOR_HEIGHT = 65;
    private static final int SHAFT_WIDTH = 100;
    private static final int SHAFT_X = 150;
    private static final int PASSENGER_SIZE = 12;

    public ElevatorView(ElevatorController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(400, FLOOR_HEIGHT * 10));
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawShaftAndFloors(g2);
        drawWaitingPassengers(g2);
        drawElevator(g2);
    }

    private void drawShaftAndFloors(Graphics2D g2) {
        g2.setColor(new Color(230, 230, 230));
        g2.fillRect(SHAFT_X, 0, SHAFT_WIDTH, FLOOR_HEIGHT * 10);
        g2.setColor(Color.GRAY);
        g2.translate(0, 18);
        for (int i = 0; i <= 10; i++) {
            int yLine = i * FLOOR_HEIGHT;
            g2.drawLine(0, yLine, getWidth(), yLine);
            if (i < 10) {
                g2.drawString("Piętro " + (9 - i), 10, yLine + 20);
            }
        }
    }

    private void drawElevator(Graphics2D g2) {
        int y = (int) controller.cabinY;
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(SHAFT_X, y, SHAFT_WIDTH, FLOOR_HEIGHT);
        g2.setColor(Color.BLACK);
        g2.drawRect(SHAFT_X, y, SHAFT_WIDTH, FLOOR_HEIGHT);

        if (controller.cabin.direction != Direction.IDLE) {
            g2.setColor(Color.RED);
            int[] xPoints, yPoints;
            if (controller.cabin.direction == Direction.UP) {
                xPoints = new int[]{SHAFT_X + SHAFT_WIDTH / 2, SHAFT_X + SHAFT_WIDTH / 2 - 10, SHAFT_X + SHAFT_WIDTH / 2 + 10};
                yPoints = new int[]{y + 10, y + 25, y + 25};
            } else {
                xPoints = new int[]{SHAFT_X + SHAFT_WIDTH / 2, SHAFT_X + SHAFT_WIDTH / 2 - 10, SHAFT_X + SHAFT_WIDTH / 2 + 10};
                yPoints = new int[]{y + FLOOR_HEIGHT - 10, y + FLOOR_HEIGHT - 25, y + FLOOR_HEIGHT - 25};
            }
            g2.fillPolygon(xPoints, yPoints, 3);
        }

        int pX = SHAFT_X + 5;
        int pY = y + 5;
        for (Passenger p : controller.cabin.passengers) {
            g2.setColor(p.color);
            g2.fillOval(pX, pY, PASSENGER_SIZE, PASSENGER_SIZE);
            g2.setColor(Color.BLACK);
            g2.drawOval(pX, pY, PASSENGER_SIZE, PASSENGER_SIZE);
            pX += PASSENGER_SIZE + 3;
            if (pX > SHAFT_X + SHAFT_WIDTH - (PASSENGER_SIZE + 5)) {
                pX = SHAFT_X + 5;
                pY += PASSENGER_SIZE + 3;
            }
        }
    }

    private void drawWaitingPassengers(Graphics2D g2) {
        for (Floor floor : controller.floors) {
            int pX = SHAFT_X + SHAFT_WIDTH + 10;
            int pY = yForFloor(floor.number) + (FLOOR_HEIGHT / 2) - (PASSENGER_SIZE / 2);
            for (Passenger p : floor.queue) {
                g2.setColor(p.color);
                g2.fillOval(pX, pY, PASSENGER_SIZE, PASSENGER_SIZE);
                g2.setColor(Color.BLACK);
                g2.drawOval(pX, pY, PASSENGER_SIZE, PASSENGER_SIZE);
                pX += PASSENGER_SIZE + 3;
            }
        }
    }

    public int yForFloor(int floor) {
        return (9 - floor) * FLOOR_HEIGHT;
    }
}