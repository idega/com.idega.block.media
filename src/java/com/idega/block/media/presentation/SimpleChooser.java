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

 public class SimpleChooser extends PresentationObjectContainer implements SimpleImage{

    private String sessImageParameter = "image_id";
    private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.image";
    private boolean includeLinks;

    public void setToIncludeLinks(boolean includeLinks){
      this.includeLinks = includeLinks;
    }

    public String getBundleIdentifier(){
      return IW_BUNDLE_IDENTIFIER ;
    }

    public static String getSaveImageFunctionName(){
      return "saveImageId()";
    }

    public static String getSaveImageFunction(){
      StringBuffer function = new StringBuffer("");
      function.append(" var iImageId = -1 ; \n");
      function.append("function "+getSaveImageFunctionName()+" {\n \t");
      function.append("top.window.opener.setImageId(iImageId) ; \n \t");
      function.append("window.close(); \n }");
      return function.toString();
    }

    public void  main(IWContext iwc){
      IWBundle iwb = getBundle(iwc);
      checkParameterName(iwc);

      getParentPage().getAssociatedScript().addFunction("callbim",getSaveImageFunction() );


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
     public void checkParameterName(IWContext iwc){
       if(iwc.getParameter(sessImageParameterName)!=null){
        sessImageParameter = iwc.getParameter(sessImageParameterName);
        //add(sessImageParameter);
        iwc.setSessionAttribute(sessImageParameterName,sessImageParameter);
      }
      else if(iwc.getSessionAttribute(sessImageParameterName)!=null)
        sessImageParameter = (String) iwc.getSessionAttribute(sessImageParameterName);
    }

    public PresentationObject getLinkTable(IWBundle iwb){
      Table T = new Table();

      Text add = new Text("add");
      add.setFontStyle("text-decoration: none");
      add.setFontColor("#FFFFFF");
      add.setBold();
      Link btnAdd = getNewImageLink(add);

      Text del = new Text("delete");
      del.setFontStyle("text-decoration: none");
      del.setFontColor("#FFFFFF");
      del.setBold();
      Link btnDelete = getDeleteLink(del);

      Text save = new Text("use");
      save.setFontStyle("text-decoration: none");
      save.setFontColor("#FFFFFF");
      save.setBold();
      Link btnSave = getSaveLink(save);

      Text reload = new Text("reload");
      reload.setFontStyle("text-decoration: none");
      reload.setFontColor("#FFFFFF");
      reload.setBold();
      Link btnReload = getReloadLink(reload);

      T.add(btnAdd,1,1);
      T.add(btnSave,2,1);
      T.add(btnDelete,3,1);
      T.add(btnReload,4,1);

      return T;
    }

    public Link getNewImageLink(PresentationObject mo){
      Link L = new Link(mo,SimpleUploaderWindow.class);
      L.addParameter("action","upload");
      L.addParameter("submit","new");
      L.setTarget(target2);
      return L;
    }

    public Link getSaveLink(PresentationObject mo){
      Link L = new Link(mo,SimpleViewer.class);
      L.addParameter(prmAction,actSave);
      L.setOnClick(getSaveImageFunctionName());
      L.setTarget(target2);
      return L;
    }

    public Link getDeleteLink(PresentationObject mo){
      Link L = new Link(mo,SimpleViewer.class);
      L.addParameter(prmAction,actDelete);
      L.setOnClick("top.setTimeout('top.frames.lister.location.reload()',150)");
      L.setTarget(target2);
      return L;
    }

    public Link getReloadLink(PresentationObject mo){
      Link L = new Link(mo,SimpleLister.class);
      L.setTarget(target1);
      return L;
    }
}