package com.idega.block.media.presentation;

import com.idega.block.media.data.*;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.block.media.business.SimpleImage;
import com.idega.block.media.business.ImageProperties;
import com.idega.block.media.business.ImageBusiness;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.ModuleInfo;
import com.idega.jmodule.object.ModuleObject;
import com.idega.jmodule.object.Table;
import com.idega.jmodule.object.Image;
import com.idega.util.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import com.oreilly.servlet.MultipartRequest;
import com.idega.core.data.ICFileCategory;
/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class SimpleUploaderWindow extends Window implements SimpleImage{

    String dataBaseType;
    private String sessImageParameter = "image_id";
    Connection Conn = null;

    public SimpleUploaderWindow(){

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

    public void main(ModuleInfo modinfo){
      checkParameterName(modinfo);
      this.setBackgroundColor("white");
      this.setTitle("Idega Uploader");
      control(modinfo);
    }

    public void control(ModuleInfo modinfo){
      //add(sessImageParameter);
      String sContentType = modinfo.getRequest().getContentType();
      if(sContentType !=null && sContentType.indexOf("multipart")!=-1){
       // add(sContentType);
        add(parse(modinfo));
      }
      else{
        if(modinfo.getParameter("save")!=null){
          save(modinfo);
        }
        else
          add(getMultiForm(modinfo));
      }

    }
    public Form getMultiForm(ModuleInfo modinfo){
      Form f = new Form();
      f.setMultiPart();
      f.setAction(modinfo.getRequestURI()+"?"+com.idega.jmodule.object.Page.IW_FRAME_CLASS_PARAMETER+"="+com.idega.idegaweb.IWMainApplication.getEncryptedClassName(this.getClass()));
      f.add(new FileInput());
      f.add(new SubmitButton());
      return f;
    }

    public ModuleObject parse(ModuleInfo modinfo){
      ImageProperties ip = null;
      try {
        ip = ImageBusiness.doUpload(modinfo);
        modinfo.setSessionAttribute("image_props",ip);
      }
      catch (Exception ex) {
        ex.printStackTrace();
      }

      if(ip!=null){
        Form form = new Form();
        Table T = new Table();
        T.add(new Image(ip.getWebPath()),1,1);
        T.add(new SubmitButton("save","Save"),1,2);
        T.add(new SubmitButton("newimage","New"),1,2);
        form.add(T);
        return form;
      }
      else{
        return getMultiForm(modinfo);
      }

    }

    public void save(ModuleInfo modinfo){
      ImageProperties ip = null;
      if(modinfo.getSessionAttribute("image_props")!=null){
        ip = (ImageProperties) modinfo.getSessionAttribute("image_props");
        modinfo.removeSessionAttribute("image_props");
      }
      if(ip !=null){
        int i = ImageBusiness.SaveImage(ip);
        modinfo.setSessionAttribute(sessImageParameter,String.valueOf(i));
        try {
          add(new Image(i));
        }
        catch (SQLException ex) {

        }
      }
    }
}