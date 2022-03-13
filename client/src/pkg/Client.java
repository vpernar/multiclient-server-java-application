package pkg;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    public static final int PORT = 9001;
    private BufferedReader in;
    private Socket socket;
    private PrintWriter out;


    public Client() {
        try {
            socket = new Socket("127.0.0.1", PORT);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            Scanner scanner = new Scanner(System.in);

            System.out.println("Enter username:");
            String username;

            while (true) {
                username = scanner.nextLine();
                out.println(username);

                if (!in.readLine().equals("wrong_username")) {
                    System.out.println("Welcome " + username);
                    break;
                } else {
                    System.out.println("Enter another username.");
                }
            }

            listenForMessage();

            while(true){
                out.println(scanner.nextLine());
            }

        } catch (
                IOException e) {
            closeAll(socket, in, out);
        }
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;

            while (true) {
                try {
                    msgFromGroupChat = in.readLine();
                    System.out.println(msgFromGroupChat);
                } catch (IOException e) {
                    closeAll(socket, in, out);
                }
            }
        }).start();
    }

    public void closeAll(Socket socket, BufferedReader in, PrintWriter out) {
        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (out != null) {
            out.close();
        }

        if (this.socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
