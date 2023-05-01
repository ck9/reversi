package client.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame{
    // private Player myPlayer;
    private CardLayout cardLayout = new CardLayout();
    private JPanel contentPane;
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;

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

        myPlayer.setName("Player1");
        // TODO: プレイヤ名の入力を受け付けるようにする

        contentPane = new JPanel();
        cardLayout = new CardLayout();
        contentPane.setLayout(cardLayout);
        setContentPane(contentPane);

        titlePanel = new TitlePanel(othello, myPlayer, opponentPlayer);
        networkPanel = new NetworkPanel(othello, myPlayer, opponentPlayer, port);
        gamePanel = new GamePanel(othello, myPlayer, opponentPlayer);
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
        userNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        userNameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleHeaderPanel.add(Box.createVerticalStrut(10), BorderLayout.NORTH);
        titleHeaderPanel.add(userNameLabel, BorderLayout.CENTER);
        titleScreenPanel.add(titleHeaderPanel, BorderLayout.NORTH);

        JPanel titleMainPanel = new JPanel();
        titleMainPanel.setLayout(new BoxLayout(titleMainPanel, BoxLayout.Y_AXIS));

        titleMainPanel.add(Box.createVerticalStrut(80));

        // ゲームタイトル
        JLabel titleLabel = new JLabel("Reversi !");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleMainPanel.add(titleLabel);

        titleMainPanel.add(Box.createVerticalStrut(80));


        // ゲーム開始ボタン
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JLabel networkLabel = new JLabel("ネットワーク対戦");
        networkLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        networkLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonPanel.add(networkLabel);
        buttonPanel.add(Box.createVerticalStrut(10));

        JPanel networkBtnPanel = new JPanel();
        networkBtnPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton networkBtn = new JButton("Connect");
        networkBtn.setPreferredSize(new Dimension(300, 40));
        networkBtn.setFont(new Font("Arial", Font.PLAIN,20));
        networkBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGameNetwork();
            }
        });
        networkBtnPanel.add(networkBtn);
        buttonPanel.add(networkBtnPanel);


        JLabel localLabel = new JLabel("コンピュータ対戦");
        localLabel.setFont(new Font("Arial", Font.PLAIN, 20));
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
            localButtons[i].setFont(new Font("Arial", Font.PLAIN, 20));
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
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;
    private String serverIP;
    private int port;
    private GamePanel gamePanel;

    public NetworkPanel(Othello othello, Player myPlayer, Player opponentPlayer, int port) {
        this.othello = othello;
        this.myPlayer = myPlayer;
        this.opponentPlayer = opponentPlayer;
        this.port = port;

        // TODO: ネットワーク対戦 接続処理
        JPanel connectingScreenPanel = new JPanel();
        connectingScreenPanel.setLayout(new BorderLayout());

        connectingScreenPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);

        JLabel label = new JLabel("接続画面");
        label.setFont(new Font("Arial", Font.PLAIN, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectingScreenPanel.add(label, BorderLayout.CENTER);

        add(connectingScreenPanel, BorderLayout.CENTER);
    }

    public void startConnect() {

    }

    public void startGame() {

        myPlayer.setColor("black");

        opponentPlayer.setName("testB");
        opponentPlayer.setColor("white");

        ((Client)getParent().getParent().getParent().getParent()).switchPanel("game");
        gamePanel.startGame();
    }

    public void setGamePanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

}

class GamePanel extends JPanel {
    private Othello othello;
    private Player myPlayer;
    private Player opponentPlayer;

    private JLabel gameMode;
    private JLabel myName, opponentName;
    private JPanel boardPanel;

    public GamePanel(Othello othello, Player myPlayer, Player opponentPlayer) {
        this.othello = othello;
        this.myPlayer = myPlayer;
        this.opponentPlayer = opponentPlayer;

        // TODO: ゲーム画面・インターフェース実装
        JPanel gameScreenPanel = new JPanel();
        gameScreenPanel.setLayout(new BorderLayout());
        
        gameScreenPanel.add(Box.createVerticalStrut(100), BorderLayout.NORTH);
        
        gameMode = new JLabel("ゲームモード: ");
        gameMode.setFont(new Font("Arial", Font.PLAIN, 20));
        gameMode.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameScreenPanel.add(gameMode, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.X_AXIS));
        
        myName = new JLabel(myPlayer.getName());
        myName.setFont(new Font("Arial", Font.PLAIN, 20));
        myName.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(myName);
        infoPanel.add(Box.createHorizontalStrut(20));

        JLabel vsLabel = new JLabel("VS");
        vsLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        vsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(vsLabel);
        infoPanel.add(Box.createHorizontalStrut(20));

        opponentName = new JLabel(opponentPlayer.getName());
        opponentName.setFont(new Font("Arial", Font.PLAIN, 20));
        opponentName.setAlignmentX(Component.CENTER_ALIGNMENT);
        infoPanel.add(opponentName);

        gameScreenPanel.add(infoPanel, BorderLayout.SOUTH);

        add(gameScreenPanel, BorderLayout.CENTER);
    }

    public void startGame() {
        gameMode.setText("ゲームモード: " + othello.getGameMode());
        myName.setText(myPlayer.getName());
        opponentName.setText(opponentPlayer.getName());
        boardPanel.repaint();
    }
}

