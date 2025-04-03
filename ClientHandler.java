import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private List<ClientHandler> clients;

    public ClientHandler(Socket socket, List<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void run() {
        try {
            out.println("Enter your username:");
            username = in.readLine();
            broadcast("[SERVER] " + username + " has joined the game.");

            String input;
            while ((input = in.readLine()) != null) {
                broadcast("[" + username + "]: " + input);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Connection lost with " + username);
        } finally {
            try {
                clients.remove(this);
                broadcast("[SERVER] " + username + " has left the game.");
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
    }
}