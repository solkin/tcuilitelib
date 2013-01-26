package com.tomclaw.tcuilite;

import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class PlainContent implements Content {
  
  public String text = "";
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  /** Private fields **/
  private String[] strings = new String[ 0 ];
  private boolean isBold = false;
  private boolean isItalic = false;
  public Image image = null;
  /** Sizes **/
  public int interlineheight = 2;
  
  public PlainContent(String text) {
    setText(text);
  }

  public final void setText( String text ) {
    this.text = text;
    update();
  }

  public String getText() {
    return text;
  }

  public void update() {
    strings = StringUtil.wrapText( text, width - ( Theme.upSize + 4 ) * 2
            - ( image != null ? ( Theme.upSize * 2 + image.getWidth() ) : 0 ),
            getFont() );
  }

  public void setWidth(int width) {
    if ( this.width != width ) {
      this.width = width;
      update();
    } else {
      this.width = width;
    }
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    height = Math.max( ( image != null ? Theme.upSize * 2
            + image.getHeight() : 0 ),
            getFont().getHeight() + Theme.upSize * 2 + 4
            + ( strings.length - 1 ) * ( getFont().getHeight()
            + interlineheight ) );
    return height;
  }

  private Font getFont() {
    return isBold ? Theme.titleFont
            : ( isItalic ? Theme.italicFont : Theme.font );
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public void setLocation( int x, int y ) {
    this.x = x;
    this.y = y;
  }
  
  public void setBold(boolean isBold) {
    this.isBold = isBold;
  }
  
  public void setItalic(boolean isItalic) {
    this.isItalic = isItalic;
  }

  public void paint( Graphics g ) {
    g.setFont( getFont() );
    if ( image != null ) {
      g.drawImage( image, x + Theme.upSize, y + height / 2,
              Graphics.VCENTER | Graphics.LEFT );
    }
    for ( int c = 0; c < strings.length; c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize
              + ( image != null ? ( Theme.upSize * 2 + image.getWidth() ) : 0 ),
              y + 2 + ( image == null ? Theme.upSize : ( height / 2
              - ( getFont().getHeight() + interlineheight )
              * strings.length / 2 ) ) + c * ( getFont().getHeight()
              + interlineheight ), Graphics.TOP | Graphics.LEFT );
    }
  }
  
}
