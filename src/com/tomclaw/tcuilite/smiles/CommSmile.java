package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public interface CommSmile {

  public Image getFrameARGB( int frameIndex );

  public int getWidth();

  public int getHeight();

  public int[] getFramesDelay();

  public String[] getSmileDefinitions();
}
