package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class StatSmile implements CommSmile {

    public Image image;
    public String[] smileDefinitions;

    public Image getFrameARGB(int frameIndex) {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int[] getFramesDelay() {
        return null;
    }

    public String[] getSmileDefinitions(){
        return smileDefinitions;
    }
}
