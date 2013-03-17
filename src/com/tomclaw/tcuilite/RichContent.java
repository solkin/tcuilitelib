package com.tomclaw.tcuilite;

import com.tomclaw.utils.bb.BBResult;
import com.tomclaw.utils.bb.BBUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RichContent implements Content {

  public String text = "";
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  /** Private fields **/
  public BBResult bbResult;
  private int color;
  /** Sizes **/
  public int interlineheight = 2;
  
  public RichContent(String text) {
    setText(text);
  }

  public final void setText( String text ) {
    this.text = text;
    update();
  }
  
  public void setColor(int color) {
    this.color = color;
    update();
  }

  public String getText() {
    return text;
  }

  public void update() {
    if ( width != 0 ) {
      try {
        bbResult = BBUtil.processText( text, 0, 0,
                width - ( Theme.upSize + 4 ) * 2, color );
        height = 2 * 2 + bbResult.height;
      } catch ( Throwable ex1 ) {
      }
    }
  }

  public void setWidth(int width) {
    if ( this.width != width ) {
      this.width = width;
      update();
    } else {
      this.width = width;
    }
  }

  public void setLocation( int x, int y ) {
    if ( this.x != x || this.y != y || height == 0 ) {
      this.x = x;
      this.y = y;
    }
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

  public void paint( Graphics g ) {
    for ( int c = 0; c < bbResult.bbStyleString.length; c++ ) {
      bbResult.bbStyleString[c].paint( g, x + 2 + Theme.upSize, y
              + 2 + Theme.upSize );
    }
  }
}
