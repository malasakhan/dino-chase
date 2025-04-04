import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final int PORT = 2005;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, Player> playerStates = new ConcurrentHashMap<>();
    private static String chaserId = null;
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);
    private static boolean gameEnded = false;

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

    public static void tryStartGame() {
        if (playerStates.size() >= 2 && chaserId == null) {
            List<String> ids = new ArrayList<>(playerStates.keySet());
            chaserId = ids.get(new Random().nextInt(ids.size()));
            playerStates.get(chaserId).setChaser(true);
            System.out.println("[GAME] Chaser is " + chaserId);

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(Server::endGame, 30, TimeUnit.SECONDS);
        }
    }

    public static void endGame() {
        if (gameEnded) return;
        gameEnded = true;
        System.out.println("[GAME] Time's up!");

        List<Player> ranking = new ArrayList<>(playerStates.values());
        ranking.sort((a, b) -> Integer.compare(b.getTagCount(), a.getTagCount()));

        StringBuilder leaderboard = new StringBuilder("LEADERBOARD:");
        for (int i = 0; i < ranking.size(); i++) {
            Player p = ranking.get(i);
            leaderboard.append((i + 1)).append(". ")
                .append(p.id()).append(" – ")
                .append(p.getTagCount()).append(" tag(s)");
            if (i < ranking.size() - 1) leaderboard.append(";");
        }
        for (ClientHandler client : clients) {
            client.send(leaderboard.toString());
        }

        String message = leaderboard.toString().trim();
        System.out.println("[SERVER] Sending leaderboard:\n" + message);
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public static void broadcastPositions() {
        if (gameEnded) return;
        StringBuilder sb = new StringBuilder();
        for (Player p : playerStates.values()) {
            sb.append(p.id()).append(",")
              .append(p.x()).append(",")
              .append(p.y()).append(",")
              .append(p.isChaser() ? "1" : "0").append(",")
              .append(p.getTagCount()).append(";");
        }
        String message = sb.toString();
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public static void checkForTags() {
        if (chaserId == null || gameEnded) return;
        Player chaser = playerStates.get(chaserId);
        for (Player p : playerStates.values()) {
            if (!p.id().equals(chaserId)) {
                if (Math.abs(p.x() - chaser.x()) < 40 && Math.abs(p.y() - chaser.y()) < 40) {
                    p.setChaser(true);
                    chaser.setChaser(false);
                    chaserId = p.id();
                    System.out.println("[TAG] " + p.id() + " is now the new chaser!");
                    break;
                }
            }
        }
    }
}
