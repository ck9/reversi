package server.java;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class Server{
	private int port;
	private Server2 client1, client2;
	private String S1;
	private String S2;
	private int color=0; //先手後手決定のためのint
	private boolean color1;

	public Server(int port) {
		this.port=port;
		Server2 client1 = new Server2();
		Server2 client2 = new Server2();

	}

	public void run() {
		try {
			client1.connect(getConnection());
			client2.connect(getConnection());
			if((client1.isConnected())&&(client2.isConnected())) {
				client1.setPlayerName(client1.receiveFromClient());
				client2.setPlayerName(client2.receiveFromClient());
				client1.setColor(sendColor());
				client2.setColor(sendColor());
				client1.sendToClient(client2.getPlayerName());
				client1.sendToClient(client2.getColor());
				client2.sendToClient(client1.getPlayerName());
				client2.sendToClient(client1.getColor());
				while(true) {
					if(client1.getColor()=="black") {
						client2.sendToClient(client1.receiveFromClient());
						client1.sendToClient(client2.receiveFromClient());
					} else if(client1.getColor()=="white") {
						client1.sendToClient(client2.receiveFromClient());
						client2.sendToClient(client1.receiveFromClient());
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private Socket getConnection() {
			ServerSocket ss1 = null;
			Socket socket = null;
			BufferedReader reader = null;
			PrintWriter writer = null;
			try {
				ss1 = new ServerSocket();
				ss1.bind(new InetSocketAddress("127.0.0.1",port));
				return ss1.accept();

			} catch (IOException e) {
				e.printStackTrace();
			}finally{

			}
		}


	public boolean isBlocked(String PlayerIP) {
		BufferedReader br1 = null;
		try {
			br1 = new BufferedReader(new FileReader("black_list.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		String s;
		try {
			while((s = br1.readLine()) != null) {
				if(s.equals(PlayerIP)) {
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}


	public void printStatus(){ //クライアント接続状態の確認
	}

	public String sendColor(){//先手後手情報(白黒)の送信
		String a;
		if(color==0) {
			color=1;
		Random r = new Random();
		int r1 = r.nextInt(2);
		if(r1 == 0) {
			a=("black");
			color1 = false;
		    return a;
		}else {
			a=("white");
			color1 = true;
			return a;
		}
		}else if(color==1) {
			color=0;
			if(color1==false) {
				a=("white");
				return a;
			}else {
				a=("black");
				return a;
			}
		}
		return null;

	}


	public static void main(String[] args){ //main
		if (args.length > 0) {
            try {
                int port = Integer.parseInt(args[0]);
                if (port > 0 && port < 65536) {
                	Server server = new Server(port);
                    return;
                }
            } catch (NumberFormatException e) {
                 return;
            }
        }
        Server server = new Server(8888);



        return;
	}
}
