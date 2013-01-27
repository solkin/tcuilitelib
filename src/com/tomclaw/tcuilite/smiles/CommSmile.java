package com.tomclaw.tcuilite.smiles;

import javax.microedition.lcdui.Graphics;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public interface CommSmile {

  public void paint(Graphics g, int x, int y, int frameIndex);

  public int getWidth();

  public int getHeight();

  public int[] getFramesDelay();

  public String[] getSmileDefinitions();
}
