package com.tomclaw.images;

import java.io.IOException;
import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Splitter {

  protected static Image tempImage;
  protected static Hashtable hashtable = new Hashtable();
  public static int imageMaxSize = 0;

  /**
   * Split an image to image group
   * @param fileName
   * @return
   */
  public static ImageGroup splitImage( String fileName ) {
    return splitImage( fileName, true );
  }

  /**
   * Split an image to image group
   * @param fileName
   * @param isSizeIndex 
   * @return
   */
  public static ImageGroup splitImage( String fileName, boolean isSizeIndex ) {
    ImageGroup imageGroup = new ImageGroup();
    try {
      /** Loading image file **/
      tempImage = Image.createImage( fileName );
      /** Creating images group oject **/
      imageGroup.size = tempImage.getHeight();
      imageGroup.images = new Image[ tempImage.getWidth() / imageGroup.size ];
      /** Checking and updating maximum image size **/
      if ( isSizeIndex && imageGroup.size > imageMaxSize ) {
        imageMaxSize = imageGroup.size;
      }
      /** Cutting images **/
      for ( int c = 0; c < imageGroup.images.length; c++ ) {
        imageGroup.images[c] = Image.createImage( tempImage, c * imageGroup.size, 0, imageGroup.size, imageGroup.size, Sprite.TRANS_NONE );
      }
      hashtable.put( String.valueOf( fileName.hashCode() ), imageGroup );
    } catch ( IOException ex ) {
    }
    tempImage = null;
    return imageGroup;
  }

  /**
   * Output an image to graphics
   * @param g
   * @param imageGroup
   * @param imageIndex
   */
  public static int drawImage( Graphics g, ImageGroup imageGroup, int imageIndex, int x, int y, boolean isYCenter ) {
    if ( imageIndex >= imageGroup.images.length || imageIndex < 0 ) {
      return 0;
    }
    try {
      g.drawImage( imageGroup.images[imageIndex], x, ( isYCenter ? y - imageGroup.size / 2 : y ), Graphics.TOP | Graphics.LEFT );
    } catch ( NullPointerException ex1 ) {
      return 0;
    }
    return imageGroup.size;
  }

  public static int drawImage( Graphics g, int imageFileHash, int imageIndex, int x, int y, boolean isYCenter ) {
    try {
      /** Integer.toString is eating heap. Fix it **/
      return drawImage( g, ( ImageGroup ) hashtable.get( String.valueOf( imageFileHash ) ), imageIndex, x, y, isYCenter );
    } catch ( NullPointerException ex1 ) {
      return 0;
    }
  }

  public static int drawImage( Graphics g, String imageLink, int x, int y, boolean isYCenter ) {
    int linkDivIndex = imageLink.lastIndexOf( ':' );
    if ( linkDivIndex == -1 ) {
      return 0;
    }
    /* TODO: Integer.parseInt() eats memory */
    return drawImage( g, Integer.parseInt( imageLink.substring( 0, linkDivIndex ) ), Integer.parseInt( imageLink.substring( linkDivIndex + 1 ) ), x, y, isYCenter );
  }

  /**
   * Requesting image group by image link of format: "<fileName.hashCode()>:<imageIndex>"
   * @param imageLink
   * @return
   */
  public static ImageGroup getImageGroup( String imageLink ) {
    int linkDivIndex = imageLink.lastIndexOf( ':' );
    if ( linkDivIndex == -1 ) {
      return ( ImageGroup ) hashtable.get( imageLink );
    }
    return ( ImageGroup ) hashtable.get( imageLink.substring( 0, linkDivIndex ) );
  }

  public static ImageGroup getImageGroup( int imageFileHash ) {
    return ( ImageGroup ) hashtable.get( String.valueOf( imageFileHash ) );
  }
}
