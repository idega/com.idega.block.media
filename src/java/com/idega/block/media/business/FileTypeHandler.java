package com.idega.block.media.business;

import com.idega.block.media.data.MediaProperties;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.Singleton;
import com.idega.util.caching.Cache;

/**
 * Title: com.idega.block.media.business.FileTypeHandler
 * Description: This is the FileTypeHandler interface it defines one static method get
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public abstract class FileTypeHandler extends java.lang.Object implements Singleton {

public abstract PresentationObject getPresentationObject(int icFileId, IWContext iwc);

public abstract PresentationObject getPresentationObject(MediaProperties props, IWContext iwc);

public static FileTypeHandler getInstance(IWMainApplication iwma, String handlerClass){
  FileTypeHandler handler = (FileTypeHandler)iwma.getAttribute(handlerClass);
  if(handler==null){
    try {
      handler = (FileTypeHandler) Class.forName(handlerClass).newInstance();
      iwma.setAttribute(handlerClass,handler);
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
  }
  return handler;
}

public static FileTypeHandler getInstance(IWMainApplication iwma, Class handlerClass){
  return getInstance(iwma,handlerClass.getName());
}

public static Cache getCachedFileInfo(int icFileId, IWContext iwc){
  return MediaBusiness.getCachedFileInfo(icFileId,iwc.getIWMainApplication());
}

}

