package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MissingMimeTypeException;
import com.idega.block.media.data.MediaProperties;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.Window;

/**
 * Title: com.idega.block.media.presentation.MediaUploaderWindow
 * Description: The default uploader window for uploading into the ic_file table
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaUploaderWindow extends Window{

private IWBundle iwb;
private IWResourceBundle iwrb;

/* this parameter is changed right away */
    private String fileInSessionParameter = "";

    public MediaUploaderWindow(){
      setBackgroundColor( MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR );
      setAllMargins( 0 );
    }


    public void main(IWContext iwc) throws Exception{
      super.main(iwc);
      iwrb = getResourceBundle(iwc);
      iwb = getBundle(iwc);
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
      handleEvents(iwc);
    }

    private void handleEvents(IWContext iwc){

      /* Uploading and checking for a valid mimetype */
      if( iwc.isMultipartFormData() ){
      	System.out.println("handling multipart form");
        MediaProperties mediaProps = MediaBusiness.uploadToDiskAndGetMediaProperties(iwc);


        /*if the upload succeded*/
        if(mediaProps!=null){
          iwc.setSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME,mediaProps);
          try {
            /*this will throw an exection if the mimetype does not exist*/
            
            // Getting mimetype from file extension if a binary file uploaded (aron@idega.is)
			String mimeType = mediaProps.getMimeType();
			if(mimeType.equalsIgnoreCase("application/octet-stream")){
			   String name = mediaProps.getName();
			   java.net.FileNameMap fileNameMap = java.net.URLConnection.getFileNameMap();
			   String mime = fileNameMap.getContentTypeFor(name);
			   if(mime!=null)
				   mediaProps.setMimeType(mime);
			}
            // added by aron, or else the missingmimeTypeException is never thrown otherwise than runtime exception
           
            MediaBusiness.getFileType(iwc,mediaProps.getMimeType());
            
            viewUploadedMedia(mediaProps);
          }
          catch (MissingMimeTypeException ex) {
              StringBuffer text = new StringBuffer();
              text.append(iwrb.getLocalizedString("uploader.window.nomimetype.firsthalf","The mimetype"));
              text.append(" ");
              text.append(mediaProps.getMimeType());
              text.append(iwrb.getLocalizedString("uploader.window.nomimetype.secondhalf"," is not in the database."));
              add(text.toString());
              addBreak();
              add(new MimeTypeWindow(mediaProps.getMimeType()));
          }
        }
        //upload failed try again
        else{
          add(iwrb.getLocalizedString("uploader.window.select","You must select something to upload first"));
          add(getMultiPartUploaderForm(iwc));
        }

      }
      /*Saving to database or uploading a new file*/
      else{
        String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
        if(MediaConstants.MEDIA_ACTION_NEW.equals(action)){
          /*add a new file*/
          add(getMultiPartUploaderForm(iwc));
        }
        else{
          MediaProperties mediaProps = ( MediaProperties ) iwc.getSessionAttribute( MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME );
          if( mediaProps!=null ){
            iwc.removeSessionAttribute( MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME );

            if( MediaConstants.MEDIA_ACTION_SAVE.equals(action)  ){
              setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
              int pId = -1;
              String parentId = iwc.getParameter(fileInSessionParameter);

              if(parentId!=null){
                pId = Integer.parseInt(parentId);
              }

              /* if saving a new mimetype */
              if( iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME)!=null ){
                String mimeType = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME);
                String mimeDescription = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME);
                int fileTypeId = Integer.parseInt(iwc.getParameter(MediaConstants.MEDIA_FILE_TYPE_PARAMETER_NAME));
                MediaBusiness.saveMimeType(mimeType,mimeDescription,fileTypeId);
              }

              //also deletes the file from disk and return a MediaViewer
              mediaProps = MediaBusiness.saveMediaToDB( mediaProps, pId, iwc);
             // add(new MediaToolbar(mediaProps));
            //  add(new MediaViewer(mediaProps));
            }
			add(new MediaToolbar(mediaProps));
			add(new MediaViewer(mediaProps));
        }
      }
    }
  }


  protected void viewUploadedMedia(MediaProperties mediaProps){
    Table T = new Table(1,2);
    T.setHeight(1,1,"16");
    T.setHeight(1,2,Table.HUNDRED_PERCENT);
    T.setWidthAndHeightToHundredPercent();
    T.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
    T.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);

    T.add(new MediaToolbar(mediaProps),1,1);
    T.add(new MediaViewer(mediaProps),1,2);

    add(T);
  }


  protected Form getMultiPartUploaderForm(IWContext iwc){
    Table table = new Table(1,3);
    table.setWidth(300);
    table.setHeight(120);
    table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
    table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
    table.setVerticalAlignment(1,3,Table.VERTICAL_ALIGN_TOP);

    Text select = new Text(iwrb.getLocalizedString("me.uploadtext","Select a file to upload."));
    select.setFontFace(Text.FONT_FACE_ARIAL);
    select.setFontSize(Text.FONT_SIZE_10_HTML_2);
    select.setBold();
    table.add(select,1,1);

    Form f = new Form();
    f.setMultiPart();
   // String s = iwc.getRequestURI()+"?"+com.idega.+"="+com.idega.idegaweb.IWMainApplication.getEncryptedClassName(this.getClass());
    //String s = com.idega.idegaweb.IWMainApplication.getObjectInstanciatorURL(this.getClass());
    //f.setAction(s);
    f.setClassToInstanciateAndSendTo(this.getClass(),iwc);

    IWBundle core = iwc.getIWMainApplication().getCoreBundle();
    Image transparent = core.getImage("transparentcell.gif");
    Image busy = core.getImage("busy.gif");

    getParentPage().setOnLoad(";preLoadImages('"+transparent.getURL()+"');preLoadImages('"+busy.getURL()+"')");

    table.add(transparent,1,2);
    table.add(new FileInput(),1,3);
    table.add(new SubmitButton(iwrb.getLocalizedString("me.submit","Submit")),1,3);
    String parentId = iwc.getParameter(fileInSessionParameter);
    if( parentId!=null ) table.add(new HiddenInput(fileInSessionParameter,parentId),1,3);
    f.setOnSubmit("swapImage('"+transparent.getID()+"','','"+busy.getURL()+"',1);return true");

    f.add(table);

    return f;
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}
