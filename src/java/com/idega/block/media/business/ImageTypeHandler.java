package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.ImageTypeHandler
 * Description: A type handler that handles idegaWeb system type files such as folders ( The Finder ;)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */


import com.idega.block.media.data.MediaProperties;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.ui.TextInput;
import com.idega.util.caching.Cache;
public class ImageTypeHandler extends FileTypeHandler {

  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    Cache cache = FileTypeHandler.getCachedFileInfo(icFileId,iwc);
    Image image = new Image(cache.getVirtualPathToFile(),cache.getEntity().toString());
    return image;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    Table T = new Table();
    Image image = new Image(props.getWebPath(),props.getName());
    TextInput width = new TextInput("iw_im_width");
    TextInput height = new TextInput("iw_im_height");

    //image.setOnClick("javascript:findObj('iw_im_width').value=this.width;findObj('iw_im_height').value=this.height;");
    width.setOnSubmit("javascript:this.value=document.images['"+image.getID()+"'].width;return true");
    height.setOnSubmit("javascript:this.value=document.images['"+image.getID()+"'].width;return true");

    T.add(image);
  //  T.add(width);
  //  T.add(height);

    return T;
  }


}
