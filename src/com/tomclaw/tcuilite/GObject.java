package com.tomclaw.tcuilite;

import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public abstract class GObject {
  
  public int x, y, width, height;

  public void setLocation( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  public void setSize( int width, int height ) {
    this.width = width;
    this.height = height;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public void setTouchOrientation( boolean touchOrientation ) {
  }

  public abstract void repaint( Graphics g );

  public abstract void repaint( Graphics g, int paintX, int paintY );

  public abstract void keyPressed( int keyCode );

  public abstract void keyReleased( int keyCode );

  public abstract void keyRepeated( int keyCode );

  public abstract void pointerPressed( int x, int y );

  public abstract void pointerReleased( int x, int y );

  public abstract boolean pointerDragged( int x, int y );
}
