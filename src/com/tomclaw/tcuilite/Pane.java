package com.tomclaw.tcuilite;

import com.tomclaw.tcuilite.smiles.Smiles;
import com.tomclaw.utils.DrawUtil;
import com.tomclaw.utils.LogUtil;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Pane extends Scroll {

  public Vector items = null;
  /** Runtime **/
  private PaneObject paneObject;
  private int startIndex = 0;
  private int finlIndex = 0;
  public PaneObject actionObject;
  public Thread animationThread = null;
  private int focusedIndex = -1;
  public int psvLstFocusedIndex = -1;
  public PaneEvent actionPerformedEvent = null;
  public boolean isSelectedState = false;
  /** Colors **/
  public static int backColor = 0xFFFFFF;
  /** Sizes **/
  private int yLocation = 0;
  public int moveStep = 10;

  /**
   * Pane initializing. Window must be placed here only if Pane contains
   * ChatItem
   *
   * @param window
   */
  public Pane( final Window window, boolean isAnimated ) {
    items = new Vector();
    moveStep = Theme.font.getHeight();
    repaintScrollWidth = Theme.scrollWidth;
    if ( isAnimated && Smiles.smilesType == 0x00 ) {
      animationThread = new Thread() {
        public void run() {
          while ( true ) {
            try {
              Thread.sleep( 100 );
              /** Checking for this object is fully on screen and there is no scrolling **/
              if ( Screen.screen.activeWindow != null && Screen.screen.activeWindow.equals( window )
                      && !Screen.screen.isSwitchMode && !Screen.screen.isSlideMode
                      && !Screen.screen.activeWindow.soft.isLeftPressed && !Screen.screen.activeWindow.soft.isRightPressed
                      && Screen.screen.activeWindow.dialog == null && prevYDrag == -1 ) {
                Screen.screen.repaint( Screen.REPAINT_STATE_SMILE );
              }
            } catch ( InterruptedException ex ) {
              LogUtil.outMessage( "Pane animation exception", true );
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
      paneObject = (PaneObject) items.elementAt( c );
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
    totalHeight = yLocation;
    if ( finlIndex == -1 ) {
      finlIndex = items.size() - 1;
    }
    /** Scroll **/
    super.repaint( g, paintX, paintY );
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
    if ( items.isEmpty() || totalHeight < height ) {
      return false;
    }
    if ( isScrollAction ) {
      scrollStart = y - this.y - scrollHeight / 2;
      yOffset = scrollStart * totalHeight / height;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > totalHeight - height ) {
        yOffset = totalHeight - height;
      } else {
        return true;
      }
      return false;
    } else if ( totalHeight > height ) {
      if ( prevYDrag == -1 ) {
        prevYDrag = yOffset + y;
        return true;
      }
      yOffset = prevYDrag - y;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > totalHeight - height ) {
        yOffset = totalHeight - height;
      } else {
        return true;
      }
      return false;
    }
    return true;
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
      paneObject = (PaneObject) items.elementAt( c );
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
            /** Object not visible **/
            yOffset -= y - paneObject.getY() + ( ( paneObject.getHeight() > height ) ? 0 : ( height - paneObject.getHeight() - 1 ) );
          }
          paneObject = (PaneObject) items.elementAt( t_FocusedIndex );
          paneObject.setFocused( false );
          paneObject = null;
          break;
        }
      }
    }
    if ( paneObject != null && totalHeight > height ) {
      yOffset += ( ( totalHeight - height - yOffset ) > moveStep ) ? moveStep : ( totalHeight - height - yOffset );
      if ( yOffset > totalHeight - height ) {
        yOffset = totalHeight - height;
      }
    }
  }

  public void focusUp() {
    if ( items.isEmpty() ) {
      return;
    }
    int t_FocusedIndex = -1;
    for ( int c = items.size() - 1; c >= startIndex; c-- ) {
      paneObject = (PaneObject) items.elementAt( c );
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
          paneObject = (PaneObject) items.elementAt( t_FocusedIndex );
          paneObject.setFocused( false );
          paneObject = null;
          break;
        }
      }
    }
    if ( paneObject != null && totalHeight > height ) {
      yOffset -= ( yOffset > moveStep ) ? moveStep : yOffset;
    }
  }

  public PaneObject getFocusedPaneObject() {
    for ( int c = startIndex; c <= finlIndex; c++ ) {
      if ( c >= items.size() ) {
        break;
      }
      paneObject = (PaneObject) items.elementAt( c );
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
      paneObject = (PaneObject) items.elementAt( c );
      if ( paneObject.getFocusable() && paneObject.getVisible() ) {
        if ( ( paneObject.getX() <= x ) && ( paneObject.getX() + paneObject.getWidth() >= x )
                && ( paneObject.getY() <= y ) && ( paneObject.getY() + paneObject.getHeight() >= y ) ) {
          if ( !paneObject.getFocused() ) {
            for ( int i = 0; i < items.size(); i++ ) {
              if ( i != c ) {
                paneObject = (PaneObject) items.elementAt( i );
                paneObject.setFocused( false );
              }
            }
            isSelectedState = false;
          }
          paneObject = (PaneObject) items.elementAt( c );
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

  public void setFocused( int focusedIndex ) {
    this.focusedIndex = focusedIndex;
  }

  public int getFocused() {
    return focusedIndex;
  }
}
