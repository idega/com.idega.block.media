package com.idega.block.media.presentation;

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.block.media.business.MediaConstants;

/**
 * Title: com.idega.block.media.presentation.MediaChooser
 * Description: The Finder (FileManager) main class. This sets upp all the different object such as the tree and viewer.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is with good imput from Aron "Brockowitch" ( aron@idega.is )
 * @version 1.0
 */

 public class MediaChooser extends PresentationObjectContainer{

    private String fileInSessionParameter = "ic_file_id";

    private boolean includeLinks;
    private boolean usesOld = false;

    public void setToIncludeLinks(boolean includeLinks){
      this.includeLinks = includeLinks;
    }

    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

    public static String getSaveImageFunctionName(){
      return "saveImageId()";
    }

    public static String getSaveImageFunction(){
      StringBuffer function = new StringBuffer("");
      function.append(" var iImageId = -1 ; \n");
      function.append("function "+getSaveImageFunctionName()+" {\n \t");
      function.append("top.window.opener.setImageId(iImageId) ; \n \t");
      function.append("top.window.close(); \n }");
      return function.toString();
    }

    public void  main(IWContext iwc){
      IWBundle iwb = getBundle(iwc);
      checkParameterName(iwc);

      if(iwc.getApplication().getSettings().getProperty(MediaServlet.USES_OLD_TABLES)!=null)
        usesOld = true;
      getParentPage().getAssociatedScript().addFunction("callbim",getSaveImageFunction() );

      Table Frame = new Table();
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
      IFrame ifList = new IFrame(MediaConstants.TARGET_MEDIA_TREE,MediaTreeViewer.class);
      //**@todo insert mediaviewer**/
      IFrame ifViewer = new IFrame(MediaConstants.TARGET_MEDIA_VIEWER, MediaViewer.class);


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
      fileInSessionParameter = prmName;
    }
    public String getSessionSaveParameterName(){
      return fileInSessionParameter;
    }
     public void checkParameterName(IWContext iwc){
       if(iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null){
        fileInSessionParameter = iwc.getParameter(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
        //add(fileInSessionParameter);
        iwc.setSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME,fileInSessionParameter);
      }
      else if(iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME)!=null)
        fileInSessionParameter = (String) iwc.getSessionAttribute(MediaConstants.FILE_IN_SESSION_PARAMETER_NAME);
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
      Class C = MediaUploaderWindow.class;
      Link L = new Link(mo,C);/*
      L.addParameter("action","upload");
      L.addParameter("submit","new");*/
      L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
      return L;
    }

    public Link getSaveLink(PresentationObject mo){
      Class C = MediaViewer.class;
      Link L = new Link(mo,C);
      L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE);
      L.setOnClick(getSaveImageFunctionName());
      L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
      return L;
    }

    public Link getDeleteLink(PresentationObject mo){
      Class C = MediaViewer.class;
      Link L = new Link(mo,C);
      L.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_DELETE);
      L.setOnClick("top.setTimeout('top.frames.lister.location.reload()',150)");
      L.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
      return L;
    }

    public Link getReloadLink(PresentationObject mo){
      Class C = MediaTreeViewer.class;
      Link L = new Link(mo,C);
      L.setTarget(MediaConstants.TARGET_MEDIA_TREE);
      return L;
    }
}