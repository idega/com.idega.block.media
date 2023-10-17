package com.idega.block.media.business;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.util.caching.Cache;

/**
 * Title: com.idega.block.media.business.FileTypeHandler
 * Description: This is the FileTypeHandler interface it defines one static method get
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public abstract class FileTypeHandler extends java.lang.Object {

public abstract PresentationObject getPresentationObject(int icFileId, IWContext iwc);

public abstract PresentationObject getPresentationObject(MediaProperties props, IWContext iwc);

public static FileTypeHandler getInstance(IWMainApplication iwma, String handlerClass){
  FileTypeHandler handler = (FileTypeHandler)iwma.getAttribute(handlerClass);
  if(handler==null){
    try {
      handler = (FileTypeHandler) RefactorClassRegistry.forName(handlerClass).newInstance();
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

public static Cache getCachedFileInfo(IWContext iwc, String fileUniqueId, String fileToken){
  return MediaBusiness.getCachedFileInfo(iwc, fileUniqueId, fileToken, iwc.getIWMainApplication());
}

	public static ICFile getFile(int fileId) {
		if (fileId > 0) {
			try {
				ICFileHome fileHome = (ICFileHome) IDOLookup.getHome(ICFile.class);
				ICFile file = fileHome.findByPrimaryKey(fileId);
				return file;
			} catch (Exception e) {
				Logger.getLogger(FileTypeHandler.class.getName()).log(Level.WARNING, "Error getting file's (" + fileId + ") unique ID", e);
			}
		}
		return null;
	}

}