package com.tomclaw.tcuilite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class PopupItem extends ListItem {

  public String name = null;
  public Popup subPopup = null;
  public boolean forciblyNotEmpty = false;

  public PopupItem ( String title ) {
    super ( title );
  }

  public PopupItem ( String title, int imageFileHash, int imageIndex ) {
    super ( title, imageFileHash, imageIndex );
  }

  public void setTitle ( String title ) {
    this.title = title;
  }

  public void addSubItem ( PopupItem popupItem ) {
    if ( subPopup == null ) {
      subPopup = new Popup ();
    }
    subPopup.addItem ( popupItem );
  }

  public boolean isEmpty () {
    return forciblyNotEmpty ? false : ( subPopup == null || subPopup.items.isEmpty () );
  }

  public void removeAllChilds () {
    /** Checking for item spresent **/
    if ( !isEmpty () ) {
      /** Removeing all childs **/
      subPopup.items.removeAllElements ();
    }
  }

  /** Invokes when sub items is shown **/
  public void expandPerformed () {
  }
}
