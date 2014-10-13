package picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Picture {

    private int[] data;
    private final int width;
    private final int height;
    
    private ConcurrentHashMap<Square, Double> distCache;

    public Picture(int[] data, int width, int height) {
        this.data = Arrays.copyOf(data, data.length);
        this.width = width;
        this.height = height;
        
        flushCache();
    }

    public Picture(int width, int height) {
        this.width = width;
        this.height = height;
        init();
        
        flushCache();
    }

    public int[] getData() {
        return data;
    }

    public final int getWidth() {
        return width;
    }

    public final int getHeight() {
        return height;
    }

    private int rAt(final int x, final int y) {
        return data[3 * width * y + 3 * x];
    }

    private int gAt(final int x, final int y) {
        return data[3 * width * y + 3 * x + 1];
    }

    private int bAt(final int x, final int y) {
        return data[3 * width * y + 3 * x + 2];
    }

    private void setRAt(final int x, final int y, int newVal) {
        data[3 * width * y + 3 * x] = newVal;
    }

    private void setGAt(final int x, final int y, int newVal) {
        data[3 * width * y + 3 * x + 1] = newVal;
    }

    private void setBAt(final int x, final int y, int newVal) {
        data[3 * width * y + 3 * x + 2] = newVal;
    }

    public int[] rgbAt(final int x, final int y) {
        return new int[]{rAt(x, y), gAt(x, y), bAt(x, y)};
    }

    public void paintTriangle(Triangle triangle) {
        flushCache();
        
        Edge[] edges = triangle.getEdges();
        List<Integer> lineEndPoints = new ArrayList();
        boolean onRightSide = gatherEndPoints(edges, lineEndPoints);

        int i = 0;
        i = scanLine(edges[0], triangle.getRGBA(), lineEndPoints, onRightSide, i);
        scanLine(edges[1], triangle.getRGBA(), lineEndPoints, onRightSide, i);
    }

    private boolean gatherEndPoints(Edge[] edges, List<Integer> endPoints) {
        if (!endPoints.isEmpty()) {
            throw new IllegalArgumentException("End points should be empty");
        }

        Arrays.sort(edges);
        int height = edges[2].getHeight();

        Point remPoint = edges[0].p1;
        if (remPoint == edges[2].p1 || remPoint == edges[2].p2) {
            remPoint = edges[0].p2;
        }

        Point longestPt = new Point(edges[2].p2.x - edges[2].p1.x, edges[2].p2.y - edges[2].p1.y);
        Point remPt = new Point(remPoint.x - edges[2].p1.x, remPoint.y - edges[2].p1.y);
        boolean onRightSide = (longestPt.x * remPt.y - longestPt.y * remPt.x) < 0;

        int dx = Math.abs(edges[2].p1.x - edges[2].p2.x);
        int dy = height - 1;

        int sx = (edges[2].p1.x < edges[2].p2.x) ? 1 : -1;

        int i = 0;
        int err = dx - dy;
        int x1 = edges[2].p1.x;
        int y1 = edges[2].p1.y;
        int x2 = edges[2].p2.x;
        int y2 = edges[2].p2.y;

        // The idea is to fill vector endPoints to tell which is the farthest
        // point to color at the same y-coordinate, when handling the last two
        // edges. 
        endPoints.add(x1);
        while (true) {
            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;
            int nx = x1;
            int ny = y1;

            if (e2 > -dy) {
                err -= dy;
                nx += sx;
            }

            if (e2 < dx) {
                err += dx;
                i++;
                ny++;
            }

            if (ny != y1) {
                if (endPoints.size() == i) {
                    endPoints.add(nx);
                }
                endPoints.set(i, nx);
            } else if (nx > x1 && !onRightSide) {
                if (endPoints.size() == i) {
                    endPoints.add(nx);
                }
                endPoints.set(i, nx);
            } else if (nx < x1 && onRightSide) {
                if (endPoints.size() == i) {
                    endPoints.add(nx);
                }
                endPoints.set(i, nx);
            }

            x1 = nx;
            y1 = ny;
        }

        if (edges[1].p1.y < edges[0].p1.y) {
            Edge tmp = edges[0];
            edges[0] = edges[1];
            edges[1] = tmp;
        }

        return onRightSide;
    }

    private int scanLine(Edge edge, int[] RGBA, List<Integer> lineEndPoints, boolean onRightSide, int i) {
        if (edge.p1.y == edge.p2.y) {
            if (onRightSide && edge.p1.x < edge.p2.x) {
                Point tmp = edge.p1;
                edge.p1 = edge.p2;
                edge.p2 = tmp;
            } else if (!onRightSide && edge.p1.x > edge.p2.x) {
                Point tmp = edge.p1;
                edge.p1 = edge.p2;
                edge.p2 = tmp;
            }
        }

        int dx = Math.abs(edge.p1.x - edge.p2.x);
        int dy = edge.getHeight() - 1;
        int sx = (edge.p1.x < edge.p2.x) ? 1 : -1;
        int err = dx - dy;

        int x1 = edge.p1.x;
        int y1 = edge.p1.y;
        int x2 = edge.p2.x;
        int y2 = edge.p2.y;

        while (true) {
            if (onRightSide) {
                for (int x = x1; x >= lineEndPoints.get(i); x--) {
                    paintColor(x, y1, RGBA);
                }

                lineEndPoints.set(i, Math.max(x1 + 1, lineEndPoints.get(i)));
            } else {
                for (int x = x1; x <= lineEndPoints.get(i); x++) {
                    paintColor(x, y1, RGBA);
                }

                lineEndPoints.set(i, Math.min(x1 - 1, lineEndPoints.get(i)));
            }

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1++;
                i++;
            }
        }

        return i;
    }

    private void paintColor(final int x, final int y, final int[] rgba) {
        paintColor(x, y, rgba[0], rgba[1], rgba[2], rgba[3]);
    }

    private void paintColor(final int x, final int y, final int r, final int g, final int b, final int a) {
        if (!insidePicture(x, y)) {
            return;
        }

        double normalizedAlpha = 1.0 * a / 255.0;
        setRAt(x, y, (int) ((1.0 - normalizedAlpha) * rAt(x, y) + normalizedAlpha * r));
        setGAt(x, y, (int) ((1.0 - normalizedAlpha) * gAt(x, y) + normalizedAlpha * g));
        setBAt(x, y, (int) ((1.0 - normalizedAlpha) * bAt(x, y) + normalizedAlpha * b));
    }

    private boolean insidePicture(final int x, final int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public final double distance(Picture anotherPicture) throws IllegalArgumentException {
        if (this.width != anotherPicture.getWidth() || this.height != anotherPicture.getHeight()) {
            throw new IllegalArgumentException("Invalid image size. Sizes must match when comparing.");
        }

        double distance = 0.0;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double tmp = 0;
                double val;

                val = this.rAt(x, y) - anotherPicture.rAt(x, y);
                tmp += val * val;

                val = this.gAt(x, y) - anotherPicture.gAt(x, y);
                tmp += val * val;

                val = this.bAt(x, y) - anotherPicture.bAt(x, y);
                tmp += val * val;

                distance += Math.sqrt(tmp);
            }
        }

        return distance;
    }

    public final double distance(Picture anotherPicture, Square s) throws IllegalArgumentException {
        if (this.width != anotherPicture.getWidth() || this.height != anotherPicture.getHeight()) {
            throw new IllegalArgumentException("Invalid image size. Sizes must match when comparing.");
        }
        
        if(distCache.containsKey(s)) {
            return distCache.get(s);
        }

        double distance = 0.0;

        for (int x = s.xmin; x <= s.xmax; x++) {
            for (int y = s.ymin; y <= s.ymax; y++) {
                double tmp = 0;
                double val;

                val = this.rAt(x, y) - anotherPicture.rAt(x, y);
                tmp += val * val;

                val = this.gAt(x, y) - anotherPicture.gAt(x, y);
                tmp += val * val;

                val = this.bAt(x, y) - anotherPicture.bAt(x, y);
                tmp += val * val;

                distance += Math.sqrt(tmp);
            }
        }
        
        distCache.put(s, distance);

        return distance;
    }

    private void init() {
        data = new int[width * height * 3]; // one byte for each color
        clear();
    }

    private void clear() {
        for (int i = 0; i < data.length; i++) {
            data[i] = 255;
        }
    }

    public final void addTriangle(Triangle t) {
        paintTriangle(t);
    }

    // used for sampling the colors inside a given triangle
    private int scanLineToRGBA(Edge edge, int[] RGBA, List<Integer> lineEndPoints, boolean onRightSide, int i, int[] pixels) {
        if (edge.p1.y == edge.p2.y) {
            if (onRightSide && edge.p1.x < edge.p2.x) {
                Point tmp = edge.p1;
                edge.p1 = edge.p2;
                edge.p2 = tmp;
            } else if (!onRightSide && edge.p1.x > edge.p2.x) {
                Point tmp = edge.p1;
                edge.p1 = edge.p2;
                edge.p2 = tmp;
            }
        }

        int dx = Math.abs(edge.p1.x - edge.p2.x);
        int dy = edge.getHeight() - 1;
        int sx = (edge.p1.x < edge.p2.x) ? 1 : -1;
        int err = dx - dy;

        int x1 = edge.p1.x;
        int y1 = edge.p1.y;
        int x2 = edge.p2.x;
        int y2 = edge.p2.y;

        while (true) {
            if (onRightSide) {
                for (int x = x1; x >= lineEndPoints.get(i); x--) {
                    if (insidePicture(x, y1)) {
                        RGBA[0] += rAt(x, y1);
                        RGBA[1] += gAt(x, y1);
                        RGBA[2] += bAt(x, y1);
                        pixels[0]++;
                    }
                }

                lineEndPoints.set(i, Math.max(x1 + 1, lineEndPoints.get(i)));
            } else {
                for (int x = x1; x <= lineEndPoints.get(i); x++) {
                    if (insidePicture(x, y1)) {
                        RGBA[0] += rAt(x, y1);
                        RGBA[1] += gAt(x, y1);
                        RGBA[2] += bAt(x, y1);
                        pixels[0]++;
                    }
                }

                lineEndPoints.set(i, Math.min(x1 - 1, lineEndPoints.get(i)));
            }

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }

            if (e2 < dx) {
                err += dx;
                y1++;
                i++;
            }
        }

        return i;
    }

    public int[] getAvgColorAsRgba(Point p1, Point p2, Point p3) {
        Edge[] edges = new Edge[3];
        edges[0] = new Edge(p1, p2);
        edges[1] = new Edge(p2, p3);
        edges[2] = new Edge(p3, p1);

        List<Integer> endPoints = new ArrayList();
        boolean onRightSide = gatherEndPoints(edges, endPoints);
        int i = 0;
        int[] rgba = new int[4];
        int[] pixels = {0};
        i = scanLineToRGBA(edges[0], rgba, endPoints, onRightSide, i, pixels);
        scanLineToRGBA(edges[1], rgba, endPoints, onRightSide, i, pixels);

        if (pixels[0] == 0) {
            rgba[0] = 0;
            rgba[1] = 0;
            rgba[2] = 0;
            rgba[3] = 0;
        } else {
            for (i = 0; i < 3; i++) {
                rgba[i] = rgba[i] / pixels[0];
            }
            rgba[3] = 255;
        }

        return rgba;
    }
    
    private void flushCache() {
        this.distCache = new ConcurrentHashMap<>();
    }
    
    public int getCacheSize() {
        return distCache.size();
    }
}
