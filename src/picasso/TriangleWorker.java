/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picasso;

import java.util.Arrays;
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
                System.out.println("Worker: " + workerNumber + " starting point " + state.p1 + " of triangle " + state.triangleNumber);
                CalcState returnState = work(state);
                Main.doneQueue.put(returnState);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(TriangleWorker.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private CalcState work(CalcState state) {

        CalcState bestState = state;

        for (int point2 = state.point1 + 1; point2 < conf.numPoints; point2++) {
            Point p2 = new Point(Main.xFromPoint(point2), Main.yFromPoint(point2));
//            System.out.println("triangle: " + state.triangleNumber + " p1: " + state.p1 + " p2: " + p2);
//            System.out.println((1.0 * state.point1 / conf.numPoints + point2 / conf.numPoints / conf.numPoints) * 100 + "%");
            for (int point3 = point2 + 1; point3 < conf.numPoints; point3++) {
                Point p3 = new Point(Main.xFromPoint(point3), Main.yFromPoint(point3));

                //System.out.println(bestTriangle);
//                        if (bestTriangle != null) {
//                            System.out.println(Arrays.toString(bestTriangle.rgba));
//                        }
                //System.out.println(bestDist);
                CalcState tempState = testPoints(state, p2, p3);

                if (tempState.bestDist < bestState.bestDist) {
                    bestState = tempState;
//                    System.out.println("triangle " + bestState.triangleNumber + "\t" + bestState.bestTriangle + "\t" + Arrays.toString(bestState.bestTriangle.rgba) + "\t" + bestState.bestDist);

                }

            }
        }
        if (bestState.bestDist < state.bestDist) {
            return bestState;
        } else {
            return new CalcState(null, state.triangleNumber, state.point1, state.p1, Double.POSITIVE_INFINITY, null);
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
                        tmpPic.addTriangle(t);
                        double distance = tmpPic.distance(conf.pic);
                        if (distance < bestDist) {
                            bestTriangle = new Triangle(rgba.clone(), state.p1, p2, p3);
                            bestDist = distance;
                            //System.out.println(bestTriangle + " " + Arrays.toString(t.rgba));
                            //System.out.println(bestDist);
                        }
                    }
                }
            }
        }
        return new CalcState(state.p, state.triangleNumber, state.point1, state.p1, bestDist, bestTriangle);
    }

    private int expandColor(int color) {
        int ret = color * (256 / conf.colorSteps);
        if (ret == 256) {
            return 255;
        }
        return ret;
    }
}
