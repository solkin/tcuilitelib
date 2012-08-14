package com.tomclaw.tcuilite;

import java.util.Vector;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class RadioGroup {

  private String name = null;
  /**
   * Radio group data
   */
  Vector objects = new Vector();

  /**
   * RadioGroup constructor
   */
  public RadioGroup() {
  }

  public void addRadio( Radio radio ) {
    radio.radioIndex = objects.size();
    objects.addElement( radio );
    radio.setRadioGroup( this );
  }

  public void removeRadio( Radio radio ) {
    objects.removeElement( radio );
  }

  public void setCombed( Radio radio ) {
    Radio tempRadio;
    for ( int c = 0; c < objects.size(); c++ ) {
      tempRadio = ( Radio ) objects.elementAt( c );
      if ( tempRadio.equals( radio ) ) {
        tempRadio.radioState = true;
        continue;
      }
      tempRadio.radioState = false;
    }
  }

  public void setCombed( int radioIndex ) {
    Radio tempRadio;
    for ( int c = 0; c < objects.size(); c++ ) {
      tempRadio = ( Radio ) objects.elementAt( c );
      if ( tempRadio.radioIndex == radioIndex ) {
        tempRadio.radioState = true;
        continue;
      }
      tempRadio.radioState = false;
    }
  }

  public int getCombed() {
    Radio tempRadio;
    for ( int c = 0; c < objects.size(); c++ ) {
      tempRadio = ( Radio ) objects.elementAt( c );
      if ( tempRadio.radioState ) {
        return c;
      }
    }
    return -1;
  }

  public String getCombedString() {
    Radio tempRadio;
    for ( int c = 0; c < objects.size(); c++ ) {
      tempRadio = ( Radio ) objects.elementAt( c );
      if ( tempRadio.radioState ) {
        return tempRadio.caption;
      }
    }
    return null;
  }
  
  public void setName( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
  
  public Radio getObjectByName( String name ) {
    Radio radio;
    for ( int c = 0; c < objects.size(); c++ ) {
      radio = ( Radio ) objects.elementAt( c );
      if ( radio.getName() != null
          && radio.getName().equals( name ) ) {
        return radio;
      }
    }
    return null;
  }
}
