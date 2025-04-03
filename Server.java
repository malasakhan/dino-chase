
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


public class Server {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("[SERVER] Listening on port " + PORT);

        while (true) {
            Socket clientSocket = listener.accept();
            ClientHandler clientThread = new ClientHandler(clientSocket, clients);
            clients.add(clientThread);
            pool.execute(clientThread);
        }
    }
}