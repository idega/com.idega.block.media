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
import com.idega.block.media.business.MediaProperties;
import com.idega.util.FileUtil;
import com.idega.idegaweb.IWCacheManager;

/**
 * Title: com.idega.block.media.business.MediaBusiness
 * Description: The main business class of the Media classes which does all the real work.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */


public class MediaBusiness  {

  public static int SaveMediaToDB(MediaProperties mediaProps){
    int id = -1;

    try{
      FileInputStream input = new FileInputStream(mediaProps.getRealPath());
      ICFile file = new ICFile();
      file.setName(mediaProps.getName());
      file.setMimeType(mediaProps.getContentType());
      System.out.println("MIMETYPE"+mediaProps.getContentType());
      file.setFileValue(input);
      file.setFileSize((int)mediaProps.getSize());
      file.insert();

      id = file.getID();
    }
    catch(Exception e){
      e.printStackTrace(System.err);
      mediaProps.setId(-1);
      return -1;
    }

    return id;
  }

  public static MediaProperties doUpload(IWContext iwc) throws Exception{
    String sep = FileUtil.getFileSeparator();
    StringBuffer pathToFile = new StringBuffer();
    pathToFile.append(iwc.getApplication().getApplicationRealPath());
    pathToFile.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
    pathToFile.append(sep);

    FileUtil.createFolder(pathToFile.toString());


    MediaProperties  mediaProps = null;

    MultipartParser mp = new MultipartParser(iwc.getRequest(),MediaConstants.FILE_UPLOAD_MAXIMUM_SIZE);/**@todo the maximum size should be flexible could just match the filesiz we have? or don't we**/
    Part part;
    File dir = null;
    String value = null;
    while ((part = mp.readNextPart()) != null) {
      String name = part.getName();
      if(part.isParam()){
        ParamPart paramPart = (ParamPart) part;
        value = paramPart.getStringValue();

      }
      else if (part.isFile()) {
        // it's a file part
        FilePart filePart = (FilePart) part;
        String fileName = filePart.getFileName();

        if (fileName != null) {
          pathToFile.append(fileName);
          String filePath = pathToFile.toString();
          StringBuffer webPath = new StringBuffer();
          webPath.append('/');
          webPath.append(IWCacheManager.IW_ROOT_CACHE_DIRECTORY);
          webPath.append('/');
          webPath.append(fileName);

          File file = new File(filePath);
          int size = (int) filePart.writeTo(file);
      /*
        System.out.println("MediaBusiness : File size"+size);
        System.out.println("MediaBusiness : File filePath"+filePath);
        System.out.println("MediaBusiness : File webPath"+webPath.toString());
        System.out.println("MediaBusiness : File getContentType"+filePart.getContentType());
        System.out.println("MediaBusiness : File fileName"+fileName);
        */
          mediaProps = new MediaProperties(fileName,filePart.getContentType(),filePath,webPath.toString(),size);
        }
      }
    }

    return mediaProps;
}

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

    String id = null;
    if(iwc.getParameter(fileInSessionParameter)!=null){
      id = iwc.getParameter(fileInSessionParameter);
      iwc.setSessionAttribute(fileInSessionParameter+"_2",id);
    }
    else if(iwc.getSessionAttribute(fileInSessionParameter)!=null){
      id = (String) iwc.getSessionAttribute(fileInSessionParameter);
    }
    else if(iwc.getSessionAttribute(fileInSessionParameter+"_2")!=null){
      id = (String) iwc.getSessionAttribute(fileInSessionParameter+"_2");
    }

    return id;
  }

  public static void removeMediaIdFromSession(IWContext iwc){
    iwc.removeSessionAttribute(getMediaParameterNameInSession(iwc));
  }

  public static void saveMediaId(IWContext iwc,String mediaId){
    iwc.setSessionAttribute(getMediaParameterNameInSession(iwc),mediaId);
    iwc.removeSessionAttribute(getMediaParameterNameInSession(iwc)+"_2");
  }


  public static MediaProperties uploadToDiskAndGetMediaProperties(IWContext iwc){
    MediaProperties mediaProps = null;
    try {
      mediaProps = MediaBusiness.doUpload(iwc);
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
    ICFileType type = (ICFileType) cm.getFromCachedTable(ICFileType.class,Integer.toString(mime.getFileTypeID()));
    ICFileTypeHandler typeHandler = (ICFileTypeHandler) cm.getFromCachedTable(ICFileTypeHandler.class,Integer.toString(type.getFileTypeHandlerID()));

    FileTypeHandler handler = FileTypeHandler.getInstance(iwc.getApplication(),typeHandler.getHandlerClass());
    System.out.println("SELECTED HANDLER IS :"+typeHandler.getHandlerName());
   return handler;
  }


}//end of class

