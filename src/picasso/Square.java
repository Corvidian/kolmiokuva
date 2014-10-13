package picasso;

public class Square {
    
    int xmin, xmax, ymin, ymax;
    
    public Square(Point min, Point max) {
        this.xmin = min.x;
        this.ymin = min.y;
        this.xmax = max.x;
        this.ymax = max.y;
    }

    public Square(int xmin, int xmax, int ymin, int ymax) {
        this.xmin = xmin;
        this.xmax = xmax;
        this.ymin = ymin;
        this.ymax = ymax;
    }

}
