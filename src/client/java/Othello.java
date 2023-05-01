package client.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Othello {

  int row = 8;
  int[][] current_board = new int[row][row];
  String current_turn = new String("Black");
  String game_mode = new String("normal");

  public Othello(String game_mode) {
    this.game_mode = game_mode;
    init_board(current_board);
  }

  public void init_board(int[][] board) {
    for (int y = 0; y < board.length; ++y) {
      for (int x = 0; x < board[y].length; ++x) 
        board[y][x] = -1;
    }
    board[row / 2 - 1][row / 2 - 1] = 1;
    board[row / 2 - 1][row / 2] = 0;
    board[row / 2][row / 2 - 1] = 0;
    board[row / 2][row / 2] = 1;
  }

  // 黒白の差, 壁際の石の数の差, 可能手数の差で評価値を計算（黒が有利なら正の値）
  // ゲーム終了時の評価値が無限大、または無限小になるように処理を追加予定
  public int cal_evaluation_value(int[][] board) {
    int result_value = 0, cnt_value = 0, bythewall_value = 0, possible_moves_value = 0;
    int cnt_weight = 1, bythewall_weight = 1, possible_moves_weight = 1;
    int corner_point = 5, side_point = 3;

    for (int y = 0; y < board.length; ++y) {
      for (int x = 0; x < board[y].length; ++x) {
        if (board[y][x] == 1) 
          cnt_value++; 
        else if (board[y][x] == 0)
          cnt_value--;

        if ((y == 0 && x == 0) || (y == 0 && x == row - 1) ||(y == row - 1 && x == 0) ||(y == row - 1 && x == row - 1)) {
          if (board[y][x] == 1) 
            bythewall_value += corner_point; 
          else if (board[y][x] == 0) 
            bythewall_value -= corner_point;
        } 
        else if (y == 0 || x == 0 || y == row - 1 || x == row - 1) {
          if (board[y][x] == 1)
            bythewall_value += side_point; 
          else if (board[y][x] == 0) 
            bythewall_value -= side_point;
        }
      }
    }

    ArrayList<Position> possible_moves = get_possible_moves(board, "Black");
    possible_moves_value += possible_moves.size();
    possible_moves = get_possible_moves(board, "White");
    possible_moves_value -= possible_moves.size();

    result_value = cnt_value * cnt_weight + bythewall_value * bythewall_weight + possible_moves_value * possible_moves_weight;

    return result_value;
  }

  public int minimax(int[][] board, int depth, int alpha, int beta, String turn) {
    ArrayList<Position> possible_moves = get_possible_moves(board, turn);
    if (depth <= 0 || possible_moves.isEmpty())
      return cal_evaluation_value(board);

    int val = 0;    
    if (turn.equals("Black")) {
      val = Integer.MIN_VALUE;
      for (Position move : possible_moves) {
        int[][] copiedboard = new int[row][];
        for (int i = 0; i < row; ++i)
          copiedboard[i] = board[i].clone();
        
        make_move(copiedboard, move, turn);
        int new_val = minimax(copiedboard, depth - 1, alpha, beta, "White");
        val = Math.max(val, new_val);
        alpha = Math.max(alpha, val);

        if (alpha >= beta) break;
      }
    } 
    else if (turn.equals("White")) {
      val = Integer.MAX_VALUE;
      for (Position move : possible_moves) {
        int[][] copiedboard = new int[row][];
        for (int i = 0; i < board.length; ++i)
          copiedboard[i] = board[i].clone();

        make_move(copiedboard, move, turn);
        int new_val = minimax(copiedboard, depth - 1, alpha, beta, "Black");
        val = Math.min(val, new_val);
        beta = Math.min(beta, val);

        if (alpha >= beta) break;
      }
    }

    return val;
  }

  public Position get_computer_move(int[][] board, String turn, String game_mode) {
    int val = Integer.MIN_VALUE;
    Position next_move = new Position(0, 0);
    ArrayList<Position> possible_moves = get_possible_moves(board, turn);
    // 可能手がない場合は(0, 0)を返す
    if (possible_moves.isEmpty()) 
      return next_move;

    int depth = 0;
    if (game_mode.equals("normal")) 
      depth = 5; 
    else if (game_mode.equals("hard") || game_mode.equals("easy")) 
      depth = 10;

    for (Position move : possible_moves) {
      int[][] copiedboard = new int[row][];
      for (int i = 0; i < row; ++i)
        copiedboard[i] = board[i].clone();
      make_move(copiedboard, move, turn);
      int new_val = minimax(copiedboard, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, turn);
      if ((game_mode.equals("easy") && turn.equals("White")) || (!game_mode.equals("easy") && turn.equals("Black"))) {
        if (val < new_val) {
          val = new_val;
          next_move = move;
        }
      } else {
        if (val > new_val) {
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
            if (turn.equals("Black") && is_inrange(dy, dx) && board[dy][dx] == 0) {
              while (is_inrange(dy, dx)) {
                dy += dz.y;
                dx += dz.x;
                if (is_inrange(dy, dx) && board[dy][dx] == 1) {
                  flag = true;
                  break;
                }
              }
            } else if (turn.equals("White") && is_inrange(dy, dx) && board[dy][dx] == 1) {
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
      if (turn.equals("Black") && is_inrange(dy, dx) && board[dy][dx] == 0) {
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
      else if (turn.equals("White") && is_inrange(dy, dx) && board[dy][dx] == 1) {
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
            board[dy][dx] = 0;
          }
        }
      }
    }
  }

  public boolean is_inrange(int y, int x) {
    return 0 <= y && y < row && 0 <= x && x < row;
  }

  public static void main(String[] args) {
    Othello othello = new Othello("hard");

    // ボードの初期化テスト
    othello.init_board(othello.current_board);
    System.out.println("Initial board:");
    printBoard(othello.current_board);

    // 評価値計算テスト
    int evaluationValue = othello.cal_evaluation_value(othello.current_board);
    System.out.println("Evaluation value: " + evaluationValue);

    // 手の可能性の取得テスト
    ArrayList<Position> possibleMoves = othello.get_possible_moves(othello.current_board,"Black");
    System.out.println("Possible moves for Black:");
    for (Position move : possibleMoves)
      System.out.println(move.y + ", " + move.x);

    // コンピュータの手の取得テスト
    Position computerMove = othello.get_computer_move(othello.current_board,"Black", "hard");
    System.out.println("Computer move (Black, hard mode): " + computerMove.y + ", " + computerMove.x);

    // 手を進めるテスト
    othello.make_move(othello.current_board, computerMove, "Black");
    System.out.println("Board after making a move:");
    printBoard(othello.current_board);

    // 終了状態のテスト
    boolean isEndState = othello.is_end_state(othello.current_board);
    System.out.println("Is end state: " + isEndState);

    // minimaxテスト
    int depth = 5;
    int minimaxValue = othello.minimax(othello.current_board,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,"Black");
    System.out.println("Minimax value: " + minimaxValue);

    // フリップ個数が多いテスト
    int[][] test_board = new int[][] {
      { -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, 1, 1, 1, 1, 1, -1, -1 },
      { -1, 1, 0, 0, 0, 1, -1, -1 },
      { -1, 1, 0, 0, 0, 1, -1, -1 },
      { -1, 1, 0, 0, 0, 1, -1, -1 },
      { -1, 0, 1, 1, 1, 1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1 },
      { -1, -1, -1, -1, -1, -1, -1, -1 },
    };
    System.out.println("Other test board:");
    printBoard(test_board);

    // make_moveテスト（フリップ個数が多い手）
    Position test_move = new Position(0, 1);
    othello.make_move(test_board, test_move, "White");
    System.out.println("Board after making a move:");
    printBoard(test_board);
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
