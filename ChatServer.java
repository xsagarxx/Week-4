import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // List of connected clients
    private static List<ClientThread> clients = new ArrayList<ClientThread>();

    public static void main(String[] args) {
        try {
            // Start server socket
            ServerSocket serverSocket = new ServerSocket(8888);
            System.out.println("Chat server started on port 8888");

            // Listen for incoming client connections
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientThread clientThread = new ClientThread(clientSocket);
                clients.add(clientThread);
                clientThread.start();
                System.out.println("New client connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Broadcast a message to all connected clients
    public static synchronized void broadcast(String message) {
        for (ClientThread client : clients) {
            client.sendMessage(message);
        }
    }

    // Remove a disconnected client from the list
    public static synchronized void removeClient(ClientThread client) {
        clients.remove(client);
        System.out.println("Client disconnected");
    }
}

class ClientThread extends Thread {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientThread(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            // Get client's username
            String username = in.readLine();
            System.out.println(username + " joined the chat");

            // Send welcome message to client
            out.println("Welcome to the chat, " + username + "!");

            // Listen for incoming messages
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(username + ": " + message);
                ChatServer.broadcast(username + ": " + message);
            }

            // Client disconnected
            ChatServer.removeClient(this);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message to the client
    public void sendMessage(String message) {
        out.println(message);
    }
}