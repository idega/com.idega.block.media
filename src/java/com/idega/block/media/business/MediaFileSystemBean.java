/*
 * Created on 30.7.2003 by  tryggvil in project com.project
 */
package com.idega.block.media.business;

import java.rmi.RemoteException;

import com.idega.business.IBOServiceBean;
import com.idega.core.file.business.FileIconSupplier;
import com.idega.core.file.business.ICFileSystem;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;

/**
 * MediaFileSystemBean The implementation of the FileSystem interface for the Media block
 * Copyright (C) idega software 2003
 * @author <a href="mailto:tryggvi@idega.is">Tryggvi Larusson</a>
 * @version 1.0
 */
public class MediaFileSystemBean extends IBOServiceBean implements MediaFileSystem, ICFileSystem {

	private static final long serialVersionUID = -1255970453658541226L;

	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileURL(com.idega.core.data.ICFile)
	 */
	@Override
	public String getFileURI(IWContext iwc, ICFile file)
	{
		return MediaBusiness.getMediaURL(iwc, file, this.getIWApplicationContext().getIWMainApplication());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileURL(int)
	 */
	@Override
	public String getFileURI(IWContext iwc, String fileUniqueId, String fileToken)
	{
		return MediaBusiness.getMediaURL(iwc, fileUniqueId, fileToken, this.getIWApplicationContext().getIWMainApplication());
	}
	@Override
	public String getFileURI(IWContext iwc, String fileUniqueId, String fileToken, String datasource)
	{
		return MediaBusiness.getMediaURL(iwc, fileUniqueId, fileToken, this.getIWApplicationContext().getIWMainApplication(), datasource);
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#initialize()
	 */
	@Override
	public void initialize()
	{
		//mediabundlestarter is always started!
//		MediaBundleStarter starter = new MediaBundleStarter();
//		starter.start(getIWApplicationContext().getIWMainApplication());
	}

	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getFileIconURI(com.idega.core.file.data.ICFile)
	 */
	@Override
	public String getFileIconURI(ICFile file) throws RemoteException {
		return getIconURIByMimeType(file.getMimeType());
	}
	/* (non-Javadoc)
	 * @see com.idega.core.file.business.ICFileSystem#getIconURIByMimeType(java.lang.String)
	 */
	@Override
	public String getIconURIByMimeType(String mimeType) throws RemoteException {
		FileIconSupplier iconSupplier = FileIconSupplier.getInstance();
		return iconSupplier.getFileIconURLByMimeType(mimeType);
	}
}
