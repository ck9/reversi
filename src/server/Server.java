package server;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Random;

public class Server{
	private int port; // サーバの待ち受けポート
	private ClientSocket client1, client2; // クライアントソケット

	//コンストラクタ
	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		client1 = new ClientSocket();
		client2 = new ClientSocket();
	}

	// メソッド
	public void acceptClient(){ //クライアントの接続(サーバの起動)
		System.out.println("[info] サーバーを起動します。 Port:" + port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while(true){
				if (!client1.isConnected()) {
					System.out.println("[info] クライアント1の接続を待っています。");
					Socket socket = serverSocket.accept(); //クライアントからの接続を待つ
					if (client1.connect(socket)) {
						client1.setName(client1.receiveFromClient()); //クライアントからプレイヤー名を受け取りセット
						System.out.println(client1.getName() + "(" + client1.getClientIp() + "): 接続しました．");
					}
				} else if (!client2.isConnected()) {
					System.out.println("[info] クライアント2の接続を待っています。");
					Socket socket = serverSocket.accept(); //クライアントからの接続を待つ
					if (client2.connect(socket)) {
						client2.setName(client2.receiveFromClient()); //クライアントからプレイヤー名を受け取りセット
						System.out.println(client2.getName() + "(" + client2.getClientIp() + "): 接続しました．");
						System.out.println("[info] 接続が完了しました。ゲームを開始します。");
						startGame(); //ゲーム開始
					}
				}
				Thread.sleep(10);
			}
		} catch(Exception e){
			System.out.println("[error] サーバーの起動に失敗しました。");
		}
	}

	public void startGame(){ //ゲームの開始
		// 各クライアントに対して相手の名前を通知
		client1.sendToClient(client2.getName());
		client2.sendToClient(client1.getName());

		// 先攻後攻をランダムに決めて各クライアントに通知
		Random random = new Random();
		int turn = random.nextInt(2);
		try {
			if (turn == 0) {
				client1.sendToClient("black");
				client2.sendToClient("white");
				System.out.println("black: " + client1.getName() + "(" + client1.getClientIp() + ") vs white: " + client2.getName() + "(" + client2.getClientIp() + ")");
			} else {
				client1.sendToClient("white");
				client2.sendToClient("black");
				System.out.println("black: " + client2.getName() + "(" + client2.getClientIp() + ") vs white: " + client1.getName() + "(" + client1.getClientIp() + ")");
			}
		} catch (Exception e) {
			System.out.println("クライアントの接続が切断されました．");
			endGame();
			return;
		}

		// クライアントからのメッセージを受け取り，相手に送信するスレッドをそれぞれ起動
		new Thread(() -> {
			try {
				while (true) {
					String message = client1.receiveFromClient();
					if (message == null) {
						System.out.println(client1.getName() + "(" + client1.getClientIp() + "): 切断しました。");
						break;
					}
					System.out.println(client1.getName() + "(" + client1.getClientIp() + "): " + message);
					if (client2.isConnected()){
						client2.sendToClient(message);
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				endGame();
			}
		}).start();

		new Thread(() -> {
			try {
				while (true) {
					String message = client2.receiveFromClient();
					if (message == null) {
						System.out.println(client2.getName() + "(" + client2.getClientIp() + "): 切断しました。");
						break;
					}
					System.out.println(client2.getName() + "(" + client2.getClientIp() + "): " + message);
					if (client1.isConnected()){
						client1.sendToClient(message);
					}
				}
			} catch (IOException e) {
				System.err.println(e.getMessage());
			} finally {
				endGame();
			}
		}).start();
	}

	private void endGame() {
		try {
			client1.disconnect();
			client2.disconnect();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public static void main(String[] args){ //main
		Integer port = 8888;
		if (args.length > 0) {
			try {
				Integer argPort = Integer.parseInt(args[0]);
				if (argPort > 0 && argPort < 65536) {
					port = argPort;
				}
			} catch (NumberFormatException e) {
				System.out.println("ポート番号は1~65535の整数で指定してください。");
				return;
			}
		}
		Server server = new Server(port); //サーバオブジェクトを用意
		server.acceptClient(); //クライアント受け入れを開始
		return;
	}

}