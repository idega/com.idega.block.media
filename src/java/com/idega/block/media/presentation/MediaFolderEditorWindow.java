package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.core.data.ICFile;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

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
  private String fileInSessionParameter = "";

  public MediaFolderEditorWindow() {
  }


  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    iwrb = getResourceBundle(iwc);
    handleEvents(iwc);
  }

  private void handleEvents(IWContext iwc) throws Exception {
    setBackgroundColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
    setAllMargins(0);

    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession( iwc );

    int mediaId = MediaBusiness.getMediaId(iwc);

    if( action.equals(MediaConstants.MEDIA_ACTION_NEW) ){
      Form form = new Form();
      Table table = new Table(1,3);
      table.setWidth(300);
      table.setHeight(120);
      table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
      table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
      table.setVerticalAlignment(1,3,Table.VERTICAL_ALIGN_TOP);

      TextInput folderName = new TextInput(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      //Link save = new Link("Save");
      //save.setAsImageButton(true);
      //save.setToFormSubmit(form);
      Text add = new Text(iwrb.getLocalizedString("mediafoldereditwindow.name.the.folder","Name the folder"));
      add.setStyle(Text.FONT_FACE_ARIAL);
      add.setFontSize(Text.FONT_SIZE_10_HTML_2);
      add.setBold();

      table.add(add,1,1);
      table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_FOLDER_SAVE),1,2);
      table.add(folderName,1,3);
      table.add(new SubmitButton(iwrb.getLocalizedString("mv.save","save")),1,3);


      form.add(table);

      if( mediaId != -1){
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),String.valueOf(mediaId)));
      }
      else{
        ICFile rootNode = (ICFile)iwc.getApplication().getIWCacheManager().getCachedEntity(com.idega.core.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
        form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),Integer.toString(rootNode.getID())));
      }

      add(form);
    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_EDIT) ){
      /**@todo add edit code**/

    }
    else if( action.equals(MediaConstants.MEDIA_ACTION_FOLDER_SAVE) ){
      String folderName = iwc.getParameter(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
      if( (folderName!=null) && !(folderName.equalsIgnoreCase("")) ){


        ICFile folder = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).createLegacy();
        folder.setName(folderName);
        folder.setMimeType(com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);

        folder = MediaBusiness.saveMediaToDB(folder,mediaId,iwc);
        setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
        add(new MediaToolbar(folder.getID()));
        add(new MediaViewer(folder.getID()));

//        Text created = new Text(iwrb.getLocalizedString("mediafoldereditwindow.folder.saved","Folder created"));
//        created.setStyle(Text.FONT_FACE_ARIAL);
//        created.setFontSize(Text.FONT_SIZE_10_HTML_2);


      }
    }
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }





}
