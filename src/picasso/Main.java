package picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    static BlockingQueue<CalcState> workQueue;
    static BlockingQueue<CalcState> doneQueue;
    static Configuration conf;

    public static void main(String[] args) throws Exception {

        Picture pic = PictureReader.readPictureFromTga(args[0]);
        int maxTriangles = Integer.parseInt(args[1]);

        int xSteps = 8;
        int ySteps = 8;
        int colorSteps = 4;
        int alphaSteps = 4;

        conf = new Configuration(pic, xSteps, ySteps, colorSteps, alphaSteps);

        workQueue = new LinkedBlockingQueue<>();
        doneQueue = new LinkedBlockingQueue<>();

        int threads = Runtime.getRuntime().availableProcessors();
        List<TriangleWorker> workers = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            workers.add(new TriangleWorker(i, conf));
        }
        for (TriangleWorker tw : workers) {
            tw.start();
        }

        ArrayList<Triangle> triangles = new ArrayList<>();

        Picture p = new Picture(pic.getWidth(), pic.getHeight());
        double bestDist = p.distance(pic);

        for (int i = 0; i < maxTriangles; i++) {

            int workUnits = 0;

            for (int point1 = 0; point1 < conf.numPoints; point1++) {
                Point p1 = new Point(xFromPoint(point1), yFromPoint(point1));

                CalcState state = new CalcState(p, i, point1, p1, bestDist, null);
                workQueue.add(state);
                workUnits++;
            }

            while (doneQueue.size() < workUnits) {
                if (!doneQueue.isEmpty()) {
                    CalcState bestState = Collections.min(doneQueue);

                    System.out.println(now() + bestState.bestTriangle + " " + bestState.bestDist);
                }
                System.out.println(now() + "Triangle: " + i + " done: " + doneQueue.size() + "/" + workUnits
                        + " (" + (1.0 * doneQueue.size() / workUnits) * 100 + "%)"
                        + " (" + (1.0 * doneQueue.size() / workUnits) / maxTriangles * 100 + "%)");

                Thread.sleep(60000);
            }

            CalcState bestState = Collections.min(doneQueue);
            bestDist = bestState.bestDist;
            Triangle bestTriangle = bestState.bestTriangle;

            System.out.println(bestTriangle + Arrays.toString(bestTriangle.rgba));
            System.out.println(triangles.size() + " " + bestDist);

            p.addTriangle(bestTriangle);
            triangles.add(bestTriangle);
            Rasterizer.writeTga(p, triangles.size());
            TriangleWriter.writeTriangles("le_triangles", p.getWidth(), p.getHeight(), triangles);
        }

        for (TriangleWorker tw : workers) {
            tw.interrupt();
        }
    }

    static int xFromPoint(int point) {
        int x = (point % conf.yPoints) * conf.pic.getWidth() / conf.xSteps;
        if (x == conf.pic.getWidth()) {
            x = x - 1;
        }
        return x;
    }

    static int yFromPoint(int point) {
        int y = (point / conf.xPoints) * conf.pic.getHeight() / conf.ySteps;
        if (y == conf.pic.getHeight()) {
            y = y - 1;
        }
        return y;
    }
    
    static String now() {
        String timeStamp = new SimpleDateFormat("[yyyyMMdd-HHmmss] ").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
