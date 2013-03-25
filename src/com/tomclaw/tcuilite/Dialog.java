package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public final class Dialog {

  public String message;
  public int x, y, width, height;
  public int yOffset = 0;
  public int textOffset = 0;
  public Graphics imageGraphics;
  public String title;
  private String[] strings = new String[ 0 ];
  public int startY = -1;
  public int clipX, clipY, clipW, clipH;
  public Soft soft;
  public Screen screen;
  /** Theme **/
  public static int titleColor = 0xDEDFDE;
  public static int textColor = 0xFFFFFF;
  public static int alphaBackColor = 0x0A0A0A;
  public static int plainBackColor = 0x292C29;
  public static int hrLineColor = 0x000000;
  public static int hrLineShadow = 0x393839;
  public static int shadowColor = 0x0A0A0A;
  /** Final **/
  public static final int alphaBackIndex = 0xDC << 24;
  public static final int shadowIndex = 0x80 << 24;
  public static final int shadowSize = 10;
  public static final int interlineheight = 2;
  /** Runtime **/
  private int boldFontHeight;
  private int textFontHeight;
  private static Image shadowCorner;
  private Image shadowHorizontal, shadowVertical;
  private Image back;

  public Dialog( Screen screen, Soft soft, String title, String message ) {
    this.screen = screen;
    this.soft = soft;
    this.title = title;
    this.message = message;
    updateFontSize();
    initImageGraphics( screen.getWidth(), screen.getHeight() );
  }

  public void paint( Graphics g ) {
    if ( screen != null && soft != null ) {
      if ( Soft.getWidth() != screen.getWidth() ) {
        soft.setLocation( 0, screen.getHeight() - soft.getHeight() );
        soft.setSize( screen.getWidth(), soft.getHeight() );
      }
      soft.paint( g, 0, 0 );
    }
    if ( Settings.DIALOG_DRAW_ALPHABACK && checkCache() ) {
      g.drawImage( shadowCorner, x - shadowSize, y - shadowSize + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawRegion( shadowCorner, 0, 0, shadowSize, shadowSize, Sprite.TRANS_MIRROR, x + width, y - shadowSize + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawRegion( shadowCorner, 0, 0, shadowSize, shadowSize, Sprite.TRANS_MIRROR_ROT90, x + width, y + height + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawRegion( shadowCorner, 0, 0, shadowSize, shadowSize, Sprite.TRANS_MIRROR_ROT180, x - shadowSize, y + height + yOffset, Graphics.TOP | Graphics.LEFT );

      g.drawImage( shadowVertical, x - shadowSize, y + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawImage( shadowHorizontal, x, y - shadowSize + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawRegion( shadowVertical, 0, 0, shadowSize, height, Sprite.TRANS_MIRROR, x + width, y + yOffset, Graphics.TOP | Graphics.LEFT );
      g.drawRegion( shadowHorizontal, 0, 0, width, shadowSize, Sprite.TRANS_MIRROR_ROT180, x, y + height + yOffset, Graphics.TOP | Graphics.LEFT );

      g.drawImage( back, x, y + yOffset, Graphics.TOP | Graphics.LEFT );
    } else {
      g.setColor( plainBackColor );
      g.fillRect( x, y + yOffset, width, height );
    }
    g.setColor( hrLineColor );
    g.drawLine( x + Theme.upSize, y + yOffset + boldFontHeight + Theme.upSize * 2, x + width - Theme.upSize * 2, y + yOffset + boldFontHeight + Theme.upSize * 2 );
    g.setColor( hrLineShadow );
    g.drawLine( x + Theme.upSize, y + yOffset + boldFontHeight + Theme.upSize * 2 + 1, x + width - Theme.upSize * 2, y + yOffset + boldFontHeight + Theme.upSize * 2 + 1 );

    g.setColor( titleColor );
    g.setFont( Theme.titleFont );
    g.drawString( title, x + 2 + Theme.upSize, y + yOffset + Theme.upSize, Graphics.TOP | Graphics.LEFT );
    g.setFont( Theme.font );
    clipY = y + yOffset + ( boldFontHeight + Theme.upSize * 2 + 1 ) + 2 + Theme.upSize;
    clipX = x + 2 + Theme.upSize;
    clipW = width;
    clipH = height - Theme.upSize - clipY + y + yOffset;
    g.setClip( clipX, clipY, clipW, clipH );
    g.setColor( textColor );
    for ( int c = textOffset / ( textFontHeight + interlineheight ); c < Math.min( strings.length, ( textOffset + clipH ) / ( textFontHeight + interlineheight ) + 1 ); c++ ) {
      g.drawString( strings[c], x + 2 + Theme.upSize, clipY - textOffset + c * ( textFontHeight + interlineheight ), Graphics.TOP | Graphics.LEFT );
    }
  }

  public void updateFontSize() {
    boldFontHeight = Theme.titleFont.getHeight();
    textFontHeight = Theme.font.getHeight();
  }

  public final void initImageGraphics( int width, int height ) {
    this.width = width * 4 / 5;
    strings = StringUtil.wrapText( message, this.width - Theme.upSize * 2 - 2 * 2, Theme.titleFont );
    if ( height * 4 / 5 < strings.length * textFontHeight ) {
      this.height = height * 4 / 5;
    } else {
      this.height = textFontHeight + Theme.upSize * 2 + 4 + ( strings.length - 1 ) * ( textFontHeight + interlineheight ) + ( boldFontHeight + Theme.upSize * 2 + 1 );
    }
    if ( Settings.DIALOG_DRAW_ALPHABACK ) {
      if ( shadowCorner == null ) {
        shadowCorner = DrawUtil.drawCornerShadow( shadowColor | shadowIndex, shadowSize, shadowSize, 0 );
      }

      shadowVertical = DrawUtil.drawShadow( shadowColor | shadowIndex, shadowSize, this.height, 0 );
      shadowHorizontal = DrawUtil.drawShadow( shadowColor | shadowIndex, this.width, shadowSize, 1 );

      back = DrawUtil.fillShadow( alphaBackColor | alphaBackIndex, this.width, this.height );
    }
    x = width / 2 - this.width + this.width / 2;
    y = height / 2 - this.height / 2;
  }

  public void keyPressed( int keyCode ) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      textOffset -= textFontHeight;
      correctTextOffset();
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      textOffset += textFontHeight;
      correctTextOffset();
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_LEFT_MENU ) {
      if ( soft != null && soft.leftSoft != null ) {
        soft.leftSoft.actionPerformed();
      }
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_RIGHT_MENU ) {
      if ( soft != null && soft.rightSoft != null ) {
        soft.rightSoft.actionPerformed();
      }
    }
  }

  public void keyRepeated( int keyCode ) {
    keyPressed( keyCode );
  }

  public void keyReleased( int keyCode ) {
  }

  public void pointerPressed( int x, int y ) {
    if ( x > clipX && x < clipX + clipW
            && y > clipY && y < clipY + clipH ) {
      startY = textOffset + y;
    } else {
      if ( soft != null && ( x >= Soft.getX() && y >= Soft.getY()
              && x < ( Soft.getX() + Soft.getWidth() ) && y < ( Soft.getY() + soft.getHeight() ) ) ) {
        /** Pointer pressed on soft **/
        if ( x <= ( Soft.getX() + Soft.getWidth() / 2 ) ) {
          /** Left soft is pressed **/
          soft.leftSoft.actionPerformed();
        } else {
          /** Right soft is pressed **/
          soft.rightSoft.actionPerformed();
        }
      }
    }
  }

  public void pointerReleased( int x, int y ) {
    startY = -1;
    correctTextOffset();
  }

  public boolean pointerDragged( int x, int y ) {
    if ( startY != -1 ) {
      textOffset = startY - y;
      return correctTextOffset();
    }
    return false;
  }

  public boolean correctTextOffset() {
    if ( textOffset < 0 ) {
      textOffset = 0;
      return false;
    } else if ( textOffset > strings.length * ( textFontHeight + interlineheight )
            - ( height - Theme.upSize * 2 - ( boldFontHeight + Theme.upSize * 2 + 1 ) - 2 ) ) {
      textOffset = strings.length * ( textFontHeight + interlineheight )
              - ( height - Theme.upSize * 2 - ( boldFontHeight + Theme.upSize * 2 + 1 ) - 2 );
      return false;
    }
    return true;
  }
  
  public static void clearCache() {
    shadowCorner = null;
  }
  
  private boolean checkCache() {
    return shadowCorner != null && shadowVertical != null 
            && shadowHorizontal != null  && back != null;
  }
}
