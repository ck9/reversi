package server.java;

import java.net.Socket;
import java.io.*;

public class ClientSocket {
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
	private String playerName;

    public ClientSocket() {
    }

    public Boolean connect(Socket socket) throws IOException {
        this.socket = socket;
		if (isBlocked()) {
			System.out.println(getClientIp() + "はブロックリストに登録されています。");
			disconnect();
			return false;
		}
        writer = new PrintWriter(socket.getOutputStream(), true);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return true;
    }

    public Boolean isConnected() {
        return socket != null && socket.isConnected();
    }
    
    public void disconnect() throws IOException {
        if (reader != null) reader.close();
        if (writer != null) writer.close();
        if (socket != null) socket.close();
		socket = null;
    }

    public void sendToClient(String message) {
		writer.println(message);
    }

    public String receiveFromClient() throws IOException {
		return reader.readLine();
    }

	public String getClientIp() {
		return socket.getInetAddress().getHostAddress();
	}

	private Boolean isBlocked() {
		String ipadress = getClientIp();
		File blockListFile = new File("block_List.txt");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(blockListFile)));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.equals(ipadress)) {
					br.close();
					return true;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			try {
				blockListFile.createNewFile();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void setName(String name){
		playerName = name;
	}
	public String getName(){
		return playerName;
	}

}
