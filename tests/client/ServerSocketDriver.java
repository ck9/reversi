package client;
import java.io.IOException;

public class ServerSocketDriver {
    public static void main(String[] args) {
        //test
        ServerSocket serverSocket = new ServerSocket();
        try {
            serverSocket.connect("localhost", 8888);
            System.out.println("isConnected: " + serverSocket.isConnected());
            serverSocket.sendToServer("hello");
            System.out.println(serverSocket.receiveFromServer());
            serverSocket.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
