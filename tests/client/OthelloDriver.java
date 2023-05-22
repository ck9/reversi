package client;
import java.util.ArrayList;

public class OthelloDriver {
    public static void main(String[] args) {
        Othello othello = new Othello();

        // ボードの初期化テスト
        othello.startGameLocal(0);
        System.out.println("startGameLocal:");
        printBoard(othello.get_board());

        // 手の可能性の取得テスト
        ArrayList<Position> possibleMoves = othello.get_possible_moves(othello.get_board(),"black");
        System.out.println("Possible moves for black:");
        for (Position move : possibleMoves)
        System.out.println(move.y + ", " + move.x);

        // minimaxテスト
        int depth = 5;
        int minimaxValue = othello.minimax(othello.get_board(),depth,Integer.MIN_VALUE,Integer.MAX_VALUE,"white");
        System.out.println("Minimax value: " + minimaxValue);

        // コンピュータの手の取得テスト
        System.out.println("Current board:");
        printBoard(othello.get_board());
        Position computerMove = othello.get_computer_move(othello.get_board(),"black", "easy");
        System.out.println("Computer move (black, hard mode): " + computerMove.y + ", " + computerMove.x);

        // 手を進めるテスト
        othello.make_move(othello.get_board(), computerMove, "black");
        System.out.println("Board after making a move:");
        printBoard(othello.get_board());

        // 終了状態のテスト
        int[][] endStateBoard = new int[][] {
        { -1, -1, -1, -1, -1, -1, -1, -1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        { 1, 1, 1, 1, 1, 1, 1, 1 },
        };
        System.out.println("end state board:");
        printBoard(endStateBoard);
        boolean isEndState = othello.is_end_state(endStateBoard);
        System.out.println("Is end state: " + isEndState);

        /*---フリップ枚数が多い場合---*/
        
        int[][] test_board = new int[][] {
        { 1, -1, -1, -1, 1, 0, 0, 0 },
        { 1, 1, 1, 1, 1, 1, -1, -1 },
        { -1, 1, 0, 0, 0, 1, -1, -1 },
        { -1, 1, 0, 0, 0, 1, -1, -1 },
        { -1, 1, 0, 0, 0, 1, -1, 1 },
        { -1, 0, 1, 1, 1, 1, -1, 1 },
        { -1, -1, -1, -1, -1, -1, -1, -1 },
        { -1, -1, -1, -1, -1, -1, -1, -1 },
        };
        System.out.println("test board:");
        printBoard(test_board);

        // 手を進めるテスト
        Position test_move = new Position(0, 1);
        othello.make_move(test_board, test_move, "white");
        System.out.println("Board after making a move:");
        printBoard(test_board);

        // 白黒の個数をカウントするテスト
        int[] stoneCounts = othello.get_countStones(test_board);
        System.out.println("Black stones: " + stoneCounts[0]);
        System.out.println("White stones: " + stoneCounts[1]);

        // 評価値を計算するテスト
        int evaluationValue2 = othello.cal_evaluation_value(test_board);
        System.out.println("Evaluation value: " + evaluationValue2);

    }

    // test用
    private static void printBoard(int[][] board) {
        for (int[] row : board) {
        for (int cell : row) {
            if (cell == 1) {
            System.out.print("○ ");
            } else if (cell == 0) {
            System.out.print("● ");
            } else {
            System.out.print("- ");
            }
        }
        System.out.println();
        }
    }
}
