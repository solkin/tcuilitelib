package com.tomclaw.tcuilite.smiles;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class SmileLink {

  public int smileIndex = -1;
  /** Variables **/
  public int x, y;
  public int frameIndex = 0;
  public int backColor = 0xFFFFFF;
  public long frameTime = -1;

  public SmileLink( int smileIndex ) {
    this.smileIndex = smileIndex;
  }

  public void updateLocation( int x, int y ) {
    this.x = x;
    this.y = y;
  }

  public void analyzeFrame() {
    if ( Smiles.smilesType == 0x00 ) {
      if ( frameTime != -1 && ( System.currentTimeMillis() - frameTime ) < Smiles.smiles[smileIndex].getFramesDelay()[frameIndex] ) {
        return;
      }
      frameIndex++;
      if ( frameIndex >= Smiles.smiles[smileIndex].getFramesDelay().length ) {
        frameIndex = 0;
      }
      frameTime = System.currentTimeMillis();
    }
  }

  public int getWidth() {
    return Smiles.smiles[smileIndex].getWidth();
  }

  public int getHeight() {
    return Smiles.smiles[smileIndex].getHeight();
  }
}
