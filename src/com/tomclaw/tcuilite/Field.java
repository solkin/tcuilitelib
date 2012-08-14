package com.tomclaw.tcuilite;

import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Field extends PaneObject {

  public String caption = "";
  public String title = "";
  public int maxSize = 1024;
  public int constraints = javax.microedition.lcdui.TextField.ANY;
  private String[] strings = new String[ 0 ];
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  /**
   * Runtime
   */
  public Graphics g = null;
  private boolean cancelledState = false;
  /**
   * Colors
   */
  public static int foreColor = 0x555555;
  public static int backColor = 0xFFFFFF;
  public static int borderColor = 0xD4C5ED;
  public static int focusedBackColor = 0xF5F5FF;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  /**
   * Sizes
   */
  public int interlineheight = 2;

  public Field(String caption) {
    this.caption = caption;
  }

  public void repaint(Graphics g) {
    g.setColor( borderColor );
    g.drawRect( x + 2, y + 2, width - 4, height - 4 );
    g.setColor( focusedBackColor );
    g.fillRect( x + 3, y + 3, width - 5, height - 5 );
    if ( isFocusable && isFocused ) {
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    }
    g.setFont( Theme.font );
    g.setColor( foreColor );
    for ( int c = 0; c < strings.length; c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize, y + 2 + Theme.upSize + c * ( Theme.font.getHeight() + interlineheight ), Graphics.TOP | Graphics.LEFT );
    }
  }

  public void setLocation(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public void setSize(int width, int height) {
    if ( this.width != width ) {
      this.width = width;
      updateCaption();
    } else {
      this.width = width;
    }
    this.height = getHeight();
  }

  public void keyPressed(int keyCode) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE ) {
      showInputDialog();
    }
  }

  public void keyReleased(int keyCode) {
  }

  public void showInputDialog() {
    /**
     * Setting up
     */
    Screen.screen.textBox.setTitle( title );
    Screen.screen.textBox.setString( caption );
    Screen.screen.textBox.setMaxSize( maxSize );
    Screen.screen.textBox.setConstraints( constraints == javax.microedition.lcdui.TextField.PASSWORD
            ? javax.microedition.lcdui.TextField.ANY : constraints );
    Screen.screen.setField( this );
    /**
     * Showing
     */
    Screen.screen.showTemp();
    actionPerformed();
  }

  public void keyRepeated(int keyCode) {
  }

  public void pointerPressed(int x, int y) {
    cancelledState = false;
  }

  public void pointerReleased(int x, int y) {
    if ( !cancelledState ) {
      showInputDialog();
    }
  }

  public void pointerDragged(int x, int y) {
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

  public void setTouchOrientation(boolean touchOrientation) {
  }

  public final void setCaption(String text) {
    this.caption = text;
    updateCaption();
  }

  public void updateCaption() {
    strings = StringUtil.wrapText( caption, width - ( Theme.upSize + 4 ) * 2, Theme.font,
            constraints == javax.microedition.lcdui.TextField.PASSWORD );
  }

  public void setText(String caption) {
    this.caption = caption;
    updateCaption();
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void setMaxSize(int maxSize) {
    this.maxSize = maxSize;
  }

  public void setConstraints(int constraints) {
    this.constraints = constraints;
  }

  public String getText() {
    return caption;
  }

  public String getTitle() {
    return title;
  }

  public int getmaxSize() {
    return maxSize;
  }

  public int getConstraints() {
    return constraints;
  }

  public void stopThreads() {
  }

  public String getStringValue() {
    return caption;
  }

  public void actionPerformed() {
  }
}
