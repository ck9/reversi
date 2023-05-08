package client.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Othello {
    private int row = 8; //盤面のサイズ
    private int[][] current_board = new int[row][row]; //盤面の状態
    private String current_turn = new String("black"); //手番 (black or white)
    private String game_mode = new String("normal"); //ゲームモード (easy or normal or hard or pvp)

    public Othello() {
        current_turn = "black";
        game_mode = "normal";
        init_board(current_board);
    }

    public void startGameLocal(int difficulty) {
        switch (difficulty) {
            case 0:
                game_mode = "easy";
                break;
            case 1:
                game_mode = "normal";
                break;
            case 2:
                game_mode = "hard";
                break;
            default:
                game_mode = "normal";
                break;
        }
        startGame();
    }

    public void startGameNetwork() {
        game_mode = "pvp";
        startGame();
    }

    private void startGame() {
        init_board(current_board);
        current_turn = "black";
    }

    private void init_board(int[][] board) {
        for (int y = 0; y < board.length; ++y) {
        for (int x = 0; x < board[y].length; ++x) 
            board[y][x] = -1;
        }
        board[row / 2 - 1][row / 2 - 1] = 1;
        board[row / 2 - 1][row / 2] = 0;
        board[row / 2][row / 2 - 1] = 0;
        board[row / 2][row / 2] = 1;
    }

    public int get_row() {
        return row;
    }
    public int[][] get_board() {
        return current_board;
    }
    public String getGameMode() {
        return game_mode;
    }
    public String get_turn() {
        return current_turn;
    }
    public void change_turn() {
        if (current_turn.equals("black")) 
        current_turn = "white";
        else if (current_turn.equals("white")) 
        current_turn = "black";
    }

    // ボードの差, 確定石の差, 可能手数の差で評価値を計算（黒が有利なら正の値）
    public int cal_evaluation_value(int[][] board) {
        if(is_end_state(board)){
            int[] countStones = get_countStones(board);
            if(countStones[0] > countStones[1]){
                return Integer.MAX_VALUE;
            }else if(countStones[0] < countStones[1]){
                return Integer.MIN_VALUE;
            }else{
                return 0;
            }
        }

        Random random = new Random();
        double rnd = random.nextDouble();
        int board_value = 0;
        int[][] evaluationBoard = {
          {100, -40, 20,  5,  5, 20, -40, 100},
          {-40, -80, -1, -1, -1, -1, -80, -40},
          { 20,  -1,  5,  1,  1,  5,  -1,  20},
          {  5,  -1,  1,  0,  0,  1,  -1,   5},
          {  5,  -1,  1,  0,  0,  1,  -1,   5},
          { 20,  -1,  5,  1,  1,  5,  -1,  20},
          {-40, -80, -1, -1, -1, -1, -80, -40},
          {100, -40, 20,  5,  5, 20, -40, 100}
        };

        for (int y = 0; y < board.length; ++y) {
          for (int x = 0; x < board[y].length; ++x) {
            if (board[y][x] == 1){
              board_value += evaluationBoard[y][x]; 
            }
            else if (board[y][x] == 0){
              board_value -= evaluationBoard[y][x];
            }
          }
        }
        board_value *= (int)(3 * rnd);

        //fsの実装
        int fs_value = 0;
        int black_fs = 0, white_fs = 0;
        List<Position> corners = new ArrayList<>(Arrays.asList(new Position(0, 0), new Position(row - 1,0), new Position(0, row - 1), new Position(row - 1, row - 1)));
        List<Position> dzs = new ArrayList<>(Arrays.asList(new Position(-1, 0),new Position(0, -1),new Position(0, 1), new Position(1, 0)));
        boolean[][] checked = new boolean[row][row];        
        for(Position corner:corners){
          if(board[corner.y][corner.x] == 1){
            black_fs++;
            for(Position dz: dzs){
              int dy = corner.y + dz.y, dx = corner.x + dz.x;
              while(is_inrange(dy, dx) && board[dy][dx] == 1 && !checked[dy][dx]){
                checked[dy][dx] = true;
                black_fs++;
                dy += dz.y;
                dx += dz.x;
              }
            }
          }
          else if(board[corner.y][corner.x] == 0){
            white_fs++;
            for(Position dz: dzs){
              int dy = corner.y + dz.y, dx = corner.x + dz.x;
              while(is_inrange(dy, dx) && board[dy][dx] == 0 && !checked[dy][dx]){
                checked[dy][dx] = true;
                white_fs++;
                dy += dz.y;
                dx += dz.x;
              }
            }
          }
        }
        
        //System.out.println("black_fs: " + black_fs + ", white_fs: " + white_fs);
        rnd = random.nextDouble();
        fs_value = (black_fs - white_fs) + (int)(rnd * 33);

        int possible_moves_value = 0;
        ArrayList<Position> possible_moves = get_possible_moves(board, "black");
        possible_moves_value += possible_moves.size();
        possible_moves = get_possible_moves(board, "white");
        possible_moves_value -= possible_moves.size();
        rnd = random.nextDouble();
        possible_moves_value = (possible_moves_value + (int)(rnd * 2)) * 10;

        int result_value = 0;
        int fs_weight = 5, board_weight = 2, possible_moves_weight = 1;
        //System.out.println("board_value: " + board_value + ", fs_value: " + fs_value + ", possible_moves_value: " + possible_moves_value);        
        result_value = board_value * board_weight + fs_value * fs_weight + possible_moves_value * possible_moves_weight;

        return result_value;
    }

    public int[] get_countStones(int[][] board){
      // 0: black, 1: white
      int[] countStones = new int[2];
      for(int i = 0; i < row; i++){
        for(int j = 0; j < row; j++){
          if(board[i][j] == 1)
            countStones[0]++;
          else if(board[i][j] == 0){
            countStones[1]++;
          }
        }
      }
      return countStones;
    }

    public int minimax(int[][] board, int depth, int alpha, int beta, String turn) {
        ArrayList<Position> possible_moves = get_possible_moves(board, turn);
        if (depth <= 0 || possible_moves.isEmpty())
          return cal_evaluation_value(board);
        
        int val = 0;    
        if (turn.equals("black")) {
          val = Integer.MIN_VALUE;
          for (Position move : possible_moves) {
              int[][] copiedboard = new int[row][];
              for (int i = 0; i < row; ++i)
                copiedboard[i] = board[i].clone();
            
              make_move(copiedboard, move, turn);
              int new_val = minimax(copiedboard, depth - 1, alpha, beta, "white");
              val = Math.max(val, new_val);
              alpha = Math.max(alpha, val);

              if (alpha >= beta) break;
        }
        } 
        else if (turn.equals("white")) {
          val = Integer.MAX_VALUE;
          for (Position move : possible_moves) {
              int[][] copiedboard = new int[row][];
              for (int i = 0; i < board.length; ++i)
                copiedboard[i] = board[i].clone();

              make_move(copiedboard, move, turn);
              int new_val = minimax(copiedboard, depth - 1, alpha, beta, "black");
              val = Math.min(val, new_val);
              beta = Math.min(beta, val);

              if (alpha >= beta) break;
          }
        }

        return val;
    }

    public Position get_computer_move(int[][] board, String turn, String game_mode) {
        Position next_move = new Position(-1, -1);
        ArrayList<Position> possible_moves = get_possible_moves(board, turn);

        // 可能手がない場合は(-1, -1)を返す
        if (possible_moves.isEmpty())
          return next_move;

        int depth = 3;
        if (game_mode.equals("hard") || game_mode.equals("easy")) 
          depth = 7;

        int val = (game_mode.equals("easy") && turn.equals("white")) || (!game_mode.equals("easy") && turn.equals("black")) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Position move : possible_moves) {
        int[][] copiedboard = new int[row][];
        for (int i = 0; i < row; ++i)
            copiedboard[i] = board[i].clone();
        make_move(copiedboard, move, turn);
        String next_turn = turn.equals("black") ? "white" : "black";
        int new_val = minimax(copiedboard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, next_turn);
        if ((game_mode.equals("easy") && turn.equals("white")) || ((!game_mode.equals("easy")) && turn.equals("black"))) {
          System.out.println("maximizing");
            if (val < new_val) {
            val = new_val;
            next_move = move;
            }
        } else {
            if (val > new_val) {
            System.out.println("minimizing");
            val = new_val;
            next_move = move;
            }
        }
        }
        return next_move;
    }

    public boolean is_end_state(int[][] board) {
        boolean flag = false;
        int black_count = 0, white_count = 0;
        for (int y = 0; y < board.length; ++y) {
        for (int x = 0; x < board[y].length; ++x) {
            if (board[y][x] == 1) 
            black_count++; 
            else if (board[y][x] == 0) 
            white_count++;
        }
        }
        if (black_count == 0 || white_count == 0 || black_count + white_count == 64) 
        flag = true;

        return flag;
    }

    public ArrayList<Position> get_possible_moves(int[][] board, String turn) {
        ArrayList<Position> moves = new ArrayList<Position>();
        List<Position> dzs = new ArrayList<>(
        Arrays.asList(
            new Position(-1, -1),
            new Position(-1, 0),
            new Position(-1, 1),
            new Position(0, -1),
            new Position(0, 1),
            new Position(1, -1),
            new Position(1, 0),
            new Position(1, 1)
        )
        );
        for (int y = 0; y < board.length; ++y) {
        for (int x = 0; x < board[y].length; ++x) {
            if (board[y][x] == -1) {
            boolean flag = false;
            for (Position dz : dzs) {
                int dy = y + dz.y, dx = x + dz.x;
                if (turn.equals("black") && is_inrange(dy, dx) && board[dy][dx] == 0) {
                while (is_inrange(dy, dx)) {
                    dy += dz.y;
                    dx += dz.x;
                    if (is_inrange(dy, dx) && board[dy][dx] == 1) {
                    flag = true;
                    break;
                    }
                }
                } else if (turn.equals("white") && is_inrange(dy, dx) && board[dy][dx] == 1) {
                while (is_inrange(dy, dx)) {
                    dy += dz.y;
                    dx += dz.x;
                    if (is_inrange(dy, dx) && board[dy][dx] == 0) {
                    flag = true;
                    break;
                    }
                }
                }
                if (flag) 
                break;
            }
            if (flag) 
                moves.add(new Position(y, x));
            }
        }
        }

        return moves;
    }

    public void make_move(int[][] board, Position move, String turn) {
        if(move.equals(new Position(-1, -1))) return;
        List<Position> dzs = new ArrayList<>(
        Arrays.asList(
            new Position(-1, -1),
            new Position(-1, 0),
            new Position(-1, 1),
            new Position(0, -1),
            new Position(0, 1),
            new Position(1, -1),
            new Position(1, 0),
            new Position(1, 1)
        )
        );
        for (Position dz : dzs) {
        int dy = move.y + dz.y, dx = move.x + dz.x;
        boolean can_flip = false;
        if (turn.equals("black") && is_inrange(dy, dx) && board[dy][dx] == 0) {
            while (is_inrange(dy, dx)) {
            dy += dz.y;
            dx += dz.x;
            if (is_inrange(dy, dx) && board[dy][dx] == 1) {
                can_flip = true;
                break;
            }
            }
            if (can_flip) {
            int flip_length = Math.max(Math.abs(dy - move.y), Math.abs(dx - move.x));
            while (flip_length-- > 0) {
                dy -= dz.y;
                dx -= dz.x;
                board[dy][dx] = 1;
            }
            }
        } 
        else if (turn.equals("white") && is_inrange(dy, dx) && board[dy][dx] == 1) {
            while (is_inrange(dy, dx)) {
            dy += dz.y;
            dx += dz.x;
            if (is_inrange(dy, dx) && board[dy][dx] == 0) {
                can_flip = true;
                break;
            }
            }
            if (can_flip) {
            int flip_length = Math.max(Math.abs(dy - move.y), Math.abs(dx - move.x));
            while (flip_length-- > 0) {
                dy -= dz.y;
                dx -= dz.x;
                if(is_inrange(dy, dx))
                  board[dy][dx] = 0;
            }
            }
        }
        }
    }

    public boolean is_inrange(int y, int x) {
        return 0 <= y && y < row && 0 <= x && x < row;
    }

    public boolean is_pass_state(int[][] board, String turn) {
        boolean flag = false;
        ArrayList<Position> possible_moves = get_possible_moves(board, turn);
        if (possible_moves.isEmpty()) 
          flag = true;

        return flag;
    }

    public static void main(String[] args) {
        Othello othello = new Othello();

        // ボードの初期化テスト
        othello.startGameLocal(0);
        System.out.println("startGameLocal:");
        printBoard(othello.current_board);

        // 評価値計算テスト
        int evaluationValue = othello.cal_evaluation_value(othello.current_board);
        System.out.println("Evaluation value: " + evaluationValue);

        // 手の可能性の取得テスト
        ArrayList<Position> possibleMoves = othello.get_possible_moves(othello.current_board,"black");
        System.out.println("Possible moves for black:");
        for (Position move : possibleMoves)
        System.out.println(move.y + ", " + move.x);

        // コンピュータの手の取得テスト
        System.out.println("Current board:");
        printBoard(othello.current_board);
        Position computerMove = othello.get_computer_move(othello.current_board,"black", "easy");
        System.out.println("Computer move (black, hard mode): " + computerMove.y + ", " + computerMove.x);

        // 手を進めるテスト
        othello.make_move(othello.current_board, computerMove, "black");
        System.out.println("Board after making a move:");
        printBoard(othello.current_board);

        // 終了状態のテスト
        boolean isEndState = othello.is_end_state(othello.current_board);
        System.out.println("Is end state: " + isEndState);

        // minimaxテスト
        int depth = 5;
        int minimaxValue = othello.minimax(othello.current_board,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,"white");
        System.out.println("Minimax value: " + minimaxValue);

        // フリップ個数が多いテスト
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
        System.out.println("Other test board:");
        printBoard(test_board);

        // make_moveテスト（フリップ個数が多い手）
        Position test_move = new Position(0, 1);
        othello.make_move(test_board, test_move, "white");
        System.out.println("Board after making a move:");
        printBoard(test_board);

        // get_countStoneテスト
        int[] stoneCounts = othello.get_countStones(test_board);
        System.out.println("Black stones: " + stoneCounts[0]);
        System.out.println("White stones: " + stoneCounts[1]);
        // 評価値計算テスト
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
