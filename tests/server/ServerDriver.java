package server;

public class ServerDriver {
    public static void main(String[] args){
		Integer port = 8888;
		if (args.length > 0) {
			try {
				int argPort = Integer.parseInt(args[0]);
				if (argPort > 0 && argPort < 65536) {
					port = argPort;
					return;
				}
			} catch (NumberFormatException e) {
				return;
			}
		}
		Server server = new Server(port); //サーバオブジェクトを用意
		server.acceptClient(); //クライアント受け入れを開始
		return;
	}
}
