package com.idega.block.media.business;

import com.idega.util.caching.Cache;
import com.idega.block.media.data.MediaProperties;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;


/**
 * Title: com.idega.block.media.business.ApplicationTypeHandler
 * Description: A type handler that handles applications or binary files
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class ApplicationTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Cache cache = getCachedFileInfo(icFileId,iwc);
    Link link = new Link(cache.getEntity().toString(),cache.getVirtualPathToFile());
    return link;
  }


  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Link link = new Link(props.getName(),props.getWebPath());
    return link;
  }

}
