/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picasso;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author etjheino
 */
public class TriangleWorker extends Thread {

    final Configuration conf;
    final int workerNumber;

    public TriangleWorker(int workerNumber, Configuration conf) {
        this.conf = conf;
        this.workerNumber = workerNumber;
    }

    @Override
    public void run() {
        try {
            while (true) {
                CalcState state = Main.workQueue.take();
                CalcState returnState = work(state);
                Main.doneQueue.put(returnState);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TriangleWorker.class.getName()).log(Level.SEVERE, Integer.toString(workerNumber), ex);
        }
    }

    private CalcState work(CalcState state) {

        CalcState bestState = state;

        for (int point2 = state.point1 + 1; point2 < conf.numPoints; point2++) {
            Point p2 = new Point(Main.xFromPoint(point2), Main.yFromPoint(point2));

            StringBuilder s = new StringBuilder();
            s.append(Main.now());
            s.append(String.format("Worker %d ", workerNumber));
            s.append(String.format("starting point (%9s,%9s) ", state.p1, p2));
            s.append(String.format("of triangle %d", state.triangleNumber));
            System.out.println(s);

            for (int point3 = point2 + 1; point3 < conf.numPoints; point3++) {
                Point p3 = new Point(Main.xFromPoint(point3), Main.yFromPoint(point3));

                CalcState tempState = testPoints(state, p2, p3);

                if (tempState.bestDist < bestState.bestDist) {
                    bestState = tempState;
                }

            }
        }
        if (bestState.bestDist < state.bestDist) {
            return bestState;
        } else {
            return new CalcState(null, Double.POSITIVE_INFINITY, state.triangleNumber, state.point1, state.p1, Double.POSITIVE_INFINITY, null);
        }
    }

    private CalcState testPoints(CalcState state, Point p2, Point p3) {
        double bestDist = state.bestDist;
        Triangle bestTriangle = null;
        int[] rgba = new int[4];
        for (int r = 0; r <= conf.colorSteps; r++) {
            rgba[0] = expandColor(r);
            for (int g = 0; g <= conf.colorSteps; g++) {
                rgba[1] = expandColor(g);
                for (int b = 0; b <= conf.colorSteps; b++) {
                    rgba[2] = expandColor(b);
                    for (int a = 0; a <= conf.alphaSteps; a++) {
                        //trianglesTested++;
                        rgba[3] = expandColor(a);
                        Triangle t = new Triangle(rgba, state.p1, p2, p3);

                        Picture tmpPic = new Picture(state.p.getData(), state.p.getWidth(), state.p.getHeight());

                        Square dirty = dirty(state.p1, p2, p3);
                        double dirtyDistanceBefore = state.p.distance(conf.pic, dirty);

                        tmpPic.addTriangle(t);
                        double dirtyDistanceAfter = tmpPic.distance(conf.pic, dirty);

                        double distance = state.pDist - (dirtyDistanceBefore - dirtyDistanceAfter);
                        
//                        double distanceAfter = tmpPic.distance(conf.pic);

                        if (distance < bestDist) {
                            bestTriangle = new Triangle(rgba.clone(), state.p1, p2, p3);
                            bestDist = distance;
  //                          System.out.println(bestDist);
  //                          System.out.printf("%.6f %.6f %.6f\n", state.pDist - distanceAfter, dirtyDistanceBefore - dirtyDistanceAfter, Math.abs((dirtyDistanceBefore - dirtyDistanceAfter) - (state.pDist - distanceAfter)));
                        }
                    }
                }
            }
        }
        return new CalcState(state.p, state.pDist, state.triangleNumber, state.point1, state.p1, bestDist, bestTriangle);
    }

    private int expandColor(int color) {
        int ret = color * (256 / conf.colorSteps);
        if (ret == 256) {
            return 255;
        }
        return ret;
    }

    private static Square dirty(Point p1, Point p2, Point p3) {
        //ymin = p1.y
        //ymax = p3.y
        int xmax = Math.max(p1.x, Math.max(p2.x, p3.x));
        int xmin = Math.min(p1.x, Math.min(p2.x, p3.x));

        return new Square(xmin, xmax, p1.y, p3.y);
    }

}
