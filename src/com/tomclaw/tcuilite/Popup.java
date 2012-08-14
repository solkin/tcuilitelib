package com.tomclaw.tcuilite;

import com.tomclaw.images.Splitter;
import com.tomclaw.utils.DrawUtil;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Popup implements GObject {

  public String name = null;
  public Vector items = null;
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public int yOffset = 0;
  public int selectedIndex = 0;
  public Soft soft;
  private int[] rgbData;
  static Image popup;
  /**
   * Runtime
   */
  public Graphics g = null;
  public int startIndex = 0;
  public int prevYDrag = -1;
  public int repaintScrollWidth = 0;
  public boolean isScrollAction = false;
  public boolean t_wasDrawFlag = false;
  /**
   * Colors
   */
  public static int foreColor = 0xFFFFFF;
  public static int foreSelColor = 0x000000;
  public static int backGradFrom = 0xEFEFEF;
  public static int backGradTo = 0xCECFCE;
  public static int selectedGradFrom = 0xDDDDFF;
  public static int selectedGradTo = 0xBBAAEE;
  public static int selectedUpOutline = 0xCCCCEE;
  public static int selectedBottomOutline = 0xAAAACC;
  public static int unSelectedGradFrom = 0xFFFFFF;
  public static int unSelectedGradTo = 0xF7F3F7;
  public static int scrollBack = 0xFFFFFF;
  public static int scrollGradFrom = 0xDDDDDD;
  public static int scrollGradTo = 0xAAAAAA;
  public static int scrollBorder = 0xAAAAAA;
  public static int scrollFix = 0x888888;
  public static int scrollFixShadow = 0xDDDDDD;
  public static int shadowColorFrom = 0x80;
  public static int shadowColorTo = 0x10;
  public static int a_backColor = 0xDC0A0A0A;
  public static int p_backColor = 0xFF292C29;
  public static int popupBorder = 0x000000;
  public static int hrLineColor = 0x000000;
  public static int hrLineShadow = 0x393839;
  /**
   * Sizes
   */
  public int itemHeight;
  private int scrollStart;
  private int scrollHeight;
  private PopupItem templistItem;
  public int shadowSize = 3;
  private int imageOffset = 0;

  public Popup() {
    items = new Vector();
    try {
      popup = Image.createImage( "/res/popup_img.png" );
    } catch ( IOException ex ) {
    }
  }

  public Popup( Vector items ) {
    this.items = items;
    try {
      popup = Image.createImage( "/res/popup_img.png" );
    } catch ( IOException ex ) {
    }
  }

  public void addItem( PopupItem popupItem ) {
    items.addElement( popupItem );
  }

  /*
   * public void repaint(Image image) { repaint(image.getGraphics()); }
   */
  public void repaintBackground( Graphics g, int paintX, int paintY ) {
    if ( Settings.MENU_DRAW_ALPHABACK ) {
      g.drawRGB( rgbData, 0, width + shadowSize * 2, paintX + x - shadowSize, paintY + y - shadowSize - 1, width + shadowSize * 2, height + shadowSize * 2 + 2, true );
    } else {
      g.setColor( backGradFrom );
      g.fillRect( paintX + x + 1, paintY + y + 1, width - 2 - repaintScrollWidth + 1, height - 1 );
      if ( Settings.MENU_DRAW_DIRECTSHADOWS ) {
        drawShadow( g, paintX, paintY );
      }
    }
  }

  public void repaintItems( Graphics g, int paintX, int paintY ) {
    itemHeight = Theme.font.getHeight() + Theme.upSize * 2;
    if ( !items.isEmpty() && items.size() > height / itemHeight ) {
      repaintScrollWidth = Theme.scrollWidth;
      if ( selectedIndex == -1 ) {
        selectedIndex = 0;
      }
    } else if ( items.isEmpty() ) {
      selectedIndex = -1;
    } else {
      repaintScrollWidth = -1;
      if ( selectedIndex == -1 ) {
        selectedIndex = 0;
      }
    }
    if ( !items.isEmpty() ) {
      if ( selectedIndex >= items.size() ) {
        selectedIndex = items.size() - 1;
      } else if ( selectedIndex < 0 ) {
        selectedIndex = 0;
      }
    }
    g.setFont( Theme.font );

    int t_foreColor;
    int t_foreSelColor;
    if ( Settings.MENU_DRAW_ALPHABACK ) {
      t_foreColor = foreColor;
      t_foreSelColor = foreSelColor;
    } else {
      t_foreColor = foreSelColor;
      t_foreSelColor = foreSelColor;
    }

    startIndex = ( yOffset / itemHeight );
    for ( int c = startIndex; c < startIndex + ( height ) / itemHeight + 1; c++ ) {
      if ( c == selectedIndex ) {
        DrawUtil.fillVerticalGradient( g, paintX + x + 1, paintY + y + c * itemHeight - yOffset, width - 1 - 2 - repaintScrollWidth, itemHeight, selectedGradFrom, selectedGradTo );
        g.setColor( selectedUpOutline );
        g.drawLine( paintX + x + 1, paintY + y + c * itemHeight - yOffset, paintX + x + width - 1 - 1 - repaintScrollWidth + 1, paintY + y + c * itemHeight - yOffset );
        g.setColor( selectedBottomOutline );
        g.drawLine( paintX + x + 1, paintY + y + ( c + 1 ) * itemHeight - yOffset, paintX + x + width - 1 - 1 - repaintScrollWidth + 1, paintY + y + ( c + 1 ) * itemHeight - yOffset );
        g.setColor( t_foreSelColor );
      } else {
        g.setColor( t_foreColor );
      }
      if ( c < items.size() ) {
        templistItem = ( PopupItem ) items.elementAt( c );
        if ( templistItem.imageFileHash != 0 ) {
          imageOffset = Splitter.drawImage( g, templistItem.imageFileHash, templistItem.imageIndex, paintX + x + Theme.upSize + 1, paintY + y + c * itemHeight - yOffset + itemHeight / 2, true );
          if ( imageOffset > 0 ) {
            imageOffset += Theme.upSize;
          }
        } else {
          imageOffset = 0;
        }
        if ( !templistItem.isEmpty() ) {
          g.drawImage( popup, paintX + x + width - popup.getWidth() - repaintScrollWidth, paintY + y + 1 + c * itemHeight - yOffset + itemHeight / 2, Graphics.VCENTER | Graphics.HCENTER );

        }
        g.drawString( templistItem.title, paintX + x + 1 + Theme.upSize + 1 + imageOffset, paintY + y + 1 + c * itemHeight - yOffset + Theme.upSize + 1, Graphics.TOP | Graphics.LEFT );
      }
    }

    /**
     * Scroll
     */
    if ( repaintScrollWidth > 0 ) {
      if ( !Settings.MENU_DRAW_ALPHABACK ) {
        g.setColor( scrollBack );
        g.fillRect( paintX + x + width - repaintScrollWidth, paintY + y, repaintScrollWidth, height );
      }
      scrollStart = height * yOffset / ( items.size() * itemHeight );
      scrollHeight = height * height / ( items.size() * itemHeight );
      DrawUtil.fillHorizontalGradient( g, paintX + x + width - repaintScrollWidth, paintY + y + scrollStart, repaintScrollWidth, scrollHeight - 1, scrollGradFrom, scrollGradTo );
      if ( scrollHeight > 6 ) {
        g.setColor( scrollFixShadow );
        g.fillRect( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 1, repaintScrollWidth - 2, 5 );
        g.setColor( scrollFix );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 - 2 );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 + 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 + 2 );
      }
      g.setColor( scrollBorder );
      g.drawRect( paintX + x + width - repaintScrollWidth - 1, paintY + y + height * yOffset / ( items.size() * itemHeight ), repaintScrollWidth, height * height / ( items.size() * itemHeight ) );
      if ( Settings.MENU_DRAW_ALPHABACK ) {
        g.setColor( hrLineShadow );
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y, paintX + x + width - repaintScrollWidth - 1, paintY + y + height );
      } else {
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y, paintX + x + width - repaintScrollWidth - 1, paintY + y + height - 1 );
      }
    }
    /**
     * Border
     */
    if ( !Settings.MENU_DRAW_ALPHABACK ) {
      g.setColor( scrollBorder );
      g.drawRect( paintX + x, paintY + y, width, height );
    }
  }

  public void repaint( Graphics g ) {
    repaint( g, 0, 0 );
  }

  public void repaint( Graphics g, int paintX, int paintY ) {
    repaintBackground( g, paintX, paintY );
    g.setClip( paintX + x, paintY + y, width + 1, height + 1 );
    repaintItems( g, paintX, paintY );
  }

  public void prepareBackground() {
    /**
     * Shadows
     */
    if ( Settings.MENU_DRAW_ALPHABACK ) {
      int fullWidth = ( this.width + shadowSize * 2 );
      int fullHeight = ( this.height + shadowSize * 2 ) + 2;
      rgbData = new int[ fullWidth * fullHeight + 1 ];
      int color;
      for ( int c = fullWidth; c < rgbData.length; c++ ) {
        rgbData[c] = a_backColor;
      }
      for ( int c = 0; c <= shadowSize; c++ ) {
        color = ( shadowColorTo + ( shadowColorFrom - shadowColorTo ) * ( shadowSize - c ) / shadowSize ) << 24;
        for ( int i = 0; i < fullWidth; i++ ) {
          rgbData[i + fullWidth * ( shadowSize - c )] = color;
          rgbData[i + fullWidth * ( c - shadowSize + fullHeight - 1 )] = color;
        }
        for ( int i = 0; i < fullHeight; i++ ) {
          rgbData[i * fullWidth + ( shadowSize - c )] = color;
          rgbData[i * fullWidth + fullWidth - ( shadowSize - c )] = color;
        }
      }
    }
  }

  public void drawShadow( Graphics g, int paintX, int paintY ) {
    /**
     * Shadows
     */
    int color;
    for ( int c = 0; c <= shadowSize; c++ ) {
      color = ( shadowColorTo + ( shadowColorFrom - shadowColorTo ) * ( shadowSize - c ) / shadowSize ) << 24;
      DrawUtil.drawLine( g, paintX + x + width + 1 + c, paintY + y - c, height + c * 2 + 1, false, color );  // Right
      DrawUtil.drawLine( g, paintX + x - 1 - c, paintY + y - c - 1, height + c * 2 + 1, false, color );          // Left
      DrawUtil.drawLine( g, paintX + x - c, paintY + y - 1 - c, width + c * 2 + 1, true, color );            // Upper
      DrawUtil.drawLine( g, paintX + x - c - 1, paintY + y + height + 1 + c, width + c * 2 + 1, true, color );   // Bottom
    }
  }

  public boolean defineSize( int maxHeight ) {
    int o_width = width;
    int o_height = height;
    itemHeight = Theme.font.getHeight() + Theme.upSize * 2;
    height = itemHeight * items.size() + 1;
    width = 0;
    int tempImageSize;
    for ( int c = 0; c < items.size(); c++ ) {
      tempImageSize = 0;
      if ( ( ( PopupItem ) items.elementAt( c ) ).imageFileHash != 0 ) {
        try {
          tempImageSize = Splitter.getImageGroup( ( ( PopupItem ) items.elementAt( c ) ).imageFileHash ).size + Theme.upSize;
        } catch ( NullPointerException ex ) {
        }
      }
      if ( Theme.font.stringWidth( ( ( PopupItem ) items.elementAt( c ) ).title )
              + Theme.upSize * 2 + 4 + ( ( ( PopupItem ) items.elementAt( c ) ).isEmpty()
              ? 0 : ( popup.getWidth() + Theme.upSize ) ) + tempImageSize > width ) {
        width = Theme.font.stringWidth( ( ( PopupItem ) items.elementAt( c ) ).title )
                + Theme.upSize * 2 + 4 + ( ( ( PopupItem ) items.elementAt( c ) ).isEmpty()
                ? 0 : ( popup.getWidth() + Theme.upSize ) ) + tempImageSize;
      }
    }
    if ( height + shadowSize > maxHeight ) {
      height = itemHeight * ( ( maxHeight - shadowSize ) / itemHeight );
      width += Theme.scrollWidth;
      if ( o_width != width || o_height != height ) {
        prepareBackground();
      }
      return true;
    }
    if ( o_width != width || o_height != height ) {
      prepareBackground();
    }
    return false;
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
    if ( items.isEmpty() ) {
      selectedIndex = -1;
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      if ( selectedIndex == 0 ) {
        selectedIndex = items.size() - 1;
        yOffset = itemHeight * items.size() - height + 1;
        if ( yOffset < 0 ) {
          yOffset = 0;
        }
        return;
      }
      selectedIndex--;
      if ( yOffset > selectedIndex * itemHeight ) {
        yOffset = ( selectedIndex ) * itemHeight;
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      if ( selectedIndex == items.size() - 1 ) {
        selectedIndex = 0;
        yOffset = 0;
        return;
      }
      selectedIndex++;
      if ( yOffset + height < ( selectedIndex + 1 ) * itemHeight + 1 ) {
        yOffset = ( selectedIndex + 1 ) * itemHeight - height + 1;
      }
    }
    if ( keyCode == Screen.KEY_NUM1 ) {
      selectedIndex = 0;
      yOffset = 0;
    }
    if ( keyCode == Screen.KEY_NUM7 ) {
      selectedIndex = items.size() - 1;
      yOffset = itemHeight * items.size() - height + 1;
      if ( yOffset < 0 ) {
        yOffset = 0;
      }
    }
  }

  public void keyReleased( int keyCode ) {
  }

  public void keyRepeated( int keyCode ) {
    keyPressed( keyCode );
  }

  public void pointerPressed( int x, int y ) {
    t_wasDrawFlag = false;
    if ( items.isEmpty() ) {
      selectedIndex = -1;
      return;
    }
    if ( x < this.x || y < this.y || x > this.x + width || y > this.y + height ) {
      return;
    }
    if ( x > this.x + width - repaintScrollWidth ) {
      isScrollAction = true;
    } else {
      isScrollAction = false;
      if ( ( -this.y + yOffset + y ) / itemHeight == selectedIndex ) {
        return;
      }
      selectedIndex = ( -this.y + yOffset + y ) / itemHeight;
      if ( selectedIndex > items.size() - 1 ) {
        selectedIndex = items.size() - 1;
      }
    }
  }

  public void pointerReleased( int x, int y ) {
    if ( t_wasDrawFlag == false && selectedIndex != -1 && selectedIndex < items.size() ) {
      if ( ( ( PopupItem ) items.elementAt( selectedIndex ) ).isEmpty() ) {
        ( ( PopupItem ) items.elementAt( selectedIndex ) ).actionPerformed();
        soft.isLeftPressed = false;
        soft.isRightPressed = false;
      }
    }
    t_wasDrawFlag = false;
    prevYDrag = -1;
  }

  public boolean pointerDragged( int x, int y ) {
    t_wasDrawFlag = true;
    if ( items.isEmpty() ) {
      selectedIndex = -1;
      return false;
    }
    if ( isScrollAction ) {
      scrollStart = y - this.y - scrollHeight / 2;
      yOffset = scrollStart * ( items.size() * itemHeight ) / height;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > ( items.size() ) * itemHeight - height + 1 ) {
        yOffset = ( items.size() ) * itemHeight - height + 1;
      } else {
        return true;
      }
      return false;
    } else if ( repaintScrollWidth > 0 ) {
      if ( prevYDrag == -1 ) {
        prevYDrag = yOffset + y;
        return true;
      }
      yOffset = prevYDrag - y;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > ( items.size() ) * itemHeight - height + 1 ) {
        yOffset = ( items.size() ) * itemHeight - height + 1;
      } else {
        return true;
      }
      return false;
    }
    return true;
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

  public void setTouchOrientation( boolean touchOrientation ) {
    if ( touchOrientation ) {
      Theme.scrollWidth = 15;
    } else {
      Theme.scrollWidth = 5;
    }
  }
}
