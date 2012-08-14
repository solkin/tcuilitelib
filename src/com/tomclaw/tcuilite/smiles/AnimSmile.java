package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class AnimSmile implements CommSmile {

    public String fileName;
    private Image[] framesARGB;
    public int[] framesDelay;
    public String[] smileDefinitions;
    private int width;
    private int height;

    public Image getFrameARGB(int frameIndex) {
        checkARGB();
        return framesARGB[frameIndex];
    }

    public int getWidth() {
        checkARGB();
        return width;
    }

    public int getHeight() {
        checkARGB();
        return height;
    }

    public void checkARGB() {
        if (framesARGB == null) {
            Smiles.loadSmileARGB(this);
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setFramesARGB(Image[] framesARGB) {
        this.framesARGB = framesARGB;
    }

    public void setFrameARGB(int frameIndex, Image frameARGB) {
        this.framesARGB[frameIndex] = frameARGB;
    }

    public int[] getFramesDelay() {
        return framesDelay;
    }

    public String[] getSmileDefinitions(){
        return smileDefinitions;
    }
}
