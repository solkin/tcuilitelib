package com.tomclaw.tcuilite;

import com.tomclaw.images.Splitter;
import com.tomclaw.utils.DrawUtil;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Popup extends Scroll {

  public Vector items = null;
  public int selectedIndex;
  public Soft soft;
  static Image popup;
  /** Runtime **/
  public int startIndex;
  public boolean t_wasDrawFlag = false;
  private static Image c1, c2, c3, c4;
  private Image s1, s2, s3, s4;
  private Image back;
  /** Colors **/
  public static int foreColor = 0x000000;
  public static int foreSelColor = 0x000000;
  public static int backGradFrom = 0xEFEFEF;
  public static int backGradTo = 0xCECFCE;
  public static int selectedGradFrom = 0xDDDDFF;
  public static int selectedGradTo = 0xBBAAEE;
  public static int selectedUpOutline = 0xCCCCEE;
  public static int selectedBottomOutline = 0xAAAACC;
  public static int shadowBorder = 0xD9D8D9;
  public static int alphaBackColor = 0xEAEAEA;
  public static int shadowColor = 0x0A0A0A;
  /** Final **/
  public static final int alphaBackIndex = 0xDC << 24;
  public static final int shadowIndex = 0x80 << 24;
  public static final int shadowSize = 10;
  /** Sizes **/
  public int itemHeight;
  private PopupItem templistItem;
  private int imageOffset;

  public Popup() {
    items = new Vector();
    loadPopupImage();
  }

  public Popup( Vector items ) {
    this.items = items;
    loadPopupImage();
  }

  private void loadPopupImage() {
    try {
      popup = Image.createImage( Settings.POPUP_IMAGE );
    } catch ( IOException ex ) {
    }
  }

  public void addItem( PopupItem popupItem ) {
    items.addElement( popupItem );
  }

  public void repaintBackground( Graphics g, int paintX, int paintY ) {
    if ( Settings.MENU_DRAW_SHADOWS ) {
      g.drawImage( c1, x - shadowSize, y - shadowSize, Graphics.TOP | Graphics.LEFT );
      g.drawImage( c2, x + width + 1, y - shadowSize, Graphics.TOP | Graphics.LEFT );
      g.drawImage( c3, x + width + 1, y + height, Graphics.TOP | Graphics.LEFT );
      g.drawImage( c4, x - shadowSize, y + height, Graphics.TOP | Graphics.LEFT );
      g.drawImage( s1, x - shadowSize, y, Graphics.TOP | Graphics.LEFT );
      g.drawImage( s2, x, y - shadowSize, Graphics.TOP | Graphics.LEFT );
      g.drawImage( s3, x + width + 1, y, Graphics.TOP | Graphics.LEFT );
      g.drawImage( s4, x, y + height, Graphics.TOP | Graphics.LEFT );
    }
    if ( Settings.MENU_DRAW_ALPHABACK ) {
      g.drawImage( back, x, y, Graphics.TOP | Graphics.LEFT );
    } else {
      DrawUtil.fillVerticalGradient( g, paintX + x + 1, paintY + y + 1, 
              width - 1 - repaintScrollWidth, height - 1, 
              backGradFrom, backGradTo );
      g.setColor( scrollBorder );
      g.drawRect( paintX + x, paintY + y, width, height - 1 );
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
    startIndex = ( yOffset / itemHeight );
    for ( int c = startIndex; c < startIndex + ( height ) / itemHeight + 1; c++ ) {
      if ( c == selectedIndex ) {
        DrawUtil.fillVerticalGradient( g, paintX + x, paintY + y + c * itemHeight - yOffset, width - repaintScrollWidth, itemHeight, selectedGradFrom, selectedGradTo );
        g.setColor( selectedUpOutline );
        g.drawLine( paintX + x, paintY + y + c * itemHeight - yOffset, paintX + x + width - 1 - 1 - repaintScrollWidth + 1, paintY + y + c * itemHeight - yOffset );
        g.setColor( selectedBottomOutline );
        g.drawLine( paintX + x, paintY + y + ( c + 1 ) * itemHeight - yOffset, paintX + x + width - 1 - 1 - repaintScrollWidth + 1, paintY + y + ( c + 1 ) * itemHeight - yOffset );
        g.setColor( foreSelColor );
      } else {
        g.setColor( foreColor );
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
    /** Scroll **/
    if ( repaintScrollWidth > 0 ) {
      if ( !Settings.MENU_DRAW_ALPHABACK ) {
        g.setColor( scrollBack );
        g.fillRect( paintX + x + width - repaintScrollWidth, paintY + y + 1, repaintScrollWidth, height - 2 );
      }
      scrollStart = height * yOffset / ( items.size() * itemHeight );
      scrollHeight = height * height / ( items.size() * itemHeight ) - 1;
      DrawUtil.fillHorizontalGradient( g, paintX + x + width - repaintScrollWidth, paintY + y + scrollStart, repaintScrollWidth, scrollHeight, scrollGradFrom, scrollGradTo );
      if ( scrollHeight > 6 ) {
        g.setColor( scrollFixShadow );
        g.fillRect( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 1, repaintScrollWidth - 2, 5 );
        g.setColor( scrollFix );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 - 2 );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 );
        g.drawLine( paintX + x + width - repaintScrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 + 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 + 2 );
      }
      g.setColor( scrollBorder );
      g.drawRect( paintX + x + width - repaintScrollWidth - 1, paintY + y + height * yOffset / ( items.size() * itemHeight ), repaintScrollWidth, height * height / ( items.size() * itemHeight ) - 1 );
      if ( Settings.MENU_DRAW_ALPHABACK ) {
        g.setColor( shadowBorder );
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y, paintX + x + width - repaintScrollWidth - 1, paintY + y + height - 1 );
        g.setColor( scrollBorder );
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y + scrollStart, paintX + x + width - repaintScrollWidth - 1, paintY + y + scrollStart + scrollHeight );
        g.drawLine( paintX + x + width, paintY + y + scrollStart, paintX + x + width, paintY + y + scrollStart + scrollHeight );
      } else {
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y, paintX + x + width - repaintScrollWidth - 1, paintY + y + height - 1 );
      }
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
    /** Shadows **/
    if ( Settings.MENU_DRAW_SHADOWS ) {
      if ( c1 == null || c2 == null || c3 == null || c4 == null ) {
        c1 = DrawUtil.drawCornerShadow( shadowColor | shadowIndex, shadowSize, shadowSize, 0 );
        c2 = DrawUtil.drawCornerShadow( shadowColor | shadowIndex, shadowSize, shadowSize, 1 );
        c3 = DrawUtil.drawCornerShadow( shadowColor | shadowIndex, shadowSize, shadowSize, 2 );
        c4 = DrawUtil.drawCornerShadow( shadowColor | shadowIndex, shadowSize, shadowSize, 3 );
      }

      s1 = DrawUtil.drawShadow( shadowColor | shadowIndex, shadowSize, this.height, 0 );
      s2 = DrawUtil.drawShadow( shadowColor | shadowIndex, this.width + 1, shadowSize, 1 );
      s3 = DrawUtil.drawShadow( shadowColor | shadowIndex, shadowSize, this.height, 2 );
      s4 = DrawUtil.drawShadow( shadowColor | shadowIndex, this.width + 1, shadowSize, 3 );
    }
    if ( Settings.MENU_DRAW_ALPHABACK ) {
      back = DrawUtil.fillShadow( alphaBackColor | alphaBackIndex, this.width + 1, this.height );
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
}
