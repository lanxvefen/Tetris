import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class TetrisPanel extends JPanel implements ActionListener {
    private static final int BOARD_WIDTH = 10;
    private static final int BOARD_HEIGHT = 20;
    private int[][] board;
    private Timer timer;
    private int score;
    private Block currentBlock;
    private Block nextBlock;
    private int dropInterval = 500;
    private boolean isPaused = false;
    private boolean isGameOver = false;
    
    // 添加游戏难度设置
    private enum Difficulty {
        EASY(500, "简单"),
        MEDIUM(300, "中等"),
        HARD(150, "进阶");
        
        private final int initialSpeed;
        private final String displayName;
        
        Difficulty(int initialSpeed, String displayName) {
            this.initialSpeed = initialSpeed;
            this.displayName = displayName;
        }
        
        public int getInitialSpeed() {
            return initialSpeed;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    private Difficulty currentDifficulty = Difficulty.EASY;
    
    // 添加中文字体
    private Font chineseFont = new Font("微软雅黑", Font.BOLD, 20);
    private Font chineseFontLarge = new Font("微软雅黑", Font.BOLD, 40);
    private Font scoreFont = new Font("微软雅黑", Font.BOLD, 22);
    private Font buttonFont = new Font("微软雅黑", Font.BOLD, 16);

    // 添加按钮
    private JButton changeDifficultyButton;

    public TetrisPanel() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(dropInterval, this);
        addKeyListener(new KeyHandler());
        setFocusable(true);
        setBackground(Color.DARK_GRAY);
        
        // 初始化按钮
        initButtons();
    }
    
    private void initButtons() {
        setLayout(null); // 使用绝对布局
        
        // 切换难度按钮
        changeDifficultyButton = new JButton("切换难度: " + currentDifficulty.getDisplayName());
        changeDifficultyButton.setFont(buttonFont);
        changeDifficultyButton.addActionListener(e -> cycleDifficulty());
        add(changeDifficultyButton);
        
        // 按钮位置将在组件大小调整时设置
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonPositions();
            }
        });
    }
    
    private void updateButtonPositions() {
        int blockSize = getBlockSize();
        int infoX = BOARD_WIDTH * blockSize + 20;
        int buttonWidth = getWidth() - infoX - 15;
        
        // 设置按钮位置
        changeDifficultyButton.setBounds(infoX - 5, 250, buttonWidth, 40);
    }
    
    private void cycleDifficulty() {
        switch (currentDifficulty) {
            case EASY:
                currentDifficulty = Difficulty.MEDIUM;
                break;
            case MEDIUM:
                currentDifficulty = Difficulty.HARD;
                break;
            case HARD:
                currentDifficulty = Difficulty.EASY;
                break;
        }
        changeDifficultyButton.setText("切换难度: " + currentDifficulty.getDisplayName());
        dropInterval = currentDifficulty.getInitialSpeed();
        timer.setDelay(dropInterval);
        repaint();
    }

    public void startGame() {
        clearBoard();
        score = 0;
        dropInterval = currentDifficulty.getInitialSpeed();
        timer.setDelay(dropInterval);
        nextBlock = Block.randomBlock();
        newBlock();
        isPaused = false;
        isGameOver = false;
        timer.start();
        requestFocusInWindow(); // 确保面板获得焦点以接收键盘事件
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = 0;
            }
        }
    }

    // 添加游戏结束回调接口
    public interface GameEndListener {
        void onGameEnd(int finalScore);
    }
    
    private GameEndListener gameEndListener;
    
    public void setGameEndListener(GameEndListener listener) {
        this.gameEndListener = listener;
    }

    private void newBlock() {
        currentBlock = nextBlock;
        nextBlock = Block.randomBlock();
        currentBlock.x = BOARD_WIDTH / 2;
        currentBlock.y = 0;
        if (!canMove(currentBlock, 0, 0)) {
            timer.stop();
            isGameOver = true;
            
            // 通知游戏结束
            if (gameEndListener != null) {
                gameEndListener.onGameEnd(score);
            }
        }
    }

    private boolean canMove(Block block, int dx, int dy) {
        for (int i = 0; i < 4; i++) {
            int x = block.x + block.coords[i][0] + dx;
            int y = block.y + block.coords[i][1] + dy;
            if (x < 0 || x >= BOARD_WIDTH || y >= BOARD_HEIGHT) {
                return false;
            }
            if (y >= 0 && board[y][x] != 0) {
                return false;
            }
        }
        return true;
    }

    private void mergeBlock() {
        for (int i = 0; i < 4; i++) {
            int x = currentBlock.x + currentBlock.coords[i][0];
            int y = currentBlock.y + currentBlock.coords[i][1];
            if (y >= 0) board[y][x] = currentBlock.shape + 1;
        }
        checkLines();
        newBlock();
    }

    private void checkLines() {
        int linesCleared = 0;
        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean full = true;
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                linesCleared++;
                for (int k = i; k > 0; k--) {
                    System.arraycopy(board[k - 1], 0, board[k], 0, BOARD_WIDTH);
                }
                for (int j = 0; j < BOARD_WIDTH; j++) board[0][j] = 0;
                i++;
            }
        }
        score += linesCleared * 100;
        
        // 根据当前难度调整下落速度
        if (linesCleared > 0) {
            int minSpeed = 50; // 最快速度
            int speedReduction = currentDifficulty == Difficulty.EASY ? 10 : 
                                (currentDifficulty == Difficulty.MEDIUM ? 20 : 30);
            
            if (dropInterval > minSpeed) {
                dropInterval -= speedReduction;
                timer.setDelay(dropInterval);
            }
        }
    }

    private int getBlockSize() {
        return Math.min(getWidth() / (BOARD_WIDTH + 5), getHeight() / BOARD_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int blockSize = getBlockSize();

        // 绘制游戏区域背景
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, BOARD_WIDTH * blockSize, BOARD_HEIGHT * blockSize);
        
        // 绘制信息区域背景
        g.setColor(new Color(50, 50, 50));
        g.fillRect(BOARD_WIDTH * blockSize, 0, getWidth() - BOARD_WIDTH * blockSize, getHeight());
        
        // 绘制游戏区域边框
        g.setColor(new Color(100, 100, 100));
        g.drawRect(0, 0, BOARD_WIDTH * blockSize, BOARD_HEIGHT * blockSize);

        // 绘制网格线
        g.setColor(new Color(60, 60, 60));
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            g.drawLine(0, i * blockSize, BOARD_WIDTH * blockSize, i * blockSize);
        }
        for (int j = 0; j <= BOARD_WIDTH; j++) {
            g.drawLine(j * blockSize, 0, j * blockSize, BOARD_HEIGHT * blockSize);
        }

        // 绘制已固定的方块
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != 0) {
                    Color blockColor = Color.getHSBColor((float) (board[i][j] * 0.1), 0.8f, 0.9f);
                    g.setColor(blockColor);
                    g.fillRect(j * blockSize + 1, i * blockSize + 1, blockSize - 2, blockSize - 2);
                    
                    // 添加高光效果
                    g.setColor(new Color(255, 255, 255, 100));
                    g.drawLine(j * blockSize + 1, i * blockSize + 1, j * blockSize + 1, i * blockSize + blockSize - 2);
                    g.drawLine(j * blockSize + 1, i * blockSize + 1, j * blockSize + blockSize - 2, i * blockSize + 1);
                    
                    // 添加阴影效果
                    g.setColor(new Color(0, 0, 0, 100));
                    g.drawLine(j * blockSize + blockSize - 2, i * blockSize + 1, j * blockSize + blockSize - 2, i * blockSize + blockSize - 2);
                    g.drawLine(j * blockSize + 1, i * blockSize + blockSize - 2, j * blockSize + blockSize - 2, i * blockSize + blockSize - 2);
                }
            }
        }

        // 绘制当前方块
        if (currentBlock != null) {
            Color currentBlockColor = Color.getHSBColor((float) (currentBlock.shape * 0.1), 0.8f, 0.9f);
            for (int i = 0; i < 4; i++) {
                int x = currentBlock.x + currentBlock.coords[i][0];
                int y = currentBlock.y + currentBlock.coords[i][1];
                if (y >= 0) {
                    g.setColor(currentBlockColor);
                    g.fillRect(x * blockSize + 1, y * blockSize + 1, blockSize - 2, blockSize - 2);
                    
                    // 添加高光效果
                    g.setColor(new Color(255, 255, 255, 100));
                    g.drawLine(x * blockSize + 1, y * blockSize + 1, x * blockSize + 1, y * blockSize + blockSize - 2);
                    g.drawLine(x * blockSize + 1, y * blockSize + 1, x * blockSize + blockSize - 2, y * blockSize + 1);
                    
                    // 添加阴影效果
                    g.setColor(new Color(0, 0, 0, 100));
                    g.drawLine(x * blockSize + blockSize - 2, y * blockSize + 1, x * blockSize + blockSize - 2, y * blockSize + blockSize - 2);
                    g.drawLine(x * blockSize + 1, y * blockSize + blockSize - 2, x * blockSize + blockSize - 2, y * blockSize + blockSize - 2);
                }
            }
        }

        // 绘制信息区域
        int infoX = BOARD_WIDTH * blockSize + 20;
        
        // 绘制得分区域
        g.setColor(new Color(70, 70, 70));
        g.fillRoundRect(infoX - 5, 10, getWidth() - infoX - 15, 50, 10, 10);
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(infoX - 5, 10, getWidth() - infoX - 15, 50, 10, 10);
        
        // 得分 - 使用中文字体
        g.setColor(Color.WHITE);
        g.setFont(scoreFont);
        g.drawString("得分: " + score, infoX + 10, 45);

        // 绘制下一块区域 - 调整大小以刚好包围内容
        g.setColor(new Color(70, 70, 70));
        g.fillRoundRect(infoX - 5, 70, getWidth() - infoX - 15, 110, 10, 10);
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(infoX - 5, 70, getWidth() - infoX - 15, 110, 10, 10);
        
        // 下一块 - 使用中文字体
        g.setColor(new Color(135, 206, 250)); // 淡蓝色
        g.setFont(chineseFont);
        g.drawString("下一块：", infoX + 10, 95);
        
        // 绘制下一块预览
        if (nextBlock != null) {
            // 计算居中位置
            int previewWidth = 4 * blockSize;
            int previewHeight = 4 * blockSize;
            int previewAreaWidth = getWidth() - infoX - 15;
            int previewAreaHeight = 110; // 调整后的下一块区域的高度
            
            // 计算方块的中心位置
            int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
            
            for (int i = 0; i < 4; i++) {
                minX = Math.min(minX, nextBlock.coords[i][0]);
                maxX = Math.max(maxX, nextBlock.coords[i][0]);
                minY = Math.min(minY, nextBlock.coords[i][1]);
                maxY = Math.max(maxY, nextBlock.coords[i][1]);
            }
            
            int blockWidth = maxX - minX + 1;
            int blockHeight = maxY - minY + 1;
            
            // 调整偏移量，确保方块在预览区域内完全居中
            int offsetX = infoX + (previewAreaWidth - blockWidth * blockSize) / 2 - minX * blockSize;
            int offsetY = 70 + (previewAreaHeight - blockHeight * blockSize) / 2 - minY * blockSize + 10;
            
            // 绘制方块
            Color nextColor = Color.getHSBColor((float) (nextBlock.shape * 0.1), 0.8f, 0.9f);
            for (int i = 0; i < 4; i++) {
                int x = nextBlock.coords[i][0];
                int y = nextBlock.coords[i][1];
                
                g.setColor(nextColor);
                g.fillRect(offsetX + x * blockSize, offsetY + y * blockSize, blockSize - 2, blockSize - 2);
                
                // 添加高光效果
                g.setColor(new Color(255, 255, 255, 100));
                g.drawLine(offsetX + x * blockSize, offsetY + y * blockSize, 
                           offsetX + x * blockSize, offsetY + y * blockSize + blockSize - 3);
                g.drawLine(offsetX + x * blockSize, offsetY + y * blockSize, 
                           offsetX + x * blockSize + blockSize - 3, offsetY + y * blockSize);
                
                // 添加阴影效果
                g.setColor(new Color(0, 0, 0, 100));
                g.drawLine(offsetX + x * blockSize + blockSize - 3, offsetY + y * blockSize, 
                           offsetX + x * blockSize + blockSize - 3, offsetY + y * blockSize + blockSize - 3);
                g.drawLine(offsetX + x * blockSize, offsetY + y * blockSize + blockSize - 3, 
                           offsetX + x * blockSize + blockSize - 3, offsetY + y * blockSize + blockSize - 3);
            }
        }
        
        // 绘制难度显示区域
        g.setColor(new Color(70, 70, 70));
        g.fillRoundRect(infoX - 5, 190, getWidth() - infoX - 15, 50, 10, 10);
        g.setColor(new Color(200, 200, 200));
        g.drawRoundRect(infoX - 5, 190, getWidth() - infoX - 15, 50, 10, 10);
        
        // 难度显示
        g.setColor(new Color(255, 215, 0)); // 金色
        g.setFont(chineseFont);
        g.drawString("难度: " + currentDifficulty.getDisplayName(), infoX + 10, 225);

        // 绘制游戏状态
        if (isPaused) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, BOARD_WIDTH * blockSize, BOARD_HEIGHT * blockSize);
            g.setColor(new Color(255, 100, 100));
            g.setFont(chineseFontLarge);  // 使用大号中文字体
            FontMetrics fm = g.getFontMetrics();
            String pauseText = "暂停中";
            int textWidth = fm.stringWidth(pauseText);
            g.drawString(pauseText, (BOARD_WIDTH * blockSize - textWidth) / 2, BOARD_HEIGHT * blockSize / 2);
        } else if (isGameOver) {
            g.setColor(new Color(0, 0, 0, 150));
            g.fillRect(0, 0, BOARD_WIDTH * blockSize, BOARD_HEIGHT * blockSize);
            g.setColor(new Color(255, 100, 100));
            g.setFont(chineseFontLarge);  // 使用大号中文字体
            FontMetrics fm = g.getFontMetrics();
            String gameOverText = "游戏结束";
            int textWidth = fm.stringWidth(gameOverText);
            g.drawString(gameOverText, (BOARD_WIDTH * blockSize - textWidth) / 2, BOARD_HEIGHT * blockSize / 2 - 30);
            
            g.setFont(chineseFont);
            String scoreText = "得分: " + score;
            fm = g.getFontMetrics();
            textWidth = fm.stringWidth(scoreText);
            g.drawString(scoreText, (BOARD_WIDTH * blockSize - textWidth) / 2, BOARD_HEIGHT * blockSize / 2 + 20);
            
            // 修改"按R键重新开始"文字的绘制，确保居中
            String restartText = "按 R 键重新开始";
            fm = g.getFontMetrics(); // 重新获取当前字体的FontMetrics
            textWidth = fm.stringWidth(restartText);
            g.drawString(restartText, (BOARD_WIDTH * blockSize - textWidth) / 2, BOARD_HEIGHT * blockSize / 2 + 60);
        }
        
        // 更新按钮位置
        updateButtonPositions();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!isPaused) {
            if (canMove(currentBlock, 0, 1)) {
                currentBlock.y++;
            } else {
                mergeBlock();
            }
            repaint();
        }
    }

    private void hardDrop() {
        while (canMove(currentBlock, 0, 1)) {
            currentBlock.y++;
        }
        mergeBlock();
        repaint();
    }

    // 添加暂停/继续游戏的公共方法
    public void togglePauseGame() {
        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
        } else {
            timer.start();
        }
        repaint();  // 重绘界面以显示暂停状态
    }
    
    // 删除或修改 togglePause() 方法，统一使用 togglePauseGame()
    private void togglePause() {
        togglePauseGame();  // 调用公共方法，保持一致性
    }
    
    // 获取游戏运行状态
    
    // 修改游戏运行状态判断逻辑
    public boolean isGameRunning() {
        return timer.isRunning();  // 只需检查计时器是否在运行
    }
    
    // 添加游戏暂停状态判断方法
    public boolean isGamePaused() {
        return isPaused;
    }
    
    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            if (key == KeyEvent.VK_R && isGameOver) {
                startGame();
                return;
            }
            
            if (key == KeyEvent.VK_P) {
                togglePauseGame();
                return;
            }
            
            if (currentBlock == null || isPaused || isGameOver) return;

            switch (key) {
                case KeyEvent.VK_LEFT:
                    if (canMove(currentBlock, -1, 0)) currentBlock.x--;
                    break;
                case KeyEvent.VK_RIGHT:
                    if (canMove(currentBlock, 1, 0)) currentBlock.x++;
                    break;
                case KeyEvent.VK_DOWN:
                    if (canMove(currentBlock, 0, 1)) currentBlock.y++;
                    break;
                case KeyEvent.VK_UP:
                    Block rotated = currentBlock.rotate();
                    if (canMove(rotated, 0, 0)) currentBlock = rotated;
                    break;
                case KeyEvent.VK_SPACE:
                    hardDrop();
                    break;
            }
            repaint();
        }
    }
}
