/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picasso;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author etjheino
 */
public class Configuration {

    final int xSteps;
    final int ySteps;
    final int colorSteps;
    final int alphaSteps;

    final Picture pic;
    final String filenamePrefix;

    final int xPoints;
    final int yPoints;
    final int numPoints;
    
    final int maxTriangles;

    public Configuration(String picname, int xSteps, int ySteps, int colorSteps, int alphaSteps, int maxTriangles) throws Exception {
        this.xSteps = xSteps;
        this.ySteps = ySteps;
        this.colorSteps = colorSteps;
        this.alphaSteps = alphaSteps;
        
        this.pic = PictureReader.readPictureFromTga(picname);
        this.filenamePrefix = picname.replaceFirst("[.][^.]+$", "");;

        this.xPoints = this.xSteps + 1;
        this.yPoints = this.ySteps + 1;

        this.numPoints = this.xPoints * this.yPoints;
        
        this.maxTriangles = maxTriangles;
    }

    public static Configuration fromFile(String filename) {
        Configuration c = null;
        InputStream i = null;
        try {
            Properties prop = new Properties();
            i = new FileInputStream(filename);
            prop.load(i);

            String picname = prop.getProperty("picname");

            int xSteps = Integer.parseInt(prop.getProperty("xsteps"));
            int ySteps = Integer.parseInt(prop.getProperty("ysteps"));
            int colorSteps = Integer.parseInt(prop.getProperty("colorsteps"));
            int alphaSteps = Integer.parseInt(prop.getProperty("alphasteps"));
            
            int maxTriangles = Integer.parseInt(prop.getProperty("maxtriangles"));

            c = new Configuration(picname, xSteps, ySteps, colorSteps, alphaSteps, maxTriangles);
        } catch (Exception ex) {
            Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (i != null) {
                try {
                    i.close();
                } catch (IOException e) {
                    Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        }
        return c;
    }

}
