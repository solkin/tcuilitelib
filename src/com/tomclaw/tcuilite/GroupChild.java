package com.tomclaw.tcuilite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class GroupChild {

  /**
   * Main objects
   */
  public String title;
  public Runnable runnable;
  /**
   * Conditions
   */
  public int weight = 0;
  public boolean isBold = false;
  /**
   * Images of left align
   */
  public int[] imageLeftIndex;
  /**
   * Images of right align
   */
  public int[] imageRightIndex;

  public GroupChild( String title ) {
    this.title = title;
  }

  public void actionPerformed() {
    if ( runnable != null ) {
      runnable.run();
    }
  }
}
