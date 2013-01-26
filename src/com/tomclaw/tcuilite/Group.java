package com.tomclaw.tcuilite;

import com.tomclaw.images.Splitter;
import com.tomclaw.utils.DrawUtil;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Group implements GObject {

  public String name = null;
  public RecordStore recordStore;
  public GroupRmsRenderer groupRmsRenderer = null;
  public Vector items = null;
  public int x = 0;
  public int y = 0;
  public int width = 0;
  public int height = 0;
  public int yOffset = 0;
  public int selectedRow = 0;
  public int selectedColumn = 0;
  public int l_selectedLevel = 0;
  static Image _plus;
  static Image minus;
  public int columnCount = 1;
  public int minWeight = -3;
  public int maxWeight = 0;
  public boolean isShowGroups = true;
  public boolean isHideEmptyGroups = false;
  /**
   * Runtime
   */
  public int startIndex = 0;
  public int finlIndex = 0;
  private int lineCounter = 0;
  private int columnMarker = 0;
  public int totalItemsCount = 0;
  public int prevYDrag = -1;
  public boolean isScrollAction = false;
  public boolean isPointerAction = false;
  private GroupChild tempGroupChild;
  private GroupHeader tempGroupHeader;
  public int selectedRealGroup = -1;
  public int selectedRealIndex = -1;
  private int t_paintCount = 0;
  public GroupEvent actionPerformedEvent = null;
  private boolean retryRepaint = false;
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
  public int itemHeight;
  private int scrollStart;
  private int scrollHeight;
  public int imageOffset = 0;
  /**
   * Images
   */
  public int[] imageLeftFileHash;
  public int[] imageRightFileHash;

  public Group() {
    items = new Vector();
    try {
      _plus = Image.createImage( "/res/group00_img.png" );
      minus = Image.createImage( "/res/group01_img.png" );
    } catch ( IOException ex ) {
    }
  }

  public Group( String fileName ) {
    this();
    openRecordStore( fileName );
  }

  public void addHeader( GroupHeader groupHeader ) {
    this.items.addElement( groupHeader );
  }

  public void repaint( Graphics g ) {
    repaint( g, 0, 0 );
  }

  public void repaint( Graphics g, int paintX, int paintY ) {
    itemHeight = Theme.font.getHeight() + Theme.upSize * 2;
    g.setColor( backColor );
    g.fillRect( paintX + x, paintY + y, width - Theme.scrollWidth, height );
    /** Range **/
    startIndex = ( yOffset / itemHeight );
    finlIndex = startIndex + height / itemHeight;
    /** Counters **/
    lineCounter = -1;
    t_paintCount = 0;
    /** Paint cycle **/
    int t_childsCount = 0;
    int pseudoRealIndex;
    /** Checking for group policy **/
    if ( isShowGroups ) {
      for ( int c = 0; c < items.size(); c++ ) {
        tempGroupHeader = ( GroupHeader ) getElement( c );
        t_childsCount = tempGroupHeader.getChildsCount();
        if ( tempGroupHeader.isGroupVisible && !( isHideEmptyGroups && t_childsCount == 0 ) ) {
          drawItem( g, paintX, paintY, tempGroupHeader.title, null, null, true, tempGroupHeader.isCollapsed, c, -1, -1, -1, false );
        }
        tempGroupHeader.row = lineCounter;
        tempGroupHeader.column = columnMarker;
        if ( !( tempGroupHeader.isCollapsed && tempGroupHeader.isGroupVisible ) && tempGroupHeader.isItemsVisible
                && t_childsCount > 0 ) {
          pseudoRealIndex = 0;
          for ( int w = minWeight; w <= maxWeight; w++ ) {
            for ( int i = 0; i < t_childsCount; i++ ) {
              tempGroupChild = ( GroupChild ) tempGroupHeader.childs.elementAt( i );
              if ( tempGroupChild.weight == w ) {
                drawItem( g, paintX, paintY, tempGroupChild.title,
                        tempGroupChild.imageLeftIndex, tempGroupChild.imageRightIndex,
                        false, false, c, pseudoRealIndex, i, t_childsCount, tempGroupChild.isBold );
                pseudoRealIndex++;
              }
            }
          }
        }
      }
    } else {
      pseudoRealIndex = 0;
      for ( int w = minWeight; w <= maxWeight; w++ ) {
        for ( int c = 0; c < items.size(); c++ ) {
          tempGroupHeader = ( GroupHeader ) getElement( c );
          if ( tempGroupHeader.isItemsVisible ) {
            t_childsCount += tempGroupHeader.getChildsCount();
            for ( int i = 0; i < tempGroupHeader.getChildsCount(); i++ ) {
              tempGroupChild = ( GroupChild ) tempGroupHeader.childs.elementAt( i );
              if ( tempGroupChild.weight == w ) {
                drawItem( g, paintX, paintY, tempGroupChild.title,
                        tempGroupChild.imageLeftIndex, tempGroupChild.imageRightIndex,
                        false, false, c, pseudoRealIndex, i, t_childsCount, tempGroupChild.isBold );
                pseudoRealIndex++;
              }
            }
          }
        }
      }
    }
    totalItemsCount = lineCounter + 1;
    /** Scroll **/
    g.setColor( scrollBack );
    g.fillRect( paintX + x + width - Theme.scrollWidth, paintY + y, Theme.scrollWidth, height );
    if ( totalItemsCount * itemHeight > height ) {
      scrollStart = height * yOffset / ( totalItemsCount * itemHeight );
      scrollHeight = height * height / ( totalItemsCount * itemHeight );
      DrawUtil.fillHorizontalGradient( g, paintX + x + width - Theme.scrollWidth, paintY + y + scrollStart, Theme.scrollWidth, scrollHeight, scrollGradFrom, scrollGradTo );
      if ( scrollHeight > 6 ) {
        g.setColor( scrollFixShadow );
        g.fillRect( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 1, Theme.scrollWidth - 2, 5 );
        g.setColor( scrollFix );
        g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 - 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 - 2 );
        g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 );
        g.drawLine( paintX + x + width - Theme.scrollWidth + 1, paintY + y + scrollStart + scrollHeight / 2 + 2, paintX + x + width - 2, paintY + y + scrollStart + scrollHeight / 2 + 2 );
      }
      g.setColor( scrollBorder );
      g.drawRect( paintX + x + width - Theme.scrollWidth - 1, paintY + y + height * yOffset / ( totalItemsCount * itemHeight ), Theme.scrollWidth, height * height / ( totalItemsCount * itemHeight ) );
      g.drawLine( paintX + x + width - Theme.scrollWidth - 1, paintY + y, paintX + x + width - Theme.scrollWidth - 1, paintY + y + height - 1 );
    } else {
      g.setColor( scrollBorder );
      g.drawLine( paintX + x + width - Theme.scrollWidth - 1, paintY + y, paintX + x + width - Theme.scrollWidth - 1, paintY + y + height - 1 );
    }
    /** Checking group position **/
    if ( selectedRow >= totalItemsCount ) {
      selectedRow = totalItemsCount - 1;
      retryRepaint = true;
    }
    if ( yOffset != 0 && totalItemsCount * itemHeight - yOffset < height ) {
      if ( totalItemsCount * itemHeight > height ) {
        yOffset = totalItemsCount * itemHeight - height;
      } else {
        yOffset = 0;
      }
      retryRepaint = true;
    }
    if ( retryRepaint ) {
      retryRepaint = false;
      repaint( g, paintX, paintY );
    }
  }

  public void drawItem( Graphics g, int paintX, int paintY, String title,
          int[] imageLeftIndex, int[] imageRightIndex,
          boolean isHeader, boolean isCollapsed, int realGroup, int realIndex,
          int sequenceIndex, int groupItemsCount, boolean isBold ) {
    if ( !isBold ) {
      g.setFont( Theme.font );
    } else {
      g.setFont( Theme.titleFont );
    }
    int objX, objWidth;
    int objY;
    /** Multicolumn **/
    if ( ( realIndex == -1 || columnCount == 1
            || ( ( groupItemsCount - 1 ) == realIndex
            && ( realIndex - columnCount
            * ( ( int ) ( realIndex / columnCount ) ) + 1 )
            < columnCount ) ) ) {
      objWidth = width - 2 - Theme.scrollWidth;
    } else {
      objWidth = ( width - 2 - Theme.scrollWidth ) / columnCount;
    }
    if ( realIndex % columnCount == 0 || realIndex == -1 || columnCount == 1 ) {
      objX = paintX + x;
      lineCounter++;
    } else {
      objX = paintX + x + objWidth * ( realIndex - columnCount
              * ( realIndex / columnCount ) ) + 1;
    }
    /**  Applying **/
    columnMarker = ( realIndex - columnCount * ( realIndex / columnCount ) );
    /**  Drawing visible objects **/
    objY = paintY + y + lineCounter * itemHeight - yOffset;
    if ( objY + itemHeight >= 0 && objY - ( paintY + y ) < height ) {
      t_paintCount++;

      g.setColor( hrLine );
      g.drawLine( paintX + x, objY + itemHeight,
              paintX + x + width, objY + itemHeight );

      if ( lineCounter == selectedRow
              && ( columnMarker == selectedColumn || isHeader
              || ( groupItemsCount - 1 == realIndex
              && columnMarker + 1 < columnCount ) ) ) {
        selectedRealGroup = realGroup;
        selectedRealIndex = sequenceIndex;

        DrawUtil.fillVerticalGradient( g, objX, objY, objWidth, itemHeight,
                selectedGradFrom, selectedGradTo );
        g.setColor( selectedUpOutline );
        g.drawLine( objX, objY, objX + objWidth, objY );
        g.setColor( selectedBottomOutline );
        g.drawLine( objX, objY + itemHeight, objX + objWidth,
                objY + itemHeight );
      } else {
        g.setColor( Group.backColor );
        g.fillRect( objX, objY + 1, objWidth, itemHeight - 1 );
      }
      /** Drawing images **/
      imageOffset = 0;
      if ( imageLeftFileHash != null && imageLeftFileHash.length > 0
              && imageLeftIndex != null && imageLeftIndex.length > 0 ) {
        for ( int c = 0; c < imageLeftFileHash.length; c++ ) {
          if ( c >= imageLeftIndex.length ) {
            break;
          }
          if ( imageLeftIndex[c] != -1 ) {
            imageOffset += Splitter.drawImage( g, imageLeftFileHash[c], imageLeftIndex[c],
                    objX + Theme.upSize + 1 + imageOffset, objY + 1 + itemHeight / 2, true );
            if ( imageOffset > 0 ) {
              imageOffset++;
            }
          }
        }
        if ( imageOffset > 0 ) {
          imageOffset += Theme.upSize;
        }
      }
      /** Drawing text **/
      g.setColor( foreColor );
      g.drawString( title, objX + Theme.upSize + 1 + ( isHeader ? ( _plus.getWidth() + Theme.upSize ) : 0 ) + imageOffset, objY + 1 + ( itemHeight - Theme.font.getHeight() ) / 2, Graphics.TOP | Graphics.LEFT );
      if ( isHeader ) {
        g.drawImage( isCollapsed ? _plus : minus, objX + Theme.upSize + 1, objY + 1 + ( itemHeight - _plus.getHeight() ) / 2, Graphics.TOP | Graphics.LEFT );
      }
      imageOffset = 0;
      if ( imageRightFileHash != null && imageRightFileHash.length > 0
              && imageRightIndex != null && imageRightIndex.length > 0 ) {
        for ( int c = 0; c < imageRightFileHash.length; c++ ) {
          if ( c >= imageRightIndex.length ) {
            break;
          }
          if ( imageRightIndex[c] != -1 ) {
            if ( imageOffset == 0 ) {
              imageOffset = Splitter.getImageGroup( imageRightFileHash[c] ).size;
            }
            imageOffset += Splitter.drawImage( g, imageRightFileHash[c], imageRightIndex[c],
                    objX + objWidth - 1 - imageOffset, objY + 1 + itemHeight / 2, true );
            if ( imageOffset > 0 ) {
              imageOffset++;
            }
          }
        }
      }
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
    isPointerAction = false;
    if ( items.isEmpty() ) {
      return;
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.UP ) {
      selectedRow--;
      if ( selectedRow < 0 ) {
        selectedRow = totalItemsCount - 1;
        if ( totalItemsCount * itemHeight > height ) {
          yOffset = totalItemsCount * itemHeight - height;
        }
        return;
      }
      if ( selectedRow - 1 < startIndex ) {
        yOffset = selectedRow * itemHeight;
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.DOWN ) {
      selectedRow++;
      if ( selectedRow >= totalItemsCount ) {
        selectedRow = 0;
        yOffset = 0;
        return;
      }
      if ( selectedRow >= finlIndex ) {
        yOffset = ( selectedRow + 1 ) * itemHeight - height;
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.LEFT ) {
      selectedColumn--;
      if ( selectedColumn < 0 ) {
        selectedColumn = columnCount - 1;
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.RIGHT ) {
      selectedColumn++;
      if ( selectedColumn >= columnCount ) {
        selectedColumn = 0;
      }
    }
    if ( Screen.getExtGameAct( keyCode ) == Screen.FIRE && selectedRealGroup != -1 ) {
      tempGroupHeader = ( GroupHeader ) getElement( selectedRealGroup );
      if ( selectedRealIndex != -1 ) {
        tempGroupChild = ( GroupChild ) tempGroupHeader.childs.elementAt( selectedRealIndex );
        if ( actionPerformedEvent != null ) {
          actionPerformedEvent.actionPerformed( tempGroupChild );
        }
        tempGroupChild.actionPerformed();
      } else {
        tempGroupHeader.isCollapsed = !tempGroupHeader.isCollapsed;
        tempGroupHeader.actionPerformed();
      }
    }
  }

  public void keyReleased( int keyCode ) {
  }

  public void keyRepeated( int keyCode ) {
    keyPressed( keyCode );
  }

  public void pointerPressed( int x, int y ) {
    isPointerAction = true;
    if ( items.isEmpty() ) {
      return;
    }
    if ( x < this.x || y < this.y || x > this.x + width || y > this.y + height ) {
      return;
    }
    if ( x > this.x + width - Theme.scrollWidth ) {
      isScrollAction = true;
      return;
    } else {
      isScrollAction = false;
      if ( isSelectedState && selectedRealGroup != -1 ) {
        if ( selectedRow == ( yOffset + y - this.y ) / itemHeight && selectedColumn == columnCount * x / ( width - Theme.scrollWidth ) ) {
          tempGroupHeader = ( GroupHeader ) getElement( selectedRealGroup );
          if ( selectedRealIndex != -1 ) {
            tempGroupChild = ( GroupChild ) tempGroupHeader.childs.elementAt( selectedRealIndex );
            if ( actionPerformedEvent != null ) {
              actionPerformedEvent.actionPerformed( tempGroupChild );
            }
            tempGroupChild.actionPerformed();
          } else {
            tempGroupHeader.isCollapsed = !tempGroupHeader.isCollapsed;
            tempGroupHeader.actionPerformed();
          }
          return;
        }
        isSelectedState = false;
      }
      selectedRow = ( yOffset + y - this.y ) / itemHeight;
      selectedColumn = columnCount * x / ( width - Theme.scrollWidth );
    }
    isSelectedState = true;
  }

  public void pointerReleased( int x, int y ) {
    prevYDrag = -1;
  }

  public boolean pointerDragged( int x, int y ) {
    if ( items.isEmpty() ) {
      return false;
    }
    if ( isScrollAction ) {
      scrollStart = y - this.y - scrollHeight / 2;
      yOffset = scrollStart * ( totalItemsCount * itemHeight ) / height;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > ( totalItemsCount ) * itemHeight - height ) {
        yOffset = ( totalItemsCount ) * itemHeight - height;
      } else {
        return true;
      }
      return false;
    } else if ( totalItemsCount * itemHeight > height ) {
      if ( prevYDrag == -1 ) {
        prevYDrag = yOffset + y;
        return true;
      }
      yOffset = prevYDrag - y;
      if ( yOffset < 0 ) {
        yOffset = 0;
      } else if ( yOffset > ( totalItemsCount ) * itemHeight - height ) {
        yOffset = ( totalItemsCount ) * itemHeight - height;
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

  private boolean openRecordStore( String fileName ) {
    try {
      recordStore = RecordStore.openRecordStore( fileName, false );
      items.setSize( recordStore.getNumRecords() );
      return true;
    } catch ( RecordStoreException ex ) {
      recordStore = null;
      return false;
    }
  }

  public GroupHeader getElement( int index ) {
    if ( items.elementAt( index ) != null ) {
      return ( GroupHeader ) items.elementAt( index );
    } else if ( recordStore != null ) {
      try {
        byte[] abyte0 = recordStore.getRecord( index + 1 );
        GroupHeader groupHeader = groupRmsRenderer.getRmsGroupHeader( abyte0 );
        items.setElementAt( groupHeader, index );
        return groupHeader;
      } catch ( Throwable ex ) {
      }
      return null;
    }
    return null;
  }
}
