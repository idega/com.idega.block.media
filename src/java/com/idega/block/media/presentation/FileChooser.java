package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.builder.business.BuilderConstants;
import com.idega.builder.business.IBFileChooser;
import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.AbstractChooser;

/**
 * Title: com.idega.block.media.presentation.FileChooser
 * Description: The chooser object for files. Is used by default if a setXX method uses a ICFile as a parameter
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class FileChooser extends AbstractChooser implements IBFileChooser {
  private String style;
  private final String defaultChooserName = "f_c_n";

  public FileChooser() {
	addForm(false);
	setChooserParameter(defaultChooserName);
  }

  public FileChooser(String chooserName) {
	this();
    setChooserParameter(chooserName);
  }

  public FileChooser(String chooserName,String style) {
    this(chooserName);
    setInputStyle(style);
  }

  public Class getChooserWindowClass() {
    return MediaChooserWindow.class;
  }

  public void main(IWContext iwc){
    IWBundle iwb = iwc.getIWMainApplication().getBundle(BuilderConstants.STANDARD_IW_BUNDLE_IDENTIFIER);
    
    
    setChooseButtonImage(iwb.getImage("open.gif","Choose file"));
    addParameterToChooserLink(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME,MediaConstants.MEDIA_CHOOSER_FILE);
    if( getChooserValue()!= null ){
      addParameterToChooserLink(MediaBusiness.getMediaParameterNameInSession(iwc),getChooserValue());
    }
  }

  public void setSelectedFile(ICFile file){
    super.setChooserValue(file.getName(),file.getPrimaryKey().toString());
  }

  public void setValue(Object file){
    setSelectedFile((ICFile)file);
  }

}
