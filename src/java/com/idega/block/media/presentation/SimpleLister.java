package com.idega.block.media.presentation;

import com.idega.jmodule.object.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.block.media.data.ImageEntity;
import com.idega.util.idegaTimestamp;
import com.idega.data.EntityFinder;
import java.sql.SQLException;
import java.util.List;

import com.idega.jmodule.object.ModuleObjectContainer;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2000-2001 idega.is All Rights Reserved
 * Company:      idega
  *@author <a href="mailto:aron@idega.is">Aron Birkir</a>
 * @version 1.1
 */

public class SimpleLister extends ModuleObjectContainer {

    private String target = "viewer";
    public String viewUrl = "/image/singleview.jsp";
    public String prmImageView = "img_view_id";
    public String sessImageParameterName = "im_image_session_name";
    public String sessImageParameter = "image_id";

    public void  main(ModuleInfo modinfo){
      getParentPage().setAllMargins(0);
      List L = listOfImages();

      checkParameterName(modinfo);

      if(L!= null){
        Table Frame = new Table();
          Frame.setWidth("100%");
        Frame.setCellpadding(0);
        Frame.setCellspacing(0);
        Table T = new Table();
          T.setWidth("100%");
        int len = L.size();
        int row = 1;
        T.add(formatText("Pictures"),1,row++);
        for (int i = 0; i < len; i++) {
          ImageEntity image = (ImageEntity) L.get(i);
          T.add(getImageLink(image,target,prmImageView),1,row);
          T.add(formatText(new idegaTimestamp(image.getDateAdded()).getISLDate()),2,row);
          row++;
        }
        T.setCellpadding(2);
        T.setCellspacing(0);

        T.setHorizontalZebraColored("#CBCFD3","#ECEEF0");
        Frame.add(T,1,1);
        add(Frame);
      }
    }

  public void checkParameterName(ModuleInfo modinfo){
     if(modinfo.getParameter(sessImageParameterName)!=null){
      sessImageParameter = modinfo.getParameter(sessImageParameterName);
      modinfo.setSessionAttribute(sessImageParameterName,sessImageParameter);
    }
    else if(modinfo.getSessionAttribute(sessImageParameterName)!=null)
      sessImageParameter = (String) modinfo.getSessionAttribute(sessImageParameterName);
  }

  public Link getImageLink(ImageEntity image,String target,String prm){
    Link L = new Link(image.getName(),SimpleViewer.class);
    L.setFontSize(1);
    L.addParameter(sessImageParameter,image.getID());
    L.setTarget(target);
    return L;
  }

  public List listOfImages(){
    List L = null;
    try {
      L = EntityFinder.findAllDescendingOrdered(new ImageEntity(),"image_id");
    }
    catch (SQLException ex) {
      L = null;
    }
    return L;
  }

  public Text formatText(String s){
    Text T= new Text();
    if(s!=null){
      T= new Text(s);

      T.setFontColor("#000000");
      T.setFontSize(1);
    }
    return T;
  }
  public Text formatText(int i){
    return formatText(String.valueOf(i));
  }
}