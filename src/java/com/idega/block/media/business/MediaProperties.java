package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.MediaProperties
 * Description: A wrapper class for known file information after uploading to disk
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaProperties {

  private String sName,sContentType,sRealPath,sWebPath;
  private long lSize;
  private int iId;


  public MediaProperties(){
    this.sName = "";
    this.sContentType = "";
    this.sRealPath  = "";
    this.sWebPath = "";
    this.lSize = 0;
    this.iId = -1;
  }
  public MediaProperties(String name,String type,String realpath,String webpath ,long size){
    this.sName = name;
    this.sContentType = type;
    this.sRealPath  = realpath;
    this.sWebPath = webpath;
    this.lSize  = size;
    this.iId = -1;
  }

  public String getName(){
    return this.sName;
  }
  public void setName(String name){
    this.sName = name;
  }
  public String getContentType(){
    return this.sContentType;
  }
  public void setContentType(String type){
    this.sContentType = type;
  }
  public String getRealPath(){
    return this.sRealPath;
  }
  public void setRealPath(String realpath){
    this.sRealPath = realpath;
  }
  public String getWebPath(){
    return this.sWebPath;
  }
  public void setWebPath(String webpath){
    this.sWebPath = webpath;
  }
  public long getSize(){
    return this.lSize;
  }
  public void setSize(long size){
    this.lSize = size;
  }
  public int getId(){
    return this.iId;
  }
  public void setId(int id){
    this.iId = id;
  }

}

