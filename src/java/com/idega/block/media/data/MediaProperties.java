package com.idega.block.media.data;

import java.util.Map;
import com.idega.io.UploadFile;

/**
 * Title: com.idega.block.media.data.MediaProperties
 * Description: A wrapper class for known file information after uploading to disk
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 * @deprecated replaced by com.idega.io.UploadFile
 */

public class MediaProperties {

//  private String sName,sMimeType,sRealPath,sWebPath;
  private Map parameterMap;
//  private long lSize;
//  private int iId;
  private UploadFile _uploadFile = null;

  public MediaProperties(UploadFile uploadFile){
    this(uploadFile,null);
  }

  public MediaProperties(UploadFile uploadFile, Map parameterMap){
    _uploadFile = _uploadFile;
    parameterMap = parameterMap;
  }

//  public MediaProperties(){
//    this.sName = "";
//    this.sMimeType = "";
//    this.sRealPath  = "";
//    this.sWebPath = "";
//    this.lSize = 0;
//    this.iId = -1;
//  }
//  public MediaProperties(String name,String type,String realpath,String webpath ,long size){
//    this.sName = name;
//    this.sMimeType = type;
//    this.sRealPath  = realpath;
//    this.sWebPath = webpath;
//    this.lSize  = size;
//    this.iId = -1;
//  }
//
//
//  public MediaProperties(String name,String type,String realpath,String webpath ,long size, Map parameterMap){
//    this(name,type,realpath,webpath,size);
//    setParameterMap(parameterMap);
//  }



  public UploadFile getUploadFile(){
    return _uploadFile;
  }

  public String getName(){
    return _uploadFile.getName();
  }
  public void setName(String name){
    _uploadFile.setName(name);
  }
  public String getMimeType(){
    return _uploadFile.getMimeType();
  }
  public void setMimeType(String type){
    _uploadFile.setMimeType(type);
  }
  public String getRealPath(){
    return _uploadFile.getRealPath();
  }
  public void setRealPath(String realpath){
    _uploadFile.setRealPath(realpath);
  }
  public String getWebPath(){
    return _uploadFile.getWebPath();
  }
  public void setWebPath(String webpath){
    _uploadFile.setWebPath(webpath);
  }
  public long getSize(){
    return _uploadFile.getSize();
  }
  public void setSize(long size){
    _uploadFile.setSize(size);
  }
  public int getId(){
    return _uploadFile.getId();
  }
  public void setId(int id){
    _uploadFile.setId(id);
  }




//  public String getName(){
//    return this.sName;
//  }
//  public void setName(String name){
//    this.sName = name;
//  }
//  public String getMimeType(){
//    return this.sMimeType;
//  }
//  public void setMimeType(String type){
//    this.sMimeType = type;
//  }
//  public String getRealPath(){
//    return this.sRealPath;
//  }
//  public void setRealPath(String realpath){
//    this.sRealPath = realpath;
//  }
//  public String getWebPath(){
//    return this.sWebPath;
//  }
//  public void setWebPath(String webpath){
//    this.sWebPath = webpath;
//  }
//  public long getSize(){
//    return this.lSize;
//  }
//  public void setSize(long size){
//    this.lSize = size;
//  }
//  public int getId(){
//    return this.iId;
//  }
//  public void setId(int id){
//    this.iId = id;
//  }

  public Map getParameterMap(){
    return this.parameterMap;
  }
  public void setParameterMap(Map parameterMap){
    this.parameterMap = parameterMap;
  }

}

