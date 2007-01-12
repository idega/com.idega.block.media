package com.idega.block.media.presentation;

import java.util.HashMap;
import java.util.Iterator;

import com.idega.block.media.business.MediaConstants;
import com.idega.core.file.data.ICFileType;
import com.idega.core.file.data.ICMimeType;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWMainApplication;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.BackButton;
import com.idega.presentation.ui.DropdownMenu;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

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
private String mimeType = null;

  public MimeTypeWindow(){
  }

  public MimeTypeWindow(String mimeType){
    this.mimeType = mimeType;
    super.setEmpty();
  }

  public void main(IWContext iwc){
    setBackgroundColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
    setAllMargins(0);
    this.iwrb = getResourceBundle(iwc);
    this.iwb = getBundle(iwc);
    setBackgroundColor("white");
    /**@todo add localkey to bundle**/
    setTitle(this.iwrb.getLocalizedString("mimetype.window.title","Add mimetype"));

    handleEvents(iwc);
  }

  private void handleEvents(IWContext iwc){
    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    if( action != null ){
      //saving the mimetype
      if( action.equals(MediaConstants.MEDIA_ACTION_SAVE) ){
        saveMimeType(iwc);
      }
    }
    else{//adding a mimetype
      addMimeTypeForm(iwc);
    }
  }

  private void saveMimeType(IWContext iwc){
    this.mimeType = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME);
    String mimeDescription = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME);
    int fileTypeId = Integer.parseInt(iwc.getParameter(MediaConstants.MEDIA_FILE_TYPE_PARAMETER_NAME));

    try{
    	System.out.println("storing new mimetype");
      ICMimeType mime = ((com.idega.core.file.data.ICMimeTypeHome)com.idega.data.IDOLookup.getHomeLegacy(ICMimeType.class)).createLegacy();
      mime.setMimeTypeAndDescription(this.mimeType,mimeDescription);
      mime.setFileTypeId(fileTypeId);
      mime.insert();
    }
    catch( Exception ex ){
     ex.printStackTrace(System.out);
     add(this.iwrb.getLocalizedString("mimetype.window.errorinsave","Try again, an error occured while saving."));
     add(new BackButton());
    }
  }

  private void addMimeTypeForm(IWContext iwc){
    if(this.mimeType==null) {
		this.mimeType = iwc.getParameter(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME);
	}
     //insert dropdowns and form
    Form form = new Form();
    Table table = new Table(3,4);
    DropdownMenu typemenu = new DropdownMenu(MediaConstants.MEDIA_FILE_TYPE_PARAMETER_NAME);
    IWCacheManager cm = IWMainApplication.getIWCacheManager();
    HashMap types =  (HashMap) cm.getCachedTableMap(ICFileType.class);
    Iterator iter = types.keySet().iterator();
    while (iter.hasNext()) {
      ICFileType type = (ICFileType) types.get(iter.next());
      typemenu.addMenuElement(type.getID(),type.getDisplayName());
    }

    HiddenInput mime = new HiddenInput(MediaConstants.MEDIA_MIME_TYPE_PARAMETER_NAME,this.mimeType);
    Text mimeText = new Text("Mimetype");
    Text mimeTypeText = new Text(this.mimeType);
    Text mimeDescription = new Text(this.iwrb.getLocalizedString("mimetype.window.description","Description"));
    Text fileTypeText = new Text(this.iwrb.getLocalizedString("mimetype.window.filetype","File type"));
    TextInput descriptionInput = new TextInput(MediaConstants.MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME);

    table.add(mime,1,1);
    table.add(mimeText,1,1);
    table.add(mimeDescription,1,2);
    table.add(fileTypeText,1,3);

    table.add(mimeTypeText,2,1);
    table.add(descriptionInput,2,2);
    table.add(typemenu,2,3);


    SubmitButton save = new SubmitButton(this.iwb.getImageButton(this.iwrb.getLocalizedString("mimetype.window.save","SAVE")));
	table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE));
    Link close = new Link(this.iwrb.getLocalizedString("mimetype.window.close","CLOSE"));
    close.setOnClick("window.close()");
    close.setAsImageButton(true);

    table.add(close,2,4);
    table.add(save,2,4);

    form.add(table);
    add(form);
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}
