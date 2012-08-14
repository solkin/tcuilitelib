package com.tomclaw.tcuilite.smiles;

import com.tomclaw.utils.DataUtil;
import com.tomclaw.utils.StringUtil;
import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class Smiles {

  public static String smilesDatapath = "/res/smiles/tcui_smiles.dat";
  public static String smilesPath = "/res/smiles/";
  public static CommSmile[] smiles = null;
  public static Image tempImage;
  public static int averageWidth = 38;
  public static int averageHeight = 38;
  public static int smilesType = -1;

  public static void readSmileData( boolean isBBAdapted ) {
    /*
     * Loading resource file
     */
    DataInputStream is;
    try {
      is = new DataInputStream( Runtime.getRuntime().getClass().getResourceAsStream( smilesDatapath ) );
      if ( is != null ) {
        try {
          smilesType = is.readChar();
          System.out.println( "smilesType = " + smilesType );
          if ( smilesType == 0x00 ) {
            byte[] block;
            int blocksCount = is.readChar();
            smiles = new AnimSmile[ blocksCount ];
            String fileName;
            for ( int i = 0; i < blocksCount; i++ ) {
              int length = is.readChar();
              block = new byte[ length ];
              is.read( block );
              int offset = 0;
              byte fileNameLength = DataUtil.get8( block, offset );
              offset += 1;
              fileName = DataUtil.byteArray2string( block, offset, fileNameLength );
              offset += fileNameLength;
              /** Image info **/
              int framesCount = DataUtil.get16( block, offset );
              // System.out.println("framesCount = " + framesCount);
              offset += 2;
              // System.out.println("fileName = " + fileName + " | framesCount = " + framesCount);

              /** Creating smile oject **/
              AnimSmile animSmile = new AnimSmile();
              animSmile.fileName = fileName;

              /** Reading delays **/
              animSmile.framesDelay = new int[ framesCount ];
              for ( int c = 0; c < framesCount; c++ ) {
                int frameDelay = DataUtil.get16( block, offset );
                animSmile.framesDelay[c] = frameDelay;
                offset += 2;
              }
              /** Reading definitions **/
              byte definitionCount = DataUtil.get8( block, offset );
              offset += 1;
              animSmile.smileDefinitions = new String[ definitionCount ];
              // System.out.println("definitionCount = " + definitionCount);
              for ( int c = 0; c < definitionCount; c++ ) {
                byte definitionLength = DataUtil.get8( block, offset );
                offset += 1;
                String definition = DataUtil.byteArray2string( block, offset, definitionLength );
                offset += definitionLength;
                animSmile.smileDefinitions[c] = definition;
                // System.out.println("definition = " + definition);
              }
              smiles[i] = animSmile;
            }
          } else if ( smilesType == 0x01 ) {
            String smilesFileName = is.readUTF();
            Image smilesImage = Image.createImage( smilesPath.concat( smilesFileName ) );
            smiles = new StatSmile[ is.readChar() ];
            int x, y, w, h;
            int determCount;
            StatSmile tempSmile;
            for ( int c = 0; c < smiles.length; c++ ) {
              x = is.readChar();
              y = is.readChar();
              w = is.readChar();
              h = is.readChar();
              tempSmile = new StatSmile();
              tempSmile.image = Image.createImage( smilesImage, x, y, w, h, Sprite.TRANS_NONE );
              determCount = is.readChar();
              tempSmile.smileDefinitions = new String[ determCount ];
              for ( int i = 0; i < determCount; i++ ) {
                tempSmile.smileDefinitions[i] = is.readUTF();
                if ( isBBAdapted ) {
                  tempSmile.smileDefinitions[i] = StringUtil.replace( tempSmile.smileDefinitions[i], "[", "\\[" );
                  tempSmile.smileDefinitions[i] = StringUtil.replace( tempSmile.smileDefinitions[i], "]", "\\]" );
                }
              }
              smiles[c] = tempSmile;
            }
          }
        } catch ( IOException ex ) {
          // ex.printStackTrace();
        }
      }
    } catch ( Throwable ex1 ) {
      // ex1.printStackTrace();
    }
  }

  public static String replaceSmilesForCodes( String textWithSmileys ) {
    for ( int c = 0; c < smiles.length; c++ ) {
      // System.out.println("width = " +smiles[c].width);
      for ( int j = 0; j < smiles[c].getSmileDefinitions().length; j++ ) {
        int prevIndex = 0;
        for ( int i = 0; i < textWithSmileys.length(); i++ ) {
          if ( textWithSmileys.regionMatches( true, i, smiles[c].getSmileDefinitions()[j], 0, smiles[c].getSmileDefinitions()[j].length() ) ) {
            textWithSmileys = textWithSmileys.substring( prevIndex, i )
                    + "[smile=" + c + "/]"
                    + textWithSmileys.substring( i + smiles[c].getSmileDefinitions()[j].length(), textWithSmileys.length() );
            prevIndex = 0;
          }
        }
      }
    }
    return textWithSmileys;
  }

  public static void loadSmileARGB( AnimSmile animSmile ) {
    // System.out.println("Loading file ".concat(animSmile.fileName) + "...");
    try {
      /** Loading image file **/
      tempImage = Image.createImage( smilesPath.concat( animSmile.fileName ) );
      /** Creating smile oject **/
      animSmile.setWidth( tempImage.getWidth() / animSmile.framesDelay.length );
      animSmile.setHeight( tempImage.getHeight() );
      animSmile.setFramesARGB( new Image[ animSmile.framesDelay.length ] );
      /** Cutting smiles **/
      for ( int c = 0; c < animSmile.framesDelay.length; c++ ) {
        animSmile.setFrameARGB( c, Image.createImage( tempImage, c * animSmile.getWidth(), 0, animSmile.getWidth(), animSmile.getHeight(), Sprite.TRANS_NONE ) );
        // animSmile.setFrameARGB(c, new int[(animSmile.getWidth()) * animSmile.getHeight()]);
        // tempImage.getRGB(animSmile.getFrameARGB(c), 0, animSmile.getWidth(), c * animSmile.getWidth(), 0, animSmile.getWidth(), animSmile.getHeight());
      }
    } catch ( IOException ex ) {
      // ex.printStackTrace();
    }
    // System.out.println("Complete.");
  }
}
