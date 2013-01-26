package com.tomclaw.tcuilite;

import com.tomclaw.utils.DataUtil;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2013
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class GroupRmsRenderer {

  public GroupHeader getRmsGroupHeader( byte[] data ) {
    int offset;
    GroupHeader groupHeader = new GroupHeader( StringUtil.byteArrayToString( data, 2, offset = DataUtil.get16( data, 0 ), true ) );
    offset += 2;
    int childsCount = DataUtil.get16( data, offset );
    offset += 2;
    GroupChild groupChild;
    int t_Int;
    for ( int c = 0; c < childsCount; c++ ) {
      t_Int = DataUtil.get16( data, offset );
      offset += 2;
      groupChild = new GroupChild( StringUtil.byteArrayToString( data, offset, t_Int, true ) );
      offset += t_Int;
      /**
       * Left images
       */
      t_Int = DataUtil.get16( data, offset );
      offset += 2;
      if ( t_Int > 0 ) {
        groupChild.imageLeftIndex = new int[ t_Int ];
        for ( int i = 0; i < t_Int; i++ ) {
          groupChild.imageLeftIndex[i] = DataUtil.get16( data, offset );
          offset += 2;
        }
      }
      /**
       * Right images
       */
      t_Int = DataUtil.get16( data, offset );
      offset += 2;
      if ( t_Int > 0 ) {
        groupChild.imageRightIndex = new int[ t_Int ];
        for ( int i = 0; i < t_Int; i++ ) {
          groupChild.imageRightIndex[i] = DataUtil.get16( data, offset );
          offset += 2;
        }
      }
      groupHeader.addChild( groupChild );
    }
    return groupHeader;
  }

  public byte[] getRmsData( GroupHeader groupHeader ) {
    byte[] data;
    byte[] titleData = StringUtil.stringToByteArray( groupHeader.title, true );
    data = new byte[ 4 + titleData.length ];
    DataUtil.put16( data, 0, titleData.length );
    DataUtil.putArray( data, 2, titleData );
    DataUtil.put16( data, 2 + titleData.length, groupHeader.getChildsCount() );
    byte[] itemData;
    byte[] t_Byte;
    int offset;
    GroupChild groupChild;
    for ( int c = 0; c < groupHeader.getChildsCount(); c++ ) {
      groupChild = ( GroupChild ) groupHeader.childs.elementAt( c );
      titleData = StringUtil.stringToByteArray( groupChild.title, true );
      itemData = new byte[ ( groupChild.imageLeftIndex == null ? 0 : groupChild.imageLeftIndex.length * 2 ) + ( groupChild.imageRightIndex == null ? 0 : groupChild.imageRightIndex.length * 2 ) + 6 + titleData.length ];
      /**
       * Title *
       */
      DataUtil.put16( itemData, 0, titleData.length );
      DataUtil.putArray( itemData, 2, titleData );
      offset = 2 + titleData.length;
      /**
       * Images *
       */
      if ( groupChild.imageLeftIndex == null ) {
        DataUtil.put16( itemData, offset, 0 );
        offset += 2;
      } else {
        DataUtil.put16( itemData, offset, groupChild.imageLeftIndex.length );
        offset += 2;
        for ( int i = 0; i < groupChild.imageLeftIndex.length; i++ ) {
          DataUtil.put16( itemData, offset, groupChild.imageLeftIndex[i] );
          offset += 2;
        }
      }
      if ( groupChild.imageRightIndex == null ) {
        DataUtil.put16( itemData, offset, 0 );
        offset += 2;
      } else {
        DataUtil.put16( itemData, offset, groupChild.imageRightIndex.length );
        offset += 2;
        for ( int i = 0; i < groupChild.imageRightIndex.length; i++ ) {
          DataUtil.put16( itemData, offset, groupChild.imageRightIndex[i] );
          offset += 2;
        }
      }
      t_Byte = new byte[ data.length + itemData.length ];
      System.arraycopy( data, 0, t_Byte, 0, data.length );
      System.arraycopy( itemData, 0, t_Byte, data.length, itemData.length );
      data = t_Byte;
    }
    return data;
  }
}
