package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.SystemTypeHandler
 * Description: A type handler that handles idegaWeb system type files such as folders ( The Finder ;)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.text.*;
import com.idega.block.reports.business.Content;
import com.idega.block.reports.presentation.ContentViewer;
import com.idega.presentation.IWContext;
import java.util.Iterator;
import java.util.Vector;
import com.idega.util.caching.Cache;
import com.idega.presentation.Image;


import com.idega.core.data.ICFile;
public class SystemTypeHandler extends FileTypeHandler {

public static String[] LIST_VIEW_HEADERS = {"Select","Name","Date modified","Size","Mimetype"};


  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    ContentViewer listView = null;
    try {

      ICFile file = (ICFile)this.getCachedFileInfo(icFileId,iwc).getEntity();
      Vector V = new Vector();

      //**@todo debug only do this if not a folder**/
      V.add(getContentObject(file));

      Iterator iter = file.getChildren();
      if( iter != null ){
        while (iter.hasNext()) {
          ICFile item = (ICFile) iter.next();
          V.add(getContentObject(item));
        }
      }

      listView = new ContentViewer(LIST_VIEW_HEADERS,V);
      //CV1.setICObjectInstanceID(2);
      listView.setDisplayNumber(2);
      listView.setAllowOrder(true);



    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return listView;

  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Image image = new Image(props.getWebPath(),props.getName());
    table.add(image);

    return table;
  }
  private Content getContentObject(ICFile item){
    Object[] objs = new Object[5];
    objs[0] = new CheckBox(Integer.toString(item.getID()));
    objs[1] = (item.getName() != null ) ? item.getName() : "";
    objs[2] = (item.getModificationDate() != null ) ? item.getModificationDate().toString() : item.getCreationDate().toString();
    objs[3] = (item.getFileSize() != null ) ? item.getFileSize().toString() : "";
    objs[4] = (item.getMimeType() != null ) ? item.getMimeType() : "";

    Content C = new Content(objs);

    return C;
  }

}