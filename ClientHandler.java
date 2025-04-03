import java.awt.Color;
import java.io.*;
import java.net.*;
import java.util.*;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private List<ClientHandler> clients;
    private Map<String, Player> playerStates;

    public ClientHandler(Socket socket, List<ClientHandler> clients, Map<String, Player> playerStates) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.playerStates = playerStates;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            out.println("Enter your username:");
            username = in.readLine();
            Player newPlayer = new Player(username, 100, 100, Color.GREEN);
            playerStates.put(username, newPlayer);
            broadcast("[SERVER] " + username + " has joined the game.");

            String input;
            while ((input = in.readLine()) != null) {
                if (input.contains(",")) { // position update: id,x,y
                    String[] parts = input.split(",");
                    if (parts.length == 3) {
                        int x = Integer.parseInt(parts[1]);
                        int y = Integer.parseInt(parts[2]);
                        Player p = playerStates.get(parts[0]);
                        if (p != null) {
                            p.setX(x);
                            p.setY(y);
                        }
                        Server.broadcastPositions();
                    }
                } else {
                    broadcast("[" + username + "]: " + input);
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
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
    }
}