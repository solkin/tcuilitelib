package com.tomclaw.tcuilite;

import javax.microedition.lcdui.Graphics;

/**
 *
 * @author solkin
 */
public interface Content {
  
  public void setText(String text);
  public String getText();
  public void update();
  public void setWidth(int width);
  public void setLocation( int x, int y );
  public int getX();
  public int getY();
  public int getWidth();
  public int getHeight();
  public void paint(Graphics g);
}
