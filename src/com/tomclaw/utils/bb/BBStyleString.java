package com.tomclaw.utils.bb;

import com.tomclaw.tcuilite.smiles.SmileLink;
import com.tomclaw.tcuilite.smiles.Smiles;
import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class BBStyleString {

  public BBStyle style;
  public String string = null;
  public SmileLink smileLink = null;
  public int local_x;
  public int local_y;

  public void paint( Graphics g, int x, int y ) {
    if ( style != null ) {
      g.setFont( style.font );
      g.setColor( style.color );
    }
    if ( string != null ) {
      g.drawString( string, x + local_x, y + local_y, Graphics.TOP | Graphics.LEFT );
    } else if ( smileLink != null ) {
      smileLink.updateLocation( x + local_x, y + local_y );
      Smiles.smiles[smileLink.smileIndex].paint( g, smileLink.x, smileLink.y, smileLink.frameIndex );
      smileLink.analyzeFrame();
    }
  }

  public BBStyleString trim() {
    /** Checking for string is not null **/
    if ( string != null ) {
      /** Trimming string **/
      string = string.trim();
    }
    return this;
  }
}
