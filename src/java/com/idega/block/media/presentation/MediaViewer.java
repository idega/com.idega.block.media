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
import com.idega.block.media.business.FileTypeHandler;
import java.sql.*;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.util.caching.Cache;

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

      String mediaId = MediaBusiness.getMediaId(iwc);
      String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);



      System.out.println("Media ID :"+mediaId);

      if( (mediaId!=null) && !(mediaId.equalsIgnoreCase("-1")) ){
        //saveMediaId(iwc,mediaId);
        if(action != null){
          if(action.equals(MediaConstants.MEDIA_ACTION_SAVE)){
            MediaBusiness.saveMediaId(iwc,mediaId);
          }
          else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE)){
           ConfirmDeleteMedia(mediaId);
          }
          else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM)){
            deleteMedia( mediaId);
            MediaBusiness.removeMediaIdFromSession(iwc);
          }
        }
        else{

          int id = Integer.parseInt(mediaId);
          Cache cache = FileTypeHandler.getCachedFileInfo(id,iwc);
          ICFile file = (ICFile) cache.getEntity();
          Table T = new Table();
          T.add(file.getName(),1,1);

          FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc,file.getMimeType());
          T.add(handler.getPresentationObject(id,iwc),1,2);

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
          L.addParameter(fileInSessionParameter,id);
          add(L);

          add(T);
        }
      }
    }

    public boolean deleteMedia(String mediaId){
      try {
        int iMediaId = Integer.parseInt(mediaId);
        new ICFile(iMediaId).delete();
        return true;
      }
      catch (SQLException ex) {
        ex.printStackTrace(System.err);
        return false;
      }
      catch (NumberFormatException ex){
        ex.printStackTrace(System.err);
        return false;
      }
    }

    public void ConfirmDeleteMedia(String mediaId){
       Table T = new Table();
       T.setWidth("100%");
       T.setHeight("100%");
       int id = Integer.parseInt(mediaId);
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
            confirm.addParameter(fileInSessionParameter,mediaId);
            T.add(confirm,1,3);

          }
          catch (SQLException ex) {
            T.add("An unexpected error");
            ex.printStackTrace(System.err);
          }
        add(T);
    }


    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

  }