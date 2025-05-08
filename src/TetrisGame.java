import javax.swing.*;

public class TetrisGame {
    public static void main(String[] args) {
        // 在事件调度线程中创建和显示 GUI，确保线程安全
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("俄罗斯方块");
            TetrisPanel panel = new TetrisPanel();
            frame.add(panel);
            // 设置窗口关闭时的操作
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500,1000);
            // 让窗口居中显示
            frame.setLocationRelativeTo(null);
            // 禁止用户调整窗口大小
            frame.setResizable(false);
            frame.setVisible(true);
            panel.startGame();
        });
    }
}
