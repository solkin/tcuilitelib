package com.tomclaw.tcuilite;

import com.tomclaw.utils.DataUtil;
import com.tomclaw.utils.StringUtil;

/**
 * Solkin Igor Viktorovich, TomClaw Software, 2003-2012
 * http://www.tomclaw.com/
 * @author Solkin
 */
public class ListRmsRenderer {

  public ListItem getRmsItem( byte[] data ) {
    ListItem listItem = new ListItem( StringUtil.byteArrayToString( data, 8, DataUtil.get16( data, 6 ), true ),
            ( int ) DataUtil.get32( data, 0, true ), DataUtil.get16( data, 4 ) );
    return listItem;
  }

  public byte[] getRmsData( ListItem listItem ) {
    byte[] titleData = listItem.title == null ? new byte[ 0 ] : StringUtil.stringToByteArray( listItem.title, true );
    byte[] data = new byte[ 8 + titleData.length ];
    DataUtil.put32( data, 0, listItem.imageFileHash );
    DataUtil.put16( data, 4, listItem.imageIndex );
    if ( listItem.title != null ) {
      DataUtil.put16( data, 6, titleData.length );
      DataUtil.putArray( data, 8, titleData );
    }
    return data;
  }
}
