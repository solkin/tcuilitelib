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
public class Grid extends Scroll {

  public Vector items = null;
  public int itemWidth = 42;
  public int itemHeight = 42;
  public int focusedRow = 0;
  public int focusedColumn = 0;
  public boolean isShowGrid = false;
  /** Runtime **/
  private int startIndex = 0;
  private int finlIndex = 0;
  private int eqvYOffset = 0;
  private int xStart = 0;
  public int rows = 0;
  public int columns = 0;
  private int finlRow = 0;
  private int finlColumn = 0;
  private PaneObject paneObject;
  public Thread animationThread = null;
  public PaneObject actionObject;
  public PaneEvent actionPerformedEvent = null;
  public boolean isSelectedState = false;
  /** Colors **/
  public static int backColor = 0xFFFFFF;
  public static int hrLine = 0xDDDDDD;

  public Grid( final Window window, boolean isAnimated ) {
    items = new Vector();
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
            } catch ( Throwable ex ) {
              LogUtil.outMessage( "Grid animation exception", true );
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
    if ( g != null ) {
      g.setFont( Theme.font );
      g.setColor( backColor );
      g.fillRect( paintX + x, paintY + y, width, height );
      g.setColor( hrLine );
    }
    columns = ( width - Theme.scrollWidth ) / itemWidth;
    rows = height / itemHeight;
    eqvYOffset = yOffset % itemHeight;
    xStart = ( width - Theme.scrollWidth ) % itemWidth;
    if ( g != null && isShowGrid ) {
      for ( int pX = paintX + x + xStart / 2; pX < paintX + x + width - Theme.scrollWidth; pX += itemWidth ) {
        g.drawLine( pX, paintY + y, pX, paintY + y + height );
      }
      for ( int pY = paintY + y - eqvYOffset; pY < paintY + y + height + itemHeight; pY += itemHeight ) {
        g.drawLine( paintX + xStart / 2, pY, paintX + width - Theme.scrollWidth - xStart / 2, pY );
      }
    }
    startIndex = ( (int) ( yOffset / itemHeight ) ) * ( columns );
    finlIndex = startIndex + Math.min( columns * ( rows + 2 ), items.size() - startIndex );
    for ( int c = startIndex; c < finlIndex; c++ ) {
      paneObject = (PaneObject) items.elementAt( c );
      paneObject.setLocation( paintX + x + xStart / 2 + c % columns * itemWidth, paintY + y - eqvYOffset + ( (int) ( ( c - startIndex ) / columns ) ) * itemHeight );
      if ( c % columns == focusedColumn
              && ( (int) ( c / columns ) ) == focusedRow ) {
        paneObject.setFocused( true );
      } else {
        paneObject.setFocused( false );
      }
      paneObject.setSize( itemWidth, itemHeight );
      if ( g != null ) {
        paneObject.repaint( g );
      }
    }
    finlColumn = ( finlIndex - 1 ) % columns;
    finlRow = ( (int) ( ( finlIndex - 1 ) / columns ) );
    totalHeight = ( ( items.size() - 1 ) / columns + 1 ) * itemHeight;
    /** Scroll **/
    super.repaint( g, paintX, paintY );
  }

  public void keyPressed( int keyCode ) {
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      focusedRow--;
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      focusedRow++;
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.LEFT ) {
      focusedColumn--;
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.RIGHT ) {
      focusedColumn++;
    } else {
      if ( !items.isEmpty() ) {
        actionObject = (PaneObject) items.elementAt( focusedColumn + columns * focusedRow );
      } else {
        actionObject = null;
      }
      if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE && actionPerformedEvent != null ) {
        actionPerformedEvent.actionPerformed( actionObject );
      }
      if ( actionObject != null ) {
        actionObject.keyPressed( keyCode );
      }
    }
    checkFocus();
  }

  public void keyReleased( int keyCode ) {
  }

  public void keyRepeated( int keyCode ) {
    keyPressed( keyCode );
  }

  public void pointerPressed( int x, int y ) {
    actionObject = null;
    if ( items.isEmpty() ) {
      return;
    }
    if ( x < this.x || y < this.y || x > this.x + width || y > this.y + height ) {
      return;
    }
    if ( x > this.x + width - Theme.scrollWidth ) {
      isScrollAction = true;
    } else {
      isScrollAction = false;
      if ( ( x - this.x ) / itemWidth + columns * ( ( y + yOffset - this.y ) / itemHeight ) < items.size() ) {
        actionObject = (PaneObject) items.elementAt( ( x - this.x ) / itemWidth + columns * ( ( y + yOffset - this.y ) / itemHeight ) );

        setFocusedRowColomn( ( ( y + yOffset - this.y ) / itemHeight ),
                ( x - this.x ) / itemWidth );
        if ( isSelectedState ) {
          if ( actionPerformedEvent != null ) {
            actionPerformedEvent.actionPerformed( actionObject );
          }
        }
        if ( actionObject != null ) {
          actionObject.pointerPressed( x, y );
        }
      }
      isSelectedState = true;
    }
  }

  public void pointerReleased( int x, int y ) {
    prevYDrag = -1;
    if ( items.isEmpty() ) {
      return;
    }
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

  public void addItem( PaneObject paneObject ) {
    items.addElement( paneObject );
  }

  public void checkFocus() {
    if ( focusedColumn >= columns ) {
      focusedColumn = 0;
      focusedRow++;
    }
    if ( focusedColumn < 0 ) {
      focusedColumn = columns - 1;
      focusedRow--;
    }
    if ( focusedRow >= finlRow ) {
      focusedRow = finlRow;
      if ( focusedColumn > finlColumn ) {
        focusedColumn = finlColumn;
      }
    }
    if ( focusedRow < 0 ) {
      focusedRow = 0;
    }
    if ( focusedRow * itemHeight + itemHeight > yOffset + height ) {
      yOffset = focusedRow * itemHeight + itemHeight - height;
    }
    if ( focusedRow * itemHeight < yOffset ) {
      yOffset = focusedRow * itemHeight;
    }
    if ( yOffset < 0 ) {
      yOffset = 0;
    }
    if ( yOffset > ( (int) ( items.size() / columns ) + 1 ) * itemHeight - height + 1 ) {
      if ( ( (int) ( items.size() / columns ) + 1 ) * itemHeight - height + 1 > 0 ) {
        yOffset = ( (int) ( items.size() / columns ) + 1 ) * itemHeight - height + 1;
      } else {
        yOffset = 0;
      }
    }
  }

  private void setFocusedRowColomn( int focusedRow, int focusedColumn ) {
    if ( focusedRow != this.focusedRow || focusedColumn != this.focusedColumn ) {
      this.focusedRow = focusedRow;
      this.focusedColumn = focusedColumn;
      isSelectedState = false;
    }
  }
}
