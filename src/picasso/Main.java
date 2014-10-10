package picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static int xSteps;
    static int ySteps;
    static int colorSteps;
    static int alphaSteps;

    static int xPoints;
    static int yPoints;
    static Picture pic;
    //static Picture p;

    static long trianglesTested = 0;
    //static List<Triangle> triangles;

    public static void main(String[] args) throws Exception {

        pic = PictureReader.readPictureFromTga(args[0]);
        int maxTriangles = Integer.parseInt(args[1]);
        // unfortunately tga cannot be stored as triangles

        // you can also compare the difference between the images by the dist
        // method -- given that they are the same size
        // which is useful when wondering about the distance between your current
        // image, and the next possible choice
        // sampling a color from a picture might also be interesting.
        // you get rgba 
        xSteps = 8;
        ySteps = 8;
        colorSteps = 4;
        alphaSteps = 4;
        xPoints = xSteps + 1;
        yPoints = ySteps + 1;

        int numPoints = xPoints * yPoints;

        // let's try to build our own pic from an existing one!!
        ArrayList<Triangle> triangles = new ArrayList<>();

        Picture p = new Picture(pic.getWidth(), pic.getHeight());
        double bestDist = p.distance(pic);

//        Rasterizer.writeTga(p, 1);
//        System.out.println(p.distance(pic));
//        Point p1 = new Point(0, 0);
//        Point p2 = new Point(0, 299);
//        Point p3 = new Point(299, 0);
//        int[] red = {255, 0, 0, 255};
//        Triangle t = new Triangle(red, p1, p2, p3);
//        triangles.add(t);
//        p.addTriangle(t);
//        System.out.println(p.distance(pic));
//        Rasterizer.writeTga(p, 2);
//        int[] green = {0, 255, 0, 255};
//        p1 = new Point(0, 299);
//        p2 = new Point(299, 0);
//        p3 = new Point(299, 299);
//        t = new Triangle(green, p1, p2, p3);
//        triangles.add(t);
//        p.addTriangle(t);
//        Rasterizer.writeTga(p, 3);
//        System.out.println(p.distance(pic));
//        TriangleWriter.writeTriangles("le_triangles", p.getWidth(), p.getHeight(), triangles);
        for (int i = 0; i < maxTriangles; i++) {
            Triangle bestTriangle = null;

            for (int point1 = 0; point1 < numPoints; point1++) {
                Point p1 = new Point(xFromPoint(point1), yFromPoint(point1));
                System.out.println("p1: " + p1);
                for (int point2 = point1 + 1; point2 < numPoints; point2++) {
                    Point p2 = new Point(xFromPoint(point2), yFromPoint(point2));
                    System.out.println("triangle: " + i + " p1: " + p1 + " p2: " + p2);
                    System.out.println((1.0*point1/numPoints + point2/numPoints/numPoints)*100+ "%");
                    for (int point3 = point2 + 1; point3 < numPoints; point3++) {
                        Point p3 = new Point(xFromPoint(point3), yFromPoint(point3));

                        //System.out.println(bestTriangle);
//                        if (bestTriangle != null) {
//                            System.out.println(Arrays.toString(bestTriangle.rgba));
//                        }
                        //System.out.println(bestDist);
                        Triangle t = testPoints(p, p1, p2, p3, bestDist);

                        if (t != null) {
                            Picture tmpPic = new Picture(p.getData(), p.getWidth(), p.getHeight());
                            bestTriangle = t;
                            tmpPic.addTriangle(bestTriangle);
                            bestDist = tmpPic.distance(pic);
                            System.out.println("triangle " + i + "\t" + t +"\t"+ Arrays.toString(bestTriangle.rgba) +"\t"+ bestDist);

                        }

                    }
                }
            }
            System.out.println(bestTriangle + Arrays.toString(bestTriangle.rgba));
            System.out.println(triangles.size() + " " + bestDist);
            System.out.println("Tested: " + trianglesTested);

            p.addTriangle(bestTriangle);
            triangles.add(bestTriangle);
            Rasterizer.writeTga(p, triangles.size());
            TriangleWriter.writeTriangles("le_triangles", p.getWidth(), p.getHeight(), triangles);
        }
    }

    private static int xFromPoint(int point) {
        int x = (point % yPoints) * pic.getWidth() / xSteps;
        if (x == pic.getWidth()) {
            x = x - 1;
        }
        return x;
    }

    private static int yFromPoint(int point) {
        int y = (point / xPoints) * pic.getHeight() / ySteps;
        if (y == pic.getHeight()) {
            y = y - 1;
        }
        return y;
    }

    private static Triangle testPoints(Picture p, Point p1, Point p2, Point p3, double bestDist) {
        Triangle bestTriangle = null;
        int[] rgba = new int[4];
        for (int r = 0; r <= colorSteps; r++) {
            rgba[0] = expandColor(r);
            for (int g = 0; g <= colorSteps; g++) {
                rgba[1] = expandColor(g);
                for (int b = 0; b <= colorSteps; b++) {
                    rgba[2] = expandColor(b);
                    for (int a = 0; a <= alphaSteps; a++) {
                        trianglesTested++;
                        rgba[3] = expandColor(a);
                        Triangle t = new Triangle(rgba, p1, p2, p3);

                        Picture tmpPic = new Picture(p.getData(), p.getWidth(), p.getHeight());
                        tmpPic.addTriangle(t);
                        double distance = tmpPic.distance(pic);
                        if (distance < bestDist) {
                            bestTriangle = new Triangle(rgba.clone(), p1, p2, p3);
                            bestDist = distance;
                            //System.out.println(bestTriangle + " " + Arrays.toString(t.rgba));
                            //System.out.println(bestDist);
                        }
                    }
                }
            }
        }
        return bestTriangle;
    }
//
//    public void turha() {
//        Picture tmpPic = new Picture(p.getData(), p.getWidth(), p.getHeight());
//        tmpPic.addTriangle(t);
//
//        if (bestDist > tmpPic.distance(pic)) {
//            triangles.add(t);
//            p = tmpPic;
//            bestDist = tmpPic.distance(pic);
//            System.out.println("New dist: " + bestDist);
//            Rasterizer.writeTga(p, triangles.size());
//            TriangleWriter.writeTriangles("le_triangles", p.getWidth(), p.getHeight(), triangles);
//        }
//    }

    private static int expandColor(int color) {
        int ret = color * (256 / colorSteps);
        if (ret == 256) {
            return 255;
        }
        return ret;
    }
}
