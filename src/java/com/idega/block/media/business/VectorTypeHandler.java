package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.VectorTypeHandler
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
import com.idega.util.caching.Cache;
import com.idega.presentation.Image;

import com.idega.core.data.ICFile;
public class VectorTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Cache cache = this.getCachedFileInfo(icFileId,iwc);
    Image image = new Image(cache.getVirtualPathToFile(),cache.getEntity().getName());
    table.add(image);

    return table;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Image image = new Image(props.getWebPath(),props.getName());
    table.add(image);

    return table;
  }

}