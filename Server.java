import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, Player> playerStates = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("[SERVER] Listening on port " + PORT);

        while (true) {
            Socket clientSocket = listener.accept();
            ClientHandler clientThread = new ClientHandler(clientSocket, clients, playerStates);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }

    public static void broadcastPositions() {
        StringBuilder sb = new StringBuilder();
        for (Player p : playerStates.values()) {
            sb.append(p.id()).append(",").append(p.x()).append(",").append(p.y()).append(";");
        }
        String message = sb.toString();
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }
}