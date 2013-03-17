package com.tomclaw.tcuilite;

import com.tomclaw.utils.DrawUtil;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public abstract class Scroll extends GObject {

  /** Colors **/
  public static int scrollBack = 0xFFFFFF;
  public static int scrollGradFrom = 0xDDDDDD;
  public static int scrollGradTo = 0xAAAAAA;
  public static int scrollBorder = 0xAAAAAA;
  public static int scrollFix = 0x888888;
  public static int scrollFixShadow = 0xDDDDDD;
  /** Runtime **/
  public int repaintScrollWidth = 0;
  public int yOffset = 0;
  protected int totalHeight;
  protected int scrollStart;
  protected int scrollHeight;
  public boolean isScrollAction = false;
  public int prevYDrag = -1;

  public void repaint( Graphics g, int paintX, int paintY ) {
    /** Scroll **/
    if ( g != null && repaintScrollWidth > 0 ) {
      g.setColor( scrollBack );
      g.fillRect( paintX + x + width - repaintScrollWidth, paintY + y, repaintScrollWidth, height );
      if ( totalHeight > height ) {
        scrollStart = height * yOffset / totalHeight;
        scrollHeight = height * height / totalHeight;
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
        g.drawRect( paintX + x + width - repaintScrollWidth - 1, paintY + y + height * yOffset / totalHeight, repaintScrollWidth, height * height / totalHeight );
        g.drawLine( paintX + x + width - repaintScrollWidth - 1, paintY + y, paintX + x + width - repaintScrollWidth - 1, paintY + y + height - 1 );
      } else {
        g.setColor( scrollBorder );
        g.drawLine( paintX + x + width - Theme.scrollWidth - 1, paintY + y, paintX + x + width - Theme.scrollWidth - 1, paintY + y + height - 1 );
      }
    }
  }
  
  public int getTotalHeight() {
    return totalHeight;
  }
}
