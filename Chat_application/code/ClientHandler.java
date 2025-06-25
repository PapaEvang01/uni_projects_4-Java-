package chat;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Handles communication for a single client in the group chat.
 * Broadcasts messages to all other connected clients.
 */
public class ClientHandler implements Runnable {

    // Static list of all active client handlers (shared across all instances)
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUserName;

    /**
     * Initializes streams and announces the user to the chat.
     */
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            
            // First message from the client is their username
            this.clientUserName = bufferedReader.readLine();
            
            clientHandlers.add(this);
            broadCastMessage("SERVER: " + clientUserName + " has entered the chat!");
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Continuously listens for messages from the client and broadcasts them.
     */
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCastMessage(messageFromClient);
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    /**
     * Sends a message to all clients except the sender.
     */
    public void broadCastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientUserName.equals(this.clientUserName)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeAll(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    /**
     * Removes this client from the list and notifies others.
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage("SERVER: " + clientUserName + " has left the chat.");
    }

    /**
     * Closes all client resources and prints a server-side disconnection message.
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler(); // Notify others first

        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();

            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            System.out.println("[" + time + "] Client '" + clientUserName + "' has disconnected.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
