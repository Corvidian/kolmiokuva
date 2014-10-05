package picasso;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Rasterizer {

    public static void writeTga(Picture p, Integer count) throws Exception {
        if (count != null) {
            writeTga(p, "image_" + count);
        } else {
            writeTga(p, "image");
        }
    }

    public static void writeTga(Picture p, String filenameprefix) throws Exception {
        File f = new File(filenameprefix + ".tga");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
        bos.write(createTgaData(p));
        bos.flush();
        bos.close();
    }

    private static byte[] createTgaData(Picture p) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte header[] = {0, 0,
            2, //image format == uncompressed RGB
            0, 0,
            0, 0,
            0,
            0, 0, // x-coordinate of bottom left corner
            0, 0, // y-coordinate of bottom left corner
            (byte) (p.getWidth() & 0x00FF),
            (byte) ((p.getWidth() & 0xFF00) >> 8), // width
            (byte) (p.getHeight() & 0x00FF),
            (byte) ((p.getHeight() & 0xFF00) >> 8), // height
            24, //bits per pixel
            0};

        baos.write(header);
        int[] data = p.getData();

        for (int i = 0; i < data.length; i += 3) {
            baos.write(data[i + 2]);
            baos.write(data[i + 1]);
            baos.write(data[i]);
        }

        return baos.toByteArray();
    }
}
