package client.java;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Client extends JFrame{
    // private Player myPlayer;
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPane;
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;
    private Server server;

    private TitlePanel titlePanel;
    private NetworkPanel networkPanel;
    private GamePanel gamePanel;


    public Client(int port) {
        setTitle("Reversi"); //ウィンドウタイトルを設定
        setSize(400, 600); //ウィンドウサイズを設定(幅400, 高さ600)
        setResizable(false); //ウィンドウサイズ変更不可に
        setLocationRelativeTo(null); //ウィンドウを画面中央に表示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ウィンドウを閉じるとプログラム終了

        othello = new Othello();
        myPlayer = new Player();
        opponentPlayer = new Player();
        server = new Server();

        // String playerName = JOptionPane.showInputDialog("プレイヤ名を入力してください。");
        // if (playerName == null) {
        //     playerName = "Player";
        // }
        // myPlayer.setName(playerName);
        myPlayer.setName("Player"); //TODO: 後で上記と差し替える

        contentPane = new JPanel();
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);
        setContentPane(contentPane);

        titlePanel = new TitlePanel(othello, myPlayer, opponentPlayer);
        networkPanel = new NetworkPanel(myPlayer, opponentPlayer, port, server);
        gamePanel = new GamePanel(othello, myPlayer, opponentPlayer, server);
        titlePanel.setGamePanel(gamePanel);
        titlePanel.setNetworkPanel(networkPanel);
        networkPanel.setGamePanel(gamePanel);

        contentPane.add(titlePanel, "title"); //タイトル画面
        contentPane.add(networkPanel, "network"); //ネットワーク対戦画面
        contentPane.add(gamePanel, "game"); //ゲーム画面

        cardLayout.show(contentPane, "title");

        setVisible(true);
    }
    public void switchPanel(String panelName) {
        cardLayout.show(contentPane, panelName);
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            try {
                int port = Integer.parseInt(args[0]);
                if (port > 0 && port < 65536) {
                    new Client(port);
                    return;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "ポート番号は1~65535の整数で指定してください。", "エラー", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        new Client(8888);
        return;
    }
}

class TitlePanel extends JPanel {
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;
    private GamePanel gamePanel;
    private NetworkPanel networkPanel;
    Random random = new Random();

    public TitlePanel(Othello othello, Player myPlayer, Player opponentPlayer) {
        this.othello = othello;
        this.myPlayer = myPlayer;
        this.opponentPlayer = opponentPlayer;

        setLayout(new BorderLayout());
        
        JPanel titleScreenPanel = new JPanel();
        titleScreenPanel.setLayout(new BorderLayout());

        // ヘッダー(ユーザー名)
        JPanel titleHeaderPanel = new JPanel();
        titleHeaderPanel.setLayout(new BorderLayout());
        JLabel userNameLabel = new JLabel("  User: " + myPlayer.getName());
        userNameLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleHeaderPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        titleHeaderPanel.add(userNameLabel, BorderLayout.CENTER);
        titleScreenPanel.add(titleHeaderPanel, BorderLayout.NORTH);

        JPanel titleMainPanel = new JPanel();
        titleMainPanel.setLayout(new BoxLayout(titleMainPanel, BoxLayout.Y_AXIS));

        titleMainPanel.add(Box.createVerticalStrut(80));

        // ゲームタイトル
        JLabel titleLabel = new JLabel("Reversi !");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleMainPanel.add(titleLabel);

        titleMainPanel.add(Box.createVerticalStrut(80));


        // ゲーム開始ボタン
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JLabel networkLabel = new JLabel("ネットワーク対戦");
        networkLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        networkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(networkLabel);
        buttonPanel.add(Box.createVerticalStrut(10));

        JPanel networkBtnPanel = new JPanel();
        networkBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton networkBtn = new JButton("Connect");
        networkBtn.setPreferredSize(new Dimension(300, 40));
        networkBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN,20));
        networkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGameNetwork();
            }
        });
        networkBtnPanel.add(networkBtn);
        buttonPanel.add(networkBtnPanel);


        JLabel localLabel = new JLabel("コンピュータ対戦");
        localLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        localLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(localLabel);
        buttonPanel.add(Box.createVerticalStrut(10));

        JPanel localBtnPanel = new JPanel();
        localBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        String[] difficulties = new String[] {"Easy", "Normal", "Hard"};
        JButton[] localButtons = new JButton[difficulties.length];
        for (int i = 0; i < difficulties.length; i++) {
            int difficulty = i;
            localButtons[i] = new JButton(difficulties[i]);
            localButtons[i].setPreferredSize(new Dimension(100, 40));
            localButtons[i].setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
            localButtons[i].addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    startGameLocal(difficulty);
                }
            });
            localBtnPanel.add(localButtons[i]);
        }

        buttonPanel.add(localBtnPanel);
        titleMainPanel.add(buttonPanel);

        titleMainPanel.add(Box.createVerticalStrut(30));

        titleScreenPanel.add(titleMainPanel, BorderLayout.CENTER);
        add(titleScreenPanel, BorderLayout.CENTER);
    }
    /*
     * ローカルでゲームを開始する(vs CPU)
     * difficulty: 0: easy, 1: normal, 2: hard
     */
    public void startGameLocal(int difficulty) {
        othello.startGameLocal(difficulty);
        opponentPlayer.setName("CPU(" + othello.getGameMode() + ")");
        random.setSeed(System.currentTimeMillis());
        if (random.nextInt(2) == 0) {
            myPlayer.setColor("black");
            opponentPlayer.setColor("white");
        } else {
            myPlayer.setColor("white");
            opponentPlayer.setColor("black");
        }

        ((Client)getParent().getParent().getParent().getParent()).switchPanel("game");
        gamePanel.startGame();
    }

    /*
     * ネットワーク対戦でゲームを開始する
     */
    public void startGameNetwork() {
        ((Client)getParent().getParent().getParent().getParent()).switchPanel("network");
        networkPanel.startConnect();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    public void setNetworkPanel(NetworkPanel networkPanel) {
        this.networkPanel = networkPanel;
    }
}

