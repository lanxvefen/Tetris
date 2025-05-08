import javax.swing.*;
import java.awt.*;

public class TetrisGame extends JFrame {
    private TetrisPanel gamePanel;
    private JButton gameButton;  // 将按钮声明为成员变量

    public TetrisGame() {
        setTitle("俄罗斯方块");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));  // 添加组件间距
        getContentPane().setBackground(new Color(40, 44, 52));  // 设置深色背景
        
        // 创建游戏面板并添加边框
        gamePanel = new TetrisPanel();
        gamePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        gamePanel.setGameEndListener(finalScore -> {
            // 游戏结束时重置按钮
            gameButton.setText("开始游戏");
            gameButton.setBackground(new Color(50, 205, 50));  // 绿色
        });
        
        // 创建一个包含游戏面板的容器，添加标题
        JPanel gamePanelContainer = new JPanel(new BorderLayout());
        gamePanelContainer.setBackground(new Color(50, 54, 62));
        gamePanelContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel gameTitle = new JLabel("俄罗斯方块游戏", JLabel.CENTER);
        gameTitle.setFont(new Font("微软雅黑", Font.BOLD, 22));
        gameTitle.setForeground(Color.WHITE);
        gameTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        gamePanelContainer.add(gameTitle, BorderLayout.NORTH);
        gamePanelContainer.add(gamePanel, BorderLayout.CENTER);
        
        add(gamePanelContainer, BorderLayout.CENTER);

        // 创建侧边栏面板
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(new Color(60, 63, 65));
        sidePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        sidePanel.setPreferredSize(new Dimension(250, 0));  // 增加侧边栏宽度

        // 使用支持中文的字体，增大字体大小
        Font chineseFont = new Font("微软雅黑", Font.BOLD, 24);  // 增大标题字体
        Font chinesePlainFont = new Font("微软雅黑", Font.PLAIN, 18);  // 增大说明文字字体
        Font buttonFont = new Font("微软雅黑", Font.BOLD, 20);  // 为按钮创建专用字体

        JLabel title = new JLabel("游戏说明");
        title.setFont(chineseFont);
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 创建带有背景色的游戏说明面板
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BorderLayout());
        helpPanel.setBackground(new Color(70, 73, 75));
        helpPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JTextArea helpText = new JTextArea(
                "← →: 移动\n" +
                "↑: 旋转\n" +
                "↓: 快速下落\n" +
                "空格: 硬降\n" +
                "P: 暂停/继续\n" +
                "R: 游戏结束后重新开始"
        );
        helpText.setEditable(false);
        helpText.setBackground(new Color(70, 73, 75));
        helpText.setForeground(Color.WHITE);
        helpText.setFont(chinesePlainFont);
        helpPanel.add(helpText, BorderLayout.CENTER);

        // 创建更美观的游戏控制按钮
        gameButton = new JButton("开始游戏");
        gameButton.setFont(buttonFont);
        gameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gameButton.setPreferredSize(new Dimension(180, 50));  // 设置按钮大小
        gameButton.setMaximumSize(new Dimension(180, 50));    // 确保按钮不会被拉伸
        gameButton.setBackground(new Color(50, 205, 50));     // 设置绿色背景
        gameButton.setForeground(Color.WHITE);                // 设置白色文字
        gameButton.setFocusPainted(false);                    // 移除焦点边框
        gameButton.setBorderPainted(true);                    // 保留边框
        gameButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createRaisedBevelBorder(),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));  // 添加立体边框效果
        gameButton.setCursor(new Cursor(Cursor.HAND_CURSOR)); // 鼠标悬停时显示手型光标
        
        // 添加鼠标悬停效果
        gameButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                gameButton.setBackground(new Color(0, 180, 0));  // 鼠标悬停时颜色变深
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (gameButton.getText().equals("开始游戏")) {
                    gameButton.setBackground(new Color(50, 205, 50));  // 恢复原来的颜色
                } else if (gameButton.getText().equals("暂停游戏")) {
                    gameButton.setBackground(new Color(255, 165, 0));  // 橙色
                } else {
                    gameButton.setBackground(new Color(65, 105, 225));  // 蓝色
                }
            }
        });
        
        // 修改按钮的动作监听器
        gameButton.addActionListener(e -> {
            String buttonText = gameButton.getText();
            
            if (buttonText.equals("开始游戏")) {
                // 开始新游戏
                gamePanel.startGame();
                gameButton.setText("暂停游戏");
                gameButton.setBackground(new Color(255, 165, 0));  // 橙色
            } else if (buttonText.equals("暂停游戏")) {
                // 暂停游戏
                gamePanel.togglePauseGame();
                gameButton.setText("继续游戏");
                gameButton.setBackground(new Color(65, 105, 225));  // 蓝色
            } else if (buttonText.equals("继续游戏")) {
                // 继续游戏
                gamePanel.togglePauseGame();
                gameButton.setText("暂停游戏");
                gameButton.setBackground(new Color(255, 165, 0));  // 橙色
            }
            
            // 确保游戏面板获得焦点，这样键盘事件才能被捕获
            gamePanel.requestFocusInWindow();
        });

        // 创建一个面板来容纳按钮，以便更好地控制按钮的位置
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(60, 63, 65));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        buttonPanel.add(gameButton);
        
        sidePanel.add(Box.createVerticalStrut(20));  // 增加上边距
        sidePanel.add(title);
        sidePanel.add(Box.createVerticalStrut(15));  // 增加标题和说明之间的间距
        sidePanel.add(helpPanel);
        sidePanel.add(Box.createVerticalStrut(30));  // 增加说明和按钮之间的间距
        sidePanel.add(buttonPanel);
        sidePanel.add(Box.createVerticalGlue());  // 添加弹性空间

        add(sidePanel, BorderLayout.EAST);

        pack();
        setSize(850, 700);  // 增大窗口初始大小
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        // 确保使用正确的字符编码
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(TetrisGame::new);
    }
}
