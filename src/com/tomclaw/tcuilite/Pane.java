package com.tomclaw.tcuilite;

import com.tomclaw.tcuilite.smiles.Smiles;
import com.tomclaw.utils.DrawUtil;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Pane implements GObject {

  public String name = null;
  public Vector items = null;
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public int yOffset = 0;
  public int moveStep = 10;
  /**
   * Runtime
   */
  public int prevYDrag = -1;
  public boolean isScrollAction = false;
  private PaneObject paneObject;
  private int startIndex = 0;
  private int finlIndex = 0;
  public PaneObject actionObject;
  public Thread animationThread = null;
  private int focusedIndex = -1;
  public int psvLstFocusedIndex = -1;
  public PaneEvent actionPerformedEvent = null;
  public boolean isSelectedState = false;
  /**
   * Colors
   */
  public static int foreColor = 0x555555;
  public static int backColor = 0xFFFFFF;
  public static int hrLine = 0xDDDDDD;
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
  /**
   * Sizes
   */
  private int scrollStart;
  private int scrollHeight;
  private int yLocation = 0;
  public int maxHeight;

  /**
   * Pane initializing. Window must be placed here only if Pane containts
   * ChatItem
   *
   * @param window
   */
  public Pane( final Window window, boolean isAnimated ) {
    items = new Vector();
    moveStep = Theme.font.getHeight();
    if ( isAnimated && Smiles.smilesType == 0x00 ) {
      animationThread = new Thread() {

        public void run() {
          while ( true ) {
            try {
              Thread.sleep( 100 );
            } catch ( InterruptedException ex ) {
            }
            if ( prevYDrag != -1 ) {
              continue;
            }
            if ( Screen.screen.activeWindow != null && Screen.screen.activeWindow.equals( window )
                && !Screen.screen.isSwitchMode && !Screen.screen.isSlideMode
                && !Screen.screen.activeWindow.soft.isLeftPressed && !Screen.screen.activeWindow.soft.isRightPressed
                && Screen.screen.activeWindow.dialog == null ) {
              Screen.screen.repaint( Screen.REPAINT_STATE_SMILE );
            }
          }
        }
      };
      animationThread.setPriority( Thread.MIN_PRIORITY );
      animationThread.start();
    }
  }

  public void repaint( Graphics g ) {
    repaint( g, 0, 0 );
  }

  public void repaint( Graphics g, int paintX, int paintY ) {
    g.setFont( Theme.font );
    g.setColor( backColor );
    g.fillRect( paintX + x, paintY + y, width, height );
    startIndex = -1;
    finlIndex = -1;
    yLocation = 0;
    for ( int c = 0; c < items.size(); c++ ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( !paneObject.getVisible() ) {
        continue;
      }
      paneObject.setSize( width - Theme.scrollWidth - 2, paneObject.getHeight() );
      paneObject.setLocation( paintX + x, paintY + y + yLocation - yOffset );
      if ( focusedIndex >= 0 ) {
        if ( c == focusedIndex && paneObject.getFocusable() ) {
          paneObject.setFocused( true );
          psvLstFocusedIndex = c;
          focusedIndex = -2;
        } else {
          paneObject.setFocused( false );
        }
      } else if ( focusedIndex == -2 ) {
        paneObject.setFocused( false );
      } else if ( focusedIndex == -1 ) {
        if ( paneObject.getFocused() && paneObject.getFocusable() ) {
          focusedIndex = c;
          psvLstFocusedIndex = c;
        } else {
          paneObject.setFocused( false );
        }
      }
      if ( paintY + y + yLocation + paneObject.getHeight() - yOffset > 0
          && yLocation - yOffset < height ) {
        if ( startIndex == -1 ) {
          startIndex = c;
        }
        paneObject.repaint( g );
      } else if ( startIndex >= 0 && finlIndex == -1 ) {
        finlIndex = c;
      }
      yLocation += paneObject.getHeight() + 1;
    }
    focusedIndex = -1;
    maxHeight = yLocation;
    if ( finlIndex == -1 ) {
      finlIndex = items.size() - 1;
    }
    /**
     * Scroll
     */
    if ( Theme.scrollWidth > 0 && !items.isEmpty() ) {
      g.setColor( scrollBack );
      g.fillRect( paintX + x + width - Theme.scrollWidth, paintY + y, Theme.scrollWidth, height );
      if ( maxHeight > height ) {
        scrollStart = height * yOffset / maxHeight;
        scrollHeight = height * height / maxHeight;
        DrawUtil.fillHorizontalGradient( g, paintX + x + width - Theme.scrollWidth, paintY + y + scrollStart, Theme.scrollWidth, scrollHeight, scrollGradFrom, scrollGradTo );
        if ( scrollHeight > 6 ) {
          g.setColor( scrollFixShadow );
          g.fillRect( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 1, Theme.scrollWidth - 2, 5 );
          g.setColor( scrollFix );
          g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 - 2 );
          g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 );
          g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 + 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 + 2 );
        }
      }
      g.setColor( scrollBorder );
      g.drawRect( paintX + x + width - Theme.scrollWidth - 1, paintY + y + height * yOffset / maxHeight, Theme.scrollWidth, height * height / maxHeight );
      g.drawLine( paintX + x + width - Theme.scrollWidth - 1, paintY + y, paintX + x + width - Theme.scrollWidth - 1, paintY + y + height - 1 );
    }
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
    if ( items.isEmpty() && Screen.getExtGameAct( keyCode ) != Screen.FIRE && actionPerformedEvent != null ) {
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      focusUp();
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      focusDown();
    } else {
      if ( !items.isEmpty() ) {
        paneObject = getFocusedPaneObject();
      } else {
        paneObject = null;
      }
      if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE && actionPerformedEvent != null ) {
        actionPerformedEvent.actionPerformed( paneObject );
      }
      if ( paneObject != null ) {
        paneObject.keyPressed( keyCode );
      }
    }
  }

  public void keyReleased( int keyCode ) {
    if ( items.isEmpty() ) {
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
    } else {
      paneObject = getFocusedPaneObject();
      if ( paneObject != null ) {
        paneObject.keyReleased( keyCode );
      }
    }
  }

  public void keyRepeated( int keyCode ) {
    if ( items.isEmpty() ) {
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      focusUp();
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      focusDown();
    } else {
      paneObject = getFocusedPaneObject();
      if ( paneObject != null ) {
        paneObject.keyRepeated( keyCode );
      }
    }
  }

  public void pointerPressed( int x, int y ) {
    actionObject = null;
    if ( !items.isEmpty() ) {
      if ( x < this.x || y < this.y || x > this.x + width || y > this.y + height ) {
        return;
      }
      if ( x > this.x + width - Theme.scrollWidth ) {
        isScrollAction = true;
      } else {
        isScrollAction = false;
        actionObject = getFocusedPaneObject( x, y );
        if ( actionObject != null ) {
          actionObject.pointerPressed( x, y );
        }
      }
    }
  }

  public void pointerReleased( int x, int y ) {
    if ( items.isEmpty() ) {
      if ( actionPerformedEvent != null ) {
        actionPerformedEvent.actionPerformed( actionObject );
      }
      return;
    } else if ( prevYDrag == -1 ) {
      if ( isSelectedState ) {
        if ( actionPerformedEvent != null ) {
          actionPerformedEvent.actionPerformed( actionObject );
        }
      }
    }
    isSelectedState = true;
    prevYDrag = -1;
    if ( actionObject != null ) {
      actionObject.pointerReleased( x, y );
      actionObject = null;
    }
  }

  public boolean pointerDragged( int x, int y ) {
    if ( actionObject != null ) {
      actionObject.pointerDragged( x, y );
      actionObject = null;
    }
    if ( items.isEmpty() || maxHeight < height ) {
      return false;
    }
    if ( isScrollAction ) {
      scrollStart = y - this.y - scrollHeight / 2;
      yOffset = scrollStart * maxHeight / height;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > maxHeight - height ) {
        yOffset = maxHeight - height;
      } else {
        return true;
      }
      return false;
    } else if ( maxHeight > height ) {
      if ( prevYDrag == -1 ) {
        prevYDrag = yOffset + y;
        return true;
      }
      yOffset = prevYDrag - y;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > maxHeight - height ) {
        yOffset = maxHeight - height;
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

  /**
   * Own methods
   */
  public void focusDown() {
    if ( items.isEmpty() ) {
      return;
    }
    int t_FocusedIndex = -1;
    for ( int c = 0; c <= finlIndex; c++ ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getFocusable() && paneObject.getVisible() ) {
        if ( paneObject.getFocused() ) {
          t_FocusedIndex = c;
        } else if ( t_FocusedIndex != -1 ) {
          if ( c < startIndex || c > finlIndex ) {
            break;
          }
          if ( c == finlIndex && paneObject.getY() > y + height ) {
            break;
          }
          paneObject.setFocused( true );
          if ( paneObject.getY() + paneObject.getHeight() > y + height ) {
            /**
             * Object not visible
             */
            yOffset -= y - paneObject.getY() + ( ( paneObject.getHeight() > height ) ? 0 : ( height - paneObject.getHeight() - 1 ) );
          }
          paneObject = ( PaneObject ) items.elementAt( t_FocusedIndex );
          paneObject.setFocused( false );
          paneObject = null;
          break;
        }
      }
    }
    if ( paneObject != null && maxHeight > height ) {
      yOffset += ( ( maxHeight - height - yOffset ) > moveStep ) ? moveStep : ( maxHeight - height - yOffset );
      if ( yOffset > maxHeight - height ) {
        yOffset = maxHeight - height;
      }
    }
  }

  public void focusUp() {
    if ( items.isEmpty() ) {
      return;
    }
    int t_FocusedIndex = -1;
    for ( int c = items.size() - 1; c >= startIndex; c-- ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getFocusable() && paneObject.getVisible() ) {
        if ( paneObject.getFocused() ) {
          t_FocusedIndex = c;
        } else if ( t_FocusedIndex != -1 ) {
          if ( c < startIndex || c > finlIndex ) {
            break;
          }
          paneObject.setFocused( true );
          if ( paneObject.getY() - y < 0 ) {
            yOffset -= y - paneObject.getY() + ( ( paneObject.getHeight() > height ) ? ( height - paneObject.getHeight() - 1 ) : 0 );
          }
          paneObject = ( PaneObject ) items.elementAt( t_FocusedIndex );
          paneObject.setFocused( false );
          paneObject = null;
          break;
        }
      }
    }
    if ( paneObject != null && maxHeight > height ) {
      yOffset -= ( yOffset > moveStep ) ? moveStep : yOffset;
    }
  }

  public PaneObject getFocusedPaneObject() {
    for ( int c = startIndex; c <= finlIndex; c++ ) {
      if ( c >= items.size() ) {
        break;
      }
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getFocusable() && paneObject.getVisible() ) {
        if ( paneObject.getFocused() ) {
          return paneObject;
        }
      }
    }
    return null;
  }

  public PaneObject getFocusedPaneObject( int x, int y ) {
    for ( int c = startIndex; c <= finlIndex; c++ ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getFocusable() && paneObject.getVisible() ) {
        if ( ( paneObject.getX() <= x ) && ( paneObject.getX() + paneObject.getWidth() >= x )
            && ( paneObject.getY() <= y ) && ( paneObject.getY() + paneObject.getHeight() >= y ) ) {
          if ( !paneObject.getFocused() ) {
            for ( int i = 0; i < items.size(); i++ ) {
              if ( i != c ) {
                paneObject = ( PaneObject ) items.elementAt( i );
                paneObject.setFocused( false );
              }
            }
            isSelectedState = false;
          }
          paneObject = ( PaneObject ) items.elementAt( c );
          if ( !paneObject.getFocused() ) {
            paneObject.setFocused( true );
          }
          return paneObject;
        }
      }
    }
    return null;
  }

  public void addItem( PaneObject paneObject ) {
    items.addElement( paneObject );
  }

  public void setFocused( int index ) {
    this.focusedIndex = index;
  }

  public int getFocused() {
    return this.focusedIndex;
  }
}
