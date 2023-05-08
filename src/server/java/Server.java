package server.java;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
import java.util.Random;

public class Server{
	private int port; // サーバの待ち受けポート
	private boolean [] online; //オンライン状態管理用配列
	private PrintWriter [] out; //データ送信用オブジェクト
	private Receiver [] receiver; //データ受信用オブジェクト
	private int color=0; //先手後手決定のためのint
	private boolean color1;

	//コンストラクタ
	public Server(int port) { //待ち受けポートを引数とする
		this.port = port; //待ち受けポートを渡す
		out = new PrintWriter [2]; //データ送信用オブジェクトを2クライアント分用意
		receiver = new Receiver [2]; //データ受信用オブジェクトを2クライアント分用意
		online = new boolean[2]; //オンライン状態管理用配列を用意
	}

	// データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ
		private int playerNo; //プレイヤを識別するための番号

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket, int playerNo){
			try{
				this.playerNo = playerNo; //プレイヤ番号を渡す
				sisr = new InputStreamReader(socket.getInputStream());
				br = new BufferedReader(sisr);
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {// データを受信し続ける
					String inputLine = br.readLine();//データを一行分読み込む
					if (inputLine != null){ //データを受信したら
						forwardMessage(inputLine, playerNo); //もう一方に転送する
					}
				}
			} catch (IOException e){ // 接続が切れたとき
				System.err.println("プレイヤ " + playerNo + "との接続が切れました．");
				online[playerNo] = false; //プレイヤの接続状態を更新する
				printStatus(); //接続状態を出力する
			}
		}
	}

	// メソッド

	public void acceptClient(){ //クライアントの接続(サーバの起動)
		try {
			System.out.println("サーバが起動しました．");
			ServerSocket ss = new ServerSocket(port); //サーバソケットを用意
			while (true) {
				Socket socket = ss.accept(); //新規接続を受け付ける
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}
	}

	public boolean block(String PlayerIP) {
		BufferedReader br1 = null;
		try {
			br1 = new BufferedReader(new FileReader("blacklist.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			File file = new File("blacklist.txt");
		}
		String s;
		try {
			while((s = br1.readLine()) != null) {
				if(s.equals(PlayerIP)) {
					return false;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}


	public void printStatus(){ //クライアント接続状態の確認
	}

	public void sendColor(int playerNo){ //先手後手情報(白黒)の送信
		if(color==0) {
		Random r = new Random();
		int r1 = r.nextInt(2);
		if(r1 == 0) {
		out[playerNo].println("black");
		color1 = false;
		}else {
			out[playerNo].println("white");
			color1 = true;
		}
		color=1;
		}else if(color==1) {
			if(color1==false) {
				out[playerNo].println("white");
			}else {
				out[playerNo].println("black");
			}
			color=0;
		}

	}

	public void forwardMessage(String msg, int playerNo){ //操作情報の転送
		out[playerNo].println(msg);
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
        server.acceptClient(); //クライアント受け入れを開始
        return;
	}
}
