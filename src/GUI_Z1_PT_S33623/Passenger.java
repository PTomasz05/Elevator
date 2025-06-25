package GUI_Z1_PT_S33623;

import java.awt.*;
import java.util.Random;

public class Passenger {

    public final int origin;
    public int destination;
    public final Color color;

    public Passenger(int origin) {
        this.origin = origin;
        this.destination = -1;
        Random rand = new Random();
        this.color = new Color(rand.nextInt(200), rand.nextInt(200), rand.nextInt(255));
    }
}