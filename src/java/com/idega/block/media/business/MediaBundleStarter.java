package com.idega.block.media.business;


/**
 * Title:MediaBundleStarter
 * Description: MediaBundleStarter implements the IWBundleStartable interface. The start method of this
 * object is called during the Bundle loading when starting up a idegaWeb applications.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.core.data.ICMimeType;
import com.idega.core.data.ICFileType;

import java.util.HashMap;

public class MediaBundleStarter implements IWBundleStartable{

  public MediaBundleStarter() {
  }

  public void start(IWBundle bundle){
    IWCacheManager cm = bundle.getApplication().getIWCacheManager();

    ICFileType types = new ICFileType();
    types.cacheEntity();

    ICMimeType mimes = new ICMimeType();
    mimes.cacheEntity();

    ICMimeType test = (ICMimeType) cm.getFromCachedTable(ICMimeType.class,"application/pdf");
    System.out.println("XXXXXXXXXXXXX MediaBundleStarter caching MimeType "+test.getDescription());

    ICFileType test2 = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_APPLICATION);
    System.out.println("XXXXXXXXXXXXX MediaBundleStarter caching FileType "+test2.getDescription());

    //other initially created types that don't aren't easily recognizes as certain file types
      /*
      type = new ICMimeType();
      type.setMimeType("application/pdf");
      type.setDescription("Adobe PDF Document");
      type.setFileTypeId(IWMainApplication.);
      type.insert();

      */
  }
}