package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class StatSmile implements CommSmile {

  public static Image image;
  public String[] smileDefinitions;
  public int x, y, width, height;
  
  public StatSmile(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public int[] getFramesDelay() {
    return null;
  }

  public String[] getSmileDefinitions() {
    return smileDefinitions;
  }

  public void paint( Graphics g, int x, int y, int frameIndex ) {
    g.drawRegion( image, this.x, this.y, this.width, this.height, 
            Sprite.TRANS_NONE, x, y, Graphics.TOP | Graphics.LEFT );
  }
}
