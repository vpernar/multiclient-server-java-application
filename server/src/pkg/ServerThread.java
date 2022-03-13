package pkg;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ServerThread implements Runnable {

    private Socket socket;
    public static List<ServerThread> serverThreads = Collections.synchronizedList(new ArrayList<>());
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private static List<ChatMessage> chatMessages = Collections.synchronizedList(new ArrayList<>());

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        in = null;
        out = null;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            while (true) {
                username = in.readLine();
                if (serverThreads.stream().map(s -> s.getUsername()).collect(Collectors.toList()).contains(username)) {
                    out.println("wrong_username");
                } else {
                    serverThreads.add(this);
                    out.println("username_okay");
                    broadcastMessage(username + " has joined!");
                    for(ChatMessage ch: chatMessages){
                        out.println(ch.toString());
                    }
                    break;
                }
            }

            listenMessage();
        } catch (IOException e) {
            closeAll(socket, in, out);
        }
    }

    public void listenMessage(){
        new Thread(() -> {
            String msgFromGroupChat;

            while (true) {
                try {
                    msgFromGroupChat = in.readLine();
                    ChatMessage chatMessage = new ChatMessage(username, msgFromGroupChat);
                    chatMessages.add(chatMessage);
                    if(chatMessages.size()>=100) {
                        chatMessages.clear();
                    }
                    broadcastMessage(chatMessage.toString());

                } catch (IOException e) {
                    closeAll(socket, in, out);
                }
            }
        }).start();
    }

    public void recieveMessage(String message) {
        out.println(message);
    }

    public void broadcastMessage(String message) {
        for (ServerThread thread : serverThreads) {
            if (!thread.getUsername().equals(username)) {
                thread.recieveMessage(message);
            }
        }
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

    public String getUsername() {
        return username;
    }
}
