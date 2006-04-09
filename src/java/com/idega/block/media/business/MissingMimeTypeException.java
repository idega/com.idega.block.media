package com.idega.block.media.business;
import com.idega.core.file.data.ICFileType;

/**
 * Title: com.idega.block.media.business.MissingMimeTypeException
 * Description: This is the exception that is thrown when the user uploads a file with an unknown mimetype
 *             <br>It can contain a explanation string and a likely ICFileType for the missing mimetype
 * Copyright:    Copyright (c) 2002
 * Company:      idega software
 * @author <a href="mailto:eiki@idega.is">Eirikur S. Hrafnsson</a>
 * @version 1.0
 */

public class MissingMimeTypeException extends RuntimeException {

  private ICFileType type = null;
  private String mime = null;

  public MissingMimeTypeException() {
    super();
  }

  public MissingMimeTypeException(String explanation,String missingMimeType){
    super(explanation);
    this.mime = missingMimeType;
  }

  public MissingMimeTypeException(ICFileType icFileType, String missingMimeType){
    super();
    this.type = icFileType;
    this.mime = missingMimeType;
  }

  public MissingMimeTypeException(String explanation,String missingMimeType,ICFileType icFileType){
    super(explanation);
    this.type = icFileType;
  }

  public ICFileType getSuggestedFileType(){
    return this.type;
  }

  public String getMimeType(){
    return this.mime;
  }

}
