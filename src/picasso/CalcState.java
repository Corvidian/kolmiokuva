package picasso;

/**
 *
 * @author etjheino
 */
public class CalcState implements Comparable<CalcState> {
    final Picture p;
    final int triangleNumber;
    final int point1;
    final Point p1;
    
    final double bestDist;
    final Triangle bestTriangle;

    public CalcState(Picture p, int triangleNumber, int point1, Point p1, double bestDist, Triangle bestTriangle) {
        this.p = p;
        this.point1 = point1;
        this.bestDist = bestDist;
        this.triangleNumber = triangleNumber;
        this.p1 = p1;
        this.bestTriangle = bestTriangle;
    }    

    @Override
    public int compareTo(CalcState that) {
        return Double.compare(this.bestDist, that.bestDist);
    }
}
