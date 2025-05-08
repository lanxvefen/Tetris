public class Block {
    int x, y;
    int shape;
    int[][] coords;

    private static final int[][][] SHAPES = {
        {{0, 0}, {1, 0}, {0, 1}, {1, 1}}, // 正方形
        {{0, 0}, {1, 0}, {2, 0}, {3, 0}}, // 直线
        {{0, 0}, {1, 0}, {0, 1}, {0, 2}}, // L 形
        {{0, 0}, {1, 0}, {1, 1}, {2, 1}}, // Z 形
        {{0, 1}, {1, 1}, {1, 0}, {2, 0}}, // S 形
        {{0, 0}, {1, 0}, {2, 0}, {1, 1}}, // T 形
        {{0, 1}, {1, 1}, {2, 1}, {2, 0}}  // 反 L 形
    };

    public Block(int shape) {
        this.shape = shape;
        this.coords = new int[4][2];
        System.arraycopy(SHAPES[shape], 0, this.coords, 0, 4);
    }

    public Block rotate() {
        Block rotated = new Block(shape);
        for (int i = 0; i < 4; i++) {
            int x = rotated.coords[i][1];
            int y = -rotated.coords[i][0];
            rotated.coords[i][0] = x;
            rotated.coords[i][1] = y;
        }
        return rotated;
    }
}