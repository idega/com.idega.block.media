package com.idega.block.media.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.core.data.ICFile;
import com.idega.util.idegaTimestamp;
import com.idega.block.media.business.MediaConstants;
import java.sql.*;
import com.idega.block.media.servlet.MediaServlet;

/**
 * Title: com.idega.block.media.presentation.MediaViewer
 * Description: A viewer class for viewing data from the ic_file table using the correct plugin (if needed)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaViewer extends PresentationObjectContainer{
    private String fileInSessionParameter = "ic_file_id";

    public void  main(IWContext iwc){

      String sMediaId = getMediaId(iwc);
      String sAction = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);

      if(sMediaId != null){
        //saveMediaId(iwc,sMediaId);
        if(sAction != null){
          if(sAction.equals(MediaConstants.MEDIA_ACTION_SAVE)){
            saveMediaId(iwc,sMediaId);
          }
          else if(sAction.equals(MediaConstants.MEDIA_ACTION_DELETE)){
           ConfirmDeleteMedia(sMediaId);
          }
          else if(sAction.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM)){
            deleteMedia( sMediaId);
            removeFromSession(iwc);
          }
        }
       {
          int id = Integer.parseInt(sMediaId);
          try {
            ICFile file = new ICFile(id);
            Table T = new Table();
            T.add(file.getName(),1,1);

            /** @todo insert fileHandler */
            T.add(new IFrame("media", MediaServlet.getMediaURL(file.getID())), 1,2);

            add(T);

          }
          catch (SQLException ex) {
            ex.printStackTrace(System.err);
          }
        }
      }
    }

    public void checkParameterName(IWContext iwc){
     if(iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null){
      fileInSessionParameter = iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
      iwc.setSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME,fileInSessionParameter);
    }
    else if(iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null)
      fileInSessionParameter = (String) iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
    }

    public boolean deleteMedia(String sMediaId){
      try {
        int iMediaId = Integer.parseInt(sMediaId);
        new ICFile(iMediaId).delete();
        return true;
      }
      catch (SQLException ex) {
        ex.printStackTrace();
        return false;
      }
      catch (NumberFormatException ex){
        return false;
      }
    }

    public void ConfirmDeleteMedia(String sMediaId){
       Table T = new Table();
       T.setWidth("100%");
       T.setHeight("100%");
       int id = Integer.parseInt(sMediaId);
          try {
            ICFile file = new ICFile(id);

            Text warning = new Text("Are you sure ?");/**@todo localize this*/
            warning.setFontSize(6);
            warning.setFontColor("FF0000");
            warning.setBold();

/**@todo add FileHandler here**/
            /*Image image = new Image(id);
            image.setURL(com.idega.block.media.servlet.MediaServlet.getMediaURL(id));
            T.setBackgroundImage(1,2,image);*/
            T.add(file.getName(),1,1);
            T.add(warning,1,2);
            T.setHeight(1,2,"100%");
            T.setAlignment(1,2,"center");
            Link confirm = new Link("delete");
            confirm.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME ,MediaConstants.MEDIA_ACTION_DELETE_CONFIRM);
            confirm.addParameter(fileInSessionParameter,sMediaId);
            T.add(confirm,1,3);

          }
          catch (SQLException ex) {
            T.add("error");
          }
        add(T);
    }

    public String getMediaId(IWContext iwc){
      if(iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null)
       fileInSessionParameter = iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
      else if(iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null){
        fileInSessionParameter = (String) iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
      }
      //add(fileInSessionParameter);
      String s = null;
      if(iwc.getParameter(fileInSessionParameter)!=null){
        s = iwc.getParameter(fileInSessionParameter);
        iwc.setSessionAttribute(fileInSessionParameter+"_2",s);
      }
      else if(iwc.getSessionAttribute(fileInSessionParameter)!=null)
        s = (String) iwc.getSessionAttribute(fileInSessionParameter);
      else if(iwc.getSessionAttribute(fileInSessionParameter+"_2")!=null)
        s = (String) iwc.getSessionAttribute(fileInSessionParameter+"_2");
      //add(" " +s);
      return s;
    }


    public void removeFromSession(IWContext iwc){
      iwc.removeSessionAttribute(fileInSessionParameter);
    }

    public void saveMediaId(IWContext iwc,String sMediaId){
      iwc.setSessionAttribute(fileInSessionParameter,sMediaId);
      iwc.removeSessionAttribute(fileInSessionParameter+"_2");
    }


    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

  }