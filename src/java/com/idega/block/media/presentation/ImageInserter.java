package com.idega.block.media.presentation;

/**
 * Title: ImageInserter
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company: idega
 * @author Eirikur Hrafnsson, eiki@idega.is
 * @version 1.0
 *
 */

import java.sql.*;
import java.util.*;
import java.io.*;
import com.idega.util.*;
import com.idega.presentation.text.*;
import com.idega.presentation.*;
import com.idega.presentation.ui.*;
import com.idega.block.media.business.ImageBusiness;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;

public class ImageInserter extends Block{

private final static String IW_BUNDLE_IDENTIFIER="com.idega.block.image";
private int imageId = -1;
private String imSessionImageName =null;
private String sHiddenInputName = null;
//private String adminURL = "/image/insertimage.jsp";
private String adminURL = null;
private String nameOfWindow;
private String sUseBoxString;
private int maxImageWidth = 140;
private int imageWidth = 0;
private int imageHeight = 0;
private boolean hasUseBox = true;
private boolean selected = false;
private boolean openInWindow = false;
private Class windowClass = SimpleChooserWindow.class;
private Image setImage;
private boolean limitWidth = true;
public final String sessionImageParameterName = "im_image_session_name";
private String prmUseBox = "insertImage";
private boolean maintainSessionParameter = false;
private boolean setWindowToReloadParent = false;

private IWBundle iwb;
private IWResourceBundle iwrb;

public ImageInserter(){
  this.imSessionImageName="image_id";
  this.sHiddenInputName = "image_id";
}

public ImageInserter(Image setImage){
  this();
  this.setImage=setImage;
}

public ImageInserter(int imageId) {
  this.imageId=imageId;
  this.imSessionImageName="image_id";
  this.sHiddenInputName = "image_id";
}

public ImageInserter(String imSessionImageName) {
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
}

public ImageInserter(String imSessionImageName, boolean hasUseBox) {
  this(imSessionImageName);
  setHasUseBox(hasUseBox);
}

public ImageInserter(int imageId, String imSessionImageName) {
  this.imageId=imageId;
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
}

public ImageInserter(Class WindowToOpen) {
  this.imSessionImageName=imSessionImageName;
  this.sHiddenInputName = imSessionImageName;
  windowClass = WindowToOpen;
  openInWindow = true;

}

  public void main(IWContext iwc)throws Exception{
      this.empty();

      iwb = getBundle(iwc);
      iwrb = getResourceBundle(iwc);

      nameOfWindow = iwrb.getLocalizedString("new_image","New image");
      sUseBoxString = iwrb.getLocalizedString("use_image","Use image");
      //System.err.println("Imageinserter : imSessionImageName "+imSessionImageName);
      String imageSessionId = (String) iwc.getSessionAttribute(imSessionImageName);
      // debug
      //add(imSessionImageName + " "+imageSessionId);

      if ( imageSessionId != null ) {
        imageId = Integer.parseInt(imageSessionId);
        if(!maintainSessionParameter){
          iwc.removeSessionAttribute(imSessionImageName);
        }
      }


      Image image=setImage;
        if(image==null){
          if ( imageId == -1 ) {
            image = iwrb.getImage("picture.gif",iwrb.getLocalizedString("new_image","New image"),138,90);
          }
          else {
            image = new Image(imageId);//,"rugl");
          }
          if( limitWidth ) {
            image.setMaxImageWidth(this.maxImageWidth);
          }
          if(imageWidth > 0 ){
            image.setWidth(imageWidth);
          }
          if(imageHeight > 0){
            image.setHeight(imageHeight );
          }
          image.setNoImageLink();
        }
        image.setName("rugl");
        Page P = getParentPage();
        if(P!=null){
          Script S = P.getAssociatedScript();
          if(S!=null)
            S.addFunction("imchange",getImageChangeJSFunction());
        }

      Link imageAdmin = null;
      if(adminURL == null){
        imageAdmin = new Link(image);
        imageAdmin.setWindowToOpen(windowClass);
      }
      else{
        Window insertNewsImageWindow = new Window(nameOfWindow,ImageBusiness.IM_BROWSER_WIDTH,ImageBusiness.IM_BROWSER_HEIGHT,adminURL);
        imageAdmin = new Link(image,insertNewsImageWindow);
      }
      if(setWindowToReloadParent)
        imageAdmin.addParameter(SimpleChooserWindow.prmReloadParent,"true");
      imageAdmin.addParameter("submit","new");
      imageAdmin.addParameter(sessionImageParameterName,imSessionImageName);
			String sImageId = imageId > 0 ?String.valueOf(imageId):"";
      if ( imageId != -1 )
        imageAdmin.addParameter(imSessionImageName,imageId);


      HiddenInput hidden = new HiddenInput(sHiddenInputName,Integer.toString(imageId));
        hidden.keepStatusOnAction();
      CheckBox insertImage = new CheckBox(prmUseBox,"Y");
        insertImage.setChecked(selected);

      Text imageText = new Text(sUseBoxString+":&nbsp;");
        imageText.setFontSize(1);

      Table borderTable = new Table(1,1);
        borderTable.setWidth("100%");
        borderTable.setCellspacing(1);
        borderTable.setCellpadding(0);
        borderTable.setColor("#000000");
        borderTable.add(imageAdmin);

      Table imageTable = new Table(1,2);
        imageTable.setAlignment(1,2,"right");
        imageTable.add(borderTable,1,1);
        if(hasUseBox){
          imageTable.add(imageText,1,2);
          imageTable.add(insertImage,1,2);
        }

        imageTable.add(hidden,1,2);

      add(imageTable);
  }

