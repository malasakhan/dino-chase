import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

class GamePanel extends JPanel implements KeyListener {
    private final int SPEED = 5;
    private String myId = "me";
    private Map<String, Player> allPlayers = new HashMap<>();
    private TriConsumer<String, Integer, Integer> networkSender;

    public GamePanel() {
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);
    }

    public void setMyId(String id) {
        this.myId = id;
        allPlayers.putIfAbsent(id, new Player(id, 100, 100, Color.GREEN));
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
        System.out.println("My ID: " + myId);
        System.out.println("Players in map: " + updated.keySet());
        
        

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Player p : allPlayers.values()) {
            
            g.setColor(p.isChaser() ? Color.RED : Color.GREEN);
            g.fillOval(p.x(), p.y(), p.size(), p.size());
        }
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
        me.move(dx, dy);
        repaint();
        if (networkSender != null) networkSender.accept(myId, me.x(), me.y());
    }
    

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}