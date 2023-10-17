package com.idega.block.media.business;

import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.util.caching.Cache;

/**
 * Title: com.idega.block.media.business.DocumentTypeHandler
 * Description: A type handler that handles documents such as Word files or compressed archives
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class DocumentTypeHandler extends FileTypeHandler {

  @Override
public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
	ICFile file = getFile(icFileId);
	Cache cache = getCachedFileInfo(iwc, file.getUniqueId(), file.getToken());
    Link link = new Link(cache.getEntity().toString(),cache.getVirtualPathToFile());
    return link;
  }

  @Override
public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Link link = new Link(props.getName(),props.getWebPath());
    return link;
  }

}
