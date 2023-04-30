package client.java;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame{
    // private Player myPlayer;

    public Client(int port) {
        setTitle("Reversi"); //ウィンドウタイトルを設定
        setSize(400, 600); //ウィンドウサイズを設定(幅400, 高さ600)
        setResizable(false); //ウィンドウサイズ変更不可に
        setLocationRelativeTo(null); //ウィンドウを画面中央に表示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //ウィンドウを閉じるとプログラム終了

        // プレイヤークラスができたらコメントアウトを外す
        // myPlayer = new Player();
        // titleScreen(myPlayer.getUserName());

        titleScreen("testUser");

        setVisible(true);

    }

    private void titleScreen(String userName) {
        setLayout(new BorderLayout());
        
        JPanel titleScreenPanel = new JPanel();
        titleScreenPanel.setLayout(new BorderLayout());

        // ヘッダー(ユーザー名)
        JPanel titleHeaderPanel = new JPanel();
        titleHeaderPanel.setLayout(new BorderLayout());
        JLabel userNameLabel = new JLabel("  User: " + userName);
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
        System.out.println("startGameLocal difficulty: " + difficulty);
        // Othello othello = new Othello();
    }

    /*
     * ネットワーク対戦でゲームを開始する
     */
    public void startGameNetwork() {
        System.out.println("startGameNetwork");
        // Othello othello = new Othello();
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
