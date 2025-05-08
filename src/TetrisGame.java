import javax.swing.*;
import java.awt.*;

public class TetrisGame extends JFrame {
    public TetrisGame() {
        setTitle("俄罗斯方块");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        TetrisPanel gamePanel = new TetrisPanel();
        add(gamePanel, BorderLayout.CENTER);

        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.LIGHT_GRAY);
        sidePanel.setPreferredSize(new Dimension(200, 0));

        JLabel title = new JLabel("游戏说明");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea helpText = new JTextArea(
                "← →: 移动\n" +
                "↑: 旋转\n" +
                "↓: 快速下落\n" +
                "空格: 硬降\n" +
                "P: 暂停/继续"
        );
        helpText.setEditable(false);
        helpText.setBackground(Color.LIGHT_GRAY);
        helpText.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton startButton = new JButton("开始游戏");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.addActionListener(e -> gamePanel.startGame());

        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(title);
        sidePanel.add(Box.createVerticalStrut(10));
        sidePanel.add(helpText);
        sidePanel.add(Box.createVerticalStrut(20));
        sidePanel.add(startButton);

        add(sidePanel, BorderLayout.EAST);

        pack();
        setSize(600, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TetrisGame::new);
    }
}
