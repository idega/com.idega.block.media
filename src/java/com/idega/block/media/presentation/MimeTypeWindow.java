package com.idega.block.media.presentation;

import com.idega.presentation.ui.Window;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaProperties;
import com.idega.block.media.business.MediaBusiness;
import com.idega.presentation.Table;
import com.idega.presentation.ui.*;
import com.idega.presentation.text.*;
import java.sql.*;
import com.idega.presentation.IWContext;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.core.data.ICFileType;
import com.idega.core.data.ICFileTypeHandler;
import com.idega.core.data.ICMimeType;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.idegaweb.IWCacheManager;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Title: com.idega.block.media.presentation.MimeTypeWindow
 * Description: A window for adding new mimetypes and associating them with filetypes and handlers
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MimeTypeWindow extends IWAdminWindow{

private IWBundle iwb;
private IWResourceBundle iwrb;

  public void main(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    iwb = getBundle(iwc);
    setBackgroundColor("white");
    /**@todo add localkey to bundle**/
    setTitle(iwrb.getLocalizedString("mimetype.window.title","Add mimetype"));

    handleEvents(iwc);
  }

  private void handleEvents(IWContext iwc){
    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    if( action != null ){
      //saving the mimetype
      if( action.equals(MediaConstants.MEDIA_ACTION_SAVE) ){
        String mimeType = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME);
        String mimeDescription = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME);
        int fileTypeId = Integer.parseInt(iwc.getParameter(MediaConstants.MEDIA_FILE_TYPE_PARAMETER_NAME));

        try{
          ICMimeType mime = new ICMimeType();
          mime.setMimeTypeAndDescription(mimeType,mimeDescription);
          mime.setFileTypeId(fileTypeId);
          mime.insert();
        }
        catch( Exception ex ){
         ex.printStackTrace(System.err);
         add(iwrb.getLocalizedString("mimetype.window.errorinsave","Try again, an error occured while saving."));
         add(new BackButton());
        }

      }

    }
    else{
      String mimeType = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME);
       //insert dropdowns and form
      Form form = new Form();
      Table table = new Table(3,4);
      DropdownMenu typemenu = new DropdownMenu(MediaConstants.MEDIA_FILE_TYPE_PARAMETER_NAME);
      IWCacheManager cm = iwc.getApplication().getIWCacheManager();
      HashMap types =  (HashMap) cm.getCachedTableMap(ICFileType.class);
      Iterator iter = types.keySet().iterator();
      while (iter.hasNext()) {
        ICFileType type = (ICFileType) types.get((String)iter.next());
        typemenu.addMenuElement(type.getID(),type.getDisplayName());
      }
      /** @todo add strings to the media bundle **/
      HiddenInput mime = new HiddenInput(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME,mimeType);
      Text mimeText = new Text("Mimetype");
      Text mimeTypeText = new Text(mimeType);
      Text mimeDescription = new Text(iwrb.getLocalizedString("mimetype.window.description","Description"));
      Text fileTypeText = new Text(iwrb.getLocalizedString("mimetype.window.filetype","File type"));
      TextInput descriptionInput = new TextInput(MediaConstants.MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME);


      table.add(mimeText,1,1);
      table.add(mimeDescription,1,2);
      table.add(fileTypeText,1,3);

      table.add(mimeTypeText,2,1);
      table.add(descriptionInput,2,2);
      table.add(typemenu,2,3);

      Link save = new Link(iwrb.getLocalizedString("mimetype.window.save","SAVE"));
      save.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE);
      save.setToFormSubmit(form);

      Link close = new Link(iwrb.getLocalizedString("mimetype.window.close","CLOSE"));
      close.setOnClick("window.close()");

      table.add(close,2,4);
      table.add(save,2,4);

      form.add(table);

      add(table);

    }


  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}