  public static String getFunction(int id){
    return "setImageId("+id+")";
  }

  public String getImageChangeJSFunction(){
    StringBuffer function = new StringBuffer("");//var imageName = \"rugl\"; \n");
    function.append("function setImageId(imageId) { \n \t");
    function.append("if (document.images) { \n \t\t");
    function.append("document.rugl.src = \"/servlet/MediaServlet/\"+imageId+\"media?media_id=\"+imageId; \n\t ");
    function.append("document.forms[0]."+sHiddenInputName+".value = imageId \n\t}\n }");

    return function.toString();
  }

  public void setHasUseBox(boolean useBox){
    this.hasUseBox = useBox;
  }

  public void setHasUseBox(boolean useBox,String prmUseBox){
    this.hasUseBox = useBox;
    this.prmUseBox = prmUseBox;
  }

  public void setUseBoxParameterName(String prmUseBox){
    this.prmUseBox = prmUseBox;
  }

  public void setSelected(boolean selected){
    this.selected = selected;
  }

  public void setUseBoxString(String sUseBoxString){
    this.sUseBoxString = sUseBoxString;
  }

  public void setHiddenInputName(String name){
    sHiddenInputName = name;
  }

  public String getHiddenInputName(){
    return sHiddenInputName;
  }

  public void setMaxImageWidth(int maxWidth){
    this.maxImageWidth = maxWidth;
  }

  public void setImageHeight(int imageHeight){
    this.imageHeight = imageHeight;
  }

  public void setImageWidth(int imageWidth){
    this.imageWidth = imageWidth;
  }

  public void setAdminURL(String adminURL) {
    this.adminURL=adminURL;
  }

  public void setImageId(int imageId) {
    this.imageId=imageId;
  }

  public void setImSessionImageName(String imSessionImageName) {
    this.imSessionImageName=imSessionImageName;
    this.sHiddenInputName=imSessionImageName;
  }

  public String getImSessionImageName() {
    return this.imSessionImageName;
  }

  public void maintainSessionParameter(){
    maintainSessionParameter = true;
  }

  public void setWindowToReload(boolean reload){
    setWindowToReloadParent = reload;
  }

  public void setWindowClassToOpen(Class WindowClass){
    windowClass = WindowClass;
    openInWindow = true;
  }

  public String getBundleIdentifier(){
    return IW_BUNDLE_IDENTIFIER;
  }

  public void limitImageWidth(boolean limitWidth){
   this.limitWidth = limitWidth;
  }
}