package picasso;


import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PictureReader {

    public static Picture readPictureFromTga(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            throw new IllegalArgumentException("File at " + path + " does not exist. " + f.getAbsolutePath());
            
        }

        FileInputStream in = new FileInputStream(f);

        int[] header = new int[18];
        for (int i = 0; i < header.length; i++) {
            header[i] = in.read();
        }
        if (header[2] != 2 || header[16] != 24 || header[8] != 0
                || header[9] != 0 || header[10] != 0 || header[11] != 0) {
            throw new IllegalArgumentException("Unsupported TGA-format. Use uncompressed RGB's.");
        }

        int width = header[12] | (header[13] << 8);
        int height = header[14] | (header[15] << 8);

        int[] data = new int[width * height * 3];
        for (int i = 0; i < data.length; i += 3) {
            data[i + 2] = in.read();
            data[i + 1] = in.read();
            data[i] = in.read();
        }

        return new Picture(data, width, height);
    }

    public static Picture readPictureFromTriangles(String path) throws Exception {
        List<Triangle> triangles = new ArrayList<Triangle>();
        File file = new File(path);
        // File file = new File("/home/arto/Popup-course/triangulation/testi.tr");
        Scanner reader = new Scanner(file);

        String size = reader.nextLine();
        String[] dimension = size.split("\\s+");

        int width = Integer.parseInt(dimension[0]);
        int height = Integer.parseInt(dimension[1]);

        int numOfTriangles = Integer.parseInt(reader.nextLine());

        while (reader.hasNextLine()) {
            String line = reader.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+");
            int[] rgba = new int[]{Integer.parseInt(parts[6]), Integer.parseInt(parts[7]), Integer.parseInt(parts[8]), Integer.parseInt(parts[9])};

            Triangle t = new Triangle(rgba,
                    new Point(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])),
                    new Point(Integer.parseInt(parts[2]), Integer.parseInt(parts[3])),
                    new Point(Integer.parseInt(parts[4]), Integer.parseInt(parts[5])));

            triangles.add(t);
        }

        assert triangles.size() == numOfTriangles;
        Picture p = new Picture(width, height);
        for (Triangle t : triangles) {
            p.addTriangle(t);
        }

        return p;
    }
}
