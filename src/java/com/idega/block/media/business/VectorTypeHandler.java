package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.VectorTypeHandler
 * Description: A type handler that handles vector graphics
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
public class VectorTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){

    Cache cache = this.getCachedFileInfo(icFileId,iwc);
    Flash flash = new Flash();
    flash.setWidth("100%");
    flash.setHeight("100%");
    flash.setFile((ICFile)cache.getEntity());
    return flash;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Flash flash = new Flash(props.getWebPath(),props.getName());
    flash.setWidth("100%");
    flash.setHeight("100%");
    return flash;
  }

}