package com.idega.block.media.presentation;

import com.idega.presentation.PresentationObjectContainer;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWCacheManager;
import com.idega.presentation.Table;
import com.idega.presentation.ui.Form;
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
import com.idega.block.media.data.MediaProperties;

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
  private MediaProperties props = null;



  public MediaViewer(){
  }

  public MediaViewer(MediaProperties props){
    this();
    this.props = props;
  }

    public void  main(IWContext iwc) throws Exception{
      setBackgroundColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
      setAllMargins(0);

      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
      String mediaId = null;
      String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
      if( action==null ) action = "";

      if(props!=null) mediaId = String.valueOf(props.getId());
      if( (mediaId==null) || ("-1".equals(mediaId)) ) mediaId = MediaBusiness.getMediaId(iwc);

      if( !mediaId.equals("-1") ){
        if( action.equals("") || action.equals(MediaConstants.MEDIA_ACTION_USE) || (action.equals(MediaConstants.MEDIA_ACTION_SAVE) ) ){//viewing/pressed the use button/viewing after save
          viewOrUse(iwc,action,mediaId);
        }
        else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE)){
         confirmDeleteMedia(mediaId,iwc);
         setOnUnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
        }
        else if(action.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM)){
          deleteMedia(mediaId);
          MediaBusiness.removeMediaIdFromSession(iwc);
          add(new Text("Deleted"));/** @todo localize**/
        }
      }
      else if( props!=null ){
        viewFileFromDisk(iwc,props);
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

    public void confirmDeleteMedia(String mediaId, IWContext iwc){
      int id = Integer.parseInt(mediaId);
      Cache cache = FileTypeHandler.getCachedFileInfo(id,iwc);
      ICFile file = (ICFile) cache.getEntity();
      Table T = new Table(1,3);
      T.setAlignment(1,3,"center");

      Link confirm = new Link("delete");
      confirm.setAsImageButton(true);
      confirm.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME ,MediaConstants.MEDIA_ACTION_DELETE_CONFIRM);
      confirm.addParameter(fileInSessionParameter,mediaId);
      T.add(confirm,1,1);

      Link cancel = MediaBusiness.getMediaViewerLink();
      cancel.setText("cancel");
      cancel.setAsImageButton(true);
      cancel.addParameter(fileInSessionParameter,mediaId);
      T.add(cancel,1,1);

      Text warning = new Text("Are you sure ?");/**@todo localize this*/
      warning.setFontSize(6);
      warning.setFontColor("FF0000");
      warning.setBold();

      T.add(file.getName(),1,2);
      T.add(Text.getBreak(),1,3);
      T.add(warning,1,3);

      add(T);
    }

  public void viewFileFromDisk(IWContext iwc, MediaProperties props) throws Exception{
    FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc,props.getMimeType());
    add(handler.getPresentationObject(props,iwc));
  }


   public void viewOrUse(IWContext iwc, String action, String mediaId){
    if( action.equals(MediaConstants.MEDIA_ACTION_USE) ){
      MediaBusiness.saveMediaIdToSession(iwc,mediaId);
      setOnLoad("top.window.close()");
    }
    else{
      iwc.removeSessionAttribute(fileInSessionParameter);
      IWCacheManager cm = iwc.getApplication().getIWCacheManager();
      int id = Integer.parseInt(mediaId);
      Cache cache = FileTypeHandler.getCachedFileInfo(id,iwc);
      ICFile file = (ICFile) cache.getEntity();
      FileTypeHandler handler = MediaBusiness.getFileTypeHandler(iwc,file.getMimeType());
      Table T = new Table(1,2);
      T.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
      T.setHeight(1,"15");
      T.setCellpadding(0);
      T.setCellspacing(0);
      T.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
      T.setColumnAlignment(1,Table.HORIZONTAL_ALIGN_LEFT);
      T.setHeight(Table.HUNDRED_PERCENT);
      T.setWidth(Table.HUNDRED_PERCENT);
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
        use.setOnClick(ONCLICK_FUNCTION_NAME+"('"+file.getName()+"','"+file.getID()+"');top.window.close()");
      }
      T.add(use,1,1);

      /**@todo use mediabusiness**/
      Link newLink = new Link("New",MediaUploaderWindow.class);
      newLink.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
      newLink.setAsImageButton(true);
      newLink.addParameter(fileInSessionParameter,id);
      newLink.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW);
      T.add(newLink,1,1);

      Link delete = new Link("Delete",MediaViewer.class);
      delete.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
      delete.setAsImageButton(true);
      delete.addParameter(fileInSessionParameter,id);
      delete.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE);
      T.add(delete,1,1);

      if( MediaBusiness.isFolder(file) ){
        Link folder = MediaBusiness.getNewFolderLink();
        folder.setText("Folder");
        folder.setAsImageButton(true);
        folder.addParameter(fileInSessionParameter,id);
        T.add(folder,1,1);
      }

      T.add(handler.getPresentationObject(id,iwc),1,2);

      add(T);

    }
   }

    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

  }