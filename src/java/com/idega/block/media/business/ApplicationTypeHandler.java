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
import com.idega.util.caching.Cache;
import com.idega.presentation.Image;
import com.idega.block.media.data.MediaProperties;

import com.idega.core.data.ICFile;
public class ApplicationTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    return ((SystemTypeHandler) FileTypeHandler.getInstance(iwc.getApplication(),SystemTypeHandler.class) ).getPresentationObject(icFileId,iwc);
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Table table = new Table();

    table.setWidth("100%");
    table.setHeight("100%");

    Link link = new Link(props.getName(),props.getWebPath());
    table.add(link);

    return table;
  }

}