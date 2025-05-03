package client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public ChatClient(String address, int port) {
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            scanner = new Scanner(System.in);

            new Thread(new Receiver()).start();
            System.out.println("Connected to the chat server!");

            while (true) {
                String message = scanner.nextLine();
                out.println(message);
                if (message.equalsIgnoreCase("/quit")) {
                    break;
                }
            }
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (socket != null) socket.close();
            if (scanner != null) scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class Receiver implements Runnable {
        public void run() {
            try {
                String serverMessage;
                while ((serverMessage = in.readLine()) != null) {
                    System.out.println(serverMessage);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient("localhost", 12345);
    }
}
