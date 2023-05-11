package client;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;

public class ServerSocket {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    public ServerSocket() {
    }

    public void connect(String host, int port) throws IOException {
        socket = new Socket(host, port);
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public Boolean isConnected() {
        return socket != null && socket.isConnected();
    }
    
    public void disconnect() throws IOException {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (socket != null) socket.close();
    }

    public void sendToServer(String message) {
        writer.println(message);
    }

    public String receiveFromServer() throws IOException {
        return reader.readLine();
    }

}
