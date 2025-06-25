package GUI_Z1_PT_S33623;

public enum Direction {

    UP(1),
    DOWN(-1),
    IDLE(0);

    public final int step;

    Direction(int step) {
        this.step = step;
    }

    public Direction opposite() {
        return this == UP ? DOWN : (this == DOWN ? UP : IDLE);
    }
}