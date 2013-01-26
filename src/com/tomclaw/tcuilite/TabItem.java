package com.tomclaw.tcuilite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class TabItem {

  public String name = null;
  public String title;
  public int imageFileHash;
  public int imageIndex;
  public int x;
  public int width;
  public int extGObjectYOffset = 0;
  public int fillPercent = 100;
  public Label tabLabel;

  public TabItem( String title, int imageFileHash, int imageIndex ) {
    this.title = title;
    this.imageFileHash = imageFileHash;
    this.imageIndex = imageIndex;
  }
}
