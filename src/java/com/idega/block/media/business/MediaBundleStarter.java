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

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import com.idega.core.data.ICApplicationBinding;
import com.idega.core.data.ICApplicationBindingHome;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.ICFileType;
import com.idega.core.file.data.ICFileTypeHandler;
import com.idega.core.file.data.ICMimeType;
import com.idega.core.file.data.ICMimeTypeHome;
import com.idega.data.IDOLookup;
import com.idega.data.IDOStoreException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;

public class MediaBundleStarter implements IWBundleStartable {

	private IWCacheManager cm;

	private String[] system = { "A Folder", com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER };

	private String[] application = { "Undefined binary data (often executable progs)", "application/octet-stream" };

	private String[] audio = { "basic audio - 8-bit u-law PCM au snd", "audio/basic", "Macintosh audio format (AIpple) aif aiff aifc", "audio/x-aiff", "Microsoft audio  wav", "audio/x-wav", "MPEG audio  mpa abs mpega", "audio/x-mpeg", "MPEG-2 audio mp2a mpa2", "audio/x-mpeg-2", "MIDI music data  mmid", "x-music/x-midi", "MPEG audio MP3", "audio/mpeg" };

	private String[] document =
		{
			"HTML text data (RFC 1866) html htm",
			"text/html",
			"Plain text: documents; program listings txt c c++ pl cc h",
			"text/plain",
			"An xml document such as .ibxml",
			com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_XML,
			"Richtext",
			"text/richtext",
			"Richtext newer",
			"text/enriched",
			"Style sheet",
			"text/css",
			"PostScript  ai eps ps  ",
			"application/postscript",
			"Microsoft Rich Text Format rtf",
			"application/rtf",
			"Adobe Acrobat PDF  pdf",
			"application/pdf",
			"Adobe Acrobat PDF  pdf",
			"application/x-pdf",
			"MS word document",
			"application/msword",
			"PowerPoint presentation (Microsoft) ppz",
			"application/mspowerpoint",
			"PowerPoint (Microsoft) ppt",
			"application/vnd.ms-powerpoint",
			"Microsoft Project (Microsoft)",
			"application/vnd.ms-project",
			"Microsoft Excel (Microsoft)",
			"application/vnd.ms-excel",
			"Works data (Microsoft)",
			"application/vnd.ms-works",
			"Macintosh Binhexed archive  hqx",
			"application/mac-binhex40",
			"Macintosh Stuffit Archive sit sea",
			"application/x-stuffit",
			"Javascript program  js ls mocha",
			"text/javascript",
			"Javascript program  js ls mocha",
			"application/x-javascript",
			"Gnu tar format gtar",
			"application/x-gtar",
			"4.3BSD tar format tar",
			"application/x-tar",
			"Adobe photoshop file",
			"image/psd" };
	private String[] image =
		{
			"GIF",
			"image/gif",
			"X-Windows bitmap (b/w)  xbm",
			"image/x-xbitmap",
			"X-Windows pixelmap (8-bit color)  xpm",
			"image/x-xpix",
			"Portable Network Graphics png",
			"image/x-png",
			"Portable Network Graphics png",
			"image/png",
			"Image Exchange Format (RFC 1314) ief",
			"image/ief",
			"JPEG  jpeg jpg jpe pjpeg",
			"image/jpeg",
			"JPEG  jpeg jpg jpe pjpeg",
			"image/pjpeg",
			"JPEG  jpeg jpg jpe pjpeg",
			"image/jpg",
			"JPEG  jpeg jpg jpe pjpeg",
			"image/jpe",
			"TIFF  tiff tif",
			"image/tiff",
			"Macintosh PICT format pict",
			"image/x-pict",
			"Macintosh PICT format pict",
			"image/pict",
			"Microsoft Windows bitmap  bmp",
			"image/x-ms-bmp",
			"Microsoft Windows bitmap  bmp",
			"image/bmp",
			"Microsoft Windows bitmap  bmp",
			"image/x-bmp",
			"pcx image",
			"image/pcx",
			"iff image",
			"image/iff",
			"ras image",
			"image/ras",
			"portable-bitmap image",
			"image/x-portable-bitmap",
			"portable-graymap image",
			"image/x-portable-graymap",
			"portable-pixmap image",
			"image/x-portable-pixmap" };

	private String[] vector = { "FutureSplash vector animation (FutureWave)  spl", "application/futuresplash", "Macromedia Shockwave (Macromedia)", "application/x-director", "Macromedia Shockwave (Macromedia)", "application/x-shockwave-flash" };

	private String[] video = { "MPEG video mpeg mpg mpe", "video/mpeg", "MPEG-2 video mpv2 mp2v", "video/mpeg-2", "Macintosh Quicktime qt mov", "video/quicktime", "Microsoft video  avi", "video/x-msvideo", "SGI Movie format movie", "video/x-sgi-movie", "QuickDraw3D scene data (Apple) 3dmf", "x-world/x-3dmf" };
	
	private String[] zip = { "Compressed Zip files", "application/x-zip-compressed" };

	public MediaBundleStarter() {
	}

