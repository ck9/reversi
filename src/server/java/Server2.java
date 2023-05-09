package server.java;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Server2 {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private String playerName;
    private String color;

    public Server2() {
    }

    public void connect(Socket socket) throws IOException {
        this.socket = socket;
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

    public void sendToClient(String message) {
        writer.println(message);
    }

    public String receiveFromClient() throws IOException {
        return reader.readLine();
    }

    public void setPlayerName(String name) {
    	playerName = name;
    }

    public String getPlayerName() {
    	return playerName;
    }

    public void setColor(String co) {
    	color = co;
    }

    public String getColor() {
    	return color;
    }

}