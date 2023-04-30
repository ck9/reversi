package client.java;

public class Othello {
	private int boardSize = 8; //盤面のサイズ
    //              board = 0: 空白, 1: 黒, 2: 白
	private int[][] board = new int[boardSize][boardSize]; //盤面の状態
	private String turn; //手番 (black or white)
	private String gameMode; //ゲームモード (easy or normal or hard or pvp)

	public Othello(){
        // 諸々初期化
		turn = "black";
        gameMode = "easy";
	}

    public void startGameLocal(int difficulty) {
        System.out.println("startGameLocal difficulty: " + difficulty);
        switch (difficulty) {
            case 0:
                gameMode = "easy";
                break;
            case 1:
                gameMode = "normal";
                break;
            case 2:
                gameMode = "hard";
                break;
        }
        System.out.println("startGameLocal gameMode: " + gameMode);
        startGame();
    }

    public void startGameNetwork() {
        gameMode = "pvp";
        startGame();
    }

    private void startGame() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++){
                board[i][j] = 0;
            }
        }
        int center = boardSize / 2;
        board[center - 1][center - 1] = 2;
        board[center][center] = 2;
        board[center - 1][center] = 1;
        board[center][center - 1] = 1;

        turn = "black";
    }

    public String getGameMode() {
        System.out.println("getGameMode: " + gameMode);
        return gameMode;
    }

	public String checkWinner(){	// 勝敗を判断

		return "";
	}

	public String getTurn(){ // 手番情報を取得
		return turn;
	}

	public int getBoardSize(){ // 盤面のサイズを取得
		return boardSize;
	}

    public int getBoard(int x, int y) {
        return board[x][y];
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

}