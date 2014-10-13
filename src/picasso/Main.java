package picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {

    static BlockingQueue<CalcState> workQueue;
    static BlockingQueue<CalcState> doneQueue;
    static Configuration conf;

    public static void main(String[] args) throws Exception {
        conf = Configuration.fromFile(args[0]);

        workQueue = new LinkedBlockingQueue<>();
        doneQueue = new LinkedBlockingQueue<>();

        int threads = Runtime.getRuntime().availableProcessors();

//        threads=1;
        List<TriangleWorker> workers = new ArrayList<>(threads);
        for (int i = 0; i < threads; i++) {
            workers.add(new TriangleWorker(i, conf));
        }
        for (TriangleWorker tw : workers) {
            tw.start();
        }

        ArrayList<Triangle> triangles = new ArrayList<>();

        Picture p = new Picture(conf.pic.getWidth(), conf.pic.getHeight());
        double pDist = p.distance(conf.pic);
        double bestDist = pDist;

        for (int i = 0; i < conf.maxTriangles; i++) {
            doneQueue = new LinkedBlockingQueue<>();

            int workUnits = 0;

            for (int point1 = 0; point1 < conf.numPoints; point1++) {
                Point p1 = new Point(xFromPoint(point1), yFromPoint(point1));

                CalcState state = new CalcState(p, pDist, i, point1, p1, bestDist, null);
                workQueue.add(state);
                workUnits++;
            }

            while (doneQueue.size() < workUnits) {
                if (!doneQueue.isEmpty()) {
                    CalcState bestState = Collections.min(doneQueue);

                    System.out.println(now() + bestState.bestTriangle + " " + Arrays.toString(bestState.bestTriangle.rgba) + " " + bestState.bestDist);
                }
                System.out.println(now() + "Triangle: " + i + " done: " + doneQueue.size() + "/" + workUnits
                        + String.format(" (%.4f%%) (%.4f%%) cache size: %d",
                                (1.0 * doneQueue.size() / workUnits) * 100,
                                (1.0 * doneQueue.size() / workUnits) / conf.maxTriangles * 100,
                                p.getCacheSize()));

                Thread.sleep(60000);
            }

            System.out.println(now() + "Triangle: " + i + " done: " + doneQueue.size() + "/" + workUnits
                    + String.format(" (%.4f%%) (%.4f%%) cache size: %d",
                            (1.0 * doneQueue.size() / workUnits) * 100,
                            (1.0 * doneQueue.size() / workUnits) / conf.maxTriangles * 100,
                            p.getCacheSize()));

            CalcState bestState = Collections.min(doneQueue);
            Triangle bestTriangle = bestState.bestTriangle;

            p.addTriangle(bestTriangle);
            triangles.add(bestTriangle);

            pDist = p.distance(conf.pic);
            bestDist = pDist;

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
