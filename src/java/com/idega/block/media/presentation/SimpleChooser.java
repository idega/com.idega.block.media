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

 public class SimpleChooser extends ModuleObjectContainer implements SimpleImage{

    private String sessImageParameter = "image_id";
    private boolean includeLinks;

    public void setToIncludeLinks(boolean includeLinks){
      this.includeLinks = includeLinks;
    }

    public void  main(ModuleInfo modinfo){
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
        Frame.add(getLinkTable(),2,2);

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
        modinfo.setSessionAttribute(sessImageParameterName,sessImageParameter);
      }
      else if(modinfo.getSessionAttribute(sessImageParameterName)!=null)
        sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
    }

    public ModuleObject getLinkTable(){
      Table T = new Table();
      Link btnAdd = getNewImageLink("add");
        btnAdd.setFontStyle("text-decoration: none");
        btnAdd.setFontColor("#FFFFFF");
        btnAdd.setBold();
      Link btnDelete = getDeleteLink("delete");
        btnDelete.setFontStyle("text-decoration: none");
        btnDelete.setFontColor("#FFFFFF");
        btnDelete.setBold();
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
      Link L = new Link(mo,"/image/insertimage.jsp");
      //Link L = new Link(mo,SimpleUploaderWindow.class);
      L.addParameter("submit","new");
      L.addParameter(sessImageParameterName,sessImageParameter);

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

    public Link getDeleteLink(String mo){
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