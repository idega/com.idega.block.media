package com.idega.block.media.presentation;

import java.sql.*;
import java.util.*;
import com.idega.util.*;
import com.idega.jmodule.object.textObject.*;
import	com.idega.jmodule.object.*;
import	com.idega.jmodule.object.interfaceobject.*;
import	com.idega.block.media.data.*;
import	com.idega.data.*;
import com.idega.util.text.*;



public class ImageTree extends JModuleObject{

private String width = "100%";
private boolean showAll = false;
private boolean refresh = false;

public Table getTreeTable(ModuleInfo modinfo) throws SQLException {

    ImageCatagory[] catagory = (ImageCatagory[]) (new ImageCatagory()).findAll("Select * from image_catagory where parent_id = -1");
    ImageEntity[] images;
    Vector items = null;

    items = (Vector) modinfo.getServletContext().getAttribute("image_tree_vector");

    Integer[] intArr = new Integer[3];
    int pos;

    Table returnTable = new Table();

    if (items == null) {
      items = new Vector();
      if ( catagory != null) {
        if (catagory.length > 0) {
          for (int i = 0 ; i < catagory.length ; i++ ) {
            findNodes(items,catagory[i].getID(),1,GenericEntity.getStaticInstance("com.idega.block.media.data.ImageCatagory"),1);

            if ( showAll ) {
              images = (ImageEntity[])catagory[i].findRelated( GenericEntity.getStaticInstance("com.idega.block.media.data.ImageEntity") );

              if (images != null) {
                if (images.length > 0 ) {
                    intArr = (Integer[])(items.lastElement());
                    pos = intArr[1].intValue()+1;
                  for (int j = 0 ; j < images.length ; j++) {
                    if (images[j].getParentId()== -1 ) {
                      findNodes(items,images[j].getID(),pos,GenericEntity.getStaticInstance("com.idega.block.media.data.ImageEntity"),2);
                    }
                  }
                }
              }
            }

          modinfo.getServletContext().setAttribute("image_tree_vector",items);
          }
        }
      }
    }


    if (items.size() > 0) {
      String openCat = modinfo.getParameter("open_catagory_id");

      if (openCat == null) { openCat = "-3";}
        Table isTable = (Table) modinfo.getServletContext().getAttribute("image_tree_table"+openCat);

        if (isTable != null) {
          returnTable = isTable;
        }
        else {
          returnTable = writeTable(items,modinfo);
        }
    }

    return returnTable;
}

public String getWidth(){
  return this.width;
}

public void setWidth(String width){
  this.width =  width;
}

public void setShowAll(boolean showAll){
  this.showAll =  showAll;
}

public Table writeTable(Vector items,ModuleInfo modinfo) throws SQLException {
  Table table = new Table();
    table.setBorder(0);
    table.setWidth(getWidth());
    table.setCellpadding(2);
    table.setCellspacing(0);
    table.setAlignment("left");

  Text more = new Text("+");
    more.setFontColor("#FFFFFF");
  String imageId = modinfo.getParameter("image_id");
  String openCat = modinfo.getParameter("open_catagory_id");
    if (openCat == null) { openCat = "-3";}
  String openImg = modinfo.getParameter("open_image_id");
    if (openImg == null) { openImg = "-3";}

  Link openLink;
  Link idLink;
  String color0 = "/pics/jmodules/image/myndamodule/menubar/yfirfl1.gif";
  String color1 = "/pics/jmodules/image/myndamodule/menubar/undirfl1.gif";
  String color2 = "/pics/jmodules/image/myndamodule/menubar/undirfl2.gif";
  int depth = 10;

  Text text;

  ImageCatagory catagory;
  ImageEntity image;
  Integer[] intArr = new Integer[3];
  int pos = 1;
  int id;
  int spe;
  int row = 0;
  int preCatId = -1;

  for (int i = 0 ; i < items.size() ; i++) {
      intArr = (Integer[]) items.elementAt(i);
      id = intArr[0].intValue();
      pos= intArr[1].intValue();
      spe= intArr[2].intValue();
      if (spe == 1) {
        ++row;
        catagory = new ImageCatagory(id);
        preCatId = id;

        table.mergeCells(1,row,depth,row);

        text = new Text(catagory.getImageCatagoryName());
          text.setFontColor("#FFFFFF");

        openLink = new Link(more);
        openLink.setFontColor("#FFFFFF");
        openLink.setAttribute("style","text-decoration:none");

        idLink = new Link(text);
        idLink.setFontColor("#FFFFFF");
        idLink.setBold();
        idLink.setAttribute("style","text-decoration:none");

        if (!openCat.equals(Integer.toString(id))) {
          openLink.addParameter("open_catagory_id",""+id);

        }
        else {
          idLink.addParameter("open_catagory_id",""+id);
        }

          idLink.addParameter("image_catagory_id",""+id);
        table.setHeight(row,"25");
        if ( showAll ) {
          table.add(openLink,pos,row);
        }
        table.addText("&nbsp;",pos,row);
        table.add(idLink,pos,row);
        table.setBackgroundImage(pos,row,new Image(color0));
      }

      if (openCat.equals(Integer.toString(preCatId)))
      if (spe == 2) {
        ++row;
        image = new ImageEntity(id);

        StringBuffer extrainfo = new StringBuffer("");
        extrainfo.append("&nbsp;");
        extrainfo.append(image.getName());

        if ( ( image.getWidth()!=null)&& ( image.getHeight()!=null) ){
          extrainfo.append(" (");
          extrainfo.append(image.getWidth());
          extrainfo.append("*");
          extrainfo.append(image.getHeight());
          extrainfo.append(")");
        }

        text = new Text(extrainfo.toString());
        text.setFontSize(1);

        idLink = new Link(text);
        idLink.setFontColor("#FFFFFF");
        idLink.setAttribute("style","text-decoration:none");
        if (preCatId != -1 ) {
          idLink.addParameter("open_catagory_id",""+preCatId);
        }

        table.mergeCells(pos,row,depth,row);
        table.setHeight(row,"21");

        if ( pos == 2 ) {
          table.setBackgroundImage(pos,row,new Image(color1));
          table.setBackgroundImage(1,row,new Image(color1));
          table.addText("",1,row);
        }
        else {
          table.setBackgroundImage(pos,row,new Image(color2));
          for ( int a = 1; a < pos; a++ ) {
            table.setBackgroundImage(a,row,new Image(color2));
            table.addText("",a,row);
          }
        }

          idLink.addParameter("image_id",""+id);

        table.add(idLink, pos,row);
      }


  }

  modinfo.getServletContext().setAttribute("image_tree_table"+openCat,table);

  return table;
//  add(table);
}



