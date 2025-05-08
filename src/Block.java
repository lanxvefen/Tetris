import java.util.Random;

public class Block {
    public int[][] coords;
    public int x, y;
    public int shape;

    private static final int[][][] SHAPES = {
        {{0,0}, {1,0}, {-1,0}, {0,1}},   // T
        {{0,0}, {1,0}, {-1,0}, {-1,1}},  // L
        {{0,0}, {-1,0}, {1,0}, {1,1}},   // J
        {{0,0}, {1,0}, {0,1}, {1,1}},    // O
        {{0,0}, {-1,0}, {0,1}, {1,1}},   // S
        {{0,0}, {1,0}, {0,1}, {-1,1}},   // Z
        {{0,0}, {-1,0}, {1,0}, {2,0}},   // I
    };

    public Block(int shape) {
        this.shape = shape;
        coords = new int[4][2];
        for (int i = 0; i < 4; i++) {
            coords[i][0] = SHAPES[shape][i][0];
            coords[i][1] = SHAPES[shape][i][1];
        }
    }

    public Block rotate() {
        Block rotated = new Block(shape);
        for (int i = 0; i < 4; i++) {
            rotated.coords[i][0] = -coords[i][1];
            rotated.coords[i][1] = coords[i][0];
        }
        rotated.x = x;
        rotated.y = y;
        return rotated;
    }

    public static Block randomBlock() {
        Random random = new Random();
        return new Block(random.nextInt(SHAPES.length));
    }
}
