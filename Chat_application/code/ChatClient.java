package chat;

import java.io.*;
import java.net.Socket;
import java.time.LocalTime;
import java.util.Scanner;

/**
 * ChatClient - Connects to a server, authenticates, and joins the group chat.
 * Supports login/register, sending formatted messages, and color-coded output.
 */
public class ChatClient {

    // Networking and user-related fields
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // File where user credentials are stored locally
    private static final String USER_FILE = "users.txt";

    /**
     * Constructor for ChatClient
     */
    public ChatClient(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Sends messages to the server. Handles graceful exit with "/exit".
     */
    public void sendMessage() {
        try {
            // Send username first to identify user on server
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();

                if (messageToSend.equalsIgnoreCase("/exit")) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    System.out.println("[You have left the chat]");
                    break;
                }

                // Format message with timestamp and username
                String time = LocalTime.now().withSecond(0).withNano(0).toString();
                String formattedMessage = "[" + time + "] [" + username + "]: " + messageToSend;

                bufferedWriter.write(formattedMessage);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter);
        }
    }

    /**
     * Listens for messages from the server on a separate thread.
     * Colors own messages green for distinction.
     */
    public void listenForMessages() {
        new Thread(() -> {
            String messageFromGroupChat;
            while (socket.isConnected()) {
                try {
                    messageFromGroupChat = bufferedReader.readLine();
                    if (messageFromGroupChat != null) {
                        if (messageFromGroupChat.contains("[" + username + "]")) {
                            // Your own messages in green
                            System.out.println("\u001B[32m" + messageFromGroupChat + "\u001B[0m");
                        } else {
                            System.out.println(messageFromGroupChat);
                        }
                    }
                } catch (IOException e) {
                    closeAll(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start();
    }

    /**
     * Safely closes all connections and streams.
     */
    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // === AUTHENTICATION METHODS ===

    /**
     * Registers a new user if username does not exist already.
     */
    private static boolean registerUser(String username, String password) throws IOException {
        File file = new File(USER_FILE);
        if (!file.exists()) file.createNewFile();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts[0].equals(username)) {
                    return false;
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(username + ":" + password);
            writer.newLine();
        }

        return true;
    }

    /**
     * Authenticates a user with the stored username/password combo.
     */
    private static boolean loginUser(String username, String password) throws IOException {
        File file = new File(USER_FILE);
        if (!file.exists()) return false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        }

        return false;
    }

    // === MAIN ENTRY POINT ===

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Do you want to login or register? (Type 'login' or 'register')");
        String option = scanner.nextLine().trim().toLowerCase();

        String username, password;
        boolean authenticated = false;

        while (!authenticated) {
            System.out.print("Enter username: ");
            username = scanner.nextLine().trim();

            System.out.print("Enter password: ");
            password = scanner.nextLine().trim();

            if (option.equals("register")) {
                if (registerUser(username, password)) {
                    System.out.println("Registration successful.");
                    authenticated = true;
                } else {
                    System.out.println("Username already exists. Please try again.");
                }
            } else if (option.equals("login")) {
                if (loginUser(username, password)) {
                    System.out.println("Login successful.");
                    authenticated = true;
                } else {
                    System.out.println("Invalid credentials. Please try again.");
                }
            } else {
                System.out.println("Invalid option. Please restart the application.");
                return;
            }

            if (authenticated) {
                // Show welcome banner
                System.out.println("\n=========================================");
                System.out.println(" Welcome to Group Chat, " + username + "!");
                System.out.println(" Type /exit to leave the chat.");
                System.out.println("=========================================\n");

                Socket socket = new Socket("127.0.0.1", 5000);
                ChatClient client = new ChatClient(socket, username);
                client.listenForMessages();
                client.sendMessage();
            }
        }

        scanner.close();
    }
}
