package com.idega.block.media.presentation;

import com.idega.presentation.ui.Window;
import com.idega.presentation.Table;
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

  private void handleEvents(IWContext iwc) throws Exception {
    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    String mediaId = MediaBusiness.getMediaId(iwc);

    if( action.equals(MediaConstants.MEDIA_ACTION_NEW) ){
      Form form = new Form();
      Table table = new Table(1,2);

      TextInput folderName = new TextInput(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      //Link save = new Link("Save");
      //save.setAsImageButton(true);
      //save.setToFormSubmit(form);

      table.add(iwrb.getLocalizedString("mediafoldereditwindow.name.the.folder","Name the folder"),1,1);
      table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE),1,1);
      table.add(folderName,1,2);
      table.add(new SubmitButton("Save"),1,2);


      form.add(table);

      if( mediaId != null){
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),mediaId));
      }
      else{
        ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),Integer.toString(rootNode.getID())));
      }

      add(form);
    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_EDIT) ){
      /**@todo add edit code**/

    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_SAVE) ){
      String folderName = iwc.getParameter(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      if( (folderName!=null) && !(folderName.equalsIgnoreCase("")) ){

        int parentId = Integer.parseInt(mediaId);

        ICFile folder = new ICFile();
        folder.setName(folderName);
        folder.setMimeType(ICMimeType.IC_MIME_TYPE_FOLDER);
        folder.insert();

        if( parentId == -1 ){
          ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);
          rootNode.addChild(folder);
        }
        else {
          ICFile parent = new ICFile(parentId);
          parent.addChild(folder);
        }

        add(iwrb.getLocalizedString("mediafoldereditwindow.folder.saved","Folder created"));
        setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");

      }
    }
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }





}