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
    
    // 添加中文字体
    private Font chineseFont = new Font("微软雅黑", Font.BOLD, 20);
    private Font chineseFontLarge = new Font("微软雅黑", Font.BOLD, 40);

    public TetrisPanel() {
        board = new int[BOARD_HEIGHT][BOARD_WIDTH];
        timer = new Timer(dropInterval, this);
        addKeyListener(new KeyHandler());
        setFocusable(true);
    }

    public void startGame() {
        clearBoard();
        score = 0;
        dropInterval = 500;
        timer.setDelay(dropInterval);
        nextBlock = Block.randomBlock();
        newBlock();
        timer.start();
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                board[i][j] = 0;
            }
        }
    }

    private void newBlock() {
        currentBlock = nextBlock;
        nextBlock = Block.randomBlock();
        currentBlock.x = BOARD_WIDTH / 2;
        currentBlock.y = 0;
        if (!canMove(currentBlock, 0, 0)) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "游戏结束！得分: " + score);
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
        if (linesCleared > 0 && dropInterval > 100) {
            dropInterval -= 20;
            timer.setDelay(dropInterval);
        }
    }

    private int getBlockSize() {
        return Math.min(getWidth() / (BOARD_WIDTH + 5), getHeight() / BOARD_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int blockSize = getBlockSize();

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.GRAY);
        for (int i = 0; i <= BOARD_HEIGHT; i++) {
            g.drawLine(0, i * blockSize, BOARD_WIDTH * blockSize, i * blockSize);
        }
        for (int j = 0; j <= BOARD_WIDTH; j++) {
            g.drawLine(j * blockSize, 0, j * blockSize, BOARD_HEIGHT * blockSize);
        }

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                if (board[i][j] != 0) {
                    Color blockColor = Color.getHSBColor((float) (board[i][j] * 0.1), 1, 1);
                    g.setColor(blockColor);
                    g.fillRect(j * blockSize, i * blockSize, blockSize, blockSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(j * blockSize, i * blockSize, blockSize, blockSize);
                }
            }
        }

        if (currentBlock != null) {
            Color currentBlockColor = Color.getHSBColor((float) (currentBlock.shape * 0.1), 1, 1);
            for (int i = 0; i < 4; i++) {
                int x = currentBlock.x + currentBlock.coords[i][0];
                int y = currentBlock.y + currentBlock.coords[i][1];
                if (y >= 0) {
                    g.setColor(currentBlockColor);
                    g.fillRect(x * blockSize, y * blockSize, blockSize, blockSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x * blockSize, y * blockSize, blockSize, blockSize);
                }
            }
        }

        // 得分 - 使用中文字体
        g.setColor(Color.WHITE);
        g.setFont(chineseFont);
        g.drawString("得分: " + score, BOARD_WIDTH * blockSize + 20, 30);

        // 下一块 - 使用中文字体
        g.setFont(chineseFont);
        g.drawString("下一块：", BOARD_WIDTH * blockSize + 20, 70);
        if (nextBlock != null) {
            Color nextColor = Color.getHSBColor((float) (nextBlock.shape * 0.1), 1, 1);
            for (int i = 0; i < 4; i++) {
                int x = nextBlock.coords[i][0];
                int y = nextBlock.coords[i][1];
                g.setColor(nextColor);
                g.fillRect(BOARD_WIDTH * blockSize + 20 + (x + 1) * blockSize, 80 + (y + 1) * blockSize, blockSize, blockSize);
                g.setColor(Color.BLACK);
                g.drawRect(BOARD_WIDTH * blockSize + 20 + (x + 1) * blockSize, 80 + (y + 1) * blockSize, blockSize, blockSize);
            }
        }

        if (isPaused) {
            g.setColor(new Color(255, 255, 255, 150));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(chineseFontLarge);  // 使用大号中文字体
            g.drawString("暂停中", getWidth() / 2 - 60, getHeight() / 2);
        }
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

    private void togglePause() {
        isPaused = !isPaused;
        repaint();
    }

    private class KeyHandler extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (currentBlock == null || isPaused) return;

            int key = e.getKeyCode();
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
                case KeyEvent.VK_P:
                    togglePause();
                    break;
            }
            repaint();
        }
    }
}
