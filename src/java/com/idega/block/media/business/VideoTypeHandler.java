package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.VideoTypeHandler
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
import com.idega.presentation.Quicktime;
import com.idega.block.media.data.MediaProperties;

import com.idega.core.data.ICFile;
public class VideoTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Cache cache = this.getCachedFileInfo(icFileId,iwc);
    Quicktime mov = new Quicktime(cache.getVirtualPathToFile(),cache.getEntity().getName());

    mov.setWidth("200");
    mov.setHeight("200");
    table.add(mov);

    return table;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Quicktime mov = new Quicktime(props.getWebPath(),props.getName());

    mov.setWidth("200");
    mov.setHeight("200");
    table.add(mov);

    return table;
  }

}