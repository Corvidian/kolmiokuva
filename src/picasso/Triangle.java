package picasso;



import java.util.Arrays;

public class Triangle {

    Point[] points;
    int[] rgba;

    public Triangle(int[] rgba, Point... points) {
        this.points = points;
        this.rgba = rgba;
    }

    public int[] getRGBA() {
        return rgba;
    }

    public Point[] getPoints() {
        return points;
    }
    private Edge[] edges;

    public Edge[] getEdges() {
        if (edges == null) {
            edges = new Edge[3];
            edges[0] = new Edge(points[0], points[1]);
            edges[1] = new Edge(points[1], points[2]);
            edges[2] = new Edge(points[2], points[0]);
        }

        return edges;
    }

    @Override
    public String toString() {
        return Arrays.asList(points).toString();
    }
}
