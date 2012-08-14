package com.tomclaw.tcuilite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class KeyEvent {

  public int keyCode;
  public boolean isTotalCapture;
  public String description;

  public KeyEvent( int keyCode, String description, boolean isTotalCapture ) {
    this.keyCode = keyCode;
    this.description = description;
    this.isTotalCapture = isTotalCapture;
  }

  public void actionPerformed() {
  }
}
