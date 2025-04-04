import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

class ClientHandler implements Runnable {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private String username;
    private final List<ClientHandler> clients;
    private final Map<String, Player> playerStates;

    public ClientHandler(Socket socket, List<ClientHandler> clients, Map<String, Player> playerStates) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.playerStates = playerStates;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public String getUsername() {
        return username;
    }

    public void run() {
        try {
            out.println("Enter your username:");
            username = in.readLine();

            Random rand = new Random();
            int x = 50 + rand.nextInt(600); // wider range
            int y = 50 + rand.nextInt(400);
            Player newPlayer = new Player(username, x, y, Color.BLUE);

            playerStates.put(username, newPlayer);
            broadcast("[SERVER] " + username + " has joined the game.");

            Server.tryStartGame();
            Server.broadcastPositions();

            String input;
            while ((input = in.readLine()) != null) {
                if (input.contains(",")) {
                    String[] parts = input.split(",");
                    if (parts.length >= 3) {
                        int xPos = Integer.parseInt(parts[1]);
                        int yPos = Integer.parseInt(parts[2]);
                        Player p = playerStates.get(parts[0]);
                        if (p != null) {
                            p.setX(xPos);
                            p.setY(yPos);
                        }
                        Server.checkForTags();
                        Server.broadcastPositions();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Connection lost with " + username);
        } finally {
            try {
                clients.remove(this);
                playerStates.remove(username);
                broadcast("[SERVER] " + username + " has left the game.");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(String msg) {
        out.println(msg);
        out.flush();
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
    }
}
