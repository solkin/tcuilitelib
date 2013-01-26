package com.tomclaw.tcuilite;

import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class GroupHeader {

  public String title;
  public boolean isCollapsed = true;
  protected Vector childs = null;
  public Thread thread;
  public boolean isGroupVisible = true;
  public boolean isItemsVisible = true;
  /**
   * Location on the screen
   */
  public int row;
  public int column;

  public GroupHeader ( String title ) {
    this.title = title;
    childs = null;
  }

  public void addChild ( GroupChild groupChild ) {
    if ( childs == null || childs.isEmpty () ) {
      childs = new Vector ();
    }
    childs.addElement ( groupChild );
  }

  public int getChildsCount () {
    if ( childs == null || childs.isEmpty () ) {
      return 0;
    }
    return childs.size ();
  }

  public Vector getChilds () {
    /** Checking for items is null-type **/
    if ( this.childs == null ) {
      /** Creating items vector **/
      this.childs = new Vector ();
    }
    return childs;
  }

  public void setChilds ( Vector childs ) {
    this.childs = childs;
  }

  public boolean isContainBuddy ( GroupChild groupChild ) {
    boolean isContainBuddy = false;
    /** Checking for childs count **/
    if ( getChildsCount () > 0 ) {
      /** Checking for child containing **/
      isContainBuddy = childs.contains ( groupChild );
    }
    return isContainBuddy;
  }

  public void removeElement ( GroupChild groupChild ) {
    /** Checking for childs is null-type **/
    if ( childs != null ) {
      /** Removing specified child **/
      childs.removeElement ( groupChild );
    }
  }
  
  public GroupChild elementAt(int index) {
    /** Checking for childs is null-type **/
    if ( childs != null ) {
      /** Removing specified child **/
      return (GroupChild) childs.elementAt ( index );
    }
    return null;
  }

  public void actionPerformed () {
    if ( thread != null ) {
      thread.run ();
    }
  }
}
