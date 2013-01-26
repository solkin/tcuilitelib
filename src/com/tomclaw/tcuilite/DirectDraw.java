package com.tomclaw.tcuilite;

import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public interface DirectDraw {

  public void paint( Graphics g );

  public void paint( Graphics g, int paintX, int paintY );
}
