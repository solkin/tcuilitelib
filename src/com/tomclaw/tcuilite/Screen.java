package com.tomclaw.tcuilite;

import com.tomclaw.images.ImageGroup;
import com.tomclaw.images.Splitter;
import com.tomclaw.utils.DrawUtil;
import java.util.Hashtable;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Screen extends Canvas {

  /**
   * Constants
   */
  public static final int KEY_CODE_LEFT_MENU = 1000001;
  public static final int KEY_CODE_RIGHT_MENU = 1000002;
  public static final int KEY_CODE_BACK_BUTTON = 1000003;
  public static final int KEY_CODE_UNKNOWN = 1000004;
  public static final int REPAINT_STATE_PLAIN = 0x00;
  public static final int REPAINT_STATE_SMILE = 0x01;
  public static final int REPAINT_STATE_WAITING = 0xFF;
  public static final String waitGroupPath = "/res/wait_group_img.png";
  public static final int waitGroupHash = waitGroupPath.hashCode();
  /**
   * Window
   */
  public Window activeWindow = null;
  public Window t_nextWindow = null;
  public static Screen screen = null;
  public MIDlet midlet = null;
  public static Runtime runtime;
  public TextBox textBox;
  public Field field = null;
  public long startTime = 0;
  public int mSecDelay = 200;
  public boolean d_isForvard = false;
  private boolean isWaitScreenState = false;
  public Hashtable hotkeys = new Hashtable();
  /**
   * Pointer actions
   */
  public boolean isPointerKinetic = false;
  public int actionSpeedX = 0; // Pixels/msec
  public int actionSpeedY = 0; // Pixels/msec
  public long prevTime = 0;
  private Thread thread = null;
  public boolean isPointerEvents = false;
  public int prevTouchX = -1, prevTouchY = -1;
  public int touchDelta = 10;
  public boolean isDragAccepted = false;
  public boolean isSwitchMode = false;
  public boolean isSlideMode = false;
  public boolean isSlideAwaiting = false;
  public boolean isSlideRight = false;
  public boolean isSlideLeft = false;
  public int x_pointStart = 0;
  public int x_pointValue = 0;
  public int slideStep = 15;
  public boolean isDirectScroll = false;
  /**
   * Runtime
   */
  public int repaintState = REPAINT_STATE_PLAIN;
  public Image wait;
  public static Graphics lGraphics;
  public Image lCache;
  public Image rCache;
  public long lastActivity;
  /**
   * Wait screen
   */
  private int waitIndex;
  private Thread waitThread;
  private long waitRepaintTime;
  private ImageGroup waitGroup;
  private int waitDelay = 100;

  /**
   * Constructor
   */
  public Screen( MIDlet midlet ) {
    this.midlet = midlet;
    screen = Screen.this;
    runtime = Runtime.getRuntime();
    isPointerEvents = hasPointerEvents();
    setFullScreenMode( true );
    textBox = new javax.microedition.lcdui.TextBox( "title", "text", 1024, TextField.ANY );
    textBox.addCommand( new Command( "OK", Command.OK, 1 ) );
    textBox.addCommand( new Command( "Cancel", Command.CANCEL, 1 ) );
    textBox.setCommandListener( new CommandListener() {

      public void commandAction( Command c, Displayable d ) {
        if ( c.getCommandType() == Command.OK ) {
          Screen.this.getField().setCaption( textBox.getString() );
        } else if ( c.getCommandType() == Command.CANCEL ) {
        }
        Screen.screen.showGraph();
        Screen.screen.textBox.setString( "" );
        Screen.screen.field = null;
      }
    } );
    waitGroup = Splitter.splitImage( waitGroupPath, false );
    updateLastActivity();
  }

  public void setField( Field field ) {
    this.field = field;
  }

  public Field getField() {
    return field;
  }

  public void showTemp() {
    Display.getDisplay( midlet ).setCurrent( textBox );
  }

  public void showGraph() {
    setFullScreenMode( true );
    Display.getDisplay( midlet ).setCurrent( this );
  }

  public void show() {
    Display.getDisplay( midlet ).setCurrent( this );
    DrawUtil.rgbData = new int[ ( getWidth() > getHeight() ? getWidth() : getHeight() ) ];
  }

  public void setActiveWindow( Window window ) {
    startTime = 0;
    d_isForvard = !d_isForvard;
    isSwitchMode = true;
    t_nextWindow = window;
    t_nextWindow.prepareGraphics();
    repaint();
  }

  public void setWaitScreenState( boolean isWaitScreenState ) {
    if ( this.isWaitScreenState != isWaitScreenState ) {
      this.isWaitScreenState = isWaitScreenState;
      if ( isWaitScreenState ) {
        waitIndex = 0;
        waitRepaintTime = 0;
        waitThread = new Thread() {

          public void run() {
            while ( Screen.this.isWaitScreenState ) {
              repaint();
              try {
                /** Waiting until second repaint **/
                sleep( waitDelay );
              } catch ( InterruptedException ex ) {
              }
            }
          }
        };
        waitThread.setPriority( Thread.MIN_PRIORITY );
        waitThread.start();
      } else {
        try {
          /** Waiting for thread **/
          waitThread.join();
        } catch ( InterruptedException ex ) {
        }
      }
      /** Repainting **/
      repaint();
    }
  }

  public boolean getWaitScreenState() {
    return isWaitScreenState;
  }

  /**
   * Paint
   */
  public void paint( Graphics g ) {
    try {
      if ( lCache == null
              || ( lCache.getWidth() != getWidth()
              || lCache.getHeight() != getHeight() ) ) {
        lCache = Image.createImage( getWidth(), getHeight() );
        lGraphics = lCache.getGraphics();
      }
      if ( isWaitScreenState ) {
        if ( System.currentTimeMillis() - waitRepaintTime >= 100 ) {
          g.drawImage( lCache, 0, 0, Graphics.TOP | Graphics.LEFT );
          Splitter.drawImage( g, waitGroupHash, waitIndex,
                  ( getWidth() - waitGroup.size ) / 2, getHeight() / 2, true );
          /** Calculating wait screen index **/
          waitIndex++;
          if ( waitIndex == waitGroup.images.length ) {
            waitIndex = 0;
          }
          waitRepaintTime = System.currentTimeMillis();
        }
        return;
      }
      if ( repaintState == REPAINT_STATE_SMILE ) {
        repaintState = REPAINT_STATE_PLAIN;
        if ( activeWindow.directDraw_background != null ) {
          activeWindow.directDraw_background.paint( g );
        }
        activeWindow.paint( lGraphics );
        g.drawImage( lCache, 0, 0, Graphics.TOP | Graphics.LEFT );
        if ( activeWindow.directDraw_afterAll != null ) {
          activeWindow.directDraw_afterAll.paint( g );
        }
        return;
      }
      if ( isSwitchMode ) {
        if ( activeWindow != null && Settings.SCREEN_SHOW_ANIMATION ) {
          long time = System.currentTimeMillis();
          if ( startTime == 0 ) {
            startTime = time;
          }
          int percent = 100 - ( int ) ( 100 * ( time - startTime ) / mSecDelay );
          if ( percent > 100 || percent < 0 ) {
            switchWindow( lGraphics );
            return;
          }
          if ( d_isForvard ) {
            t_nextWindow.paint( lGraphics, ( screen.getWidth() * ( d_isForvard ? ( 100 - percent ) : percent ) ) / 100 - screen.getWidth(), 0, true );
            activeWindow.paint( lGraphics, ( screen.getWidth() * ( d_isForvard ? ( 100 - percent ) : percent ) ) / 100, 0, true );
          } else {
            activeWindow.paint( lGraphics, ( screen.getWidth() * ( d_isForvard ? ( 100 - percent ) : percent ) ) / 100 - screen.getWidth(), 0, true );
            t_nextWindow.paint( lGraphics, ( screen.getWidth() * ( d_isForvard ? ( 100 - percent ) : percent ) ) / 100, 0, true );
          }
          g.drawImage( lCache, 0, 0, Graphics.TOP | Graphics.LEFT );
        } else {
          switchWindow( null );
          return;
        }
        repaint();
      } else if ( isSlideMode ) {
        if ( x_pointValue == 0 || x_pointStart == 0 ) {
          return;
        }
        if ( ( x_pointStart - x_pointValue > 0 && activeWindow.s_nextWindow != null )
                || ( x_pointStart - x_pointValue < 0 && activeWindow.s_prevWindow != null ) ) {
          if ( ( x_pointStart - x_pointValue > 0 && ( lCache == null || rCache == null ) )
                  || ( isSlideLeft && x_pointStart - x_pointValue > 0 ) ) {
            rCache = Image.createImage( getWidth(), getHeight() );
            activeWindow.paint( lCache.getGraphics() );
            activeWindow.s_nextWindow.paint( rCache.getGraphics() );
            isSlideRight = true;
            isSlideLeft = false;
          } else if ( ( x_pointStart - x_pointValue < 0 && ( lCache == null || rCache == null ) )
                  || ( isSlideRight && x_pointStart - x_pointValue < 0 )/*
                   * && !activeWindow.s_prevWindow.isPainted
                   */ ) {
            rCache = Image.createImage( getWidth(), getHeight() );
            activeWindow.s_prevWindow.paint( rCache.getGraphics() );
            activeWindow.paint( lCache.getGraphics() );
            isSlideRight = false;
            isSlideLeft = true;
          }
          if ( x_pointStart - x_pointValue >= getWidth() ) {
            activeWindow = activeWindow.s_nextWindow;
            t_nextWindow = null;
            isSlideMode = false;
            rCache = null;
            isSlideLeft = false;
            isSlideRight = false;
            activeWindow.windowActivated();
            repaint();
            return;
          } else if ( x_pointValue - x_pointStart >= getWidth() ) {
            activeWindow = activeWindow.s_prevWindow;
            t_nextWindow = null;
            isSlideMode = false;
            rCache = null;
            isSlideLeft = false;
            isSlideRight = false;
            activeWindow.windowActivated();
            repaint();
            return;
          }

          g.drawImage( ( ( x_pointStart - x_pointValue > 0 ) ? rCache : lCache ), ( x_pointValue - x_pointStart ) + ( ( x_pointStart - x_pointValue > 0 ) ? getWidth() : 0 ), 0, Graphics.TOP | Graphics.LEFT );
          g.drawImage( ( ( x_pointStart - x_pointValue > 0 ) ? lCache : rCache ), ( x_pointValue - x_pointStart ) - ( ( x_pointStart - x_pointValue > 0 ) ? 0 : getWidth() ), 0, Graphics.TOP | Graphics.LEFT );

        }
      } else if ( activeWindow != null ) {
        if ( activeWindow.directDraw_background != null ) {
          activeWindow.directDraw_background.paint( g );
        }
        activeWindow.paint( lGraphics );
        g.drawImage( lCache, 0, 0, Graphics.TOP | Graphics.LEFT );
        if ( activeWindow.dialog != null ) {
          if ( Settings.DIALOG_SHOW_ANIMATION ) {
            if ( activeWindow.isShowingDialog ) {
              activeWindow.dialog.yOffset =
                      getHeight() - ( int ) ( getHeight() * ( System.currentTimeMillis() - activeWindow.startTime ) / activeWindow.mSecDelay );
              if ( activeWindow.dialog.yOffset <= 0 ) {
                activeWindow.dialog.yOffset = 0;
                activeWindow.dialog.paint( g );
                g.setClip( 0, 0, getWidth(), getHeight() );
                if ( activeWindow.directDraw_afterAll != null ) {
                  activeWindow.directDraw_afterAll.paint( g );
                }
                return;
              }
            } else {
              activeWindow.dialog.yOffset =
                      0 - ( int ) ( getHeight() * ( System.currentTimeMillis() - activeWindow.startTime ) / activeWindow.mSecDelay );
              if ( activeWindow.dialog.yOffset <= -getHeight() / 2 - activeWindow.dialog.height ) {
                activeWindow.dialog = null;
              }
            }
          }
          if ( activeWindow.dialog != null ) {
            activeWindow.dialog.paint( g );
            g.setClip( 0, 0, getWidth(), getHeight() );
            if ( activeWindow.directDraw_afterAll != null ) {
              activeWindow.directDraw_afterAll.paint( g );
            }
          }
          if ( !Settings.DIALOG_SHOW_ANIMATION ) {
            return;
          }
          repaint();
        } else {
          if ( activeWindow.directDraw_afterAll != null ) {
            activeWindow.directDraw_afterAll.paint( g );
          }
        }
      }
    } catch ( Throwable ex ) {
      System.out.println( "Screen: paint - " + ex.getMessage() );
    }
  }

  public void switchWindow( Graphics g ) {
    activeWindow = t_nextWindow;
    t_nextWindow = null;
    isSwitchMode = false;
    if ( g != null ) {
      activeWindow.paint( g, 0, 0, true );
    }
    activeWindow.windowActivated();
    repaint();
  }

  /**
   * Called when a key is pressed.
   */
  protected void keyPressed( int keyCode ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      if ( Screen.getExtGameAct( keyCode ) == Screen.KEY_CODE_BACK_BUTTON ) {
        /** System hotkey - Back **/
        if ( activeWindow != null ) {
          if ( activeWindow.soft.isLeftPressed || activeWindow.soft.isRightPressed ) {
            activeWindow.soft.setLeftSoftPressed( false );
            activeWindow.soft.setRightSoftPressed( false );
          } else if ( activeWindow.s_prevWindow != null ) {
            setActiveWindow( activeWindow.s_prevWindow );
          }
          return;
        }
      }
      /** Hot keys here **/
      if ( hotkeys.containsKey( Integer.toString( keyCode ) )
              && ( activeWindow != null && !( activeWindow.soft.isLeftPressed || activeWindow.soft.isRightPressed ) ) ) {
        ( ( Thread ) hotkeys.get( Integer.toString( keyCode ) ) ).run();
      }
      activeWindow.keyPressed( keyCode );
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: keyPressed - " + ex.getMessage() );
    }
  }

  /**
   * Called when a key is released.
   */
  protected void keyReleased( int keyCode ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      activeWindow.keyReleased( keyCode );
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: keyReleased - " + ex.getMessage() );
    }
  }

  /**
   * Called when a key is repeated (held down).
   */
  protected void keyRepeated( int keyCode ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      activeWindow.keyRepeated( keyCode );
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: keyRepeated - " + ex.getMessage() );
    }
  }

  /**
   * Called when the pointer is dragged.
   */
  protected void pointerDragged( int x, int y ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      if ( ( Math.abs( prevTouchX - x ) > touchDelta
              || Math.abs( prevTouchY - y ) > touchDelta ) && !isSlideMode ) {
        if ( Math.abs( prevTouchY - y ) > slideStep ) {
          isSlideAwaiting = false;
        }
        if ( isDirectScroll || Math.abs( prevTouchX - x ) == 0 || ( Math.abs( prevTouchY - y ) * 100 / Math.abs( prevTouchX - x ) ) > 40 ) {
          isDragAccepted = true;
          activeWindow.pointerDragged( x, y );
        } else if ( isSlideAwaiting ) {
          isSlideMode = true;
          isDragAccepted = false;
          x_pointStart = x;
        }
      } else if ( isSlideMode ) {
        x_pointValue = x;
      }
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: pointerDragged - " + ex.getMessage() );
    }
  }

  /**
   * Called when the pointer is pressed.
   */
  protected void pointerPressed( int x, int y ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      isPointerKinetic = false; // Reset
      isSlideMode = false; // Reset
      isDirectScroll = false;
      isSlideLeft = false;
      isSlideRight = false;
      rCache = null;
      x_pointStart = 0;
      x_pointValue = 0;
      actionSpeedY = 0;
      actionSpeedX = 0;
      prevTime = System.currentTimeMillis();
      prevTouchX = x;
      prevTouchY = y;
      isSlideAwaiting = true;
      if ( thread != null ) {
        try {
          thread.join();
        } catch ( InterruptedException ex ) {
        }
      }
      activeWindow.pointerPressed( x, y );
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: pointerPressed - " + ex.getMessage() );
    }
  }

  /**
   * Called when the pointer is released.
   */
  protected void pointerReleased( final int x, final int y ) {
    updateLastActivity();
    try {
      if ( isWaitScreenState ) {
        return;
      }
      if ( System.currentTimeMillis() != prevTime ) {
        /** Pixel / second **/
        actionSpeedX = ( int ) ( 1000 * ( x - prevTouchX ) / ( System.currentTimeMillis() - prevTime ) );
        actionSpeedY = ( int ) ( 1000 * ( y - prevTouchY ) / ( System.currentTimeMillis() - prevTime ) );
      }
      if ( !isSlideMode && !isDragAccepted ) {
        /** There was no drag **/
        if ( isSlideMode ) {
          new Thread() {

            public void run() {
              autoSlide();
            }
          }.start();
        } else {
          activeWindow.pointerReleased( x, y );
          screen.repaint();
        }
        return;
      }
      if ( Math.abs( actionSpeedX ) > 100 || Math.abs( actionSpeedY ) > 100 ) {
        isPointerKinetic = true;
        thread = new Thread() {

          public void run() {
            int x1 = x, y1 = y;
            long time = System.currentTimeMillis();
            while ( isPointerKinetic && ( Math.abs( actionSpeedX ) > 100 || Math.abs( actionSpeedY ) > 100 ) ) {
              if ( Math.abs( actionSpeedX ) > 100 && System.currentTimeMillis() != time ) {
                x1 += actionSpeedX / ( 1000 / ( System.currentTimeMillis() - time ) );
                actionSpeedX -= 10 * actionSpeedX / Math.abs( actionSpeedX );
              }
              if ( Math.abs( actionSpeedY ) > 100 && System.currentTimeMillis() != time ) {
                y1 += actionSpeedY / ( 1000 / ( System.currentTimeMillis() - time ) );
                actionSpeedY -= 10 * actionSpeedY / Math.abs( actionSpeedY );
              }
              time = System.currentTimeMillis();
              if ( isSlideMode ) {
                x_pointValue = x1;
                screen.repaint();
              } else if ( x_pointValue != 0 ) {
                isPointerKinetic = false;
                return;
              } else {
                if ( !activeWindow.pointerDragged( x1, y1 ) ) {
                  screen.repaint();
                  break;
                }
              }
              screen.repaint();
              try {
                sleep( 10 );
              } catch ( InterruptedException ex ) {
              }
            }
            if ( isSlideMode ) {
              autoSlide();
              return;
            }
            isPointerKinetic = false;
            activeWindow.pointerReleased( x1, y1 );
          }
        };
        thread.start();
      } else {
        if ( isSlideMode ) {
          new Thread() {

            public void run() {
              autoSlide();
            }
          }.start();
        } else {
          activeWindow.pointerReleased( x, y );
        }
      }
      screen.repaint();
    } catch ( Throwable ex ) {
      System.out.println( "Screen: pointerReleased - " + ex.getMessage() );
    }
  }

  public void autoSlide() {
    int increment = getWidth() / 60;
    boolean directionLeft = ( x_pointValue < x_pointStart );
    int cycleStart;
    int cycleFinl;
    boolean isSlideForward;
    if ( Math.abs( x_pointValue - x_pointStart ) > getWidth() / 2 ) {
      /** Must cycle to help user **/
      cycleStart = directionLeft ? 0 : x_pointValue - x_pointStart;
      cycleFinl = ( directionLeft ? getWidth() - ( x_pointStart - x_pointValue ) : getWidth() );
      isSlideForward = true;
    } else {
      /** Must cycle back **/
      cycleStart = directionLeft ? x_pointValue : x_pointStart;
      cycleFinl = ( directionLeft ? x_pointStart : x_pointValue );
      isSlideForward = false;
    }

    for ( int c = cycleStart;
            c <= cycleFinl; c += increment ) {
      if ( isSlideForward ) {
        x_pointValue = ( directionLeft ? cycleFinl - c - ( getWidth() - x_pointStart ) : c + ( x_pointStart ) );
      } else {
        x_pointValue = ( directionLeft ? c : cycleStart + cycleFinl - c );
      }
      screen.repaint();
      increment += 1;
      try {
        Thread.sleep( 10 );
      } catch ( InterruptedException ex ) {
      }
    }
    if ( isSlideForward && isSlideMode ) {
      if ( directionLeft && activeWindow.s_nextWindow != null ) {
        activeWindow = activeWindow.s_nextWindow;
      } else if ( activeWindow.s_prevWindow != null ) {
        activeWindow = activeWindow.s_prevWindow;
      }
      t_nextWindow = null;
    }
    isSlideMode = false;
    rCache = null;
    isSlideLeft = false;
    isSlideRight = false;
    activeWindow.windowActivated();
    repaint();
  }

  /**
   * Game action
   */
  public static int getExtGameAct( int keyCode ) {
    try {
      int gameAct = screen.getGameAction( keyCode );
      switch ( gameAct ) {
        case Canvas.UP:
        case Canvas.DOWN:
        case Canvas.LEFT:
        case Canvas.RIGHT:
          return gameAct;
      }
    } catch ( Throwable ex ) {
    }
    try {
      String strCode = screen.getKeyName( keyCode ).toLowerCase();
      if ( "soft1".equals( strCode ) || "soft 1".equals( strCode ) || "soft_1".equals( strCode ) || "softkey 1".equals( strCode ) || "sk2(left)".equals( strCode )
              || strCode.startsWith( "left soft" ) ) {
        return KEY_CODE_LEFT_MENU;
      }
      if ( "soft2".equals( strCode ) || "soft 2".equals( strCode ) || "soft_2".equals( strCode ) || "softkey 4".equals( strCode ) || "sk1(right)".equals( strCode )
              || strCode.startsWith( "right soft" ) ) {
        return KEY_CODE_RIGHT_MENU;
      }
      if ( "on/off".equals( strCode ) || "back".equals( strCode ) ) {
        return KEY_CODE_BACK_BUTTON;
      }
      if ( "trackball".equals( strCode ) || "enter".equals( strCode ) ) {
        return Canvas.FIRE;
      }
    } catch ( Throwable ex ) {
    }
    switch ( keyCode ) {
      case -6:
      case -21:
      case 21:
      case 105:
      case -202:
      case 113:
      case 57345:
        return KEY_CODE_LEFT_MENU;
      case -7:
      case -22:
      case 22:
      case 106:
      case -203:
      case 112:
      case 57346:
        return KEY_CODE_RIGHT_MENU;
      case -11:
        return KEY_CODE_BACK_BUTTON;
    }
    try {
      int gameAct = screen.getGameAction( keyCode );
      if ( gameAct > 0 ) {
        return gameAct;
      }
    } catch ( Throwable ex ) {
    }
    return KEY_CODE_UNKNOWN;
  }

  public static boolean isNumPadKey( int keyCode ) {
    return ( ( keyCode >= Canvas.KEY_NUM0 ) && ( keyCode <= Canvas.KEY_NUM9 ) )
            || ( keyCode == Canvas.KEY_STAR )
            || ( keyCode == Canvas.KEY_POUND );
  }

  public void repaint( int repaintState ) {
    if ( repaintState == REPAINT_STATE_SMILE && this.repaintState == REPAINT_STATE_PLAIN ) {
      this.repaintState = repaintState;
      repaint();
    } else if ( repaintState == REPAINT_STATE_PLAIN ) {
      repaint();
    }
  }

  private void updateLastActivity() {
    lastActivity = System.currentTimeMillis();
  }
}
