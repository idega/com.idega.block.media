package com.idega.block.media.presentation;

import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.presentation.*;

public class ImageEditor extends JModuleObject{
private boolean refresh = false;
private boolean showAll = true;

  public void main(ModuleInfo modinfo)throws Exception{
    String refreshing = (String) modinfo.getSessionAttribute("refresh");
    String sRefresh = modinfo.getParameter("refresh");
    ImageBrowser browser = new ImageBrowser();
    browser.setShowAll(showAll);

    if( (sRefresh!=null) || refresh || (refreshing!=null) ) browser.refresh();

    add(browser);
  }

  public void refresh(){
    this.refresh=true;
  }

  public void setShowAll(boolean showAll){
    this.showAll = showAll;
  }
}
