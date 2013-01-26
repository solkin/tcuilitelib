package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Soft {

  public String name = null;
  /**
   * Colors
   */
  public static int softLine1 = 0xA575BD;
  public static int softLine2 = 0xEFDBFF;
  public static int softGradFrom = 0xD69EEF;
  public static int softGradTo = 0x5A2C6B;
  public static int fontColor = 0xFFFFFF;
  public static int fontShadow = 0x424542;
  /**
   * Data
   */
  public static int x = 0, y = 0, width = 0, height = 0;
  public PopupItem leftSoft = null;
  public PopupItem rightSoft = null;
  public boolean isLeftPressed = false;
  public boolean isRightPressed = false;
  public static int popupOverlayDividor = 3;
  /**
   * Popup GObject's
   */
  public Vector activePopups = null;
  /**
   * Screens
   */
  public Screen screen;
  /**
   * Runtime
   */
  private int pressedIndex = -1;

  public Soft( Screen screen ) {
    this.screen = screen;
  }

  public void paint( Graphics g, int paintX, int paintY ) {
    g.setFont( Theme.font );
    getHeight();
    g.setColor( softLine1 );
    g.drawLine( paintX + x, paintY + y, paintX + x + width, paintY + y );
    g.setColor( softLine2 );
    g.drawLine( paintX + x, paintY + y + 1, paintX + x + width, paintY + y + 1 );
    DrawUtil.fillVerticalGradient( g, paintX + x, paintY + y + 2, width, height - 2, softGradFrom, softGradTo );
    if ( leftSoft != null ) {
      g.setColor( fontShadow );
      g.drawString( leftSoft.title, paintX + x + Theme.upSize + 1, paintY + y + 2 + Theme.upSize + 1, Graphics.TOP | Graphics.LEFT );
      g.setColor( fontColor );
      g.drawString( leftSoft.title, paintX + x + Theme.upSize, paintY + y + 2 + Theme.upSize, Graphics.TOP | Graphics.LEFT );
      if ( isLeftPressed ) {
        /** Drawing left soft pressed **/
        if ( !leftSoft.isEmpty() && ( activePopups == null || activePopups.isEmpty() ) ) {
          activePopups = new Vector();
          addPopupToActive( leftSoft );
        }
      } else if ( !isRightPressed ) {
        isLeftPressed = false;
        activePopups = null;
      }
    }
    if ( rightSoft != null ) {
      g.setColor( fontShadow );
      g.drawString( rightSoft.title, paintX + x + width - Theme.upSize
              - Theme.font.stringWidth( rightSoft.title ) + 1,
              paintY + y + 2 + Theme.upSize + 1, Graphics.TOP | Graphics.LEFT );
      g.setColor( fontColor );
      g.drawString( rightSoft.title, paintX + x + width - Theme.upSize
              - Theme.font.stringWidth( rightSoft.title ),
              paintY + y + 2 + Theme.upSize, Graphics.TOP | Graphics.LEFT );
      if ( isRightPressed ) {
        /** Drawing right soft pressed **/
        if ( !rightSoft.isEmpty() && ( activePopups == null || activePopups.isEmpty() ) ) {
          activePopups = new Vector();
          addPopupToActive( rightSoft );
        }
      } else if ( !isLeftPressed ) {
        isRightPressed = false;
        activePopups = null;
      }
    }
    paintPopups( g, paintX, paintY );
  }

  public void paintPopups( Graphics g, int paintX, int paintY ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      Popup popup;
      for ( int c = 0; c < activePopups.size(); c++ ) {
        /** Drawing Popup **/
        popup = ( ( Popup ) activePopups.elementAt( c ) );
        popup.repaint( g, paintX, paintY );
        g.setClip( 0, 0, screen.getWidth(), screen.getHeight() );
      }
    }
  }

  public void setLocation( int x, int y ) {
    Soft.x = x;
    Soft.y = y;
  }

  public void setSize( int width, int height ) {
    Soft.width = width;
    Soft.height = height;
  }

  public void setLeftSoftPressed( boolean isPressed ) {
    this.isLeftPressed = isPressed;
  }

  public void setRightSoftPressed( boolean isPressed ) {
    this.isRightPressed = isPressed;
  }

  public static int getX() {
    return x;
  }

  public static int getY() {
    return y;
  }

  public static int getWidth() {
    return width;
  }

  public int getHeight() {
    height = Theme.font.getHeight() + Theme.upSize * 2 + 2;
    return height;
  }

  public void keyPressed( int keyCode ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      ( ( Popup ) activePopups.lastElement() ).keyPressed( keyCode );
      if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE
              || Screen.getExtGameAct( keyCode ) == Screen.RIGHT ) {
        PopupItem popupItem = ( ( PopupItem ) ( ( Popup ) activePopups.lastElement() ).items.elementAt( ( ( Popup ) activePopups.lastElement() ).selectedIndex ) );
        if ( !popupItem.isEmpty() ) {
          /** Opening sub popup **/
          addPopupToActive( popupItem );
        } else {
          popupItem.actionPerformed();
          isLeftPressed = false;
          isRightPressed = false;
        }
      } else if ( Screen.getExtGameAct( keyCode ) == Screen.LEFT ) {
        /** Closing popup **/
        activePopups.removeElementAt( activePopups.size() - 1 );
        if ( activePopups.isEmpty() ) {
          isLeftPressed = false;
          isRightPressed = false;
        }
      }
    }
  }

  public void keyReleased( int keyCode ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      ( ( Popup ) activePopups.lastElement() ).keyReleased( keyCode );
    }
  }

  public void keyRepeated( int keyCode ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      ( ( Popup ) activePopups.lastElement() ).keyRepeated( keyCode );
    }
  }

  public void pointerPressed( int x, int y ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      Popup popup = ( ( Popup ) activePopups.lastElement() );
      if ( x < popup.getX() || y < popup.getY()
              || x > ( popup.getX() + popup.getWidth() )
              || y > ( popup.getY() + popup.getHeight() ) ) {
        /** Closing popup **/
        activePopups.removeElementAt( activePopups.size() - 1 );
        if ( activePopups.isEmpty() ) {
          isLeftPressed = false;
          isRightPressed = false;
        }
      } else {
        popup.pointerPressed( x, y );
        if ( x < ( popup.getX() + popup.getWidth() - popup.repaintScrollWidth ) ) {
          if ( !( ( PopupItem ) popup.items.elementAt( popup.selectedIndex ) ).isEmpty() ) {
            /** Opening sub popup **/
            pressedIndex = popup.selectedIndex;
          }
        }
      }
    }
  }

  public void pointerReleased( int x, int y ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      Popup popup = ( ( Popup ) activePopups.lastElement() );
      if ( x < popup.getX()
              || y < popup.getY()
              || x > ( popup.getX() + popup.getWidth() )
              || y > ( popup.getY() + popup.getHeight() ) ) {
      } else {
        popup.pointerReleased( x, y );
        if ( ( ( PopupItem ) popup.items.elementAt(
                popup.selectedIndex ) ).subPopup != null
                && !( ( PopupItem ) popup.items.elementAt(
                popup.selectedIndex ) ).subPopup.items.isEmpty() ) {
          /** Opening sub popup **/
          if ( pressedIndex == popup.selectedIndex ) {
            addPopupToActive( ( ( PopupItem ) popup.items.elementAt(
                    popup.selectedIndex ) ) );
            pressedIndex = -1;
          }
        }
      }
    }
  }

  public boolean pointerDragged( int x, int y ) {
    if ( activePopups != null && !activePopups.isEmpty() ) {
      boolean toReturn = ( ( Popup ) activePopups.lastElement() ).pointerDragged( x, y );
      pressedIndex = -1;
      return toReturn;
    }
    return false;
  }

  /**
   * Tool methods
   */
  public void addPopupToActive( PopupItem parentPopup ) {
    /** Expanding method invokation **/
    parentPopup.expandPerformed();
    /** Obtain sub popup item **/
    Popup popup = parentPopup.subPopup;
    if ( activePopups.isEmpty() ) {
      popup.setTouchOrientation( screen.isPointerEvents );
      popup.defineSize( screen.getHeight() - height );
      if ( isLeftPressed ) {
        popup.setLocation( x, y - popup.getHeight() );
      } else if ( isRightPressed ) {
        popup.setLocation( x + screen.getWidth() - popup.getWidth() - 1, y - popup.getHeight() );
      }
    } else {
      Popup li_Popup = ( Popup ) activePopups.lastElement();
      for ( int i = 0; i < li_Popup.items.size(); i++ ) {
        PopupItem popupItem = ( PopupItem ) li_Popup.items.elementAt( i );
        if ( !popupItem.isEmpty() && popupItem.subPopup != null ) {
          if ( popupItem.subPopup.equals( popup ) ) {
            int h1 = li_Popup.y + ( i * li_Popup.itemHeight - li_Popup.yOffset );
            popup.setTouchOrientation( screen.isPointerEvents );
            popup.defineSize( screen.getHeight() - height );
            int popupLocationX = 0;
            if ( isLeftPressed ) {
              popupLocationX =
                      li_Popup.x + li_Popup.width / popupOverlayDividor;
              if ( popupLocationX + popup.width > screen.getWidth()
                      && li_Popup.x + li_Popup.width / popupOverlayDividor - popup.width < 0 ) {
                popupLocationX = screen.getWidth() - popup.width;
              } else if ( popupLocationX + popup.width > screen.getWidth() ) {
                popupLocationX = li_Popup.x - popup.width;
              }
            } else if ( isRightPressed ) {
              popupLocationX =
                      li_Popup.x - popup.getWidth() + ( li_Popup.width - li_Popup.width / popupOverlayDividor );
              if ( popupLocationX < 0
                      && popupLocationX + popup.width > screen.getWidth() ) {
                popupLocationX = 0;
              } else if ( popupLocationX < 0 ) {
                popupLocationX = li_Popup.x - li_Popup.width / popupOverlayDividor;
              } else if ( popupLocationX + popup.width > screen.getWidth() ) {
                popupLocationX = li_Popup.x - li_Popup.width / popupOverlayDividor;
              }
            }
            if ( h1 + popup.height > y && h1 > screen.getHeight() / 2 - height ) {
              /**  Placing popup upper && Upper bigger **/
              if ( h1 < popup.height ) {
                /** Placing on zero **/
                popup.setLocation( popupLocationX, screen.getHeight() - height - popup.height );
              } else {
                popup.setLocation( popupLocationX, h1 - popup.height + popup.itemHeight );
              }
            } else {
              /** Placing bottom **/
              if ( screen.getHeight() - height - h1 < popup.height ) {
                popup.setLocation( popupLocationX, screen.getHeight() - height - popup.height );
              } else {
                popup.setLocation( popupLocationX, h1 );
              }
            }
          }
        }
      }
    }
    popup.soft = this;
    activePopups.addElement( popup );
  }
}
