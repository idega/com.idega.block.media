package com.idega.block.media.business;

import com.idega.util.caching.Cache;
import com.idega.idegaweb.IWMainApplication;
import java.util.*;
import java.sql.SQLException;
import com.idega.block.media.data.MediaProperties;
import com.idega.block.media.presentation.MediaViewer;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileType;
import com.idega.core.data.ICFileTypeHandler;
import com.idega.core.data.ICMimeType;
import com.idega.idegaweb.IWCacheManager;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.util.FileUtil;
import com.idega.io.UploadFile;
//this package must be installed
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import java.io.File;
import java.io.FileInputStream;

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
  public static MediaProperties saveMediaToDB( MediaProperties mediaProps, int icFileParentId, IWContext iwc ) {
    int id = -1;
    try {
      FileInputStream input = new FileInputStream( mediaProps.getRealPath() );
      ICFile file = new ICFile();
      file.setName( mediaProps.getName() );
      file.setMimeType( mediaProps.getMimeType() );

      file.setFileValue( input );
      file.setFileSize( ( int ) mediaProps.getSize() );
      file.insert();

      if( icFileParentId == -1 ) {//add to root
        ICFile rootNode = ( ICFile ) iwc.getApplication().getIWCacheManager().getCachedEntity( ICFile.IC_ROOT_FOLDER_CACHE_KEY );
        rootNode.addChild( file );
      }
      else if(icFileParentId == 0){// no parent

      }
      else{//register this parent
        ICFile rootNode = new ICFile( icFileParentId );
        rootNode.addChild( file );
      }

      id = file.getID();
      mediaProps.setId( id );

      try {
        FileUtil.delete(mediaProps.getRealPath() );
      }
      catch (Exception ex) {
        System.err.println("MediaBusiness: deleting the temporary file at "+mediaProps.getRealPath()+" failed.");
      }
    }
    catch( Exception e ) {
      e.printStackTrace( System.err );
      mediaProps.setId( -1 );
      return mediaProps;
    }

    return mediaProps;
  }

  public static MediaProperties saveMediaToDBUnderRoot( MediaProperties mediaProps,IWContext iwc ) {
    return saveMediaToDB(mediaProps,-1,iwc);
  }

  public static MediaProperties saveMediaToDBWithNoRoot( MediaProperties mediaProps,IWContext iwc ) {
    return saveMediaToDB(mediaProps,0,iwc);
  }

  /**
   * @param  iwc            The IWContext used to get the multipart form
   * @return                MediaProperties An object that stores the uploaded
   *      files location and extra parameters
   * @todo                  this should be handled in IWContext Parses a
   *      multi-part form , uploads the file part and returns a MediaProperties
   *      class
   */
  public static MediaProperties uploadToDiskAndGetMediaProperties( IWContext iwc ) {
    MediaProperties  mediaProps = null;
    HashMap parameters = new HashMap();

    Enumeration enum = iwc.getParameterNames();
    if(enum != null){
      while (enum.hasMoreElements()) {
        String name = (String)enum.nextElement();
        String value = iwc.getParameter(name);
        if(value != null){
          parameters.put(name,value);
        }
      }
    }

    UploadFile file = iwc.getUploadedFile();

    if(file != null){
      mediaProps = new MediaProperties(file.getName(),file.getMimeType(),file.getRealPath(),file.getWebPath(),(int)file.getSize(),parameters);
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
  public static String getMediaParameterNameInSession( IWContext iwc ) {
    String fileInSessionParameter = null;
    if( iwc.getParameter( MediaConstants.FILE_IN_SESSION_PARAMETER_NAME ) != null ) {
      fileInSessionParameter = iwc.getParameter( MediaConstants.FILE_IN_SESSION_PARAMETER_NAME );
    } else if( iwc.getSessionAttribute( MediaConstants.FILE_IN_SESSION_PARAMETER_NAME ) != null ) {
      fileInSessionParameter = ( String ) iwc.getSessionAttribute( MediaConstants.FILE_IN_SESSION_PARAMETER_NAME );
    } else {
      //default name for the parameter
      fileInSessionParameter = MediaConstants.MEDIA_ID_IN_SESSION;
    }

    iwc.setSessionAttribute( MediaConstants.FILE_IN_SESSION_PARAMETER_NAME, fileInSessionParameter );

    return fileInSessionParameter;
  }


  /**
   *  Gets the mediaId attribute
   *
   * @param  iwc  The IWContext
   * @return      The mediaId value
   */
  public static String getMediaId( IWContext iwc ) {
    String fileInSessionParameter = getMediaParameterNameInSession( iwc );

    String id = "-1";

    if( iwc.getParameter( fileInSessionParameter ) != null ) {
      id = iwc.getParameter( fileInSessionParameter );
    } else if( iwc.getSessionAttribute( fileInSessionParameter ) != null ) {
      id = ( String ) iwc.getSessionAttribute( fileInSessionParameter );
    }

    return id;
  }


  /**
   *  Description of the Method
   *
   * @param  iwc  The IWContext
   */
  public static void removeMediaIdFromSession( IWContext iwc ) {
    iwc.removeSessionAttribute( getMediaParameterNameInSession( iwc ) );
  }


  /**
   *  Description of the Method
   *
   * @param  iwc      Description of the Parameter
   * @param  mediaId  Description of the Parameter
   */
  public static void saveMediaIdToSession( IWContext iwc, String mediaId ) {
    iwc.setSessionAttribute( getMediaParameterNameInSession( iwc ), mediaId );
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
  public static FileTypeHandler getFileTypeHandler( IWContext iwc, String mimeType ) throws MissingMimeTypeException {
    try {
      IWCacheManager cm = iwc.getApplication().getIWCacheManager();
      ICMimeType mime = ( ICMimeType ) cm.getFromCachedTable( ICMimeType.class, mimeType );
      //System.out.println("type id "+mimeType);
      ICFileType type = ( ICFileType ) cm.getFromCachedTable( ICFileType.class, Integer.toString( mime.getFileTypeID() ) );
      //System.out.println("handler id : "+type.getFileTypeHandlerID());
      ICFileTypeHandler typeHandler = ( ICFileTypeHandler ) cm.getFromCachedTable( ICFileTypeHandler.class, Integer.toString( type.getFileTypeHandlerID() ) );
      FileTypeHandler handler = FileTypeHandler.getInstance( iwc.getApplication(), typeHandler.getHandlerClass() );
      //System.out.println("SELECTED HANDLER IS : "+typeHandler.getHandlerName());
      return handler;
    }
    catch( NullPointerException x ) {
      /**
       * @todo    find a suggested icfiletype and construct the exception with
       *      it
       */
      throw new MissingMimeTypeException( "The mimetype is missing", mimeType );
    }

  }


   public static void saveMimeType(String mimeType, String description, int fileTypeId){
    try{
      ICMimeType mime = new ICMimeType();
      mime.setMimeTypeAndDescription(mimeType,description);
      mime.setFileTypeId(fileTypeId);
      mime.insert();
    }
    catch( Exception ex ){
     ex.printStackTrace();
    }
  }

  /**
   *  Gets the cached FileType entity Map
   * @todo implement
   * @param  iwc  The IWContext
   * @return      The fileTypeMap value
   */
  public static Map getFileTypeMap( IWContext iwc ) {
    return null;
  }



  /**
   *  Deletes the media (marks as deleted) and all it's children.
   *
   * @param  mediaId  The id of the media to delete
   * @return          Return true if succeeded false if failed
   */
  public static boolean deleteMedia( int mediaId ) {
    try {
      new ICFile( mediaId ).delete();
      return true;
    }
    catch( SQLException ex ) {
      ex.printStackTrace( System.err );
      return false;
    }
  }

  //presentation helper stuff

  /**
   * @return    The newFileLink value
   * @todo      Move this to a utility class Gets the newFileLink attribute
   */
  public static Link getNewFileLink() {
    Link L = ( Link ) MediaConstants.MEDIA_UPLOADER_LINK.clone();
    L.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW );
    L.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );
    return L;
  }


  /**
   *  Gets the newFolderLink attribute
   *
   * @return    The newFolderLink value
   */
  public static Link getNewFolderLink() {
    Link L = ( Link ) MediaConstants.MEDIA_FOLDER_EDITOR_LINK.clone();
    L.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW );
    L.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );
    return L;
  }


  /**
   * @return    The useImageLink value
   * @todo      Move this to a utility class Gets the useImageLink attribute
   */
  public static Link getUseImageLink() {
    Link L = ( Link ) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_USE );
    L.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );
    return L;
  }


  /**
   *  Gets the mediaViewerLink attribute
   *
   * @return    The mediaViewerLink value
   */
  public static Link getMediaViewerLink() {
    Link L = ( Link ) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );
    return L;
  }


  /**
   * @return    The deleteLink value
   * @todo      Move this to a utility class Gets the deleteLink attribute
   */
  public static Link getDeleteLink() {
    Link L = ( Link ) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE );
    L.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );
    return L;
  }


  /**
   * @return    The reloadLink value
   * @todo      Move this to a utility class Gets the reloadLink attribute
   */
  public static Link getReloadLink() {
    Link L = ( Link ) MediaConstants.MEDIA_TREE_VIEWER_LINK.clone();
    L.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_RELOAD );
    L.setTarget( MediaConstants.TARGET_MEDIA_TREE );
    return L;
  }


  /**
   *  Checks if the ICFile is a folder (has the mimetype
   *  ICMimeType.IC_MIME_TYPE_FOLDER)
   *
   * @param  file  The ICFile to check
   * @return       true if folder otherwise false
   */
  public static boolean isFolder( ICFile file ) {
    if( file.getMimeType().equals( ICMimeType.IC_MIME_TYPE_FOLDER ) ) {
      return true;
    } else {
      return false;
    }
  }


  /**
   * @param  iwc  The IWContext
   * @return      Description of the Return Value
   * @todo        Move this to a utility class Description of the Method
   */
  public static boolean reloadOnClose( IWContext iwc ) {
    if( iwc.getParameter( MediaConstants.MEDIA_ACTION_RELOAD ) != null ) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * @deprecated : tempImplementation
   * @param uplaodFile
   * @param iwc
   * @return
   * @ todo reimplement
   */
  public static ICFile SaveMediaToDB(UploadFile uploadFile, IWContext iwc){
    String parentId = getMediaId(iwc);
    ICFile file = null;
    int id = -1;
    try{
      FileInputStream input = new FileInputStream(uploadFile.getRealPath());
      file = new ICFile();
      file.setName(uploadFile.getName());
      file.setMimeType(uploadFile.getMimeType() );

      System.out.println("MIMETYPE: "+uploadFile.getMimeType());

      file.setFileValue(input);
      file.setFileSize((int)uploadFile.getSize());
      file.insert();

      if( (parentId==null) || (parentId.equals("-1")) ){
        ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);
        rootNode.addChild(file);
      }
      else {
        int iParentId = Integer.parseInt(parentId);
        ICFile rootNode = new ICFile(iParentId);
        rootNode.addChild(file);
      }

      id = file.getID();
      uploadFile.setId(id);
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      uploadFile.setId(-1);
      return file;
    }

    return file;

  }

  public static boolean moveMedia(ICFile media, ICFile newParent){
    try {
      ICFile currentParent = (ICFile)media.getParentEntity();
      if(currentParent != null && currentParent.getID() != -1){
        currentParent.removeChild(media);
      }
      newParent.addChild(media);
      return true;
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.err);
      return false;
    }
  }

  public static boolean moveMedia(int mediaId, int newParentId){
    try {
      ICFile media = new ICFile(mediaId);
      ICFile newParent = new ICFile(newParentId);
      return moveMedia(media,newParent);
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.err);
      return false;
    }
  }


  public static Cache getCachedFileInfo(int icFileId, IWMainApplication iwma){
    return (Cache) getCachedFileInfo(icFileId,ICFile.class,iwma);
  }

  public static Cache getCachedFileInfo(int id, Class entityClass, IWMainApplication iwma){
    return (Cache) iwma.getIWCacheManager().getCachedBlobObject(entityClass.getName(),id,iwma);
  }

  public static String getMediaURL(ICFile file, IWMainApplication iwma) {
   return getMediaURL(file.getID(),iwma);
  }

  public static String getMediaURL(int fileID, IWMainApplication iwma) {
    Cache cache = getCachedFileInfo(fileID,iwma);
    return cache.getVirtualPathToFile();
  }

  public static String getMediaURL(int id, Class entityClass, IWMainApplication iwma) {
    Cache cache = getCachedFileInfo(id,entityClass,iwma);
    return cache.getVirtualPathToFile();
  }

}
