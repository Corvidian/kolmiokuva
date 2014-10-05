package picasso;


public class Point {

    public int x;
    public int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Point randomPoint(int minX, int maxX, int minY, int maxY) {
        int rangeX = maxX - minX;
        int rangeY = maxY - minY;

        return new Point(
                (int) (Math.random() * rangeX + minX),
                (int) (Math.random() * rangeY + minY));
    }

    @Override
    public String toString() {
        return x + " " + y;
    }
}