class NetworkPanel extends JPanel {
    private Player myPlayer;
    private Player opponentPlayer;
    private Server server;

    private int port;
    private GamePanel gamePanel;

    private JLabel connectingInfoLabel;
    private JLabel serverInfoLabel1;
    private JLabel serverInfoLabel2;

    public NetworkPanel(Player myPlayer, Player opponentPlayer, int port, Server server) {
        this.myPlayer = myPlayer;
        this.opponentPlayer = opponentPlayer;
        this.server = server;
        this.port = port;

        JPanel connectingScreenPanel = new JPanel();
        connectingScreenPanel.setLayout(new BorderLayout());

        connectingScreenPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);

        JPanel connecctingInfoPanel = new JPanel();
        connecctingInfoPanel.setLayout(new BoxLayout(connecctingInfoPanel, BoxLayout.Y_AXIS));

        // ゲームモード表示(ネットワーク対戦)
        JLabel connectingLabel = new JLabel("ネットワーク対戦");
        connectingLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        connectingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connecctingInfoPanel.add(connectingLabel);

        connecctingInfoPanel.add(Box.createVerticalStrut(80));

        //接続状況表示
        connectingInfoLabel = new JLabel("");
        connectingInfoLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        connectingInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        connecctingInfoPanel.add(connectingInfoLabel);

        connecctingInfoPanel.add(Box.createVerticalStrut(40));

        //接続先サーバー情報表示(IPアドレス:ポート番号)
        serverInfoLabel1 = new JLabel("");
        serverInfoLabel1.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        serverInfoLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        connecctingInfoPanel.add(serverInfoLabel1);
        serverInfoLabel2 = new JLabel("");
        serverInfoLabel2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 15));
        serverInfoLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        connecctingInfoPanel.add(serverInfoLabel2);

        connectingScreenPanel.add(connecctingInfoPanel, BorderLayout.CENTER);

        add(connectingScreenPanel, BorderLayout.CENTER);
    }

    public void startConnect() {
    	try {
            connectingInfoLabel.setText("");
            serverInfoLabel1.setText("");
            serverInfoLabel2.setText("");

            // サーバーIPの入力
            String serverIP = JOptionPane.showInputDialog(this, "サーバーIPを入力してください");
            if (serverIP == null) {
                throw new Exception();
            }

            // サーバーへ接続
            server.connect(serverIP, port);

            // 接続中の表示
            connectingInfoLabel.setText("対戦相手を探しています...");
            serverInfoLabel1.setText("サーバー情報");
            serverInfoLabel2.setText(serverIP + ":" + port);

           //自分の名前を送信
           server.sendToServer(myPlayer.getName());

           // 相手が接続したら相手の名前、自分の色が順に送られてくる
           opponentPlayer.setName(server.receiveFromServer());
           myPlayer.setColor(server.receiveFromServer());

           // 相手の色を設定
           if (myPlayer.getColor().equals("black")) {
               opponentPlayer.setColor("white");
           } else {
               opponentPlayer.setColor("black");
           }

           // ゲーム開始
           ((Client)getParent().getParent().getParent().getParent()).switchPanel("game");
           gamePanel.startGame();
            
        }catch(Exception e){
        	JOptionPane.showMessageDialog(this, "接続に失敗しました","接続失敗",JOptionPane.ERROR_MESSAGE);
        	((Client)getParent().getParent().getParent().getParent()).switchPanel("title");
        }
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

}

