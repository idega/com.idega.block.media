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
import java.sql.SQLException;

import java.util.HashMap;

public class MediaBundleStarter implements IWBundleStartable{

  private IWCacheManager cm;

  private String[] application = {
                                  "Undefined binary data (often executable progs)","application/octet-stream"
                                  };
  private String[] audio = {
                            "basic audio - 8-bit u-law PCM au snd","audio/basic",
                            "Macintosh audio format (AIpple) aif aiff aifc","audio/x-aiff",
                            "Microsoft audio  wav","audio/x-wav",
                            "MPEG audio  mpa abs mpega","audio/x-mpeg",
                            "MPEG-2 audio mp2a mpa2","audio/x-mpeg-2",
                            "MIDI music data  mmid","x-music/x-midi",
                            };

  private String[] document = {
                              "HTML text data (RFC 1866) html htm","text/html",
                              "Plain text: documents; program listings txt c c++ pl cc h","text/plain",
                              "Richtext (obsolete - replaced by text/enriched)","text/richtext",
                              "PostScript  ai eps ps  ","application/postscript",
                              "Microsoft Rich Text Format rtf","application/rtf",
                              "Adobe Acrobat PDF  pdf","application/pdf",
                              "Adobe Acrobat PDF  pdf","application/x-pdf",
                              "MS word document","application/msword",
                              "PowerPoint presentation (Microsoft) ppz","application/mspowerpoint",
                              "PowerPoint (Microsoft) ppt","application/vnd.ms-powerpoint",
                              "Microsoft Project (Microsoft)","application/vnd.ms-project",
                              "Works data (Microsoft)","application/vnd.ms-works",
                              "Macintosh Binhexed archive  hqx","application/mac-binhex40",
                              "Macintosh Stuffit Archive sit sea","application/x-stuffit",
                              "Javascript program  js ls mocha","text/javascript",
                              "Javascript program  js ls mocha","application/x-javascript",
                              "Gnu tar format gtar","application/x-gtar",
                              "4.3BSD tar format tar  application/x-tar",
                              };
  private String[] image = {
                            "GIF","image/gif",
                            "X-Windows bitmap (b/w)  xbm","image/x-xbitmap",
                            "X-Windows pixelmap (8-bit color)  xpm","image/x-xpix",
                            "Portable Network Graphics png","image/x-png",
                            "Image Exchange Format (RFC 1314) ief","image/ief",
                            "JPEG  jpeg jpg jpe","image/jpeg",
                            "TIFF  tiff tif","image/tiff",
                            "Macintosh PICT format pict","image/x-pict",
                            "Microsoft Windows bitmap  bmp","image/x-ms-bmp"
                            };

  private String[] vector = {
                            "FutureSplash vector animation (FutureWave)  spl","application/futuresplash",
                            "Macromedia Shockwave (Macromedia)","application/x-director"
                            };

  private String[] video = {
                            "MPEG video mpeg mpg mpe","video/mpeg",
                            "MPEG-2 video mpv2 mp2v","video/mpeg-2",
                            "Macintosh Quicktime qt mov","video/quicktime",
                            "Microsoft video  avi","video/x-msvideo",
                            "SGI Movie format movie","video/x-sgi-movie",
                            "QuickDraw3D scene data (Apple) 3dmf","x-world/x-3dmf"
                            };


  public MediaBundleStarter() {
  }

  public void start(IWBundle bundle){
    cm = bundle.getApplication().getIWCacheManager();

    //cache file types ICFileType extends CacheableEntity
    ICFileType types = new ICFileType();
    types.cacheEntity();

    //get the default file types
    ICFileType applications = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_APPLICATION);
    ICFileType audios = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_AUDIO);
    ICFileType documents = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_DOCUMENT);
    ICFileType images = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_IMAGE);
    ICFileType vectors = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_VECTOR_GRAPHICS);
    ICFileType videos = (ICFileType) cm.getFromCachedTable(ICFileType.class,ICFileType.IC_FILE_TYPE_VIDEO);


    //cache
    ICMimeType mimes = new ICMimeType();
    mimes.cacheEntity();

    registerMimeType(application,applications);
    registerMimeType(audio,audios);
    registerMimeType(document,documents);
    registerMimeType(image,images);
    registerMimeType(vector,vectors);
    registerMimeType(video,videos);



    //other initially created types that don't aren't easily recognizes as certain file types
      /*
      type = new ICMimeType();
      type.setMimeType("application/pdf");
      type.setDescription("Adobe PDF Document");
      type.setFileTypeId(IWMainApplication.);
      type.insert();

      */
  }


  public void registerMimeType(String[] array,ICFileType type){
    int typeId = type.getID();
    ICMimeType mimetype;

    for (int i = 0; i < (array.length); i++) {
      //check if these common mimetypes exist and insert if not.
      mimetype = (ICMimeType) cm.getFromCachedTable(ICMimeType.class,array[i]);
      if( mimetype == null ){
        mimetype = new ICMimeType();
        mimetype.setMimeTypeAndDescription(array[i+1],array[i]);
        mimetype.setFileTypeId(typeId);
      }
      try {
        mimetype.insert();
      }
      catch (SQLException ex) {
        ex.printStackTrace(System.err);
      }

      i++;

    }

  }
}