package com.tomclaw.tcuilite;

import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ObjectGroup {

  private String name;
  public Vector items;

  public ObjectGroup() {
    items = new Vector();
    name = null;
  }

  public void placeObject( PaneObject paneObject ) {
    items.addElement( paneObject );
    paneObject.setObjectGroup( this );
  }

  public boolean contains( PaneObject paneObject ) {
    return items.contains( paneObject );
  }

  public PaneObject getObjectByName( String name ) {
    PaneObject paneObject;
    for ( int c = 0; c < items.size(); c++ ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getName() != null
          && paneObject.getName().equals( name ) ) {
        return paneObject;
      }
    }
    return null;
  }

  public PaneObject getObjectByValue( String value ) {
    PaneObject paneObject;
    for ( int c = 0; c < items.size(); c++ ) {
      paneObject = ( PaneObject ) items.elementAt( c );
      if ( paneObject.getStringValue() != null
          && paneObject.getStringValue().equals( value ) ) {
        return paneObject;
      }
    }
    return null;
  }

  public void removeObject( PaneObject paneObject ) {
    items.removeElement( paneObject );
    paneObject.removeObjectGroup();
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
