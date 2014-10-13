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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + this.xmin;
        hash = 47 * hash + this.xmax;
        hash = 47 * hash + this.ymin;
        hash = 47 * hash + this.ymax;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Square other = (Square) obj;
        if (this.xmin != other.xmin) {
            return false;
        }
        if (this.xmax != other.xmax) {
            return false;
        }
        if (this.ymin != other.ymin) {
            return false;
        }
        if (this.ymax != other.ymax) {
            return false;
        }
        return true;
    }

}
