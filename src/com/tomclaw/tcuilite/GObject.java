package com.tomclaw.tcuilite;

import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public interface GObject {

  public void repaint( Graphics g );

  public void repaint( Graphics g, int paintX, int paintY );

  public void setLocation( int x, int y );

  public void setSize( int width, int height );

  public void keyPressed( int keyCode );

  public void keyReleased( int keyCode );

  public void keyRepeated( int keyCode );

  public void pointerPressed( int x, int y );

  public void pointerReleased( int x, int y );

  public boolean pointerDragged( int x, int y );

  public int getX();

  public int getY();

  public int getWidth();

  public int getHeight();

  public void setTouchOrientation( boolean touchOrientation );
}
