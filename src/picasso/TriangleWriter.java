package picasso;


import java.io.FileWriter;
import java.util.List;

public class TriangleWriter {

    public static void writeTriangles(String filenamePrefix, int width, int height, List<Triangle> triangles) throws Exception {
        FileWriter fw = new FileWriter(filenamePrefix + ".tr");
        fw.write(width + " " + height + "\n");
        fw.write(triangles.size() + "\n");
        for (Triangle t : triangles) {
            fw.write(t.points[0] + " " + t.points[1] + " " + t.points[2] + " " + t.rgba[0] + " " + t.rgba[1] + " " + t.rgba[2] + " " + t.rgba[3] + "\n");
        }

        fw.flush();
        fw.close();
    }
}
