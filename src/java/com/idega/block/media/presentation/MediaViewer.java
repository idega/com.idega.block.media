package com.idega.block.media.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWCacheManager;
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

    public void  main(IWContext iwc) throws Exception{
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
      String mediaId = MediaBusiness.getMediaId(iwc);
      String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
      if( action==null ) action = "";

      if( (mediaId!=null) && !(mediaId.equalsIgnoreCase("-1")) ){
        if(action.equals("") || action.equals(MediaConstants.MEDIA_ACTION_USE) ){//viewing or have pressed the use button
          viewOrUse(iwc,action,mediaId);
        }
        else if(action.equals(MediaConstants.MEDIA_ACTION_SAVE)){
          MediaBusiness.saveMediaIdToSession(iwc,mediaId);
        }
        else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE)){
         ConfirmDeleteMedia(mediaId);
         setOnUnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
        }
        else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM)){
          deleteMedia(mediaId);
          MediaBusiness.removeMediaIdFromSession(iwc);
          add(new Text("Deleted"));
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
            T.setHeight("100%");
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


   public void viewOrUse(IWContext iwc, String action, String mediaId){
    iwc.removeSessionAttribute(fileInSessionParameter);
    IWCacheManager cm = iwc.getApplication().getIWCacheManager();


    if( action.equals(MediaConstants.MEDIA_ACTION_USE) ){
      MediaBusiness.saveMediaIdToSession(iwc,mediaId);
    }

    int id = Integer.parseInt(mediaId);
    Cache cache = FileTypeHandler.getCachedFileInfo(id,iwc);
    ICFile file = (ICFile) cache.getEntity();

    Table T = new Table();
    T.add(file.getName(),1,1);
    T.setWidth("100%");
    T.setHeight("100%");

    FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc,file.getMimeType());
    T.add(handler.getPresentationObject(id,iwc),1,2);

    getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME,"function "+ONCLICK_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+"){ }");
    getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME,"top."+AbstractChooserWindow.SELECT_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+")");

    Link use = MediaBusiness.getUseImageLink();
    use.setTextOnLink("Use");
    use.setAsImageButton(true);
//**@todo filter out types and use this differently for each plugin?
    if( handler instanceof com.idega.block.media.business.ImageTypeHandler ){
      use.setOnClick("top.window.opener.setImageId('"+file.getID()+"','"+fileInSessionParameter+"')");
      use.addParameter(fileInSessionParameter,id);
    }
    else{
      use.setURL("#");
      use.setOnClick(ONCLICK_FUNCTION_NAME+"('"+file.getName()+"','"+file.getID()+"')");
    }

    add(use);

    Link newLink = new Link("New",MediaUploaderWindow.class);
    newLink.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    newLink.setAsImageButton(true);
    newLink.addParameter(fileInSessionParameter,id);
    newLink.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW);
    add(newLink);


    Link delete = new Link("Delete",MediaViewer.class);
    delete.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    delete.setAsImageButton(true);
    delete.addParameter(fileInSessionParameter,id);
    delete.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE);
    add(delete);

    if( MediaBusiness.isFolder(file) ){
      Link folder = MediaBusiness.getNewFolderLink();
      folder.setText("Folder");
      folder.setAsImageButton(true);
      folder.addParameter(fileInSessionParameter,id);
      add(folder);
    }

    add(T);
   }

    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

  }