	public void start(IWBundle bundle) {
		//add toolbar buttons
		//    MediaToolbarButton separator = new MediaToolbarButton(bundle,true);
//		MediaToolbarButton button = new MediaToolbarButton(bundle, false);
//
//		List l = (List)bundle.getApplication().getAttribute(IBApplication.TOOLBAR_ITEMS);
//		if (l == null) {
//			l = new Vector();
//			bundle.getApplication().setAttribute(IBApplication.TOOLBAR_ITEMS, l);
//		}
//
//		l.add(button);
		//    l.add(separator);

	}

	public void start(IWMainApplication iwma) {

		//handle mimetypes

		//cache file types ICFileType extends CacheableEntity
		this.cm = iwma.getIWCacheManager();
		ICFileTypeHandler handlers = ((com.idega.core.file.data.ICFileTypeHandler)com.idega.data.IDOLookup.instanciateEntity(ICFileTypeHandler.class));
		handlers.cacheEntity();
		//cache file types ICFileType extends CacheableEntity
		ICFileType types = ((com.idega.core.file.data.ICFileType)com.idega.data.IDOLookup.instanciateEntity(ICFileType.class));
		types.cacheEntity();

		//get the default file types
		ICFileType applications = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_APPLICATION);
		ICFileType audios = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_AUDIO);
		ICFileType documents = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_DOCUMENT);
		ICFileType images = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_IMAGE);
		ICFileType vectors = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_VECTOR_GRAPHICS);
		ICFileType videos = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_VIDEO);
		ICFileType systems = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_SYSTEM);
		ICFileType zips = (ICFileType)this.cm.getFromCachedTable(ICFileType.class, com.idega.core.file.data.ICFileTypeBMPBean.IC_FILE_TYPE_ZIP);

		//cache
		ICMimeType mimes = ((com.idega.core.file.data.ICMimeTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICMimeType.class)).createLegacy();
		mimes.cacheEntity();

		try {
			//insert the mimetypes
			registerMimeType(this.system, systems);
			registerMimeType(this.application, applications);
			registerMimeType(this.audio, audios);
			registerMimeType(this.document, documents);
			registerMimeType(this.image, images);
			registerMimeType(this.vector, vectors);
			registerMimeType(this.video, videos);
			registerMimeType(this.zip, zips);
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
		}

		this.cm.removeTableFromCache(ICFileTypeHandler.class);

		handlers.cacheEntityByID();

		this.cm.removeTableFromCache(ICFileType.class);
		types.cacheEntityByID();

		try {
			//**insert the Root folder if it doesn't exist yet**/
			ICFileHome fileHome = (com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class);

			ICFile root;
			try {
				root = fileHome.findRootFolder();
			} catch (FinderException e) {
				
					ICFile file = fileHome.create();
					file.setName(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME);
					file.setLocalizationKey(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME);
					file.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
					file.setDescription("This is the top level folder it shouldn't be visible");
				try {
					ICApplicationBinding b = ((ICApplicationBindingHome)IDOLookup.getHome(ICApplicationBinding.class)).create();
					b.setKey(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_NAME);
					b.setBindingType(com.idega.core.file.data.ICFileBMPBean.IC_APPLICATION_BINDING_TYPE_SYSTEM_FOLDER);
					
					file.store();
					b.setValue(file.getPrimaryKey().toString());
					b.store();
				} catch (IDOStoreException e1) {
					e1.printStackTrace();
				} catch (EJBException e1) {
					e1.printStackTrace();
				}
				root = file;
			}

			//cache it!
			this.cm.cacheEntity(root, com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);

		} catch (RemoteException rex) {
			throw new EJBException(rex.getMessage());
		} catch (CreateException cex) {
			throw new EJBException(cex.getMessage());
		}

	}

	public void registerMimeType(String[] array, ICFileType type) throws RemoteException {
		int typeId = type.getID();
		ICMimeType mimetype;

		for (int i = 0; i < (array.length); i++) {
			//check if these common mimetypes exist and insert if not.
			mimetype = (ICMimeType)this.cm.getFromCachedTable(ICMimeType.class, array[i + 1]);
			if (mimetype == null) {
				String mimeType = array[i + 1];
				ICMimeTypeHome mimeHome = (ICMimeTypeHome)com.idega.data.IDOLookup.getHome(ICMimeType.class);
				try {
					mimetype = mimeHome.create();
					mimetype.setMimeTypeAndDescription(mimeType, array[i]);
					mimetype.setFileTypeId(typeId);
					mimetype.store();
				} catch (CreateException cex) {
					//ex.printStackTrace(System.err);
					System.err.println("[MediBundleStarter] : Error inserting MIME-TYPE for: " + mimeType);
				} catch (com.idega.data.IDOStoreException ex) {
					//ex.printStackTrace(System.err);
					System.err.println("[MediBundleStarter] : Error inserting MIME-TYPE for: " + mimeType);
				}
			}
			i++;
		}
	}

	/**
	 * @see com.idega.idegaweb.IWBundleStartable#stop(IWBundle)
	 */
	public void stop(IWBundle starterBundle) {
		//does nothing...
	}

}
