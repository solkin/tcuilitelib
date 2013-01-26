package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Label extends PaneObject {

  /** Basic **/
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  /** Private fields **/
  private Content content;
  private boolean isTitle = false;
  private boolean isHeader = false;
  /** Colors **/
  public static int foreColor = 0x555555;
  public static int backColor = 0xFFFFFF;
  public static int borderColor = 0xB08BF0;
  public static int focusedBackColor = 0xD3D1FF;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  public static int headerForeColor = 0x55555555;
  public static int headerGradFrom = 0xADAAAD;
  public static int headerGradTo = 0xE7E3E7;
  public static int headerHr = 0xD6D3D6;

  public Label( String caption ) {
    this( new PlainContent( caption ) );
  }

  public Label( Content content ) {
    this.content = content;
    setFocusable( false );
  }

  public void repaint( Graphics g ) {
    if ( isTitle ) {
      g.setColor( borderColor );
      g.drawRect( x + 2, y + 2, width - 4, height - 4 );
      g.setColor( focusedBackColor );
      g.fillRect( x + 3, y + 3, width - 5, height - 5 );
    } else if ( isHeader ) {
      DrawUtil.fillVerticalGradient( g, x, y, width, height, headerGradFrom,
              headerGradTo );
      g.setColor( headerHr );
      g.drawLine( x, y + height - 1, x + width, y + height - 1 );
    }
    if ( getFocusable() && getFocused() ) {
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    }
    g.setColor( isHeader ? headerForeColor : foreColor );
    content.paint( g );
  }

  public void setLocation( int x, int y ) {
    this.x = x;
    this.y = y;
    content.setLocation( x, y );
  }

  public void setSize( int width, int height ) {
    if ( this.width != width ) {
      this.width = width;
      content.setWidth( width );
      updateCaption();
    }
    this.height = getHeight();
  }

  public void keyPressed( int keyCode ) {
  }

  public void keyReleased( int keyCode ) {
  }

  public void keyRepeated( int keyCode ) {
  }

  public void pointerPressed( int x, int y ) {
  }

  public void pointerReleased( int x, int y ) {
  }

  public void pointerDragged( int x, int y ) {
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
    return content.getHeight();
  }

  public void setTouchOrientation( boolean touchOrientation ) {
  }

  public final void setCaption( String text ) {
    content.setText( text );
  }

  public void updateCaption() {
    content.update();
  }

  public void setTitle( boolean isTitle ) {
    this.isTitle = isTitle;
    updateCaption();
  }

  public void setHeader( boolean isHeader ) {
    this.isHeader = isHeader;
    updateCaption();
  }

  public String getStringValue() {
    return content.getText();
  }

  public void setContent( Content content ) {
    this.content = content;
  }

  public Content getContent() {
    return content;
  }

  public void actionPerformed() {
  }
}
