package com.idega.block.media.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.block.media.business.SimpleImage;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

 public class SimpleChooserWindow extends IWAdminWindow {

    public String sessImageParameter = "image_id";

    public SimpleChooserWindow(){
      super();
      setResizable(true);
      setWidth(726);
      setHeight(460);
    }

    public void  main(ModuleInfo modinfo) throws Exception{
      super.main(modinfo);
      SimpleChooser SC = new SimpleChooser();
      SC.setToIncludeLinks(false);
      add(SC);
      addHeaderObject(SC.getLinkTable());
      setTitle("Image Chooser");

      setParentToReload();
    }
}