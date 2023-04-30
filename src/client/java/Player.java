package client.java;

public class Player {
	private String myName; //プレイヤ名
	private String myColor; //先手後手情報(白黒)

	public Player(){
		myName = "";
		myColor = "";
	}
	
	public void setName(String name){ // プレイヤ名を受付
		myName = name;
	}
	
	public String getName(){	// プレイヤ名を取得
		return myName;
	}
	
	// color: 先手後手情報(black or white)
	public void setColor(String c){ // 先手後手情報の設定
		myColor = c;
	}
	
	public String getColor(){ // 先手後手情報の取得
		return myColor;
	}
}