class GamePanel extends JPanel {
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;
    private Server server;

    private JPanel gameScreenPanel;
    private JLabel myStoneIconLabel, opponentStoneIconLabel;
    private JLabel myTurnIconLabel, opponentTurnIconLabel;
    private JLabel myName, opponentName;

    private JButton passBtn, giveUpBtn;
    private JButton[] boardBtns;

    ImageIcon whiteIcon = new ImageIcon("src/client/resources/White.jpg");
    ImageIcon blackIcon = new ImageIcon("src/client/resources/Black.jpg");
    ImageIcon whiteIcon2 = new ImageIcon("src/client/resources/White.png");
    ImageIcon blackIcon2 = new ImageIcon("src/client/resources/Black.png");
    ImageIcon boardIcon = new ImageIcon("src/client/resources/GreenFrame.jpg");
    ImageIcon possibleIcon = new ImageIcon("src/client/resources/GreenPossibleFrame.jpg");
    ImageIcon turnIcon = new ImageIcon("src/client/resources/TurnTriangle.png");

    public GamePanel(Othello othello, Player myPlayer, Player opponentPlayer, Server server) {
        this.othello = othello;
        this.myPlayer = myPlayer;
        this.opponentPlayer = opponentPlayer;
        this.server = server;

        gameScreenPanel = new JPanel();
        gameScreenPanel.setLayout(new BorderLayout());
        
        JPanel passBtnsPanel = new JPanel();
        passBtnsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        passBtn = new JButton("パス");
        passBtn.setPreferredSize(new Dimension(180, 40));
        passBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        passBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                putStorn(-1, -1);
            }
        });
        passBtnsPanel.add(passBtn);
        giveUpBtn = new JButton("投了");
        giveUpBtn.setPreferredSize(new Dimension(180, 40));
        giveUpBtn.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        giveUpBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                endGame("playerGiveUp");
            }
        });
        passBtnsPanel.add(giveUpBtn);
        gameScreenPanel.add(passBtnsPanel, BorderLayout.NORTH);

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(othello.get_row(), othello.get_row()));
        boardBtns = new JButton[othello.get_row() * othello.get_row()];
        int iconWidth = 360 / othello.get_row();
        Dimension iconSize = new Dimension(iconWidth, iconWidth);
        Insets noMargin = new Insets(0, 0, 0, 0);
        for (int i = 0; i < othello.get_row(); i++) {
            for (int j = 0; j < othello.get_row(); j++) {
                int x = j;
                int y = i;
                boardBtns[i * othello.get_row() + j] = new JButton();
                boardBtns[i * othello.get_row() + j].setPreferredSize(iconSize);
                boardBtns[i * othello.get_row() + j].setMargin(noMargin);
                boardBtns[i * othello.get_row() + j].setBorder(null);
                boardBtns[i * othello.get_row() + j].setBorderPainted(false);
                boardBtns[i * othello.get_row() + j].setContentAreaFilled(false);
                boardBtns[i * othello.get_row() + j].setOpaque(false);
                boardBtns[i * othello.get_row() + j].setIcon(boardIcon);
                boardBtns[i * othello.get_row() + j].addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        putStorn(x, y);
                    }
                });
                boardPanel.add(boardBtns[i * othello.get_row() + j]);
            }
        }
        gameScreenPanel.add(boardPanel, BorderLayout.CENTER);


        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(Box.createVerticalStrut(24));

        JPanel myInfoPanel = new JPanel();
        myInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        myInfoPanel.add(Box.createHorizontalStrut(20));
        myTurnIconLabel = new JLabel(turnIcon);
        myTurnIconLabel.setPreferredSize(new Dimension(40, 40));
        myInfoPanel.add(myTurnIconLabel);
        myStoneIconLabel = new JLabel(blackIcon2);
        myStoneIconLabel.setPreferredSize(new Dimension(40, 40));
        myInfoPanel.add(myStoneIconLabel);        
        myInfoPanel.add(Box.createHorizontalStrut(8));
        myName = new JLabel(myPlayer.getName()+"(You)");
        myName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        myInfoPanel.add(myName);
        infoPanel.add(myInfoPanel);

        infoPanel.add(Box.createVerticalStrut(5));

        JPanel opponentInfoPanel = new JPanel();
        opponentInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        opponentInfoPanel.add(Box.createHorizontalStrut(20));
        opponentTurnIconLabel = new JLabel(turnIcon);
        opponentTurnIconLabel.setPreferredSize(new Dimension(40, 40));
        opponentInfoPanel.add(opponentTurnIconLabel);
        opponentStoneIconLabel = new JLabel(whiteIcon2);
        opponentStoneIconLabel.setPreferredSize(new Dimension(40, 40));
        opponentInfoPanel.add(opponentStoneIconLabel);
        opponentInfoPanel.add(Box.createHorizontalStrut(8));
        opponentName = new JLabel(opponentPlayer.getName());
        opponentName.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 30));
        opponentInfoPanel.add(opponentName);
        infoPanel.add(opponentInfoPanel);

        gameScreenPanel.add(infoPanel, BorderLayout.SOUTH);

        add(gameScreenPanel, BorderLayout.CENTER);
    }

    /**
     * @param x
     * @param y
     */
    public void putStorn(int x, int y) {
        // 自分のターンでない場合は何もしない
        if (othello.get_turn() != myPlayer.getColor()) {
            return;
        }
        // 正常な座標をクリックした場合(パスの場合は-1, -1なのでスキップ)
        if (0 <= x && x < othello.get_row() && 0 <= y && y < othello.get_row()) {
            ArrayList<Position> possible_moves = othello.get_possible_moves(othello.get_board(), othello.get_turn());
            // 置けない場所をクリックした場合は何もしない
            Boolean is_valid_move = false;
            for (Position pos : possible_moves) {
                if (pos.getX() == x && pos.getY() == y) {
                    is_valid_move = true;
                }
            }
            if (!is_valid_move) {
                return;
            }
            else {
                System.out.println("putStorn: " + x + ", " + y); //TODO: 削除
                othello.make_move(othello.get_board(), new Position(y, x), othello.get_turn());
            }
        }
        othello.change_turn();
        updateBoard();

        // ネットワーク対戦の場合はサーバーに自分の指し手を送信
        if (othello.getGameMode() == "pvp") {
            try {
                server.sendToServer(x + " " + y);
            } catch (Exception e) {
                e.printStackTrace();
                endGame("connectionError");
            }
        }

        // 相手のターンの処理
        opponentPutStorn();
    }

    public void opponentPutStorn() {
        // 相手のターンの処理(ネットワーク対戦)
        if (othello.getGameMode() == "pvp") {
            try {
                // 相手の指し手を受信
                String opponentMove = server.receiveFromServer();
                System.out.println("opponentMove: " + opponentMove); //TODO: 削除
                String[] opponentMoveArray = opponentMove.split(" ");
                int x = Integer.parseInt(opponentMoveArray[0]);
                int y = Integer.parseInt(opponentMoveArray[1]);

                // "-2 -2": 相手が投了または切断、ゲーム終了
                if (x == -2 && y == -2) {
                    endGame("opponentGiveUp");
                    return;
                }
                // "-1 -1": 相手がパス (それ以外なら相手の座標を反映)
                else if (x != -1 && y != -1) {
                    othello.make_move(othello.get_board(), new Position(y, x), othello.get_turn());
                }
            } catch (Exception e) {
                e.printStackTrace();
                endGame("connectionError");
            }
        }
        // 相手のターンの処理(コンピュータ対戦)
        else{
            Position computerMove = othello.get_computer_move(othello.get_board(), othello.get_turn(), othello.getGameMode());
            System.out.println("computerMove: " + computerMove.getX() + ", " + computerMove.getY()); //TODO: 削除
            if (computerMove.getX() != -1 && computerMove.getY() != -1){
                othello.make_move(othello.get_board(), computerMove, othello.get_turn());
            }
        }
        othello.change_turn();
        updateBoard();
    }

    private void updateBoard(){
        int[][] board = othello.get_board();
        ArrayList<Position> possible_moves = othello.get_possible_moves(board, othello.get_turn());
        // パスの有効化・無効化
        if (possible_moves.size() == 0){
            passBtn.setEnabled(true);
        }
        else{
            passBtn.setEnabled(false);
        }
        // 投了の有効化・無効化(自分のターンのみ)
        if (othello.get_turn().equals(myPlayer.getColor())){
            giveUpBtn.setEnabled(true);
        }
        else{
            giveUpBtn.setEnabled(false);
        }
        // 盤面の更新
        for (int i = 0; i < othello.get_row(); i++) {
            for (int j = 0; j < othello.get_row(); j++) {
                if (board[i][j] == -1) {
                    boardBtns[i * othello.get_row() + j].setIcon(boardIcon);
                } else if (board[i][j] == 1) {
                    boardBtns[i * othello.get_row() + j].setIcon(blackIcon);
                } else if (board[i][j] == 0) {
                    boardBtns[i * othello.get_row() + j].setIcon(whiteIcon);
                }
                for (Position pos : possible_moves) {
                    if (pos.getX() == j && pos.getY() == i) {
                        boardBtns[i * othello.get_row() + j].setIcon(possibleIcon);
                    }
                }
            }
        }
        // info表示の更新
        if (myPlayer.getColor() == "black") {
            myStoneIconLabel.setIcon(blackIcon2);
            opponentStoneIconLabel.setIcon(whiteIcon2);
            if (othello.get_turn() == "black") {
                myTurnIconLabel.setIcon(turnIcon);
                opponentTurnIconLabel.setIcon(null);
            } else {
                myTurnIconLabel.setIcon(null);
                opponentTurnIconLabel.setIcon(turnIcon);
            }
        } else {
            myStoneIconLabel.setIcon(whiteIcon2);
            opponentStoneIconLabel.setIcon(blackIcon2);
            if (othello.get_turn() == "white") {
                myTurnIconLabel.setIcon(turnIcon);
                opponentTurnIconLabel.setIcon(null);
            } else {
                myTurnIconLabel.setIcon(null);
                opponentTurnIconLabel.setIcon(turnIcon);
            }
        }
        // ゲーム終了判定
        if (othello.is_end_state(board)) {
            endGame("end");
        }

        // 画面をすぐに更新(対戦相手の思考中であることがわかるように)
        gameScreenPanel.paintImmediately(0, 0, gameScreenPanel.getWidth(), gameScreenPanel.getHeight());

        System.out.println("updateBoar(Turn: " + othello.get_turn() + ")"); //TODO: 削除
    }

    public void endGame(String mode) {
        int[][] board = othello.get_board();
        String message;
        if (mode.equals("playerGiveUp")){
            message = "あなたの投了負けです";
        }
        else if (mode.equals("opponentGiveUp")){
            message = "相手が投了しました\nあなたの勝利です";
        }
        else if (mode.equals("connectionError")){
            message = "サーバーとの接続が切れました\nゲームを終了します";
        }
        else{
            int blackCount = 0;
            int whiteCount = 0;
            for (int i = 0; i < othello.get_row(); i++) {
                for (int j = 0; j < othello.get_row(); j++) {
                    if (board[i][j] == 1) {
                        blackCount++;
                    } else if (board[i][j] == 0) {
                        whiteCount++;
                    }
                }
            }
            message = "黒: " + blackCount + "  白: " + whiteCount + "\n";
            if (blackCount > whiteCount) {
                if (myPlayer.getColor() == "black") {
                    message += "You Win!\n";
                } else {
                    message += "You Lose...\n";
                }
            } else if (blackCount < whiteCount) {
                if (myPlayer.getColor() == "black") {
                    message += "You Lose...\n";
                } else {
                    message += "You Win!\n";
                }
            } else {
                message += "Draw\n";
            }
        }
        JOptionPane.showMessageDialog(this, message, "ゲーム終了", JOptionPane.INFORMATION_MESSAGE);
        ((Client)getParent().getParent().getParent().getParent()).switchPanel("title");
    }

    public void startGame() {
        myName.setText(myPlayer.getName() + "(You)");
        opponentName.setText(opponentPlayer.getName());
        // 相手が先手の場合は待機
        if (opponentPlayer.getColor() == "black") {
            opponentPutStorn();
        }
        updateBoard();
    }
}