    private void findNodes(Vector vector,int id, int position,GenericEntity entity, GenericEntity[] options, int specialValue) throws SQLException{
        Integer[] intArray = new Integer[3];
          intArray[0] = new Integer(id);
          intArray[1] = new Integer(position);
          intArray[2] = new Integer(specialValue);

        vector.addElement(intArray);

       options = (GenericEntity[]) (entity).findAllByColumn("parent_id",""+id);
        int i = 0;

        if (options != null ) {
          if (options.length > 0) {
            ++position;
            for (i = 0 ; i < options.length ; i++) {
              findNodes(vector,options[i].getID(), position,entity,options, specialValue);
            }
          }
        }


    }

    private void findNodes(Vector vector,int id, int position,GenericEntity entity) throws SQLException{
        findNodes(vector,id,position,entity,new GenericEntity[1],0);
    }

    private void findNodes(Vector vector,int id, int position,GenericEntity entity, int specialValue) throws SQLException{
        findNodes(vector,id,position,entity,new GenericEntity[1], specialValue);
    }


private void refresh(ModuleInfo modinfo) throws SQLException{
    Table table;
    Vector vector;
    String test;

    table = (Table) modinfo.getServletContext().getAttribute("image_tree_table-3");
    vector = (Vector) modinfo.getServletContext().getAttribute("image_tree_vector");
    if (table != null) {
      modinfo.getServletContext().removeAttribute("image_tree_table-3");
    }
    if (vector != null) {
      modinfo.getServletContext().removeAttribute("image_tree_vector");
    }

    test = (String) modinfo.getSessionAttribute("image_tree_catagory_id");
    if (test != null) {
      modinfo.removeSessionAttribute("image_tree_catagory_id");
    }
    test = (String) modinfo.getSessionAttribute("image_tree_image_id");
    if (test != null) {
      modinfo.removeSessionAttribute("image_tree_image_id");
    }


        ImageCatagory[] catagories = (ImageCatagory[])(new ImageCatagory()).findAll();

        if (catagories != null) {
            if (catagories.length > 0 ) {
                for (int i = 0 ; i < catagories.length ; i++ ) {
                    table = (Table) modinfo.getServletContext().getAttribute("image_tree_table"+catagories[i].getID());
                    if (table != null) {
                        modinfo.getServletContext().removeAttribute("image_tree_table"+catagories[i].getID());
                    }
                }
            }
        }
    }


public void refresh(){
  this.refresh=true;
}

public void main(ModuleInfo modinfo)throws Exception{
  //this.isAdmin=this.isAdministrator(modinfo);
  //setSpokenLanguage(modinfo);

  if(refresh) refresh(modinfo);

  String tempImageId = modinfo.getParameter("image_id");
  String tempCatagoryId = modinfo.getParameter("catagory_id");
  String imageId = null;
  String catagoryId = null;

  if (tempImageId != null) {
     modinfo.setSessionAttribute("image_tree_image_id",tempImageId);
     modinfo.removeSessionAttribute("image_tree_catagory_id");
  }
  if (tempCatagoryId != null) {
     modinfo.setSessionAttribute("image_tree_catagory_id",tempCatagoryId);
     modinfo.removeSessionAttribute("image_tree_image_id");
  }
     imageId = (String) modinfo.getSessionAttribute("image_tree_image_id");
     catagoryId = (String) modinfo.getSessionAttribute("image_tree_catagory_id");

  add(getTreeTable(modinfo));

  }

}
