/*
 * Created on 30.7.2003 by  tryggvil in project com.project
 */
package com.idega.block.media.business;

import java.rmi.RemoteException;
import com.idega.business.IBOServiceBean;
import com.idega.core.file.business.FileIconSupplier;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;

/**
 * MediaFileSystemBean The implementation of the FileSystem interface for the Media block
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class MediaFileSystemBean extends IBOServiceBean implements MediaFileSystem,ICFileSystem
{
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileURL(com.idega.core.data.ICFile)
	 */
	public String getFileURI(ICFile file)
	{
		return MediaBusiness.getMediaURL(file,this.getIWApplicationContext().getIWMainApplication());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileURL(int)
	 */
	public String getFileURI(int fileId)
	{
		return MediaBusiness.getMediaURL(fileId,this.getIWApplicationContext().getIWMainApplication());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#initialize()
	 */
	public void initialize()
	{
		MediaBundleStarter starter = new MediaBundleStarter();  		
		starter.start(getIWApplicationContext().getIWMainApplication());
	}

	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileIconURI(com.idega.core.file.data.ICFile)
	 */
	public String getFileIconURI(ICFile file) throws RemoteException {
		return getIconURIByMimeType(file.getMimeType());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getIconURIByMimeType(java.lang.String)
	 */
	public String getIconURIByMimeType(String mimeType) throws RemoteException {
		FileIconSupplier iconSupplier = FileIconSupplier.getInstance();
		return iconSupplier.getFileIconURLByMimeType(mimeType);
	}
}
