package com.idega.block.media.business;


public class MediaFileSystemHomeImpl extends com.idega.business.IBOHomeImpl implements MediaFileSystemHome
{
 protected Class getBeanInterfaceClass(){
  return MediaFileSystem.class;
 }


 public MediaFileSystem create() throws javax.ejb.CreateException{
  return (MediaFileSystem) super.createIBO();
 }



}