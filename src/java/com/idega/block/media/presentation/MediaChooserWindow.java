package com.idega.block.media.presentation;

import com.idega.idegaweb.presentation.IWAdminWindow;
import com.idega.presentation.*;
import com.idega.presentation.text.*;
import com.idega.presentation.ui.*;
import com.idega.util.idegaTimestamp;
import com.idega.idegaweb.IWBundle;
import com.idega.block.media.business.MediaConstants;
import com.idega.presentation.FrameSet;
import com.idega.block.media.business.MediaBusiness;
/**
 * Title: com.idega.block.media.presentation.MediaChooserWindow
 * Description: The frame window that displays the filesystem
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

 //public class MediaChooserWindow extends FrameSet {
 public class MediaChooserWindow extends AbstractChooserWindow {
  private IWBundle iwb;
  //  public static String prmReloadParent = "simple_upl_wind_rp";
  private String fileInSessionParameter = "ic_file_id";
  private FrameSet frame = null;

  public MediaChooserWindow(){
    super();
    setEmpty();//for IWAdminWindow
    setOnlyScript(true);//for AbstractChooserWindow
    setWidth(640);
    setHeight(480);
    setResizable(true);

    frame = new FrameSet();
    frame.add(Top.class);
    frame.add(BottomFrameSet.class);
    frame.setSpanPixels(1,24);
    frame.setScrollbar(false);
    frame.setScrolling(1,false);
    frame.setScrolling(2,false);
    frame.setSpanAdaptive(2);
    frame.setResizable(true);
  }

  public void displaySelection(IWContext iwc){
    //MediaBusiness.getMediaParameterNameInSession(iwc);//store the parameter in session
System.err.println(MediaBusiness.getMediaParameterNameInSession(iwc));
    if( MediaBusiness.reloadOnClose(iwc) ){
      frame.setParentToReload();
    }

    add(frame);
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER;
  }


  public static class FileTree extends Page{
   public FileTree(){
    add(new MediaTreeViewer());
   }
  }

  public static class FileViewer extends Page{
   public FileViewer(){
    add(new MediaViewer());
   }
  }

  public static class BottomFrameSet extends FrameSet{
   public BottomFrameSet(){
    add(FileTree.class);
    add(FileViewer.class);
    setFrameName(1,MediaConstants.TARGET_MEDIA_TREE);
    setFrameName(2,MediaConstants.TARGET_MEDIA_VIEWER);

    this.setSpanPixels(1,210);
    this.setHorizontal();
    // this.setSpanPixels(3,35);
    this.setScrollbar(false);
    this.setScrolling(1,true);
    this.setScrolling(2,true);
   }
  }

  public static class Top extends Page{

    public Top(){
     setAllMargins(0);
     setBackgroundColor(IWAdminWindow.HEADER_COLOR);
    }

   public void main(IWContext iwc) throws Exception{
    Table headerTable = new Table();
    headerTable.setCellpadding(0);
    headerTable.setCellspacing(0);
    headerTable.setWidth("100%");
    headerTable.setHeight("100%");
    headerTable.setAlignment(1,1,"right");
    headerTable.add(iwc.getApplication().getCoreBundle().getImage("/editorwindow/idegaweb.gif","idegaWeb"),1,1);
    add(headerTable);
   }

  }


}




