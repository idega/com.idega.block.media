package com.idega.block.media.business;


public interface MediaFileSystem extends com.idega.business.IBOService,com.idega.core.file.business.ICFileSystem
{
 public java.lang.String getFileURI(com.idega.core.data.ICFile p0) throws java.rmi.RemoteException;
 public java.lang.String getFileURI(int p0) throws java.rmi.RemoteException;
}
