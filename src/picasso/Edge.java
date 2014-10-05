package picasso;



public class Edge implements Comparable<Edge> {

    Point p1;
    Point p2;

    public Edge(Point p1, Point p2) {
        if (p1.y > p2.y) {
            this.p1 = p2;
            this.p2 = p1;
        } else {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    public int getHeight() {
        return p2.y - p1.y + 1;
    }

    @Override
    public int compareTo(Edge t) {
        return new Integer(getHeight()).compareTo(t.getHeight());
    }

    @Override
    public String toString() {
        return "(" + p1.toString() + " - " + p2.toString() + ")";
    }
}
