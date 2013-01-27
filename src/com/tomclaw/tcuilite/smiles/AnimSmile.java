package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class AnimSmile implements CommSmile {

  public String fileName;
  private Image framesARGB;
  public int[] framesDelay;
  public String[] smileDefinitions;
  private int width;
  private int height;

  public int getWidth() {
    checkARGB();
    return width;
  }

  public int getHeight() {
    checkARGB();
    return height;
  }

  public void checkARGB() {
    if ( framesARGB == null ) {
      Smiles.loadSmileARGB( this );
    }
  }

  public void setWidth( int width ) {
    this.width = width;
  }

  public void setHeight( int height ) {
    this.height = height;
  }

  public void setFramesARGB( Image framesARGB ) {
    this.framesARGB = framesARGB;
  }

  public int[] getFramesDelay() {
    return framesDelay;
  }

  public String[] getSmileDefinitions() {
    return smileDefinitions;
  }

  public void paint( Graphics g, int x, int y, int frameIndex ) {
    checkARGB();
    g.drawRegion( framesARGB, frameIndex * width, 0, 
            width, height, Sprite.TRANS_NONE, x, y, 
            Graphics.TOP | Graphics.LEFT );
  }
}
