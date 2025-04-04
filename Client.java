import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 2005;

    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Dino Chase");
    private GamePanel gamePanel;

    public Client() {
        gamePanel = new GamePanel();
        frame.getContentPane().add(gamePanel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        gamePanel.setNetworkSender((id, x, y) -> out.println(id + "," + x + "," + y));
        gamePanel.requestFocusInWindow();
    }

    private void run() throws IOException {
        Socket socket = new Socket(SERVER_IP, PORT);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        while (true) {
            String line = in.readLine();
            if (line.startsWith("Enter your username:")) {
                String name = JOptionPane.showInputDialog(frame, line);
                gamePanel.setMyId(name);
                out.println(name);
            } else if (line.contains(",")) {
                String[] playerChunks = line.split(";");
Map<String, Player> newState = new HashMap<>();
for (String chunk : playerChunks) {
    String[] parts = chunk.split(",");
    if (parts.length >= 4) {
        String id = parts[0];
        int x = Integer.parseInt(parts[1]);
        int y = Integer.parseInt(parts[2]);
        boolean isChaser = parts[3].equals("1");

        Player p = new Player(id, x, y, Color.GREEN);
        p.setChaser(isChaser);  
        newState.put(id, p);
    }
}
gamePanel.updateAllPlayers(newState);

            }
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }
}