package com.idega.block.media.presentation;

import com.idega.block.media.data.*;
import com.idega.jmodule.object.interfaceobject.Window;
import com.idega.block.media.business.SimpleImage;
import com.idega.jmodule.object.interfaceobject.*;
import com.idega.jmodule.object.textObject.*;
import com.idega.jmodule.object.ModuleInfo;
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
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
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
      String whichButton = modinfo.getParameter("submit");
      String image_id = null;
      String sessImageParameter = "image_id";

      add(sessImageParameter);

      try{
      Conn = com.idega.util.database.ConnectionBroker.getConnection();
      if (Conn != null) {
        if (whichButton!=null && whichButton!=""){
            // opening a new upload window
            if( whichButton.equals("new")){
              main(modinfo,Conn,image_id);
            }
            // done uploading
            else if (whichButton.equals("Vista")){
              setParentToReload();
              vista(modinfo,Conn);
              close();
            }
            // updating
            else{
              image_id = (String) modinfo.getSessionAttribute(sessImageParameter);
              add(image_id);
              main(modinfo,Conn,image_id);
            }
          }
          else {
            Upload(modinfo,Conn);//no if here because of the multipart-request
          }
        }
      }
      catch (Exception E){
        E.printStackTrace();
      }
      finally{
        com.idega.util.database.ConnectionBroker.freeConnection(Conn);
      }
    }

    void main(ModuleInfo modinfo,Connection Conn, String image_id)throws IOException,SQLException{
     if( image_id!=null){//var til fyrir
        drawUploadTable(modinfo,image_id,true);
      }
      else{
        drawUploadTable(modinfo,getImageID(modinfo,Conn),false);
      }
    }

    public String getImageID(ModuleInfo modinfo,Connection Conn)throws SQLException{

      dataBaseType = com.idega.data.DatastoreInterface.getDataStoreType(Conn);
      Statement Stmt = Conn.createStatement();
      ResultSet RS;
      String image_id;

        if( !(dataBaseType.equals("oracle")) ) {
              RS = Stmt.executeQuery("select gen_id(image_gen,1) from rdb$database");
        }
        else{		//oracle
              RS = Stmt.executeQuery("select image_seq.nextval from dual");
        }
        RS.next();
        image_id = RS.getString(1);

        Stmt.close();
        RS.close();
        modinfo.setSessionAttribute(sessImageParameter,image_id);

    return image_id;
    }

    public void drawUploadTable(ModuleInfo modinfo,String image_id,boolean replace){
      Form MultipartForm = new Form();
      MultipartForm.setMultiPart();
      if (replace) {
        MultipartForm.add(new HiddenInput("statement","update image set image_value=?,content_type=?,image_name=? where image_id="+image_id+""));
      }
      else {
        idegaTimestamp dags = new idegaTimestamp();
        if( !dataBaseType.equals("oracle") )
          MultipartForm.add(new HiddenInput("statement","insert into image (image_id,image_value,content_type,image_name,date_added,from_file) values("+image_id+",?,?,?,'"+dags.getTimestampRightNow().toString()+"','N')"));
        else  MultipartForm.add(new HiddenInput("statement","insert into image (image_id,image_value,content_type,image_name,date_added,from_file) values("+image_id+",?,?,?,"+dags.RightNow().toOracleString()+",'N')"));
      }
      MultipartForm.add(new HiddenInput("toDatabase","true"));
      MultipartForm.add(new FileInput());
      MultipartForm.add(new SubmitButton());
      Table UploadTable = new Table(1,3);
      UploadTable.add(new Text("Veldu mynd af harðadisknum þínum með \"Browse\" hnappnum"),1,1);
      UploadTable.add(new Text("og smelltu svo á \"Submit\". ATH ef myndin er stór getur þetta tekið lengri tíma"),1,2);
      UploadTable.add(MultipartForm,1,3);
      add(UploadTable);
    }

    public  void Upload(ModuleInfo modinfo,Connection Conn)throws IOException,SQLException{

      Form newImageForm = new Form();
      newImageForm.setMethod("GET");
      MultipartRequest multi=null;

      try {

        multi = new MultipartRequest(getRequest(),Conn,".", 5 * 1024 * 1024);
        ICFileCategory[] imgCat = (ICFileCategory[]) (new ICFileCategory()).findAll();
        DropdownMenu category = new DropdownMenu("category");
        for (int i = 0 ; i < imgCat.length ; i++ ) {
                category.addMenuElement(imgCat[i].getID(),imgCat[i].getName());
        }

        Table UploadDoneTable = new Table(2,3);
        UploadDoneTable.mergeCells(1,1,2,1);
        UploadDoneTable.mergeCells(1,2,2,2);
        UploadDoneTable.setBorder(0);
        newImageForm.add(UploadDoneTable);
        UploadDoneTable.add(category,2,3);
        UploadDoneTable.add(new Text("Hér er myndin eins og hún kemur út á vefnum. Veldu aftur ef eitthvað fór úrskeiðis"),1,1);
        UploadDoneTable.add(new Image(Integer.parseInt((String)modinfo.getSessionAttribute(sessImageParameter)) ),1,2);

        UploadDoneTable.add(new SubmitButton("submit","Ný mynd"),1,3);
        UploadDoneTable.add(new SubmitButton("submit","Vista"),1,3);
        add(newImageForm);

        }
        catch (Exception e) {
          e.printStackTrace();
        }
    }

    public void vista(ModuleInfo modinfo,Connection Conn)throws SQLException {
      int img_id = Integer.parseInt((String)modinfo.getSessionAttribute(sessImageParameter));
      int cat_id = Integer.parseInt(modinfo.getParameter("category"));
      Statement Stmt = Conn.createStatement();
      Stmt.executeUpdate("INSERT INTO image_image_catagory values ("+img_id+","+cat_id+")");
      Stmt.close();
    }
  }