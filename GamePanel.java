import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;
import javax.swing.Timer; // for the countdown timer

class GamePanel extends JPanel implements KeyListener {
    private final int SPEED = 5;
    private String myId = "me";
    private Map<String, Player> allPlayers = new HashMap<>();
    private TriConsumer<String, Integer, Integer> networkSender;
    private int timeRemaining = 30;

    public GamePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        // Timer countdown
        new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
                repaint();
            }
        }).start();
    }

    public void setMyId(String id) {
        this.myId = id;
        allPlayers.putIfAbsent(id, new Player(id, 100, 100, Color.BLUE));
    }

    public String getMyId() {
        return myId;
    }

    public void setNetworkSender(TriConsumer<String, Integer, Integer> sender) {
        this.networkSender = sender;
    }

    public void updateAllPlayers(Map<String, Player> updated) {
        this.allPlayers = updated;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Player me = allPlayers.get(myId);
        if (me == null) return;

        for (Player p : allPlayers.values()) {
            // Draw player
            g.setColor(p.isChaser() ? Color.RED : Color.BLUE);
            g.fillOval(p.x(), p.y(), p.size(), p.size());

            // Draw player name
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.drawString(p.id(), p.x(), p.y() - 5);
        }

        // Draw timer
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Time: " + timeRemaining + "s", 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        Player me = allPlayers.get(myId);
        if (me == null) return;
        int dx = 0, dy = 0;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> dy = -SPEED;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> dy = SPEED;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> dx = -SPEED;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> dx = SPEED;
        }
        if (networkSender != null) networkSender.accept(myId, me.x() + dx, me.y() + dy);
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
