package com.idega.block.media.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;
import java.sql.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class SimpleViewer extends PresentationObjectContainer{
    public String prmImageView = "img_view_id";
    public static final String prmAction = "img_view_action";
    public static final String actSave = "save",actDelete = "delete";
    public static final String sessionSaveParameter = "img_id";
    public static final String sessionParameter = "image_id";
    public String sessImageParameterName = "im_image_session_name";
    public String sessImageParameter = "image_id";

    public void  main(IWContext iwc){

      String sImageId = getImageId(iwc);
      String sAction = iwc.getParameter(prmAction);

      if(sImageId != null){
        saveImageId(iwc,sImageId);
        if(sAction != null){
          if(sAction.equals(actSave)){
            saveImageId(iwc,sImageId);
          }
          else if(sAction.equals(actDelete)){
            deleteImage(sImageId);
            removeFromSession(iwc);
          }
        }
        else{
          int id = Integer.parseInt(sImageId);
          try {
            ImageEntity ieImage = new ImageEntity(id);
            Table T = new Table();
            T.add(ieImage.getName(),1,1);
            T.add(new Image(id),1,2);
            add(T);
          }
          catch (SQLException ex) {
            add("error");
          }
        }
      }
    }

    public boolean deleteImage(String sImageId){
      Connection Conn = null;
      try{
        Conn = com.idega.util.database.ConnectionBroker.getConnection();
        ResultSet RS;
        Statement Stmt = Conn.createStatement();
        int r = Stmt.executeUpdate("DELETE FROM IMAGE_IMAGE_CATAGORY WHERE IMAGE_ID = "+sImageId);
        Stmt.close();
      }
      catch(SQLException ex){
        ex.printStackTrace();
      }
      finally{
        if(Conn != null)
          com.idega.util.database.ConnectionBroker.freeConnection(Conn);
      }

      try {
        int iImageId = Integer.parseInt(sImageId);
        new ImageEntity(iImageId).delete();
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

    public String getImageId(IWContext iwc){
      if(iwc.getParameter(sessImageParameterName)!=null)
       sessImageParameter = iwc.getParameter(sessImageParameterName);
      else if(iwc.getSessionAttribute(sessImageParameterName)!=null){
        sessImageParameter = (String) iwc.getSessionAttribute(sessImageParameterName);
      }
      //add(sessImageParameter);
      String s = null;
      if(iwc.getParameter(sessImageParameter)!=null){
        s = iwc.getParameter(sessImageParameter);
      }
      else if(iwc.getSessionAttribute(sessImageParameter)!=null)
        s = (String) iwc.getSessionAttribute(sessImageParameter);
      return s;
    }


    public void removeFromSession(IWContext iwc){
      iwc.removeSessionAttribute(sessImageParameter);
    }

    public void saveImageId(IWContext iwc,String sImageId){
      iwc.setSessionAttribute(sessImageParameter,sImageId);
      iwc.setSessionAttribute(sessImageParameter+"2",sImageId);
    }

    public void saveImage(IWContext iwc,String sImageId){
      iwc.setSessionAttribute(sessionSaveParameter,sImageId);
    }

  }