package com.tomclaw.tcuilite;

import com.tomclaw.utils.StringUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public final class Dialog {

  public String message;
  public int x, y, width, height;
  public int yOffset = 0;
  public int textOffset = 0;
  public Graphics imageGraphics;
  private int[] rgbData;
  public String title;
  private String[] strings = new String[ 0 ];
  public int startY = -1;
  public int clipX, clipY, clipW, clipH;
  public Soft soft;
  public Screen screen;
  /**
   * Theme
   */
  public static int titleColor = 0xDEDFDE;
  public static int textColor = 0xFFFFFF;
  public static int a_backColor = 0xDC0A0A0A;
  public static int p_backColor = 0xFF292C29;
  public static int hrLineColor = 0x000000;
  public static int hrLineShadow = 0x393839;
  public static int shadowColorFrom = 0x80;
  public static int shadowColorTo = 0x10;
  public static int shadowSize = 3;
  public static int interlineheight = 2;
  /**
   * Runtime
   */
  private int boldFontHeight;
  private int textFontHeight;

  public Dialog ( Screen screen, Soft soft, String title, String message ) {
    this.screen = screen;
    this.soft = soft;
    this.title = title;
    this.message = message;
    updateFontSize ();
    initImageGraphics ( screen.getWidth (), screen.getHeight () );
  }

  public void paint ( Graphics g ) {
    if ( screen != null && soft != null ) {
      if ( Soft.getWidth () != screen.getWidth () ) {
        soft.setLocation ( 0, screen.getHeight () - soft.getHeight () );
        soft.setSize ( screen.getWidth (), soft.getHeight () );
      }
      soft.paint ( g, 0, 0 );
    }
    if ( Settings.DIALOG_DRAW_ALPHABACK || Settings.DIALOG_DRAW_SHADOWS ) {
      g.drawRGB ( rgbData, 0, width, x, y + yOffset, width, height, true );
    } else {
      g.setColor ( p_backColor );
      g.fillRect ( x, y + yOffset, width, height );
    }
    g.setColor ( hrLineColor );
    g.drawLine ( x + shadowSize * 2, y + shadowSize + yOffset + boldFontHeight + Theme.upSize * 2, x + width - shadowSize * 2, y + yOffset + boldFontHeight + Theme.upSize * 2 + shadowSize );
    g.setColor ( hrLineShadow );
    g.drawLine ( x + shadowSize * 2, y + shadowSize + yOffset + boldFontHeight + Theme.upSize * 2 + 1, x + width - shadowSize * 2, y + yOffset + boldFontHeight + Theme.upSize * 2 + 1 + shadowSize );

    g.setColor ( titleColor );
    g.setFont ( Theme.titleFont );
    g.drawString ( title, x + shadowSize + Theme.upSize, y + shadowSize + yOffset + Theme.upSize, Graphics.TOP | Graphics.LEFT );
    g.setFont ( Theme.font );
    clipY = y + shadowSize + yOffset + ( boldFontHeight + Theme.upSize * 2 + 1 ) + 2 + Theme.upSize;
    clipX = x + 2 + Theme.upSize + shadowSize;
    clipW = width;
    clipH = height - Theme.upSize - shadowSize * 2 - clipY + y + yOffset;
    g.setClip ( clipX, clipY, clipW, clipH );
    g.setColor ( textColor );
    for ( int c = textOffset / ( textFontHeight + interlineheight ); c < Math.min ( strings.length, ( textOffset + clipH ) / ( textFontHeight + interlineheight ) + 1 ); c++ ) {
      g.drawString ( strings[c], x + 2 + Theme.upSize + shadowSize, clipY - textOffset + c * ( textFontHeight + interlineheight ), Graphics.TOP | Graphics.LEFT );
    }
  }

  public void updateFontSize () {
    boldFontHeight = Theme.titleFont.getHeight ();
    textFontHeight = Theme.font.getHeight ();
  }

  public void prepareBackground () {
    /**
     * Shadows
     */
    if ( Settings.DIALOG_DRAW_ALPHABACK || Settings.DIALOG_DRAW_SHADOWS ) {
      rgbData = new int[ this.width * this.height + 1 ];
      int color;
      int backColor;
      if ( Settings.DIALOG_DRAW_ALPHABACK ) {
        backColor = a_backColor;
      } else {
        backColor = p_backColor;
      }
      for ( int c = 0; c < rgbData.length; c++ ) {
        rgbData[c] = backColor;
      }
      if ( Settings.DIALOG_DRAW_SHADOWS ) {
        for ( int c = 0; c <= shadowSize; c++ ) {
          color = ( shadowColorTo + ( shadowColorFrom - shadowColorTo ) * ( shadowSize - c ) / shadowSize ) << 24;
          for ( int i = 0; i < width; i++ ) {
            rgbData[i + width * ( shadowSize - c )] = color;
            rgbData[i + width * ( c - shadowSize + height - 1 )] = color;
          }
          for ( int i = 0; i < height; i++ ) {
            rgbData[i * width + ( shadowSize - c )] = color;
            rgbData[i * width + width - ( shadowSize - c )] = color;
          }
        }
      }
    }

  }

  public final void initImageGraphics ( int width, int height ) {
    this.width = width * 4 / 5 + shadowSize * 2;
    strings = StringUtil.wrapText ( message, this.width - Theme.upSize * 2 - shadowSize * 2, Theme.titleFont );
    if ( height * 4 / 5 + shadowSize * 2 < strings.length * textFontHeight ) {
      this.height = height * 4 / 5 + shadowSize * 2;
    } else {
      this.height = textFontHeight + Theme.upSize * 2 + 4 + ( strings.length - 1 ) * ( textFontHeight + interlineheight ) + ( boldFontHeight + Theme.upSize * 2 + 1 ) + shadowSize * 2;
    }
    x = width / 2 - this.width + this.width / 2;
    y = height / 2 - this.height / 2;
    prepareBackground ();
  }

  public void keyPressed ( int keyCode ) {
    if ( Screen.getExtGameAct ( keyCode ) == Screen.UP ) {
      textOffset -= textFontHeight;
      correctTextOffset ();
    } else if ( Screen.getExtGameAct ( keyCode ) == Screen.DOWN ) {
      textOffset += textFontHeight;
      correctTextOffset ();
    } else if ( Screen.getExtGameAct ( keyCode ) == Screen.KEY_CODE_LEFT_MENU ) {
      if ( soft != null && soft.leftSoft != null ) {
        soft.leftSoft.actionPerformed ();
      }
    } else if ( Screen.getExtGameAct ( keyCode ) == Screen.KEY_CODE_RIGHT_MENU ) {
      if ( soft != null && soft.rightSoft != null ) {
        soft.rightSoft.actionPerformed ();
      }
    }
  }

  public void keyRepeated ( int keyCode ) {
    keyPressed ( keyCode );
  }

  public void keyReleased ( int keyCode ) {
  }

  public void pointerPressed ( int x, int y ) {
    if ( x > clipX && x < clipX + clipW
            && y > clipY && y < clipY + clipH ) {
      startY = textOffset + y;
    } else {
      if ( soft != null && ( x >= Soft.getX () && y >= Soft.getY ()
              && x < ( Soft.getX () + Soft.getWidth () ) && y < ( Soft.getY () + soft.getHeight () ) ) ) {
        /** Pointer pressed on soft **/
        if ( x <= ( Soft.getX () + Soft.getWidth () / 2 ) ) {
          /** Left soft is pressed **/
          soft.leftSoft.actionPerformed ();
        } else {
          /** Right soft is pressed **/
          soft.rightSoft.actionPerformed ();
        }
      }
    }
  }

  public void pointerReleased ( int x, int y ) {
    startY = -1;
    correctTextOffset ();
  }

  public boolean pointerDragged ( int x, int y ) {
    if ( startY != -1 ) {
      textOffset = startY - y;
      return correctTextOffset ();
    }
    return false;
  }

  public boolean correctTextOffset () {
    if ( textOffset < 0 ) {
      textOffset = 0;
      return false;
    } else if ( textOffset > strings.length * ( textFontHeight + interlineheight )
            - ( height - Theme.upSize * 2 - shadowSize * 2 - ( boldFontHeight + Theme.upSize * 2 + 1 ) - 2 ) ) {
      textOffset = strings.length * ( textFontHeight + interlineheight )
              - ( height - Theme.upSize * 2 - shadowSize * 2 - ( boldFontHeight + Theme.upSize * 2 + 1 ) - 2 );
      return false;
    }
    return true;
  }
}
