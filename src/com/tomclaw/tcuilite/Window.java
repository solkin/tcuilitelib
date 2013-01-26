package com.tomclaw.tcuilite;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Window {

  public String name = null;
  public Screen screen = null;
  private GObject gObject = null;
  public Header header = null;
  public Soft soft = null;
  public Window s_prevWindow = null;
  public Window s_nextWindow = null;
  public boolean isPainted = false;
  public Dialog dialog = null;
  public long startTime = System.currentTimeMillis();
  public int mSecDelay = 500;
  public boolean isShowingDialog = false;
  public Hashtable keyEvents;
  public KeyEvent capKeyEvent = null;
  public Enumeration e;
  /**
   * Direct draw
   */
  public DirectDraw directDraw_background = null;
  public DirectDraw directDraw_beforePopup = null;
  public DirectDraw directDraw_afterAll = null;

  /**
   * constructor
   */
  public Window( Screen screen ) {
    this.screen = screen;
  }

  public void prepareGraphics() {
    paint( Screen.lGraphics, screen.lCache.getWidth(), 0 );
  }

  /**
   * paint
   */
  public void paint( Graphics g ) {
    paint( g, 0, 0 );
  }

  public void paint( Graphics g, int paintX, int paintY ) {
    paint( g, paintX, paintY, false );
  }

  public void paint( Graphics g, int paintX, int paintY, boolean isBlock ) {
    if ( ( dialog != null && !isPainted ) || dialog == null || isBlock ) {
      if ( header != null && header.getWidth() != screen.getWidth() ) {
        header.setLocation( 0, 0 );
        header.setSize( screen.getWidth(), 0 );
      }
      if ( soft != null && Soft.getWidth() != screen.getWidth() ) {
        soft.setLocation( 0, screen.getHeight() - soft.getHeight() );
        soft.setSize( screen.getWidth(), soft.getHeight() );
      }
      if ( gObject != null ) {
        if ( gObject.getWidth() != screen.getWidth()
            || gObject.getHeight() != screen.getHeight()
            - ( header != null ? header.getHeight() : 0 )
            - ( soft != null ? soft.getHeight() : 0 ) ) {
          gObject.setSize( screen.getWidth(), screen.getHeight()
              - ( header != null ? header.getHeight() : 0 )
              - ( soft != null ? soft.getHeight() : 0 ) );
          gObject.setLocation( 0, ( header != null ? header.getHeight() : 0 ) );
        }
        gObject.repaint( g, paintX, paintY );
      }
      if ( header != null ) {
        header.paint( g, paintX, paintY );
      }
      if ( directDraw_beforePopup != null ) {
        directDraw_beforePopup.paint( g, paintX, paintY );
      }
      /**
       * Here was soft painting
       */
      if ( soft != null ) {
        soft.paint( g, paintX, paintY );
      }
      isPainted = true;
    }
  }

  public void showDialog( final Dialog dialog ) {
    Window.this.dialog = dialog;
    if ( Settings.DIALOG_SHOW_ANIMATION ) {
      Window.this.dialog.yOffset = screen.getHeight() / 2;
    } else {
      Window.this.dialog.yOffset = 0;
    }
    startTime = System.currentTimeMillis();
    isShowingDialog = true;
    screen.repaint( Screen.REPAINT_STATE_PLAIN );
  }

  public void closeDialog() {
    if ( Settings.DIALOG_SHOW_ANIMATION ) {
      Window.this.dialog.yOffset = 0;
    } else {
      Window.this.dialog = null;
      return;
    }
    startTime = System.currentTimeMillis();
    isShowingDialog = false;
    screen.repaint( Screen.REPAINT_STATE_PLAIN );
  }

  /**
   * Called when a key is pressed.
   */
  protected void keyPressed( int keyCode ) {
    if ( dialog != null ) {
      dialog.keyPressed( keyCode );
      return;
    }
    if ( ( soft == null || !( soft.isLeftPressed || soft.isRightPressed ) ) && keyEvents != null ) {
      e = keyEvents.elements();
      KeyEvent keyEvent;
      while ( e.hasMoreElements() ) {
        keyEvent = ( KeyEvent ) e.nextElement();
        if ( keyEvent.keyCode == keyCode ) {
          keyEvent.actionPerformed();
          if ( keyEvent.isTotalCapture ) {
            return;
          } else {
            break;
          }
        }
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_LEFT_MENU ) {
      /** Inverting left trigger **/
      if ( soft.isLeftPressed || !( soft.isLeftPressed || soft.isRightPressed ) ) {
        if ( !soft.isLeftPressed ) {
          soft.leftSoft.actionPerformed();
        }
        if ( !soft.leftSoft.isEmpty() ) {
          soft.setLeftSoftPressed( !soft.isLeftPressed );
        }
      }
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_RIGHT_MENU ) {
      /** Inverting right trigger **/
      if ( soft.isRightPressed || !( soft.isLeftPressed || soft.isRightPressed ) ) {
        if ( !soft.isRightPressed ) {
          soft.rightSoft.actionPerformed();
        }
        if ( !soft.rightSoft.isEmpty() ) {
          soft.setRightSoftPressed( !soft.isRightPressed );
        }
      }
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_RIGHT_MENU ) {
      if ( s_prevWindow != null ) {
        screen.setActiveWindow( s_prevWindow );
      }
    } else if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      soft.keyPressed( keyCode );
    } else {
      gObject.keyPressed( keyCode );
    }
  }

  /**
   * Called when a key is released.
   */
  protected void keyReleased( int keyCode ) {
    if ( capKeyEvent != null ) {
      capKeyEvent.keyCode = keyCode;
      capKeyEvent.actionPerformed();
    }
    if ( dialog != null ) {
      dialog.keyReleased( keyCode );
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_LEFT_MENU ) {
      if ( soft.leftSoft.isEmpty() ) {
        soft.setLeftSoftPressed( false );
      }
    } else if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_RIGHT_MENU ) {
      if ( soft.rightSoft.isEmpty() ) {
        soft.setRightSoftPressed( false );
      }
    } else if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      soft.keyReleased( keyCode );
    } else {
      gObject.keyReleased( keyCode );
    }
  }

  /**
   * Called when a key is repeated (held down).
   */
  protected void keyRepeated( int keyCode ) {
    if ( dialog != null ) {
      dialog.keyRepeated( keyCode );
      return;
    }
    if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      soft.keyRepeated( keyCode );
    } else {
      gObject.keyRepeated( keyCode );
    }
  }

  /**
   * Called when the pointer is dragged.
   */
  protected boolean pointerDragged( int x, int y ) {
    if ( dialog != null ) {
      return dialog.pointerDragged( x, y );
    }
    if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      /** Soft is pressed down **/
      return soft.pointerDragged( x, y );
    }
    return gObject.pointerDragged( x, y );
  }

  /**
   * Called when the pointer is pressed.
   */
  protected void pointerPressed( int x, int y ) {
    if ( dialog != null ) {
      dialog.pointerPressed( x, y );
      return;
    }
    if ( soft != null && ( x >= Soft.getX() && y >= Soft.getY()
        && x < ( Soft.getX() + Soft.getWidth() ) && y < ( Soft.getY() + soft.getHeight() ) ) ) {
      /** Pointer pressed on soft **/
      if ( x <= ( Soft.getX() + Soft.getWidth() / 2 ) ) {
        /** Left soft is pressed (inverting trigger) **/
        if ( soft.isLeftPressed || !( soft.isLeftPressed || soft.isRightPressed ) ) {
          if ( !soft.isLeftPressed ) {
            soft.leftSoft.actionPerformed();
          }
          soft.setLeftSoftPressed( !soft.isLeftPressed );
        }
      } else {
        /** Right soft is pressed (inverting trigger) **/
        if ( soft.isRightPressed || !( soft.isLeftPressed || soft.isRightPressed ) ) {
          if ( !soft.isRightPressed ) {
            soft.rightSoft.actionPerformed();
          }
          soft.setRightSoftPressed( !soft.isRightPressed );
        }
      }
    } else if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      soft.pointerPressed( x, y );
    } else {
      gObject.pointerPressed( x, y );
    }
  }

  /**
   * Called when the pointer is released.
   */
  protected void pointerReleased( int x, int y ) {
    if ( dialog != null ) {
      dialog.pointerReleased( x, y );
      return;
    }
    if ( soft != null && ( soft.isLeftPressed || soft.isRightPressed ) ) {
      /** If any soft is already pressed down **/
      if ( soft.isLeftPressed && soft.leftSoft.isEmpty() ) {
        soft.setLeftSoftPressed( false );
      } else if ( soft.isRightPressed && soft.rightSoft.isEmpty() ) {
        soft.setRightSoftPressed( false );
      } else {
        soft.pointerReleased( x, y );
      }
      return;
    }
    gObject.pointerReleased( x, y );
  }

  public void setGObject( GObject gObject ) {
    this.gObject = gObject;
    this.gObject.setTouchOrientation( screen.isPointerEvents );
  }

  public GObject getGObject() {
    return gObject;
  }

  public void addKeyEvent( KeyEvent keyEvent ) {
    if ( keyEvents == null ) {
      keyEvents = new Hashtable();
    }
    keyEvents.put( keyEvent.description, keyEvent );
  }

  public void removeKeyEvent( String description ) {
    if ( keyEvents == null ) {
      return;
    }
    if ( keyEvents.containsKey( description ) ) {
      keyEvents.remove( keyEvents.get( description ) );
    }
  }

  public void removeAllKeyEvents() {
    if ( keyEvents == null ) {
      return;
    }
    keyEvents.clear();
  }

  public KeyEvent getKeyEvent( String description ) {
    if ( keyEvents != null && keyEvents.containsKey( description ) ) {
      return ( KeyEvent ) keyEvents.get( description );
    }
    return null;
  }
  
  public void windowActivated() {
    
  }
}
