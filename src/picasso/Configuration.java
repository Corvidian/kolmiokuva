/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package picasso;

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

    final int xPoints;
    final int yPoints;
    final int numPoints;

    public Configuration(Picture pic, int xSteps, int ySteps, int colorSteps, int alphaSteps) {
        this.xSteps = xSteps;
        this.ySteps = ySteps;
        this.colorSteps = colorSteps;
        this.alphaSteps = alphaSteps;
        this.pic = pic;

        this.xPoints = this.xSteps + 1;
        this.yPoints = this.ySteps + 1;

        this.numPoints = this.xPoints * this.yPoints;
    }

}
