package client.java;

public class Othello {
	private int row = 8;	//オセロ盤の縦横マス数(2の倍数のみ)
	private String [] grids = new String [row * row]; //局面情報
	private String turn; //手番
	private String gamemode;
	private String myColor = ""; //先手後手情報(白黒)

	// コンストラクタ
	public Othello(){
		turn = "black"; //黒が先手
		for(int i = 0 ; i < row * row ; i++){
			grids[i] = "board"; //初めは石が置かれていない
			int center = row * row / 2;
			grids[center - row / 2 - 1] = "black";
			grids[center + row / 2    ] = "black";
			grids[center - row / 2    ] = "white";
			grids[center + row / 2 - 1] = "white";
		}
	}

	// メソッド
	public String checkWinner(){	// 勝敗を判断
		int BlackWin = 0;
		for(int i = 0 ; i < row * row ; i++){
			switch(grids[i]){
				case "black":
					BlackWin++;
					break;
				case "white":
					BlackWin--;
					break;
			}
		}
		if(BlackWin>0)
			return "black";
		else if(BlackWin<0)
			return "white";
		else
			return "draw";
		
	}
	public String getTurn(){ // 手番情報を取得
		return turn;
	}
	public String [] getGrids(){ // 局面情報を取得
		return grids;
	}
	public void changeTurn(){ // 手番を変更
		switch(turn) {
		case "black":
			turn = "white";
			break;
		case "white":
			turn = "black";
			break;
		}
	}
	public boolean isGameover(){	// 対局終了を判断
		return true;
	}
	public boolean putStone(int i, String color, boolean effect_on){ // (操作を)局面に反映
		return true;
	}
	public int getRow(){ //縦横のマス数を取得
		return row;
	}
}