package com.idega.block.media.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.block.media.business.SimpleImage;
import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

 public class SimpleChooser extends ModuleObjectContainer implements SimpleImage{

    private String sessImageParameter = "image_id";
    private boolean includeLinks;
    private IWBundle iwb;
    private String IW_BUNDLE_IDENTIFIER="com.idega.block.image";

    public void setToIncludeLinks(boolean includeLinks){
      this.includeLinks = includeLinks;
    }
    public String getBundleIdentifier(){
      return IW_BUNDLE_IDENTIFIER;
    }

    public void  main(ModuleInfo modinfo){
      iwb = getBundle(modinfo);
      checkParameterName(modinfo);
      Table Frame = new Table();
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
      IFrame ifList = new IFrame(target1,SimpleLister.class);
      IFrame ifViewer = new IFrame(target2, SimpleViewer.class);
      ifList.setWidth(210);
      ifList.setHeight(410);
      ifViewer.setWidth(500);
      ifViewer.setHeight(410);

      ifList.setBorder(1);
      ifViewer.setBorder(1);
      Frame.add(ifList,1,1);
      Frame.add(ifViewer,2,1);
      Frame.setBorderColor("#00FF00");
      if(includeLinks)
        Frame.add(getLinkTable(iwb),2,2);

      add(Frame);
    }

    public void setSessionSaveParameterName(String prmName){
      sessImageParameter = prmName;
    }
    public String getSessionSaveParameterName(){
      return sessImageParameter;
    }
     public void checkParameterName(ModuleInfo modinfo){
       if(modinfo.getParameter(sessImageParameterName)!=null){
        sessImageParameter = modinfo.getParameter(sessImageParameterName);
        //add(sessImageParameter);
        modinfo.setSessionAttribute(sessImageParameterName,sessImageParameter);
      }
      else if(modinfo.getSessionAttribute(sessImageParameterName)!=null)
        sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
    }

    public ModuleObject getLinkTable(IWBundle iwb){
      Table T = new Table();

      Link btnAdd = getNewImageLink("add");
        btnAdd.setFontStyle("text-decoration: none");
        btnAdd.setFontColor("#FFFFFF");
        btnAdd.setBold();
      Link btnDelete = getDeleteLink(iwb.getImage("sdelete.gif","sdelete.gif","Delete"));//"delete");
        //btnDelete.setFontStyle("text-decoration: none");
        //btnDelete.setFontColor("#FFFFFF");
        //btnDelete.setBold();
      Link btnSave = getSaveLink("save");
        btnSave.setFontStyle("text-decoration: none");
        btnSave.setFontColor("#FFFFFF");
        btnSave.setBold();
      Link btnReload = getReloadLink("reload");
        btnReload.setFontStyle("text-decoration: none");
        btnReload.setFontColor("#FFFFFF");
        btnReload.setBold();
      T.add(btnAdd,1,1);
      T.add(btnSave,2,1);
      T.add(btnDelete,3,1);
      T.add(btnReload,4,1);

      return T;
    }

    public Link getNewImageLink(String mo){
      Link L = new Link(mo,SimpleUploaderWindow.class);
      L.addParameter("action","upload");
      L.addParameter("submit","new");
      L.setTarget(target2);
      return L;
    }

    public Link getSaveLink(String mo){
      Link L = new Link(mo,SimpleViewer.class);
      L.addParameter(prmAction,actSave);
      L.setOnClick("window.close()");
      L.setTarget(target2);
      return L;
    }

    public Link getDeleteLink(ModuleObject mo){
      Link L = new Link(mo,SimpleViewer.class);
      L.addParameter(prmAction,actDelete);
      L.setOnClick("top.setTimeout('top.frames.lister.location.reload()',150)");
      L.setTarget(target2);
      return L;
    }

    public Link getReloadLink(String mo){
      Link L = new Link(mo,SimpleLister.class);
      L.setTarget(target1);
      return L;
    }
}