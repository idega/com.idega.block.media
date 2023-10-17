package com.idega.block.media.business;

import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Quicktime;
import com.idega.util.caching.Cache;


/**
 * Title: com.idega.block.media.business.AudioTypeHandler
 * Description: A type handler that handles audio files and their plugins
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class AudioTypeHandler extends FileTypeHandler {

  @Override
public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
	ICFile file = getFile(icFileId);
	Cache cache = getCachedFileInfo(iwc, file.getUniqueId(), file.getToken());
    Quicktime qt = new Quicktime(cache.getVirtualPathToFile(),cache.getEntity().toString());
    qt.setWidth(200);
    qt.setHeight(15);
    return qt;
  }

  @Override
public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Quicktime qt = new Quicktime(props.getWebPath(),props.getName());
    qt.setWidth(200);
    qt.setHeight(15);
    return qt;
  }

}
