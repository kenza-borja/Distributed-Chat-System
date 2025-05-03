package server;

import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String name = "Anonymous";

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            sendMessage("Welcome to the chat! Please set your name using /name YourName");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/name ")) {
                    String newName = message.substring(6).trim();
                    if (!newName.isEmpty()) {
                        String oldName = this.name;
                        this.name = newName;
                        ChatServer.broadcast(oldName + " changed name to " + this.name, this);
                        sendMessage("Your name is now: " + this.name);
                    }
                } else if (message.startsWith("/quit")) {
                    sendMessage("Goodbye!");
                    break;
                } else {
                    String fullMessage = this.name + ": " + message;
                    System.out.println(fullMessage);
                    ChatServer.broadcast(fullMessage, this);
                }
            }
        } catch (IOException e) {
            System.out.println(name + " connection lost.");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            ChatServer.removeClient(this);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public String getName() {
        return name;
    }
}
