package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * ChatServer - Entry point for the group chat server.
 * 
 * This server listens on a specific port and spawns a new thread
 * for each client that connects using the ClientHandler class.
 */
public class ChatServer {

    // The server socket that listens for client connections
    private ServerSocket serverSocket;

    /**
     * Constructor for ChatServer
     * 
     * @param serverSocket The socket to use for listening to connections
     */
    public ChatServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**
     * Starts the chat server and listens for client connections.
     * Each client is handled in a separate thread.
     */
    public void startServer() {
        try {
            while (!serverSocket.isClosed()) {
                // Wait and accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("[SERVER] A new client has connected!");

                // Create and start a handler thread for this client
                ClientHandler clientHandler = new ClientHandler(socket);
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Error while accepting client.");
            e.printStackTrace();
            closeServerSocket();
        }
    }

    /**
     * Safely closes the server socket.
     */
    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("[SERVER] Server socket closed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method - Entry point of the server program.
     */
    public static void main(String[] args) throws IOException {
        // Server listens on port 5000
        ServerSocket serverSocket = new ServerSocket(5000);
        ChatServer server = new ChatServer(serverSocket);
        System.out.println("[SERVER] Chat server started on port 5000.");
        server.startServer();
    }
}
