package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.FileTypeHandler
 * Description: This is the FileTypeHandler interface it defines one static method get
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import com.idega.presentation.PresentationObject;
import com.idega.idegaweb.IWMainApplication;

public abstract class FileTypeHandler extends java.lang.Object{

public abstract PresentationObject getPresentationObject(int icFileId);

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

}

