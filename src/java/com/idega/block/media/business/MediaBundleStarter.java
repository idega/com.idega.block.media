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

import java.util.*;
import com.idega.builder.app.IBApplication;
import com.idega.block.media.presentation.MediaToolbarButton;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWCacheManager;
import com.idega.core.data.ICMimeType;
import com.idega.core.data.ICFileType;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileTypeHandler;
import com.idega.data.EntityFinder;
import java.sql.SQLException;


public class MediaBundleStarter implements IWBundleStartable{

  private IWCacheManager cm;

  private String[] system = {
			    "A Folder",com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER
			    };

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
			    "MPEG audio MP3","audio/mpeg"
			    };

  private String[] document = {
			      "HTML text data (RFC 1866) html htm","text/html",
			      "Plain text: documents; program listings txt c c++ pl cc h","text/plain",
			      "An xml document such as .ibxml",com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML,
			      "Richtext","text/richtext",
			      "Richtext newer","text/enriched",
			      "Style sheet","text/css",
			      "PostScript  ai eps ps  ","application/postscript",
			      "Microsoft Rich Text Format rtf","application/rtf",
			      "Adobe Acrobat PDF  pdf","application/pdf",
			      "Adobe Acrobat PDF  pdf","application/x-pdf",
			      "MS word document","application/msword",
			      "PowerPoint presentation (Microsoft) ppz","application/mspowerpoint",
			      "PowerPoint (Microsoft) ppt","application/vnd.ms-powerpoint",
			      "Microsoft Project (Microsoft)","application/vnd.ms-project",
			      "Microsoft Excel (Microsoft)","application/vnd.ms-excel",
			      "Works data (Microsoft)","application/vnd.ms-works",
			      "Macintosh Binhexed archive  hqx","application/mac-binhex40",
			      "Macintosh Stuffit Archive sit sea","application/x-stuffit",
			      "Javascript program  js ls mocha","text/javascript",
			      "Javascript program  js ls mocha","application/x-javascript",
			      "Gnu tar format gtar","application/x-gtar",
			      "4.3BSD tar format tar","application/x-tar",
                              "Adobe photoshop file", "image/psd"
			      };
  private String[] image = {
			    "GIF","image/gif",
			    "X-Windows bitmap (b/w)  xbm","image/x-xbitmap",
			    "X-Windows pixelmap (8-bit color)  xpm","image/x-xpix",
			    "Portable Network Graphics png","image/x-png",
			    "Portable Network Graphics png","image/png",
			    "Image Exchange Format (RFC 1314) ief","image/ief",
			    "JPEG  jpeg jpg jpe pjpeg","image/jpeg",
			    "JPEG  jpeg jpg jpe pjpeg","image/pjpeg",
			    "JPEG  jpeg jpg jpe pjpeg","image/jpg",
			    "JPEG  jpeg jpg jpe pjpeg","image/jpe",
			    "TIFF  tiff tif","image/tiff",
			    "Macintosh PICT format pict","image/x-pict",
			    "Macintosh PICT format pict","image/pict",
			    "Microsoft Windows bitmap  bmp","image/x-ms-bmp",
			    "Microsoft Windows bitmap  bmp","image/bmp",
			    "Microsoft Windows bitmap  bmp","image/x-bmp",
                            "pcx image", "image/pcx",
                            "iff image", "image/iff",
                            "ras image", "image/ras",
                            "portable-bitmap image", "image/x-portable-bitmap",
                            "portable-graymap image", "image/x-portable-graymap",
                            "portable-pixmap image", "image/x-portable-pixmap"
			    };

  private String[] vector = {
			    "FutureSplash vector animation (FutureWave)  spl","application/futuresplash",
			    "Macromedia Shockwave (Macromedia)","application/x-director",
			    "Macromedia Shockwave (Macromedia)","application/x-shockwave-flash"
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
     //add toolbar buttons
//    MediaToolbarButton separator = new MediaToolbarButton(bundle,true);
    MediaToolbarButton button = new MediaToolbarButton(bundle,false);

    List l = (List)bundle.getApplication().getAttribute(IBApplication.TOOLBAR_ITEMS);
    if (l == null) {
      l = new Vector();
      bundle.getApplication().setAttribute(IBApplication.TOOLBAR_ITEMS, l);
    }


    l.add(button);
//    l.add(separator);

  }

  public void start(IWMainApplication iwma){

    //handle mimetypes

    //cache file types ICFileType extends CacheableEntity
    cm = iwma.getIWCacheManager();
    ICFileTypeHandler handlers = ((com.idega.core.data.ICFileTypeHandlerHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileTypeHandler.class)).createLegacy();
    handlers.cacheEntity();
    //cache file types ICFileType extends CacheableEntity
    ICFileType types = ((com.idega.core.data.ICFileTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICFileType.class)).createLegacy();
    types.cacheEntity();

    //get the default file types
    ICFileType applications = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_APPLICATION);
    ICFileType audios = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_AUDIO);
    ICFileType documents = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_DOCUMENT);
    ICFileType images = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_IMAGE);
    ICFileType vectors = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_VECTOR_GRAPHICS);
    ICFileType videos = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_VIDEO);
    ICFileType systems = (ICFileType) cm.getFromCachedTable(ICFileType.class,com.idega.core.data.ICFileTypeBMPBean.IC_FILE_TYPE_SYSTEM);

    //cache
    ICMimeType mimes = ((com.idega.core.data.ICMimeTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICMimeType.class)).createLegacy();
    mimes.cacheEntity();

    try {
      //insert the mimetypes
      registerMimeType(system,systems);
      registerMimeType(application,applications);
      registerMimeType(audio,audios);
      registerMimeType(document,documents);
      registerMimeType(image,images);
      registerMimeType(vector,vectors);
      registerMimeType(video,videos);
    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }

    cm.removeTableFromCache(ICFileTypeHandler.class);

    handlers.cacheEntityByID();

    cm.removeTableFromCache(ICFileType.class);
    types.cacheEntityByID();

    try {
      //**insert the Root folder if it doesn't exist yet**/
      ICFile file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
      List root = EntityFinder.findAllByColumn(file,com.idega.core.data.ICFileBMPBean.getColumnNameName(),com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME,com.idega.core.data.ICFileBMPBean.getColumnNameMimeType(),com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
      if( root == null ){
       file.setName(com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME);
       file.setMimeType(com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
       file.setDescription("This is the top level folder it shouldn't be visible");
       file.insert();
      }
      else{
       Iterator iter = root.iterator();
       while(iter.hasNext()){
	file = (ICFile)iter.next();//there is only one root
       }
      }
      //cache it!
      cm.cacheEntity(file,com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);

    }
    catch (SQLException ex) {
      ex.printStackTrace(System.err);
    }

  }


  public void registerMimeType(String[] array,ICFileType type) throws SQLException{
    int typeId = type.getID();
    ICMimeType mimetype;

    for (int i = 0; i < (array.length); i++) {
      //check if these common mimetypes exist and insert if not.
      mimetype = (ICMimeType) cm.getFromCachedTable(ICMimeType.class,array[i+1]);
      if( mimetype == null ){
        String mimeType = array[i+1];
        mimetype = ((com.idega.core.data.ICMimeTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICMimeType.class)).createLegacy();
        mimetype.setMimeTypeAndDescription(mimeType,array[i]);
        mimetype.setFileTypeId(typeId);
        try {
          mimetype.insert();
        }
        catch (SQLException ex) {
          //ex.printStackTrace(System.err);
          System.err.println("[MediBundleStarter] : Error inserting MIME-TYPE for: "+mimeType);
        }
      }
      i++;

    }

  }


}
