package com.idega.block.media.presentation;
import com.idega.block.media.business.FileTypeHandler;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.data.MediaProperties;
import com.idega.core.data.ICFile;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.util.caching.Cache;
/**

 *  Title: com.idega.block.media.presentation.MediaViewer Description: A viewer
 *  class for viewing data from the ic_file table using the correct plugin (if
 *  needed) Copyright: Copyright (c) 2001 Company: idega software
 *
 * @author     Eirikur S. Hrafnsson eiki@idega.is
 * @created    13. mars 2002
 * @version    1.0

 */
public class MediaViewer extends Block {
	private String fileInSessionParameter = "ic_file_id";
	private MediaProperties props = null;
	private IWResourceBundle iwrb;
	private int mediaId = -1;
	/**
	
	 *  Constructor for the MediaViewer object
	
	 */
	public MediaViewer() {
	}
	/**
	
	 *  Constructor for the MediaViewer object
	
	 */
	public MediaViewer(int mediaId) {
		this.mediaId = mediaId;
	}
	/**
	
	 *  Constructor for the MediaViewer object
	
	 *
	
	 * @param  props  Description of the Parameter
	
	 */
	public MediaViewer(MediaProperties props) {
		this();
		this.props = props;
		mediaId = props.getId();
	}
	/**
	
	 *  This is the main method were we decide is we show the file from disk or db
	
	 *
	
	 * @param  iwc            The IWContext
	
	 * @exception  Exception  An Exception of an unknown type
	
	 */
	public void main(IWContext iwc) throws Exception {
		if ((mediaId == -1) && (props == null)) {
			mediaId = MediaBusiness.getMediaId(iwc);
		}
		if (mediaId != -1) {
			viewFileFromDB(iwc, mediaId);
		}
		else if (props != null) {
			viewFileFromDisk(iwc, props);
		}
	}
	/**
	
	 *  Finds the right filehandler and displays the media from disk
	
	 *
	
	 * @param  iwc            The IWContext
	
	 * @param  props          The MediaProperties
	
	 * @exception  Exception  Description of the Exception
	
	 */
	protected void viewFileFromDisk(IWContext iwc, MediaProperties props) {
		FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc, props.getMimeType());
		add(handler.getPresentationObject(props, iwc));
	}
	/**
	
	 *  Finds a correct filehandler and displays the media
	
	 *
	
	 * @param  iwc      the IWContext
	
	 * @param  mediaId  The media id
	
	 */
	protected void viewFileFromDB(IWContext iwc, int id) {
		Cache cache = FileTypeHandler.getCachedFileInfo(mediaId, iwc);
		ICFile file = (ICFile) cache.getEntity();
		FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc, file.getMimeType());
		add(handler.getPresentationObject(id, iwc));
	}
	/**
	
	 *  Gets the bundleIdentifier attribute of the MediaViewer object
	
	 *
	
	 * @return    The bundleIdentifier value
	
	 */
	public String getBundleIdentifier() {
		return MediaConstants.IW_BUNDLE_IDENTIFIER;
	}
}
