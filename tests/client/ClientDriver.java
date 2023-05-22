package client;
import javax.swing.JOptionPane;

public class ClientDriver {
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
