package com.tomclaw.tcuilite;

import com.tomclaw.utils.StringUtil;
import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Radio extends PaneObject {

  public String caption = "";
  private String[] strings = new String[ 0 ];
  static Image unselected;
  static Image __selected;
  public boolean radioState = false;
  public boolean cancelledState = false;
  public int radioIndex = -1;
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public RadioGroup radioGroup = null;
  public boolean wasPressedAction = false;
  /**
   * Runtime
   */
  public Graphics g = null;
  /**
   * Colors
   */
  public static int foreColor = 0x555555;
  public static int backColor = 0xFFFFFF;
  public static int borderColor = 0xB08BF0;
  public static int focusedBackColor = 0xD3D1FF;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  /**
   * Sizes
   */
  public int interlineheight = 2;

  public Radio( String caption, boolean state ) {
    this.caption = caption;
    this.radioState = state;
    try {
      unselected = Image.createImage( "/res/radio00_img.png" );
      __selected = Image.createImage( "/res/radio01_img.png" );
    } catch ( IOException ex ) {
    }
  }

  public void repaint( Graphics g ) {
    if ( isFocusable && isFocused ) {
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    }
    g.setFont( Theme.font );
    g.setColor( foreColor );
    g.drawImage( radioState ? __selected : unselected, x + Theme.upSize, y + height / 2, Graphics.LEFT | Graphics.VCENTER );
    for ( int c = 0; c < strings.length; c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize + ( unselected.getWidth() + Theme.upSize ), y + 2 + Theme.upSize + c * ( Theme.font.getHeight() + interlineheight ), Graphics.TOP | Graphics.LEFT );
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
    wasPressedAction = true;
  }

  public void keyReleased( int keyCode ) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE && wasPressedAction ) {
      radioGroup.setCombed( this );
      actionPerformed();
    }
    wasPressedAction = false;
  }

  public void keyRepeated( int keyCode ) {
  }

  public void pointerPressed( int x, int y ) {
    cancelledState = false;
  }

  public void pointerReleased( int x, int y ) {
    if ( !cancelledState ) {
      radioGroup.setCombed( this );
      actionPerformed();
    }
  }

  public void pointerDragged( int x, int y ) {
    cancelledState = true;
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
    height = Theme.font.getHeight() + Theme.upSize * 2 + 4 + ( strings.length - 1 ) * ( Theme.font.getHeight() + interlineheight );
    return height;
  }

  public void setTouchOrientation( boolean touchOrientation ) {
  }
  
  public final void setCaption( String text ) {
    this.caption = text;
    updateCaption();
  }

  public void updateCaption() {
    strings = StringUtil.wrapText( caption, width - ( Theme.upSize + 4 ) * 2 - ( unselected.getWidth() + Theme.upSize ), Theme.font );
  }

  public void setRadioGroup( RadioGroup radioGroup ) {
    this.radioGroup = radioGroup;
  }

  public String getStringValue() {
    return radioState ? "true" : "false";
  }

  public void actionPerformed() {
  }
}
