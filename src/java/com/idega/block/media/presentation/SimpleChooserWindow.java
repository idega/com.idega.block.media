package com.idega.block.media.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.block.media.business.SimpleImage;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

 public class SimpleChooserWindow extends IWAdminWindow {
    private IWBundle iwb;
    private String IW_BUNDLE_IDENTIFIER="com.idega.block.image";

    public SimpleChooserWindow(){
      super();
      setResizable(true);
      setWidth(726);
      setHeight(460);
    }

    public String getBundleIdentifier(){
      return IW_BUNDLE_IDENTIFIER;
    }

    public void  main(IWContext iwc) throws Exception{
      iwb = getBundle(iwc);
      SimpleChooser SC = new SimpleChooser();
      SC.setToIncludeLinks(false);
      add(SC);
      addHeaderObject(SC.getLinkTable(iwb));
      setTitle("Image Chooser Block Media");
      addTitle("Image Chooser" );

      //setParentToReload();
    }
}