package com.idega.block.media.presentation;

import com.idega.jmodule.object.ModuleObjectContainer;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;
import java.sql.*;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class SimpleViewer extends ModuleObjectContainer{
    public String prmImageView = "img_view_id";
    public static final String prmAction = "img_view_action";
    public static final String actSave = "save",actDelete = "delete";
    public static final String sessionSaveParameter = "img_id";
    public static final String sessionParameter = "image_id";
    public String sessImageParameterName = "im_image_session_name";
    public String sessImageParameter = "image_id";

    public void  main(ModuleInfo modinfo){

      String sImageId = getImageId(modinfo);
      String sAction = modinfo.getParameter(prmAction);

      if(sImageId != null){
        saveImageId(modinfo,sImageId);
        if(sAction != null){
          if(sAction.equals(actSave)){
            saveImageId(modinfo,sImageId);
          }
          else if(sAction.equals(actDelete)){
            deleteImage(sImageId);
            removeFromSession(modinfo);
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

    public String getImageId(ModuleInfo modinfo){
      if(modinfo.getParameter(sessImageParameterName)!=null)
       sessImageParameter = modinfo.getParameter(sessImageParameterName);
      else if(modinfo.getSessionAttribute(sessImageParameterName)!=null){
        sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
      }
      String s = null;
      if(modinfo.getParameter(sessImageParameter)!=null){
        s = modinfo.getParameter(sessImageParameter);
      }
      else if(modinfo.getSessionAttribute(sessImageParameter)!=null)
        s = (String) modinfo.getSessionAttribute(sessImageParameter);

      return s;
    }


    public void removeFromSession(ModuleInfo modinfo){
      modinfo.removeSessionAttribute(sessImageParameter);
    }

    public void saveImageId(ModuleInfo modinfo,String sImageId){
      modinfo.setSessionAttribute(sessImageParameter,sImageId);
      modinfo.setSessionAttribute(sessImageParameter+"2",sImageId);
    }

    public void saveImage(ModuleInfo modinfo,String sImageId){
      modinfo.setSessionAttribute(sessionSaveParameter,sImageId);
    }

  }