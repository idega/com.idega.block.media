package com.idega.block.media.presentation;

import com.idega.presentation.ui.Window;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaProperties;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.FileTypeHandler;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.IFrame;
import com.idega.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import com.oreilly.servlet.MultipartRequest;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.block.media.business.MediaBusiness;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;

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

/** this parameter is changed right away**/
    private String fileInSessionParameter = "";

    public MediaUploaderWindow(){
    }

    public void main(IWContext iwc){
      iwrb = getResourceBundle(iwc);
      iwb = getBundle(iwc);
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
      setBackgroundColor("white");
      setTitle("idegaWeb uploader");
      handleEvents(iwc);
    }

    private void handleEvents(IWContext iwc){
      String contentType = iwc.getRequestContentType();

      if( (contentType !=null) && (contentType.indexOf("multipart")!=-1) ){//viewing after uploading to disk
        MediaProperties mediaProps = MediaBusiness.uploadToDiskAndGetMediaProperties(iwc);

        if(mediaProps!=null){
          Table T = new Table(2,2);

          /**@todo: insert a generated localized generated button**/
          Link submitSave = new Link("Save");
          submitSave.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE);
          submitSave.setAsImageButton(true);
          submitSave.addParameter(fileInSessionParameter,(String)mediaProps.getParameterMap().get(fileInSessionParameter));

          /**@todo: insert a generated localized generated button**/
          Link submitNew = new Link("New");
          submitNew.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_NEW);
          submitNew.addParameter(fileInSessionParameter,(String)mediaProps.getParameterMap().get(fileInSessionParameter));
          submitNew.setAsImageButton(true);

          T.add(submitNew,2,1);


          try {
            FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc,mediaProps.getContentType());
            T.add(handler.getPresentationObject(mediaProps,iwc),1,2);
            T.add(submitSave,1,1);
          }
          catch (Exception ex) {
            StringBuffer text = new StringBuffer();
            text.append(iwrb.getLocalizedString("uploader.window.nomimetype.firsthalf","The mimetype"));
            text.append(" ");
            text.append(mediaProps.getContentType());
            text.append(iwrb.getLocalizedString("uploader.window.nomimetype.secondhalf"," is not in the database."));

            Link mimeWindow = new Link(iwrb.getLocalizedString("uploader.window.mimewindowbutton","Add mimetype"));
            mimeWindow.setAsImageButton(true);
            mimeWindow.setWindowToOpen(MimeTypeWindow.class);
            mimeWindow.addParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME,mediaProps.getContentType());
            T.add(text.toString(),1,2);
            T.add(mimeWindow,2,2);
          }


          add(T);
        }
        else{//upload failed try again
          add("Error in upload try again");
          add(getMultiPartUploaderForm(iwc));
        }

      }
      else{//else saving or uploading a new file
        String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
        if( (action!=null) && action.equals(MediaConstants.MEDIA_ACTION_SAVE)  ){
        /**@todo merge with mediaviewer???**/
          setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
          add(MediaBusiness.saveMedia(iwc));//also deletes the file from disk and return a FileHandler made presentationobject
        }
        else{//add a new file
          add(getMultiPartUploaderForm(iwc));
        }
      }


    }

  private Form getMultiPartUploaderForm(IWContext iwc){
    Form f = new Form();
    f.setMultiPart();
   // String s = iwc.getRequestURI()+"?"+com.idega.+"="+com.idega.idegaweb.IWMainApplication.getEncryptedClassName(this.getClass());
    String s = com.idega.idegaweb.IWMainApplication.getObjectInstanciatorURL(this.getClass());
    f.setAction(s);

    f.add(new FileInput());
    f.add(new SubmitButton());

    f.add(new HiddenInput(fileInSessionParameter,MediaBusiness.getMediaId(iwc)));

    return f;
  }


  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}