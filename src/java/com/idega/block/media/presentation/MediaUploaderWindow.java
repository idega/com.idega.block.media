package com.idega.block.media.presentation;

import com.idega.presentation.ui.Window;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaProperties;
import com.idega.block.media.business.MediaBusiness;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.Image;
import com.idega.presentation.ui.IFrame;
import com.idega.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import com.oreilly.servlet.MultipartRequest;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.block.media.business.MediaBusiness;

/**
 * Title: com.idega.block.media.presentation.MediaUploaderWindow
 * Description: The default uploader window for uploading into the ic_file table
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaUploaderWindow extends Window{

    private String fileInSessionParameter = "ic_file_id";

    public MediaUploaderWindow(){
    }
/**@todo put this into constants class**/
    public void setSessionSaveParameterName(String prmName){
      fileInSessionParameter = prmName;
    }

    public String getSessionSaveParameterName(){
      return fileInSessionParameter;
    }


    public void main(IWContext iwc){
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
      setBackgroundColor("white");
      setTitle("idegaWeb uploader");
      control(iwc);
    }

    private void control(IWContext iwc){
      String sContentType = iwc.getRequest().getContentType();
      if(sContentType !=null && sContentType.indexOf("multipart")!=-1){
        add(parse(iwc));
      }
      else{
        /**@todo make this a statc name**/
        if(iwc.getParameter("save")!=null){
          save(iwc);//also deletes the file from disk
        }
        else{
          add(getMultiForm(iwc));
        }
      }


    }

    private Form getMultiForm(IWContext iwc){
      Form f = new Form();
      f.setMultiPart();
      String s = iwc.getRequestURI()+"?idegaweb_instance_class="+com.idega.idegaweb.IWMainApplication.getEncryptedClassName(this.getClass());
      f.setAction(s);

      f.add(new FileInput());
      f.add(new SubmitButton());
      return f;
    }

    private PresentationObject parse(IWContext iwc){
      MediaProperties mediaProps = null;
      try {
        mediaProps = MediaBusiness.doUpload(iwc);
        iwc.setSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME,mediaProps);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      if(mediaProps!=null){
        Form form = new Form();
        Table T = new Table(2,2);
        SubmitButton save = new SubmitButton("save","Save");/**@todo: insert a generated localized generated button**/
        save.setOnClick("top.setTimeout('top.frames.lister.location.reload()',150)");
        T.add(save,1,1);
        T.add(new SubmitButton("new","New"),2,1);

        /**@todo here we place File Handlers**/
        //T.add(new Image(mediaProps.getWebPath()),1,2);
        T.add(new IFrame("uploaded",mediaProps.getWebPath()),1,2);

        form.add(T);
        return form;
      }
      else{
        return getMultiForm(iwc);
      }

    }

    private void save(IWContext iwc){
      MediaProperties mediaProps = null;
      if(iwc.getSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME)!=null){
        mediaProps = (MediaProperties) iwc.getSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME);
        iwc.removeSessionAttribute(MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME);
      }
      if(mediaProps !=null){
        int i = MediaBusiness.SaveMediaToDB(mediaProps);
        iwc.setSessionAttribute(fileInSessionParameter,String.valueOf(i));
        //try {
         // add(new Image(i));/**@todo: add filehandler here**/
          add(new IFrame("saved",MediaServlet.getMediaURL(mediaProps.getId()) ));
        //}
       // catch (SQLException ex) {

       // }
      }
    }
}