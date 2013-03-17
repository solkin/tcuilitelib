package com.tomclaw.tcuilite;

import com.tomclaw.images.Splitter;
import com.tomclaw.utils.DrawUtil;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Tab extends GObject {

  public Vector items = null;
  public int xOffset = 0;
  public int selectedIndex = 0;
  public TabEvent tabEvent = null;
  public GObject gObject = null;
  public int KEY_LEFT_EVENT = Screen.LEFT;
  public int KEY_RIGHT_EVENT = Screen.RIGHT;
  /** Runtime **/
  private TabItem tempTabItem;
  public int dragXStart = -1;
  public Screen screen;
  /** Sizes **/
  public int itemHeight;
  public int imageWidth = 0;
  public int totalWidth = 0;
  public int tabLabelHeight;
  /** Colors **/
  public static int backGradFrom = 0xFFFFFF;
  public static int backGradTo = 0xFFFFFF;
  public static int selectedForeColor = 0x555555;
  public static int selectedGradFrom = 0xDDDDFF;
  public static int selectedGradTo = 0xBBAAEE;
  public static int selectedUpOutline = 0xCCCCEE;
  public static int selectedBottomOutline = 0xAAAACC;
  public static int unSelectedForeColor = 0x555555;
  public static int unSelectedGradFrom = 0xFFFFFF;
  public static int unSelectedGradTo = 0xFFFFFF;
  public static int unSelectedUpOutline = 0xDDDDDD;
  public static int unSelectedBottomOutline = 0xAAAAAA;

  public Tab( Screen screen ) {
    items = new Vector();
    this.screen = screen;
  }

  public void addTabItem( TabItem tabItem ) {
    this.items.addElement( tabItem );
  }

  public void setGObject( GObject gObject ) {
    this.gObject = gObject;
    this.gObject.setTouchOrientation( screen.isPointerEvents );
  }

  public void repaint( Graphics g ) {
    repaint( g, 0, 0 );
  }

  public void repaint( Graphics g, int paintX, int paintY ) {
    g.setFont( Theme.font );
    itemHeight = Theme.font.getHeight() + Theme.upSize * 3;
    g.setClip( paintX + x, paintY + y, width, itemHeight + 1 );
    DrawUtil.fillVerticalGradient( g, paintX + x, paintY + y, width, itemHeight, backGradFrom, backGradTo );
    int tempXOffset = 0, itemWidth;
    g.setColor( unSelectedBottomOutline );
    g.drawLine( paintX + x, paintY + y + itemHeight, paintX + x + width, paintY + y + itemHeight );
    for ( int c = 0; c < items.size(); c++ ) {
      tempTabItem = (TabItem) items.elementAt( c );
      if ( tempTabItem.imageFileHash != 0 ) {
        imageWidth = Splitter.getImageGroup( tempTabItem.imageFileHash ).size;
      } else {
        imageWidth = 0;
      }
      itemWidth = Theme.font.stringWidth( tempTabItem.title ) + Theme.upSize * 2
              + ( ( tempTabItem.imageIndex == -1 ) ? 0 : ( imageWidth + Theme.upSize ) );
      tempTabItem.x = x + tempXOffset;
      tempTabItem.width = itemWidth;
      if ( c == selectedIndex ) {
        DrawUtil.fillVerticalGradient( g, paintX + x + tempXOffset - xOffset, paintY + y + 1, itemWidth * tempTabItem.fillPercent / 100, itemHeight, selectedGradFrom, selectedGradTo );
        g.setColor( selectedUpOutline );
        g.drawLine( paintX + x + tempXOffset - xOffset + 1, paintY + y, paintX + x + tempXOffset - xOffset + itemWidth - 1, paintY + y );
        g.drawLine( paintX + x + tempXOffset - xOffset, paintY + y + 1, paintX + x + tempXOffset - xOffset, paintY + y + itemHeight - 1 );
        g.drawLine( paintX + x + tempXOffset + itemWidth - xOffset, paintY + y + 1, paintX + x + tempXOffset + itemWidth - xOffset, paintY + y + itemHeight - 1 );
        g.setColor( selectedBottomOutline );
        g.drawLine( paintX + x + tempXOffset - xOffset + 1, paintY + y + itemHeight, paintX + x + tempXOffset - xOffset + itemWidth - 1, paintY + y + itemHeight );
      } else {
        DrawUtil.fillVerticalGradient( g, paintX + x + tempXOffset - xOffset + 1, paintY + y + 1, itemWidth - 1, itemHeight - 1, unSelectedGradFrom, unSelectedGradTo );
        g.setColor( unSelectedUpOutline );
        g.drawLine( paintX + x + tempXOffset - xOffset + 1, paintY + y + Theme.upSize, paintX + x + tempXOffset - xOffset + itemWidth - 1, paintY + y + Theme.upSize );
        if ( c == 0 ) {
          g.drawLine( paintX + x + tempXOffset - xOffset, paintY + y + 1 + Theme.upSize, paintX + x + tempXOffset - xOffset, paintY + y + itemHeight - 1 );
        }
        g.drawLine( paintX + x + tempXOffset + itemWidth - xOffset, paintY + y + 1 + Theme.upSize, paintX + x + tempXOffset + itemWidth - xOffset, paintY + y + itemHeight - 1 );
      }
      if ( tempTabItem.imageIndex != -1 ) {
        Splitter.drawImage( g, tempTabItem.imageFileHash, tempTabItem.imageIndex, paintX + x + tempXOffset - xOffset + Theme.upSize, paintY + y + Theme.upSize * 2, false );
      }
      g.setColor( c == selectedIndex ? selectedForeColor : unSelectedForeColor );
      g.drawString( tempTabItem.title, paintX + x + tempXOffset - xOffset + Theme.upSize + ( ( tempTabItem.imageIndex == -1 ) ? 0 : ( imageWidth + Theme.upSize ) ), paintY + y + Theme.upSize * 2, Graphics.TOP | Graphics.LEFT );
      tempXOffset += itemWidth;
    }
    totalWidth = tempXOffset;
    /** Painting Label **/
    tabLabelHeight = 0;
    if ( selectedIndex >= 0 && selectedIndex < items.size() ) {
      /** Obtain tab item object **/
      tempTabItem = (TabItem) items.elementAt( selectedIndex );
      /** Checking for something strange and label exist **/
      if ( tempTabItem != null && tempTabItem.tabLabel != null ) {
        tempTabItem.tabLabel.setLocation( paintX + x, paintY + y + itemHeight + 1 );
        tempTabItem.tabLabel.setSize( width, -1 );
        g.setClip( paintX + x, paintY + y + itemHeight + 1, width, height - itemHeight - 1 );
        tempTabItem.tabLabel.repaint( g );
        tabLabelHeight = tempTabItem.tabLabel.getHeight();
      }
    }
    /** Painting GObject **/
    gObject.setLocation( x, y + itemHeight + 1 + tabLabelHeight );
    gObject.setSize( width, height - itemHeight - 1 - tabLabelHeight );
    g.setClip( paintX + x, paintY + y + itemHeight + 1 + tabLabelHeight, width, height - itemHeight - 1 - tabLabelHeight );
    gObject.repaint( g, paintX, paintY );
    g.setClip( 0, 0, screen.getWidth(), screen.getHeight() );
  }

  public void keyPressed( int keyCode ) {
    if ( ( ( Screen.getExtGameAct( keyCode ) == KEY_LEFT_EVENT || keyCode == KEY_LEFT_EVENT ) || ( Screen.getExtGameAct( keyCode ) == KEY_RIGHT_EVENT || keyCode == KEY_RIGHT_EVENT ) )
            && gObject instanceof Pane && !( (Pane) gObject ).items.isEmpty()
            && ( ( (Pane) gObject ).psvLstFocusedIndex ) >= 0
            && ( (PaneObject) ( ( (Pane) gObject ).items.elementAt( ( (Pane) gObject ).psvLstFocusedIndex ) ) ) instanceof Gauge ) {
      gObject.keyPressed( keyCode );
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == KEY_LEFT_EVENT || keyCode == KEY_LEFT_EVENT ) {
      focusLeft();
    } else if ( Screen.getExtGameAct( keyCode ) == KEY_RIGHT_EVENT || keyCode == KEY_RIGHT_EVENT ) {
      focusRight();
    } else {
      gObject.keyPressed( keyCode );
    }
  }

  public void keyReleased( int keyCode ) {
    if ( ( Screen.getExtGameAct( keyCode ) != KEY_LEFT_EVENT || keyCode != KEY_LEFT_EVENT ) && ( Screen.getExtGameAct( keyCode ) != KEY_RIGHT_EVENT || keyCode != KEY_RIGHT_EVENT ) ) {
      gObject.keyReleased( keyCode );
    } else if ( gObject instanceof Pane && !( (Pane) gObject ).items.isEmpty() && ( ( (Pane) gObject ).psvLstFocusedIndex ) >= 0 && ( (PaneObject) ( ( (Pane) gObject ).items.elementAt( ( (Pane) gObject ).psvLstFocusedIndex ) ) ) instanceof Gauge ) {
      gObject.keyReleased( keyCode );
    }
  }

  public void keyRepeated( int keyCode ) {
    keyPressed( keyCode );
  }

  public void pointerPressed( int x, int y ) {
    if ( y >= this.y && y <= this.y + itemHeight ) {
      dragXStart = xOffset + x;
      screen.isSlideAwaiting = false;
      screen.isSlideMode = false;
      screen.isDirectScroll = true;
      for ( int c = 0; c < items.size(); c++ ) {
        tempTabItem = ( (TabItem) items.elementAt( c ) );
        if ( tempTabItem.x <= x + xOffset && ( tempTabItem.x + tempTabItem.width ) >= x + xOffset ) {
          if ( tabEvent != null ) {
            tabEvent.stateChanged( selectedIndex, c, items.size() );
          }
          selectedIndex = c;
          focusSelectedItem();
          return;
        }
      }
      return;
    }
    dragXStart = -1;
    gObject.pointerPressed( x, y );
  }

  public void pointerReleased( int x, int y ) {
    if ( dragXStart == -1 ) {
      gObject.pointerReleased( x, y );
    }
  }

  public boolean pointerDragged( int x, int y ) {
    if ( dragXStart != -1 ) {
      xOffset = dragXStart - x;
      if ( xOffset < 0 ) {
        xOffset = 0;
      }
      if ( totalWidth > width ) {
        if ( xOffset >= totalWidth - width + 1 ) {
          xOffset = totalWidth - width + 1;
        }
      } else {
        xOffset = 0;
      }
      return true;
    }
    return gObject.pointerDragged( x, y );
  }

  public void focusLeft() {
    if ( selectedIndex - 1 >= 0 ) {
      if ( tabEvent != null ) {
        tabEvent.stateChanged( selectedIndex, --selectedIndex, items.size() );
      } else {
        selectedIndex--;
      }
    }
    tempTabItem = ( (TabItem) items.elementAt( selectedIndex ) );
    if ( tempTabItem.x - xOffset < 0 ) {
      xOffset = tempTabItem.x;
    }
  }

  public void focusRight() {
    if ( selectedIndex + 1 < items.size() ) {
      if ( tabEvent != null ) {
        tabEvent.stateChanged( selectedIndex, ++selectedIndex, items.size() );
      } else {
        selectedIndex++;
      }
    }
    tempTabItem = ( (TabItem) items.elementAt( selectedIndex ) );
    if ( tempTabItem.x + tempTabItem.width - xOffset + 1 > width ) {
      xOffset = tempTabItem.x + tempTabItem.width - width + 1;
    }
  }

  public void focusSelectedItem() {
    tempTabItem = ( (TabItem) items.elementAt( selectedIndex ) );
    if ( tempTabItem.x - xOffset < 0 ) {
      xOffset = tempTabItem.x;
    }
    if ( tempTabItem.x + tempTabItem.width - xOffset + 1 > width ) {
      xOffset = tempTabItem.x + tempTabItem.width - width + 1;
    }
  }

  public boolean switchTabTo( int tabIndex ) {
    /** Checking state **/
    if ( tabIndex == selectedIndex ) {
      return false;
    }
    /** Checking for range **/
    if ( tabIndex >= 0 && tabIndex < items.size() ) {
      /** Sending event **/
      if ( tabEvent != null ) {
        tabEvent.stateChanged( selectedIndex, tabIndex, items.size() );
      }
      selectedIndex = tabIndex;
      if ( totalWidth > width ) {
        TabItem tabItem = (TabItem) items.elementAt( tabIndex );
        /** Empty space **/
        if ( totalWidth - xOffset < width ) {
          xOffset = totalWidth - width;
        }
        /** Right corner **/
        if ( tabItem.x + tabItem.width + 1 > xOffset + width ) {
          xOffset = tabItem.x + tabItem.width - width + 1;
        }
        /** Left corner **/
        if ( tabItem.x < xOffset ) {
          xOffset = tabItem.x;
        }
      } else {
        xOffset = 0;
      }
      return true;
    }
    return false;
  }

  /**
   * Check and correct selectedIndex for items range
   * Returns true if selectedIndex modified, even returns false
   * @return boolean
   */
  public boolean validateSelection() {
    /** Checking for items empty **/
    if ( items.isEmpty() ) {
      selectedIndex = -1;
      return true;
    }
    /** Index range **/
    if ( selectedIndex < 0 ) {
      selectedIndex = 0;
      switchTabTo( selectedIndex );
      return true;
    } else if ( selectedIndex >= items.size() ) {
      selectedIndex = items.size() - 1;
      switchTabTo( selectedIndex );
      return true;
    }
    return false;
  }
}
