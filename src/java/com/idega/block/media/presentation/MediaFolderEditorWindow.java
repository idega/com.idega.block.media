package com.idega.block.media.presentation;

import com.idega.presentation.ui.Window;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaBusiness;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.core.data.*;

/**
 * Title: com.idega.block.media.presentation.MediaFolderEditorWindow
 * Description:  This class handles creating and editing of folders.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaFolderEditorWindow extends Window {
  private IWResourceBundle iwrb;

  public MediaFolderEditorWindow() {
  }


  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    iwrb = getResourceBundle(iwc);
    handleEvents(iwc);
  }

  public void handleEvents(IWContext iwc) throws Exception {
    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    String mediaId = MediaBusiness.getMediaId(iwc);

    if( action.equals(MediaConstants.MEDIA_ACTION_NEW) ){
      Form form = new Form();
      TextInput folderName = new TextInput(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      form.add(iwrb.getLocalizedString("mediafoldereditwindow.name.the.folder","Name the folder"));
      form.add(folderName);

      if( mediaId != null){
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),mediaId));
      }
      else{
        ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),Integer.toString(rootNode.getID())));
      }

      form.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE));
      add(form);
    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_EDIT) ){

    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_SAVE) ){
      String folderName = iwc.getParameter(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      if( (folderName!=null) && !(folderName.equalsIgnoreCase("")) ){
        int parentId = Integer.parseInt(mediaId);
        ICFile parent = new ICFile(parentId);
        ICFile folder = new ICFile();
        folder.setName(folderName);
        folder.setMimeType(ICMimeType.IC_MIME_TYPE_FOLDER);
        folder.insert();
        parent.addChild(folder);
        add(iwrb.getLocalizedString("mediafoldereditwindow.folder.saved","Folder created"));
      }
    }
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }





}