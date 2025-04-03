
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class GamePanel extends JPanel implements KeyListener {
    private int x = 100, y = 100;
    private final int SIZE = 40;
    private final int SPEED = 5;

    public GamePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.GREEN);
        g.fillOval(x, y, SIZE, SIZE); // Represents the player
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        switch (key) {
            case KeyEvent.VK_W -> y -= SPEED;
            case KeyEvent.VK_S -> y += SPEED;
            case KeyEvent.VK_A -> x -= SPEED;
            case KeyEvent.VK_D -> x += SPEED;
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
