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
  private static Image radioOffImg, radioOnImg;
  public boolean radioState = false;
  public boolean cancelledState = false;
  public int radioIndex = -1;
  public RadioGroup radioGroup = null;
  public boolean wasPressedAction = false;
  /** Colors **/
  public static int foreColor = 0x555555;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  /** Sizes **/
  public int interlineheight = 2;

  public Radio( String caption, boolean state ) {
    this.caption = caption;
    this.radioState = state;
    loadRadioImages();
  }

  private void loadRadioImages() {
    try {
      radioOffImg = Image.createImage( Settings.RADIO_OFF_IMAGE );
      radioOnImg = Image.createImage( Settings.RADIO_ON_IMAGE );
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
    g.drawImage( radioState ? radioOnImg : radioOffImg, x + Theme.upSize,
            y + height / 2, Graphics.LEFT | Graphics.VCENTER );
    for ( int c = 0; c < strings.length; c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize
              + ( radioOffImg.getWidth() + Theme.upSize ), y + 2 + Theme.upSize
              + c * ( Theme.font.getHeight() + interlineheight ),
              Graphics.TOP | Graphics.LEFT );
    }
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

  public int getHeight() {
    height = Theme.font.getHeight() + Theme.upSize * 2 + 4 + ( strings.length
            - 1 ) * ( Theme.font.getHeight() + interlineheight );
    return height;
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

  public final void setCaption( String text ) {
    this.caption = text;
    updateCaption();
  }

  public void updateCaption() {
    strings = StringUtil.wrapText( caption, width - ( Theme.upSize + 4 ) * 2
            - ( radioOffImg.getWidth() + Theme.upSize ), Theme.font );
  }

  public void setRadioGroup( RadioGroup radioGroup ) {
    this.radioGroup = radioGroup;
  }

  public String getStringValue() {
    return radioState ? "true" : "false";
  }
}
