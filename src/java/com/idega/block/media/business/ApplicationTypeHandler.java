package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.ApplicationTypeHandler
 * Description: A type handler that handles idegaWeb system type files such as folders ( The Finder ;)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import java.util.Iterator;
import com.idega.presentation.IWContext;

import com.idega.core.data.ICFile;
public class ApplicationTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    try {
      ICFile file = new ICFile(icFileId);
      Link link;

      Iterator iter = file.getChildren();
      while (iter.hasNext()) {
        ICFile item = (ICFile) iter.next();
        link = new Link();
        link.setFile(item);
        table.add(link);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return table;

  };

}