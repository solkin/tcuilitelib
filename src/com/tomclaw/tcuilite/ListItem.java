package com.tomclaw.tcuilite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ListItem {

  public String title;
  public String descr;
  public Runnable runnable;
  public int imageFileHash;
  public int imageIndex;

  public ListItem( String title ) {
    this.title = title;
    imageFileHash = 0;
  }

  public ListItem() {
    imageFileHash = 0;
    imageIndex = -1;
    title = "";
    descr = null;
  }

  public ListItem( String title, int imageFileHash, int imageIndex ) {
    this.title = title;
    this.descr = null;
    this.imageFileHash = imageFileHash;
    this.imageIndex = imageIndex;
  }

  public ListItem( String title, String descr, int imageFileHash, int imageIndex ) {
    this.title = title;
    this.descr = descr;
    this.imageFileHash = imageFileHash;
    this.imageIndex = imageIndex;
  }

  public void actionPerformed() {
    if ( runnable != null ) {
      runnable.run();
    }
  }

  public void setActionPerformed( Runnable runnable ) {
    this.runnable = runnable;
  }
}
