package com.idega.block.media.presentation;

import com.idega.presentation.IWContext;
import com.idega.core.data.ICFile;
import com.idega.presentation.ui.AbstractChooser;
import com.idega.idegaweb.IWBundle;
import com.idega.builder.business.BuilderLogic;
import com.idega.block.media.business.*;

/**
 * Title: com.idega.block.media.presentation.FileChooser
 * Description: The chooser object for files. Is used by default if a setXX method uses a ICFile as a parameter
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class FileChooser extends AbstractChooser {
  private String style;

  public FileChooser(String chooserName) {
    addForm(false);
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
    IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);
    setChooseButtonImage(iwb.getImage("open.gif","Choose file"));
    if( getChooserValue()!= null ){
      iwc.setSessionAttribute(MediaBusiness.getMediaParameterNameInSession(iwc),getChooserValue());
    }
  }

  public void setSelectedFile(ICFile file){
    super.setChooserValue(file.getName(),file.getID());
  }

  public void setValue(Object file){
    setSelectedFile((ICFile)file);
  }

}