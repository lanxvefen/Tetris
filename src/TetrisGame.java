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
        sidePanel.setPreferredSize(new Dimension(250, 0));  // 增加侧边栏宽度

        // 使用支持中文的字体，增大字体大小
        Font chineseFont = new Font("微软雅黑", Font.BOLD, 24);  // 增大标题字体
        Font chinesePlainFont = new Font("微软雅黑", Font.PLAIN, 18);  // 增大说明文字字体
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 20);  // 为按钮创建专用字体

        JLabel title = new JLabel("游戏说明");
        title.setFont(chineseFont);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextArea helpText = new JTextArea(
                "← →: 移动\n" +
                "↑: 旋转\n" +
                "↓: 快速下落\n" +
                "空格: 硬降\n" +
                "P: 暂停/继续\n" +
                "R: 游戏结束后重新开始"
        );
        helpText.setEditable(false);
        helpText.setBackground(Color.LIGHT_GRAY);
        helpText.setFont(chinesePlainFont);

        // 创建更美观的开始游戏按钮
        JButton startButton = new JButton("开始游戏");
        startButton.setFont(buttonFont);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(150, 50));  // 设置按钮大小
        startButton.setMaximumSize(new Dimension(150, 50));    // 确保按钮不会被拉伸
        startButton.setBackground(new Color(50, 205, 50));     // 设置绿色背景
        startButton.setForeground(Color.WHITE);                // 设置白色文字
        startButton.setFocusPainted(false);                    // 移除焦点边框
        startButton.setBorderPainted(true);                    // 保留边框
        startButton.setBorder(BorderFactory.createRaisedBevelBorder());  // 添加立体边框效果
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 鼠标悬停时显示手型光标
        
        // 添加鼠标悬停效果
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(0, 180, 0));  // 鼠标悬停时颜色变深
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(50, 205, 50));  // 恢复原来的颜色
            }
        });
        
        startButton.addActionListener(e -> {
            gamePanel.startGame();
            // 确保游戏面板获得焦点，这样键盘事件才能被捕获
            gamePanel.requestFocusInWindow();
        });

        sidePanel.add(Box.createVerticalStrut(30));  // 增加上边距
        sidePanel.add(title);
        sidePanel.add(Box.createVerticalStrut(20));  // 增加标题和说明之间的间距
        sidePanel.add(helpText);
        sidePanel.add(Box.createVerticalStrut(40));  // 增加说明和按钮之间的间距
        
        // 创建一个面板来容纳按钮，以便更好地控制按钮的位置
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.LIGHT_GRAY);
        buttonPanel.add(startButton);
        
        sidePanel.add(buttonPanel);
        sidePanel.add(Box.createVerticalStrut(30));  // 增加底部边距

        add(sidePanel, BorderLayout.EAST);

        pack();
        setSize(800, 700);  // 增大窗口初始大小
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        // 确保使用正确的字符编码
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(TetrisGame::new);
    }
}
