package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.AbstractChooserWindow;
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
    //frameset fixes
    setEmpty();//for IWAdminWindow
    setOnlyScript(true);//for AbstractChooserWindow
    //
    setWidth(640);
    setHeight(480);
    setResizable(true);

    frame = new FrameSet();
    frame.add(Top.class);
    frame.add(BottomFrameSet.class);
    frame.setSpanPixels(1,50);
    frame.setScrollbar(false);
    frame.setScrolling(1,false);
    frame.setScrolling(2,false);
    frame.setSpanAdaptive(2);
    frame.setResizable(true);
  }

  public void displaySelection(IWContext iwc){
   //store the parameter in session
    //MediaBusiness.getMediaParameterNameInSession(iwc);
    MediaBusiness.saveMediaIdToSession(iwc,MediaBusiness.getMediaId(iwc));
    
    
    String chooserType = iwc.getParameter(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME);
    if( chooserType!=null ){
      iwc.setSessionAttribute(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME,chooserType);
    }

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
    setAllMargins(0);
    setBackgroundColor(MediaConstants.MEDIA_TREE_VIEWER_BACKGROUND_COLOR);
    add(new MediaTreeViewer());
   }
  }

  public static class FileViewer extends MediaViewerWindow{
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
     /**
      * TODO: remove hack!!!
      */
     setBackgroundColor("#9DB308");//IWAdminWindow.HEADER_COLOR);
    }

   public void main(IWContext iwc) throws Exception{
   	IWResourceBundle iwrb = getResourceBundle(iwc);
    Table headerTable = new Table();
    headerTable.setCellpadding(0);
    headerTable.setCellspacing(0);
    headerTable.setWidth("100%");
    headerTable.setHeight("100%");
    headerTable.setAlignment(1,1,"left");//changed from right
    headerTable.addText(iwrb.getLocalizedString("user_property_window", "User Property Window"), IWConstants.BUILDER_FONT_STYLE_TITLE);
    /**
     * TODO: remove hack!!! 
     */
    headerTable.add(getBundle(iwc).getImage("top.gif"));
 //   headerTable.add(iwc.getApplication().getCoreBundle().getImage("/editorwindow/idegaweb.gif","idegaWeb"),1,1);
    add(headerTable);
   }

  }


}




