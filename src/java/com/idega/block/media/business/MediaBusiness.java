package com.idega.block.media.business;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import com.idega.block.media.data.MediaProperties;
import com.idega.business.IBOLookup;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.ICFileType;
import com.idega.core.file.data.ICFileTypeHandler;
import com.idega.core.file.data.ICMimeType;
import com.idega.data.IDOLookup;
import com.idega.data.IDOLookupException;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.MemoryFileBuffer;
import com.idega.io.MemoryOutputStream;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.user.business.GroupBusiness;
import com.idega.user.data.Group;
import com.idega.util.FileUtil;
import com.idega.util.caching.Cache;
/**
 *  Title: com.idega.block.media.business.MediaBusiness Description: The main
 *  business class of the Media classes which does all the real work. Copyright:
 *  Copyright (c) 2001 Company: idega software
 *
 * @author     <a href="mailto:eiki@idega.is>Eirikur S. Hrafnsson</a>
 * @created    2001
 * @version    1.0
 */
public class MediaBusiness {
	/**
	 *  Description of the Method
	 *
	 * @param  mediaProps      The MediaProperties class containing the path to the media
	 * @param  icFileParentId  The id of the media's parent in the database.
	 * <br> A value of -1 sets the parent to the default parent directory of the IWDBFS
	 * <br> A value of 0 saves the media with no parent.
	 * @param  iwc             The IWContext
	 * @return                 Return the MediaProperties class setId(the new media id) -1 if failed
	 */
	public static MediaProperties saveMediaToDB(MediaProperties mediaProps, int icFileParentId, IWContext iwc) {
		//ICFile icFile = saveMediaToDB(mediaProps.getUploadFile(),icFileParentId,iwc);
		saveMediaToDB(mediaProps.getUploadFile(), icFileParentId, iwc);
		return mediaProps;
		//    int id = -1;
		//    try {
		//
		//      long time1 = System.currentTimeMillis();
		//
		//      FileInputStream input = new FileInputStream( mediaProps.getRealPath() );
		//
		//      ICFile file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
		//      file.setName( mediaProps.getName() );
		//      file.setMimeType( mediaProps.getMimeType() );
		//
		//      file.setFileValue( input );
		//      file.setFileSize( ( int ) mediaProps.getSize() );
		//
		//
		//      file = saveMediaToDB(file,icFileParentId,iwc);
		//      long time2 = System.currentTimeMillis();
		//      System.out.println("MediaBusiness saveMediaToDB :"+ (time2 - time1 )+ " ms for "+mediaProps.getSize()+" bytes");
		//
		//      id = file.getID();
		//      mediaProps.setId( id );
		//
		//      try {
		//        FileUtil.delete(mediaProps.getRealPath() );
		//      }
		//      catch (Exception ex) {
		//        System.err.println("MediaBusiness: deleting the temporary file at "+mediaProps.getRealPath()+" failed.");
		//      }
		//    }
		//    catch( Exception e ) {
		//      e.printStackTrace( System.err );
		//      mediaProps.setId( -1 );
		//      return mediaProps;
		//    }
		//
		//    return mediaProps;
	}
	public static MediaProperties saveMediaToDBUnderRoot(MediaProperties mediaProps, IWContext iwc) {
		return saveMediaToDB(mediaProps, -1, iwc);
	}
	public static MediaProperties saveMediaToDBWithNoRoot(MediaProperties mediaProps, IWContext iwc) {
		return saveMediaToDB(mediaProps, 0, iwc);
	}
	/**
	
	 *  Description of the Method
	
	 *
	
	 * @param  file      The ICFile to insert
	
	 * @param  icFileParentId  The id of the media's parent in the database.
	
	 * <br> A value of -1 sets the parent to the default parent directory of the IWDBFS
	
	 * <br> A value of 0 saves the media with no parent.
	
	 * @param  iwc             The IWContext
	
	 * @return                 Returns the ICFile
	
	 */
	public static ICFile saveMediaToDB(ICFile file, int parentId, IWApplicationContext iwc) {
		try {
			file.store();
			if (parentId == -1) { //add to root
				ICFile rootNode = (ICFile)iwc.getIWMainApplication().getIWCacheManager().getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
				rootNode.addChild(file);
			} else if (parentId == 0) { // no parent
			} else { //register this parent
				ICFile rootNode = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(parentId));
				rootNode.addChild(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return file;
	}
	public static ICFile saveMediaToDB(ICFile file, int parentId) {
		try {
			file.store();
			if (parentId > 0) { //add to root
				ICFile rootNode = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(parentId));
				rootNode.addChild(file);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return file;
	}
	/**
	
	 * @param  iwc            The IWContext used to get the multipart form
	
	 * @return                MediaProperties An object that stores the uploaded
	
	 *      files location and extra parameters
	
	 * @todo                  this should be handled in IWContext Parses a
	
	 *      multi-part form , uploads the file part and returns a MediaProperties
	
	 *      class
	
	 */
	public static MediaProperties uploadToDiskAndGetMediaProperties(IWContext iwc) {
		MediaProperties mediaProps = null;
		HashMap parameters = new HashMap();
		Enumeration enumer = iwc.getParameterNames();
		if (enumer != null) {
			while (enumer.hasMoreElements()) {
				String name = (String)enumer.nextElement();
				String value = iwc.getParameter(name);
				if (value != null) {
					parameters.put(name, value);
				}
			}
		}
		UploadFile file = iwc.getUploadedFile();
		if (file != null) {
			mediaProps = new MediaProperties(file, parameters);
		}
		return mediaProps;
	}
	/**
	
	 *  Gets the current media parameter name from the request or session.
	
	 *
	
	 * @param  iwc  The IWContext
	
	 * @return      the parameter found or the default parameter name from
	
	 *      MediaConstants.FILE_IN_SESSION_PARAMETER_NAME
	
	 */
	public static String getMediaParameterNameInSession(IWContext iwc) {
		String fileInSessionParameter = null;
		if (iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME) != null) {
			fileInSessionParameter = iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
		} else if (iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME) != null) {
			fileInSessionParameter = (String)iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
		} else {
			//default name for the parameter
			fileInSessionParameter = MediaConstants.MEDIA_ID_IN_SESSION;
		}
		iwc.setSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME, fileInSessionParameter);
		return fileInSessionParameter;
	}
	/**
	
	 *  Gets the mediaId attribute
	
	 *
	
	 * @param  iwc  The IWContext
	
	 * @return      The mediaId value
	
	 */
	public static int getMediaId(IWContext iwc) {
		String fileInSessionParameter = getMediaParameterNameInSession(iwc);
		int id = -1;
		if (iwc.getParameter(fileInSessionParameter) != null) {
			id = Integer.parseInt(iwc.getParameter(fileInSessionParameter));
			//check parameters
		} else if (iwc.getSessionAttribute(fileInSessionParameter) != null) {
			id = Integer.parseInt((String)iwc.getSessionAttribute(fileInSessionParameter));
			//check the session parameters
		}
		return id;
	}
	/**
	
	*  Description of the Method
	
	*
	
	* @param  iwc  The IWContext
	
	*/
	public static void removeMediaIdFromSession(IWContext iwc) {
		iwc.removeSessionAttribute(getMediaParameterNameInSession(iwc));
	}
	/**
	
	 *  Saves the media id to session if not -1
	
	 *
	
	 * @param  iwc      Description of the Parameter
	
	 * @param  mediaId  Description of the Parameter
	
	 */
	public static void saveMediaIdToSession(IWContext iwc, int mediaId) {
		if (mediaId != -1) {
			iwc.setSessionAttribute(getMediaParameterNameInSession(iwc), String.valueOf(mediaId));
		}
	}
	/**
	
	 *  Gets a FileTypeHandler for this type of file (mime type)
	
	 *
	
	 * @param  iwc                           The IWContext for getting the
	
	 *      IWCacheManager
	
	 * @param  mimeType                      The mime type of this file
	
	 * @return                               A FileTypeHandler class
	
	 * @exception  MissingMimeTypeException  An exception that is thrown when the
	
	 *      database doesn't have this mimeType registered
	
	 */
	public static FileTypeHandler getFileTypeHandler(IWContext iwc, String mimeType) throws MissingMimeTypeException {
		try {
			IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
			ICFileTypeHandler typeHandler = (ICFileTypeHandler)cm.getFromCachedTable(ICFileTypeHandler.class, String.valueOf(getFileType(iwc, mimeType).getFileTypeHandlerID()));
			FileTypeHandler handler = FileTypeHandler.getInstance(iwc.getIWMainApplication(), typeHandler.getHandlerClass());
			//System.out.println("SELECTED HANDLER IS : "+typeHandler.getHandlerName());
			return handler;
		} catch (NullPointerException x) {
			/**
			
			 * @todo    find a suggested icfiletype and construct the exception with
			
			 *      it
			
			 */
			throw new MissingMimeTypeException("The mimetype is missing", mimeType);
		}
	}
	public static ICFileType getFileType(IWContext iwc, String mimeType) throws MissingMimeTypeException {
		try {
			IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
			ICFileType type = (ICFileType)cm.getFromCachedTable(ICFileType.class, String.valueOf(getFileTypeId(iwc, mimeType)));
			return type;
		} catch (NullPointerException x) {
			throw new MissingMimeTypeException("The mimetype is missing", mimeType);
		}
	}
	public static int getFileTypeId(IWContext iwc, String mimeType) throws MissingMimeTypeException {
		try {
			IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
			ICMimeType mime = (ICMimeType)cm.getFromCachedTable(ICMimeType.class, mimeType);
			return mime.getFileTypeID();
		} catch (NullPointerException x) {
			throw new MissingMimeTypeException("The mimetype is missing", mimeType);
		}
	}
	public static void saveMimeType(String mimeType, String description, int fileTypeId) {
		try {
			ICMimeType mime = ((com.idega.core.file.data.ICMimeTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICMimeType.class)).createLegacy();
			mime.setMimeTypeAndDescription(mimeType, description);
			mime.setFileTypeId(fileTypeId);
			mime.insert();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	
	*  Gets the cached getICMimeType entity Map
	
	* @todo implement
	
	* @param  iwc  The IWContext
	
	* @return      The ICMimeTypeMap value
	
	*/
	public static Map getICMimeTypeMap(IWContext iwc) {
		IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
		return cm.getCachedTableMap(ICMimeType.class);
	}
	/**
	
	 *  Gets the cached ICFileType entity Map
	
	 * @todo implement
	
	 * @param  iwc  The IWContext
	
	 * @return      The ICFileTypeMap value
	
	 */
	public static Map getICFileTypeMap(IWContext iwc) {
		IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
		return cm.getCachedTableMap(ICFileType.class);
	}
	/**
	
	*  Gets the cached ICFileTypeHandler entity Map
	
	* @todo implement
	
	* @param  iwc  The IWContext
	
	* @return      The ICFileTypeHandlerMap value
	
	*/
	public static Map getICFileTypeHandlerMap(IWContext iwc) {
		IWCacheManager cm = iwc.getIWMainApplication().getIWCacheManager();
		return cm.getCachedTableMap(ICFileTypeHandler.class);
	}
	/**
	
	 *  Deletes the media (marks as deleted) and all it's children.
	
	 *
	
	 * @param  mediaId  The id of the media to delete
	
	 * @return          Return true if succeeded false if failed
	
	 */
	public static boolean deleteMedia(int mediaId) {
		try {
			((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(mediaId)).delete();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace(System.err);
			return false;
		} catch (IDOLookupException e) {
			e.printStackTrace();
			return false;
		} catch (FinderException e) {
			e.printStackTrace();
			return false;
		}
	}
	//presentation helper stuff
	/**
	
	 * @return    The newFileLink value
	
	 * @todo      Move this to a utility class Gets the newFileLink attribute
	
	 */
	public static Link getNewFileLink() {
		Link L = (Link)MediaConstants.MEDIA_UPLOADER_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW);
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}
	/**
	
	 *  Gets the newFolderLink attribute
	
	 *
	
	 * @return    The newFolderLink value
	
	 */
	public static Link getNewFolderLink() {
		Link L = (Link)MediaConstants.MEDIA_FOLDER_EDITOR_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW);
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}

	public static Link getRenameFileLink() {
		Link L = (Link)MediaConstants.MEDIA_FOLDER_EDITOR_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_EDIT);
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}
	/**
	
	 * @return    The useImageLink value
	
	 * @todo      Move this to a utility class Gets the useImageLink attribute
	
	 */
	public static Link getUseImageLink() {
		Link L = (Link)MediaConstants.MEDIA_VIEWER_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_USE);
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}
	/**
	
	 *  Gets the mediaViewerLink attribute
	
	 *
	
	 * @return    The mediaViewerLink value
	
	 */
	public static Link getMediaViewerLink() {
		Link L = (Link)MediaConstants.MEDIA_VIEWER_LINK.clone();
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}
	/**
	
	 * @return    The deleteLink value
	
	 * @todo      Move this to a utility class Gets the deleteLink attribute
	
	 */
	public static Link getDeleteLink() {
		Link L = (Link)MediaConstants.MEDIA_VIEWER_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE);
		L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
		return L;
	}
	/**
	
	 * @return    The reloadLink value
	
	 * @todo      Move this to a utility class Gets the reloadLink attribute
	
	 */
	public static Link getReloadLink() {
		Link L = (Link)MediaConstants.MEDIA_TREE_VIEWER_LINK.clone();
		L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_RELOAD);
		L.setTarget(MediaConstants.TARGET_MEDIA_TREE);
		return L;
	}
	/**
	
	 *  Checks if the ICFile is a folder (has the mimetype
	
	 *  com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER)
	
	 *
	
	 * @param  file  The ICFile to check
	
	 * @return       true if folder otherwise false
	
	 */
	public static boolean isFolder(ICFile file) {
		return file.isFolder();
	}
	/**
	
	 * @param  iwc  The IWContext
	
	 * @return      Description of the Return Value
	
	 * @todo        Move this to a utility class Description of the Method
	
	 */
	public static boolean reloadOnClose(IWContext iwc) {
		if (iwc.getParameter(MediaConstants.MEDIA_ACTION_RELOAD) != null) {
			return true;
		} else {
			return false;
		}
	}
	public static ICFile saveMediaToDBUploadFolder(UploadFile uploadFile, IWContext iwc) {
		return saveMediaToDB(uploadFile, 0, iwc);
	}
	/**
	
	  *  Description of the Method
	
	  *
	
	  * @param  mediaProps      The MediaProperties class containing the path to the media
	
	  * @param  icFileParentId  The id of the media's parent in the database.
	
	  * <br> A value of -1 sets the parent to the default parent directory of the IWDBFS
	
	  * <br> A value of 0 saves the media with no parent.
	
	  * @param  iwc             The IWContext
	
	  * @return                 Return the ICFile class, null if failed, sets uploadfile.getID() = com.idega.core.data.ICFileBMPBean.getID() or -1 if failed
	
	  */
	public static ICFile saveMediaToDB(UploadFile uploadFile, int icFileParentId, IWContext iwc) {
		int id = -1;
		ICFile file = null;
		try {
			long time1 = System.currentTimeMillis();
			FileInputStream input = new FileInputStream(uploadFile.getRealPath());
			file = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
			file.setName(uploadFile.getName());
			file.setMimeType(uploadFile.getMimeType());
			file.setFileValue(input);
			file.setFileSize((int)uploadFile.getSize());
			file = saveMediaToDB(file, icFileParentId, iwc);
			long time2 = System.currentTimeMillis();
			System.out.println("MediaBusiness saveMediaToDB :" + (time2 - time1) + " ms for " + uploadFile.getSize() + " bytes");
			id = ((Integer)file.getPrimaryKey()).intValue();
			uploadFile.setId(id);
			try {
				FileUtil.delete(uploadFile);
				//uploadFile = null;
			} catch (Exception ex) {
				System.err.println("MediaBusiness: deleting the temporary file at " + uploadFile.getRealPath() + " failed.");
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
			uploadFile.setId(-1);
			return null;
		}
		return file;
		//    int parentId = getMediaId(iwc);
		//    ICFile file = null;
		//    int id = -1;
		//    try{
		//      FileInputStream input = new FileInputStream(uploadFile.getRealPath());
		//      file = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
		//      file.setName(uploadFile.getName());
		//      file.setMimeType(uploadFile.getMimeType() );
		//
		//      System.out.println("MIMETYPE: "+uploadFile.getMimeType());
		//
		//      file.setFileValue(input);
		//      file.setFileSize((int)uploadFile.getSize());
		//      file.insert();
		//
		//      if( parentId==-1 ){
		//        ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
		//        rootNode.addChild(file);
		//      }
		//      else {
		//        ICFile rootNode = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).findByPrimaryKeyLegacy(parentId);
		//        rootNode.addChild(file);
		//      }
		//
		//      id = file.getID();
		//      uploadFile.setId(id);
		//    }
		//    catch(Exception e){
		//      e.printStackTrace(System.err);
		//      uploadFile.setId(-1);
		//      return file;
		//    }
		//
		//    return file;
	}
	public static boolean moveMedia(ICFile media, ICFile newParent) {
		try {
			ICFile currentParent = (ICFile)media.getParentEntity();
			if (currentParent != null && currentParent.getPrimaryKey() != null) {
				currentParent.removeChild(media);
			}
			newParent.addChild(media);
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace(System.err);
			return false;
		}
	}
	public static boolean moveMedia(int mediaId, int newParentId) {
		try {
			ICFile media = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(mediaId));
			ICFile newParent = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(newParentId));
			return moveMedia(media, newParent);
		}catch (IDOLookupException e) {
			e.printStackTrace();
		} catch (FinderException e) {
			e.printStackTrace();
		}
		return false;
	}
	public static Cache getCachedFileInfo(int icFileId, IWMainApplication iwma) {
		return (Cache)getCachedFileInfo(icFileId, ICFile.class, iwma);
	}
	public static Cache getCachedFileInfo(int id, Class entityClass, IWMainApplication iwma) {
		return (Cache)iwma.getIWCacheManager().getCachedBlobObject(entityClass.getName(), id, iwma);
	}
	public static String getMediaURL(ICFile file, IWMainApplication iwma) {
		return getMediaURL(((Integer)file.getPrimaryKey()).intValue(), iwma);
	}
	public static String getMediaURL(int fileID, IWMainApplication iwma) {
		Cache cache = getCachedFileInfo(fileID, iwma);
		return cache.getVirtualPathToFile();
	}
	public static String getMediaURL(int id, Class entityClass, IWMainApplication iwma) {
		Cache cache = getCachedFileInfo(id, entityClass, iwma);
		return cache.getVirtualPathToFile();
	}
	public static ICFile createSubFolder(int parentId, String name) throws FinderException, IDOLookupException, RemoteException, CreateException, SQLException {
		ICFile parent = ((ICFileHome)IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(parentId));
		return createSubFolder(parent, name);
	}
	public static ICFile createSubFolder(ICFile parent, String name) throws java.rmi.RemoteException, IDOLookupException, CreateException, SQLException {
		ICFile folder = ((ICFileHome)IDOLookup.getHome(ICFile.class)).create();
		folder.setName(name);
		folder.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		folder.store();
		parent.addChild(folder);
		return folder;
	}
	public static MemoryFileBuffer getMediaBuffer(int fileId) throws Exception {
		ICFile file = ((ICFileHome)IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(fileId));
		return getMediaBuffer(file);
	}
	public static MemoryFileBuffer getMediaBuffer(ICFile file) throws Exception {
		BufferedInputStream bis = new BufferedInputStream(file.getFileValue());
		MemoryFileBuffer buffer = new MemoryFileBuffer();
		MemoryOutputStream bos = new MemoryOutputStream(buffer);
		byte[] buff = new byte[2048];
		int bytesRead;
		// Simple read/write loop.
		while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
			bos.write(buff, 0, bytesRead);
		}
		return buffer;
	}
	
	public static ICFile getGroupHomeFolder(Group group, IWApplicationContext iwac) throws RemoteException, CreateException{
		ICFile folder = group.getHomeFolder();
		if(folder == null){
			GroupBusiness b = (GroupBusiness)IBOLookup.getServiceInstance(iwac,GroupBusiness.class);
			folder = b.createGroupHomeFolder(group);
		}
		return folder;
	}
	
	public static Collection getGroupHomeFolders(Collection groups, IWApplicationContext iwac) throws RemoteException, CreateException{
		GroupBusiness b = null;
		Collection toReturn = new ArrayList();
		if(!groups.isEmpty()){
			for (Iterator iter = groups.iterator(); iter.hasNext();) {
				Group group = (Group)iter.next();
				if(group != null){
					ICFile folder = group.getHomeFolder();
					if(folder == null){
						if(b==null){
							b=(GroupBusiness)IBOLookup.getServiceInstance(iwac,GroupBusiness.class);
						}
						folder = b.createGroupHomeFolder(group);
					}
					toReturn.add(folder);
				}
			}
		}
		return toReturn;
	}
}
