package com.tomclaw.tcuilite;

import com.tomclaw.images.Splitter;
import com.tomclaw.utils.bb.BBResult;
import com.tomclaw.utils.bb.BBUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ChatItem extends PaneObject {

  public String text = "";
  /** Runtime **/
  public Pane pane;
  public BBResult bbResult;
  /** Colors **/
  public static int foreColor = 0x555555;
  public static int titleColor = 0x424142;
  public static int borderColor = 0x7B86DA;
  public static int actOuterLight = 0xBDC7FF;
  public static int actInnerLight = 0x8C9AFF;
  /** Sizes **/
  public int interlineheight = 2;
  private int imageOffset = 0;
  /** Chat item specific data **/
  public String buddyId = null;
  public String buddyNick = null;
  public String itemDateTime = null;
  public int itemType = 0x00;
  /**
   * 0x00 - plain message 0x01 - hyperlink 0x02 - auth message 0x03 - info (any)
   * 0x04 - error (any) 0x06 - file
   */
  public static final int TYPE_HYPERLINK_MSG = 0x00;
  public static final int TYPE_AUTH_REQ_MSG = 0x01;
  public static final int TYPE_AUTH_DENY_MSG = 0x02;
  public static final int TYPE_AUTH_OK_MSG = 0x03;
  public static final int TYPE_INFO_MSG = 0x04;
  public static final int TYPE_ERROR_MSG = 0x05;
  public static final int TYPE_FILE_TRANSFER = 0x06;
  public static final int TYPE_PLAIN_MSG = 0x07;
  public byte[] cookie = null;
  public byte dlvStatus = 0x00;
  public static final byte DLV_STATUS_NOT_SENT = 0x00;
  public static final byte DLV_STATUS_SENT = 0x01;
  public static final byte DLV_STATUS_DELIVERED = 0x02;
  public static final byte DLV_STATUS_INCOMING = 0x03;

  public ChatItem( Pane pane, String caption ) {
    this.text = caption;
    this.pane = pane;
    updateCaption();
  }

  public ChatItem( Pane pane, String buddyId, String buddyNick,
          String itemDateTime, int itemType, String text ) {
    this.pane = pane;
    this.buddyId = buddyId;
    this.buddyNick = buddyNick;
    this.itemDateTime = itemDateTime;
    this.itemType = itemType;
    this.text = text;
    updateCaption();
  }

  public void repaint( Graphics g ) {
    if ( isFocusable && isFocused && g != null ) {
      g.setColor( actOuterLight );
      g.drawRect( x, y, width, height );
      g.setColor( actInnerLight );
      g.drawRect( x + 1, y + 1, width - 2, height - 2 );
    }
    if ( g != null ) {
      if ( Settings.CHAT_IMAGE_GROUP_FILE != 0 ) {
        /** Drawing chat item image **/
        imageOffset = Splitter.drawImage( g, Settings.CHAT_IMAGE_GROUP_FILE,
                ( ( ( itemType == TYPE_PLAIN_MSG
                ? ( itemType + dlvStatus ) : itemType ) ) ),
                x + 2 + Theme.upSize,
                y + 2 + Theme.upSize, false ) + Theme.upSize;
      }
      g.setColor( borderColor );
      g.drawLine( x + 2 + Theme.upSize + imageOffset, y + 2 + Theme.upSize
              + Math.max( Theme.titleFont.getHeight(), Theme.font.getHeight() ),
              x + width - 2 - Theme.upSize, y + 2 + Theme.upSize
              + Math.max( Theme.titleFont.getHeight(), Theme.font.getHeight() ) );
      g.setFont( Theme.titleFont );
      g.setColor( titleColor );

      g.drawString( buddyNick, x + imageOffset + 2 + Theme.upSize, y + 2
              + Theme.upSize, Graphics.TOP | Graphics.LEFT );
      g.setFont( Theme.font );
      g.drawString( itemDateTime, x + width - Theme.upSize
              - Theme.font.stringWidth( itemDateTime ), y + 2 + Theme.upSize,
              Graphics.TOP | Graphics.LEFT );
      g.setColor( foreColor );
    }

    if ( g != null ) {
      for ( int c = 0; c < bbResult.bbStyleString.length; c++ ) {
        bbResult.bbStyleString[c].paint( g, x + 2 + Theme.upSize,
                y + ( Theme.titleFont.getHeight() + Theme.upSize * 2 )
                + 2 + Theme.upSize );
      }
    }
  }

  public void setLocation( int x, int y ) {
    if ( this.x != x || this.y != y || height == 0 ) {
      this.x = x;
      this.y = y;
      repaint( null );
    }
  }

  public void setSize( int width, int height ) {
    if ( this.width != width ) {
      this.width = width;
      updateCaption();
    }
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

  public final void setCaption( String text ) {
    this.text = text;
    updateCaption();
  }

  public final void updateCaption() {
    if ( width != 0 ) {
      try {
        /** Pray for BB tags to be well-formed **/
        bbResult = BBUtil.processText( text, 0, 0,
                width - ( Theme.upSize + 4 ) * 2, foreColor );
        height = ( Theme.titleFont.getHeight() + Theme.upSize * 2 )
                + 2 + Theme.upSize
                + bbResult.height + 2;
      } catch ( Throwable ex1 ) {
        /** BB format was incorrect **/
      }
    }
  }

  public String getStringValue() {
    return text;
  }
}
