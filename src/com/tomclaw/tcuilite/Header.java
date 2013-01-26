package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Header {

  public String name = null;
  /**
   * Colors
   */
  public static int headerGradFrom = 0xDEDFDE;
  public static int headerGradTo = 0xB5B2B5;
  public static int headerLine1 = 0xCECFCE;
  public static int headerLine2 = 0x9C9A9C;
  public static int fontColor = 0x000000;
  public static int fontShadow = 0xE7E7E7;
  /**
   * Data
   */
  public int x = 0, y = 0, width = 0, height = 0;
  public String title = null;

  public Header( String title ) {
    this.title = title;
  }

  public void paint( Graphics g ) {
    paint( g, 0, 0 );
  }

  public void paint( Graphics g, int paintX, int paintY ) {
    g.setFont( Theme.titleFont );
    getHeight();
    DrawUtil.fillVerticalGradient( g, paintX + x, paintY + y, width, height - 2, headerGradFrom, headerGradTo );
    g.setColor( headerLine1 );
    g.drawLine( paintX + x, paintY + y + height - 2, paintX + x + width, paintY + y + height - 2 );
    g.setColor( headerLine2 );
    g.drawLine( paintX + x, paintY + y + height - 1, paintX + x + width, paintY + y + height - 1 );
    g.setColor( fontShadow );
    g.drawString( title, paintX + x + Theme.upSize + 1, paintY + y + Theme.upSize + 1, Graphics.TOP | Graphics.LEFT );
    g.setColor( fontColor );
    g.drawString( title, paintX + x + Theme.upSize, paintY + y + Theme.upSize, Graphics.TOP | Graphics.LEFT );
  }

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
    height = Theme.titleFont.getHeight() + Theme.upSize * 2 + 2;
    return height;
  }
}
