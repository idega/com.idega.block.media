package com.idega.block.media.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.ui.IFrame;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Window;
import com.idega.core.data.ICFile;
import com.idega.util.idegaTimestamp;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.SystemTypeHandler;
import java.sql.*;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.presentation.ui.AbstractChooserWindow;

/**
 * Title: com.idega.block.media.presentation.MediaViewer
 * Description: A viewer class for viewing data from the ic_file table using the correct plugin (if needed)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaViewer extends  Window {
  /** these are used for creating a chooser function that has a unique name for this chooser**/
  public static final String ONCLICK_FUNCTION_NAME = "fileselect";
  public static final String FILE_ID_PARAMETER_NAME = "media_file_id";
  public static final String FILE_NAME_PARAMETER_NAME = "media_file_name";

  private String fileInSessionParameter = "ic_file_id";

  /*public MediaViewer(){
   super(true);
  }
*/
    public void  main(IWContext iwc) throws Exception{

      String sMediaId = MediaBusiness.getMediaId(iwc);
      String sAction = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);

      if(sMediaId != null){
        //saveMediaId(iwc,sMediaId);
        if(sAction != null){
          if(sAction.equals(MediaConstants.MEDIA_ACTION_SAVE)){
            MediaBusiness.saveMediaId(iwc,sMediaId);
          }
          else if(sAction.equals(MediaConstants.MEDIA_ACTION_DELETE)){
           ConfirmDeleteMedia(sMediaId);
          }
          else if(sAction.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM)){
            deleteMedia( sMediaId);
            MediaBusiness.removeMediaIdFromSession(iwc);
          }
        }
        else{

          int id = Integer.parseInt(sMediaId);
          try {
            ICFile file = new ICFile(id);
            Table T = new Table();
            T.add(file.getName(),1,1);

            /** @todo insert fileHandler */
            T.add(new IFrame("media", MediaServlet.getMediaURL(file.getID())), 1,2);

            getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME,"function "+ONCLICK_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+"){ }");
            getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME,"top."+AbstractChooserWindow.SELECT_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+")");

            Link l = new Link();
            l.setTextOnLink("Use");
            l.setAsImageButton(true);
            l.setURL("#");
            l.setOnClick(ONCLICK_FUNCTION_NAME+"('"+file.getName()+"','"+file.getID()+"')");
            add(l);

            Class C = MediaUploaderWindow.class;
            Link L = new Link("New",C);
            L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
            L.setAsImageButton(true);
            add(L);

            add(T);

            SystemTypeHandler handler = new SystemTypeHandler();

            add(handler.getPresentationObject(file.getID()));


          }
          catch (SQLException ex) {
            ex.printStackTrace(System.err);
          }
        }
      }
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


    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

  }