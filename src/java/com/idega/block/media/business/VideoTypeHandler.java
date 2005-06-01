package com.idega.block.media.business;

import com.idega.block.media.data.MediaProperties;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Quicktime;
import com.idega.util.caching.Cache;

/**
 * Title: com.idega.block.media.business.VideoTypeHandler
 * Description: A type handler that handles video files and their plugins
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */
public class VideoTypeHandler extends FileTypeHandler {


  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Cache cache = getCachedFileInfo(icFileId,iwc);
    Quicktime qt = new Quicktime(cache.getVirtualPathToFile(),cache.getEntity().toString());
    qt.setWidth("100%");
    qt.setHeight("100%");
    qt.setAUTOPLAY(false);
    return qt;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Quicktime qt = new Quicktime(props.getWebPath(),props.getName());
    qt.setWidth("100%");
    qt.setHeight("100%");
    qt.setAUTOPLAY(false);
    return qt;
  }

}
