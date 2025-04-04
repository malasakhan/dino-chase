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
            if (line == null) break;

            if (line.startsWith("Enter your username:")) {
                String name = JOptionPane.showInputDialog(frame, line);
                gamePanel.setMyId(name);
                out.println(name);

            } else if (line.startsWith("LEADERBOARD:")) {
                // Read full leaderboard content
                String board = line.substring("LEADERBOARD:".length());
                String[] lines = board.split(";");
                StringBuilder formatted = new StringBuilder();
                for (String lineEntry : lines) {
                    formatted.append(lineEntry.trim()).append("\n");
                }
                showLeaderboard(formatted.toString());


                break;

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

                        Player p = new Player(id, x, y, Color.BLUE);
                        p.setChaser(isChaser);
                        newState.put(id, p);
                    }
                }
                gamePanel.updateAllPlayers(newState);
            }
        }
    }

    /*private void showLeaderboard(String text) {
        JFrame leaderboardFrame = new JFrame("🏁 Final Results");
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 14));
        leaderboardFrame.add(new JScrollPane(area));
        leaderboardFrame.setSize(300, 200);
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setVisible(true);
    }*/
    private void showLeaderboard(String text) {
        System.out.println("[CLIENT] Displaying leaderboard GUI...");
    
        JFrame leaderboardFrame = new JFrame("🏁 Final Results");
        leaderboardFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 16));
    
        JScrollPane scrollPane = new JScrollPane(area);
        leaderboardFrame.getContentPane().add(scrollPane);
    
        leaderboardFrame.setSize(400, 300);
        leaderboardFrame.setLocationRelativeTo(null);
        leaderboardFrame.setAlwaysOnTop(true);   
        leaderboardFrame.setVisible(true);
        leaderboardFrame.toFront();              
        leaderboardFrame.requestFocus();
    }
    

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }
}
