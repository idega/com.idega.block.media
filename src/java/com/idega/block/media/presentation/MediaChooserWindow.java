package com.idega.block.media.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;
import com.idega.block.media.business.MediaConstants;

/**
 * Title: com.idega.block.media.presentation.MediaChooserWindow
 * Description: A window wrapper for MediaChooser
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

 public class MediaChooserWindow extends AbstractChooserWindow {
    private IWBundle iwb;
    public static String prmReloadParent = "simple_upl_wind_rp";

    public MediaChooserWindow(){
      super();
      setResizable(true);
      setWidth(726);
      setHeight(460);
    }

    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER;
    }

    public void  displaySelection(IWContext iwc){
      iwb = getBundle(iwc);
      MediaChooser SC = new MediaChooser();
      SC.setToIncludeLinks(false);
      add(SC);
      //addHeaderObject(SC.getLinkTable(iwb));
      setTitle("Media chooser");
      if(iwc.getParameter(prmReloadParent )!= null)
        setParentToReload();
    }
}