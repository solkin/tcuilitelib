package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Label extends PaneObject {

  public String caption = "";
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public Image image = null;
  /** 
   * Private fields 
   */
  private String[] strings = new String[ 0 ];
  private boolean isTitle = false;
  private boolean isHeader = false;
  private boolean isBold = false;
  /**
   * Runtime
   */
  /**
   * Colors
   */
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
  /**
   * Sizes
   */
  public int interlineheight = 2;

  public Label( String caption ) {
    setCaption( caption );
    setFocusable( false );
  }

  public void repaint( Graphics g ) {
    if ( isTitle ) {
      g.setColor( borderColor );
      g.drawRect( x + 2, y + 2, width - 4, height - 4 );
      g.setColor( focusedBackColor );
      g.fillRect( x + 3, y + 3, width - 5, height - 5 );
    } else if ( isHeader ) {
      DrawUtil.fillVerticalGradient( g, x, y, width, height, headerGradFrom, headerGradTo );
      g.setColor( headerHr );
      g.drawLine( x, y + height-1, x + width, y + height-1 );
    }
    if ( getFocusable() && getFocused() ) {
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    }
    g.setFont( getFont() );
    g.setColor( isHeader ? headerForeColor : foreColor );
    if ( image != null ) {
      g.drawImage( image, x + Theme.upSize, y + height / 2, Graphics.VCENTER | Graphics.LEFT );
    }
    for ( int c = 0; c < strings.length; c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize + ( image != null ? ( Theme.upSize * 2 + image.getWidth() ) : 0 ),
              y + 2 + ( image == null ? Theme.upSize : ( height / 2 - ( getFont().getHeight() + interlineheight ) * strings.length / 2 ) ) + c * ( getFont().getHeight() + interlineheight ), Graphics.TOP | Graphics.LEFT );
    }
  }

  public void setLocation( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  public void setSize( int width, int height ) {
    if ( this.width != width ) {
      this.width = width;
      updateCaption();
    } else {
      this.width = width;
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
    height = Math.max( ( image != null ? Theme.upSize * 2 + image.getHeight() : 0 ),
            getFont().getHeight() + Theme.upSize * 2 + 4 + ( strings.length - 1 ) * ( getFont().getHeight() + interlineheight ) );
    return height;
  }

  public void setTouchOrientation( boolean touchOrientation ) {
  }

  public final void setCaption( String text ) {
    caption = text;
    updateCaption();
  }

  public void updateCaption() {
    strings = StringUtil.wrapText( caption, width - ( Theme.upSize + 4 ) * 2 - ( image != null ? ( Theme.upSize * 2 + image.getWidth() ) : 0 ), getFont() );
  }
  
  private Font getFont() {
    return isBold ? Theme.titleFont : Theme.font;
  }
  
  public void setTitle(boolean isTitle) {
    this.isTitle = isTitle;
    updateCaption();
  }
  
  public void setHeader(boolean isHeader) {
    this.isHeader = isHeader;
    updateCaption();
  }
  
  public void setBold(boolean isBold) {
    this.isBold = isBold;
    updateCaption();
  }

  public String getStringValue() {
    return caption;
  }

  public void actionPerformed() {
  }
}
