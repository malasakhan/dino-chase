import java.awt.*;

class Player {
    private final String id;
    private int x, y;
    private final int size = 40;
    private final Color color;
    private boolean isChaser = false;
    private boolean wasChaser = false;
    private int tagCount = 0;

    public Player(String id, int x, int y, Color color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setChaser(boolean c) {
        if (c && !this.isChaser) {
            tagCount++;
            wasChaser = true;
        }
        this.isChaser = c;
    }

    public boolean isChaser() { return isChaser; }
    public boolean wasChaser() { return wasChaser; }
    public int getTagCount() { return tagCount; }
    public String id() { return id; }
    public int x() { return x; }
    public int y() { return y; }
    public int size() { return size; }
    public Color color() { return color; }
}