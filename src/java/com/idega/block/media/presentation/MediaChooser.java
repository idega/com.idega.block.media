package com.idega.block.media.presentation;

import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;
import com.idega.block.media.servlet.MediaServlet;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaBusiness;

/**
 * Title: com.idega.block.media.presentation.MediaChooser
 * Description: The Finder (FileManager) main class. This sets up all the different object such as the tree and viewer.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is with good input from Aron "Brockowich" ( aron@idega.is )
 * @version 1.0
 */

 public class MediaChooser extends PresentationObjectContainer{

    private String fileInSessionParameter = "ic_file_id";

    public String getBundleIdentifier(){
      return MediaConstants.IW_BUNDLE_IDENTIFIER ;
    }

    public void  main(IWContext iwc){
      IWBundle iwb = getBundle(iwc);
      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);


/**@todo make a FrameSet rather than IFrames**/

      Table Frame = new Table();
      Frame.setCellpadding(0);
      Frame.setCellspacing(0);
      IFrame ifList = new IFrame(MediaConstants.TARGET_MEDIA_TREE,MediaTreeViewer.class);
      //**@todo insert mediaviewer**/
      IFrame ifViewer = new IFrame(MediaConstants.TARGET_MEDIA_VIEWER, MediaViewer.class);


      ifList.setWidth(210);
      ifList.setHeight(410);
      ifViewer.setWidth(500);//500
      ifViewer.setHeight(410);//410

      ifList.setBorder(1);
      ifViewer.setBorder(1);
      Frame.add(ifList,1,1);
      Frame.add(ifViewer,2,1);
      Frame.setBorderColor("#00FF00");

      add(Frame);
    }

    public void setSessionSaveParameterName(String prmName){
      fileInSessionParameter = prmName;
    }

    public String getSessionSaveParameterName(){
      return fileInSessionParameter;
    }
/*
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
    }*/
}