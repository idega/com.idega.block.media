package com.idega.block.media.business;



import java.io.*;
import java.util.*;
import java.sql.*;

//this package must be installed
import com.oreilly.servlet.multipart.*;
import com.idega.presentation.IWContext;
import com.idega.core.data.ICFile;
import com.idega.core.data.ICFileType;
import com.idega.core.data.ICFileTypeHandler;
import com.idega.core.data.ICMimeType;
import com.idega.block.media.data.MediaProperties;
import com.idega.util.FileUtil;
import com.idega.idegaweb.IWCacheManager;
import com.idega.block.media.presentation.*;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.*;
import com.idega.io.UploadFile;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.idegaweb.IWMainApplication;

/**
 * Title: com.idega.block.media.business.MediaBusiness
 * Description: The main business class of the Media classes which does all the real work.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaBusiness  {

  public static MediaProperties SaveMediaToDB(MediaProperties mediaProps, IWContext iwc){
    String parentId = getMediaId(iwc);
    int id = -1;
    try{
      FileInputStream input = new FileInputStream(mediaProps.getRealPath());
      ICFile file = new ICFile();
      file.setName(mediaProps.getName());
      file.setMimeType(mediaProps.getMimeType() );

      System.out.println("MIMETYPE: "+mediaProps.getMimeType());

      file.setFileValue(input);
      file.setFileSize((int)mediaProps.getSize());
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
      mediaProps.setId(id);
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      mediaProps.setId(-1);
      return mediaProps;
    }

    return mediaProps;

  }

  /**
   * @deprecated : tempImplementation
   * @param uplaodFile
   * @param iwc
   * @return
   */
  public static ICFile SaveMediaToDB(UploadFile uplaodFile, IWContext iwc){
    String parentId = getMediaId(iwc);
    ICFile file = null;
    int id = -1;
    try{
      FileInputStream input = new FileInputStream(uplaodFile.getRealPath());
      file = new ICFile();
      file.setName(uplaodFile.getName());
      file.setMimeType(uplaodFile.getMimeType() );

      System.out.println("MIMETYPE: "+uplaodFile.getMimeType());

      file.setFileValue(input);
      file.setFileSize((int)uplaodFile.getSize());
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
      uplaodFile.setId(id);
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      uplaodFile.setId(-1);
      return file;
    }

    return file;

  }


  public static MediaProperties doUpload(IWContext iwc) throws Exception{
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


//  public static MediaProperties doUpload(IWContext iwc) throws Exception{
//    String sep = FileUtil.getFileSeparator();
//    StringBuffer pathToFile = new StringBuffer();
//    pathToFile.append(iwc.getApplication().getApplicationRealPath());
//    pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
//    pathToFile.append(sep);
//
//    FileUtil.createFolder(pathToFile.toString());
//
//    MediaProperties  mediaProps = null;
//
//    MultipartParser mp = new MultipartParser(iwc.getRequest(),MediaConstants.FILE_UPLOAD_MAXIMUM_SIZE);/**@todo the maximum size should be flexible could just match the filesiz we have? or don't we**/
//    Part part;
//    File dir = null;
//    String value = null;
//    HashMap parameters = new HashMap();
//
//    while ((part = mp.readNextPart()) != null) {
//      String name = part.getName();
//      if(part.isParam()){
//	ParamPart paramPart = (ParamPart) part;
//	parameters.put(paramPart.getName(),paramPart.getStringValue());
//	//System.out.println(" PARAMETERS "+paramPart.getName()+" : "+paramPart.getStringValue());
//      }
//      else if (part.isFile()) {
//	// it's a file part
//	FilePart filePart = (FilePart) part;
//	String fileName = filePart.getFileName();
//
//	if (fileName != null) {
//	  pathToFile.append(fileName);
//	  String filePath = pathToFile.toString();
//	  StringBuffer webPath = new StringBuffer();
//	  webPath.append('/');
//	  webPath.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
//	  webPath.append('/');
//	  webPath.append(fileName);
//
//	  File file = new File(filePath);
//	  int size = (int) filePart.writeTo(file);
//
//	// Opera mimetype fix ( aron@idega.is )
//	String mimetype = filePart.getContentType();
//	if(mimetype!=null){
//	  StringTokenizer tokenizer = new StringTokenizer(mimetype," ;:");
//	  if(tokenizer.hasMoreTokens())
//	    mimetype = tokenizer.nextToken();
//	}
//
//      /*
//	System.out.println("MediaBusiness : File size"+size);
//	System.out.println("MediaBusiness : File filePath"+filePath);
//	System.out.println("MediaBusiness : File webPath"+webPath.toString());
//	System.out.println("MediaBusiness : File getContentType"+filePart.getContentType());
//       System.out.println("MediaBusiness : File fileName"+fileName);
//	*/
//	  mediaProps = new MediaProperties(fileName,mimetype,filePath,webPath.toString(),size,parameters);
//	}
//      }
//    }
//
//    return mediaProps;
//}



 public static String getMediaParameterNameInSession(IWContext iwc){
    String fileInSessionParameter = null;
     if(iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null){
      fileInSessionParameter = iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
    }
    else if(iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null){
      fileInSessionParameter = (String) iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
    }
    else{//default name for the parameter
      fileInSessionParameter = MediaConstants.MEDIA_ID_IN_SESSION;
    }

    iwc.setSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME,fileInSessionParameter);

    return fileInSessionParameter;
  }

  public static String getMediaId(IWContext iwc){
    String fileInSessionParameter = getMediaParameterNameInSession(iwc);

    String id = "-1";

    if(iwc.getParameter(fileInSessionParameter)!=null){
      id = iwc.getParameter(fileInSessionParameter);
    }
    else if(iwc.getSessionAttribute(fileInSessionParameter)!=null){
      id = (String) iwc.getSessionAttribute(fileInSessionParameter);
    }



    return id;
  }

  public static void removeMediaIdFromSession(IWContext iwc){
    iwc.removeSessionAttribute(getMediaParameterNameInSession(iwc));
  }

  public static void saveMediaIdToSession(IWContext iwc,String mediaId){
    iwc.setSessionAttribute(getMediaParameterNameInSession(iwc),mediaId);
  }

  public static MediaViewer saveMedia(IWContext iwc){
    MediaProperties mediaProps = null;
    mediaProps = (MediaProperties) iwc.getSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME);
    iwc.removeSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME);
    mediaProps = MediaBusiness.SaveMediaToDB(mediaProps,iwc);
    return new MediaViewer(mediaProps);
 }

  public static MediaProperties uploadToDiskAndGetMediaProperties(IWContext iwc){
    MediaProperties mediaProps = null;
    try {
      mediaProps = MediaBusiness.doUpload(iwc);
      System.out.println("MediaBusiness : Uploading file with mimetype = "+mediaProps.getMimeType());
      iwc.setSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME,mediaProps);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }

    return mediaProps;
  }

  public static FileTypeHandler getFileTypeHandler(IWContext iwc ,String mimeType) throws NullPointerException{

    IWCacheManager cm = iwc.getApplication().getIWCacheManager();
    ICMimeType mime = (ICMimeType) cm.getFromCachedTable(ICMimeType.class,mimeType);
    //System.out.println("type id "+mimeType);
    ICFileType type = (ICFileType) cm.getFromCachedTable(ICFileType.class,Integer.toString(mime.getFileTypeID()));
    //System.out.println("handler id : "+type.getFileTypeHandlerID());
    ICFileTypeHandler typeHandler = (ICFileTypeHandler) cm.getFromCachedTable(ICFileTypeHandler.class,Integer.toString(type.getFileTypeHandlerID()));
    FileTypeHandler handler = FileTypeHandler.getInstance(iwc.getApplication(),typeHandler.getHandlerClass());
    //System.out.println("SELECTED HANDLER IS : "+typeHandler.getHandlerName());
   return handler;

  }


  //presentation helper stuff

  public static Link getNewFileLink(){
    Link L = (Link) MediaConstants.MEDIA_UPLOADER_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_NEW);
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getNewFolderLink(){
    Link L = (Link) MediaConstants.MEDIA_FOLDER_EDITOR_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_NEW);
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getUseImageLink(){
    Link L = (Link) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_USE);
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getUseFileLink(){
    Link L = (Link) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE);
    L.setOnClick(getSaveImageFunctionName());
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getMediaViewerLink(){
    Link L = (Link) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getDeleteLink(){
    Link L = (Link) MediaConstants.MEDIA_VIEWER_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_DELETE);
    L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    return L;
  }

  public static Link getReloadLink(){
    Link L = (Link) MediaConstants.MEDIA_TREE_VIEWER_LINK.clone();
    L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_RELOAD);
    L.setTarget(MediaConstants.TARGET_MEDIA_TREE);
    return L;
  }

  public static String getSaveImageFunctionName(){
    return MediaConstants.SAVE_IMAGE_FUNCTION_NAME;
  }

  public static String getSaveImageFunction(){
    if( MediaConstants.IMAGE_SAVE_JS_FUNCTION == null ){
      StringBuffer function = new StringBuffer("");
      function.append(" var iImageId = -1 ; \n");
      function.append("function "+getSaveImageFunctionName()+" {\n \t");
      function.append("top.window.opener.setImageId(iImageId) ; \n \t");
      function.append("top.window.close(); \n }");
      MediaConstants.IMAGE_SAVE_JS_FUNCTION = function.toString();
    }

    return MediaConstants.IMAGE_SAVE_JS_FUNCTION;

  }

  public static String getFunction(int id){
    return "setImageId("+id+")";
  }

  public static String getImageChangeJSFunction(String imageName){
    if( MediaConstants.IMAGE_CHANGE_JS_FUNCTION == null ){

      StringBuffer function = new StringBuffer("");//var imageName = \"rugl\"; \n");
      function.append("function setImageId(imageId,imagename) { \n \t");
      function.append("if (document.images) { \n \t\t");
      function.append("document.images['im'+imagename].src = \"/servlet/MediaServlet/\"+imageId+\"media?media_id=\"+imageId; \n\t}\n\t");
      function.append("document.forms[0].elements[getElementIndex(imagename)].value = imageId \n}\n");
      function.append("function getElementIndex(elementname){ \n \t");
      function.append("len = document.forms[0].length \n \t");
      function.append("for(i=0; i<len; i++){ \n \t \t");
      function.append("if(document.forms[0].elements[i].name == elementname.toString()){ \n\t\t ");
      function.append("return i; \n \t \t} \n  \t} \n }\n");

      MediaConstants.IMAGE_CHANGE_JS_FUNCTION = function.toString();
    }

    return MediaConstants.IMAGE_CHANGE_JS_FUNCTION;
  }
  public static boolean isFolder(ICFile file){
   if(file.getMimeType().equals(ICMimeType.IC_MIME_TYPE_FOLDER)) return true;
   else return false;
  }

  public static boolean reloadOnClose(IWContext iwc){
    if(iwc.getParameter(MediaConstants.MEDIA_ACTION_RELOAD)!=null ) return true;
    else return false;
  }

  public static boolean deleteMedia(int mediaId){
    try {
      new ICFile(mediaId).delete();
      return true;
    }
    catch (SQLException ex) {
      ex.printStackTrace(System.err);
      return false;
    }
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




  public static String getMediaURL(ICFile file) {
    if( (file!=null) && (file.getID()!=-1) ){
      StringBuffer url = new StringBuffer();
      url.append(IWMainApplication.MEDIA_SERVLET_URL);
      if(!IWMainApplication.MEDIA_SERVLET_URL.endsWith("/")){
        url.append('/');
      }
      String name = file.getName();
      if(name != null && name.length() > 0){
        int indexOfDot = name.indexOf(".");
        if(indexOfDot != -1){
          url.append(name.substring(0,indexOfDot));
        } else {
          url.append(name);
        }
      } else {
        url.append(file.getID());
        url.append("media");
      }
      url.append('?');
      url.append(com.idega.block.media.servlet.MediaServlet.PARAMETER_NAME);
      url.append('=');
      url.append(file.getID());

      return url.toString();
    }
    return null;
  }

  public static String getMediaURL(int fileID) {
    ICFile file = null;
    try {
      file = new ICFile(fileID);
    }
    catch (Exception e) {
      file = null;
    }
    return getMediaURL(file);
  }





}//end of class
