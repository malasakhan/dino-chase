
import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "localhost";
    private static final int PORT = 12345;

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
                out.println(name);
            }
            // Future: handle game data here
        }
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.run();
    }}
