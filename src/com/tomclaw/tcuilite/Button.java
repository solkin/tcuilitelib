package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Button extends PaneObject {

  public String caption = "";
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public boolean isPressed = false;
  /**
   * Runtime
   */
  public Graphics g = null;
  /**
   * Colors
   */
  public static int unactOnlTopBorder = 0xBDBABD;
  public static int unactBotBorder = 0xADAAAD;
  public static int unactInTopBorder = 0xE7E3E7;
  public static int unactInOnlBotBorder = 0xD6D3D6;
  public static int unactGradFrom = 0xD6D3D6;
  public static int unactGradTo = 0xADAEAD;
  public static int foreColor = 0x424142;
  public static int foreShadowColor = 0xDEDFDE;
  public static int actOnlTopBorder = 0xB5B2E7;
  public static int actBotBorder = 0xA59ECE;
  public static int actInOnlBotBorder = 0xC6BEF7;
  public static int actGradFrom = 0xDEDBF7;
  public static int actGradMidd = 0xB5AAEF;
  public static int actGradAftr = 0xA59EEF;
  public static int actGradFinl = 0xADA6EF;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  public static int prsdGradFrom = 0xB5AAEF;
  public static int prsdGradFinl = 0xDEDBF7;

  public Button( String caption ) {
    this.caption = caption;
  }

  public void repaint( Graphics g ) {
    if ( isFocused ) {
      g.setColor( actBotBorder );
      g.drawRect( x + 2, y + 2, width - 4, height - 4 );
      g.setColor( actOnlTopBorder );
      g.drawLine( x + 2, y + 2, x + width - 2, y + 2 );
      if ( isPressed ) {
        DrawUtil.fillVerticalGradient( g, x + 2, y + 2, width - 4, height - 4, prsdGradFrom, prsdGradFinl );
      } else {
        g.setColor( actInOnlBotBorder );
        g.drawLine( x + 3, y + height - 3, x + width - 3, y + height - 3 );
        DrawUtil.fillSharpVerticalGradient( g, x + 3, y + 3, width - 6, height - 6, actGradFrom, actGradMidd, actGradAftr, actGradFinl, 70 );
      }
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    } else {
      g.setColor( unactBotBorder );
      g.drawRect( x + 2, y + 2, width - 4, height - 4 );
      g.setColor( unactOnlTopBorder );
      g.drawLine( x + 2, y + 2, x + width - 2, y + 2 );
      g.setColor( unactInTopBorder );
      g.drawRect( x + 3, y + 3, width - 6, height - 6 );
      g.setColor( unactInOnlBotBorder );
      g.drawLine( x + 3, y + height - 3, x + width - 3, y + height - 3 );
      DrawUtil.fillVerticalGradient( g, x + 4, y + 4, width - 8, height - 8 + 1, unactGradFrom, unactGradTo );
    }
    g.setFont( Theme.font );
    g.setColor( foreShadowColor );
    g.drawString( caption, x + ( isPressed ? 1 : 0 ) + ( width - Theme.font.stringWidth( caption ) ) / 2 + 1, y + height / 2 - Theme.font.getHeight() / 2 + 1, Graphics.TOP | Graphics.LEFT );
    g.setColor( foreColor );
    g.drawString( caption, x + ( isPressed ? 1 : 0 ) + ( width - Theme.font.stringWidth( caption ) ) / 2, y + height / 2 - Theme.font.getHeight() / 2, Graphics.TOP | Graphics.LEFT );
  }

  public void setLocation( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  public void setSize( int width, int height ) {
    this.width = width;
    this.height = height;
  }

  public void keyPressed( int keyCode ) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE ) {
      isPressed = true;
    }
  }

  public void keyReleased( int keyCode ) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE && isPressed ) {
      actionPerformed();
      isPressed = false;
    }
  }

  public void keyRepeated( int keyCode ) {
  }

  public void pointerPressed( int x, int y ) {
    isPressed = true;
  }

  public void pointerReleased( int x, int y ) {
    if ( isPressed ) {
      actionPerformed();
    }
    isPressed = false;
  }

  public void pointerDragged( int x, int y ) {
    isPressed = false;
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
    height = Theme.font.getHeight() + Theme.upSize * 2 + 4;
    return height;
  }

  public void setTouchOrientation( boolean touchOrientation ) {
  }

  public void stopThreads() {
  }

  public String getStringValue() {
    return caption;
  }

  public void actionPerformed() {
  }
}
