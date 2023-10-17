package com.idega.block.media.business;

import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.Flash;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.util.caching.Cache;

/**
 * Title: com.idega.block.media.business.VectorTypeHandler
 * Description: A type handler that handles vector graphics and their plugins
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class VectorTypeHandler extends FileTypeHandler {

  @Override
public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
	ICFile file = getFile(icFileId);
	Cache cache = getCachedFileInfo(iwc, file.getUniqueId(), file.getToken());
    Flash flash = new Flash(cache.getVirtualPathToFile());
    flash.setWidth("100%");
    flash.setHeight("100%");

    return flash;
  }

  @Override
public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Flash flash = new Flash(props.getWebPath(),props.getName());
    flash.setWidth("100%");
    flash.setHeight("100%");
    return flash;
  }

}
