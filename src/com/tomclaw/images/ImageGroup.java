package com.tomclaw.images;

import javax.microedition.lcdui.Image;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ImageGroup {

  public int size;
  public Image images;

  public int getCount() {
    return images.getWidth() / size;
  }
}
