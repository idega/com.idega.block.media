package com.idega.block.media.presentation;

import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.core.data.ICFile;


/**
 * Title: com.idega.block.media.presentation.FileChooserWindow
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class FileChooserWindow extends AbstractChooserWindow {
  /** these are used for creating a chooser function that has a unique name for this chooser**/
  public static final String ONCLICK_FUNCTION_NAME = "fileselect";
  public static final String FILE_ID_PARAMETER_NAME = "media_file_id";
  public static final String FILE_NAME_PARAMETER_NAME = "media_file_name";


  public FileChooserWindow(){
    this.setName("File Chooser");
    this.setWidth(300);
    this.setHeight(500);
    add("Select a File");
  }

  public void displaySelection(IWContext iwc){

    try{
      getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME,"function "+ONCLICK_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+"){ }");
      getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME,SELECT_FUNCTION_NAME+"("+FILE_NAME_PARAMETER_NAME+","+FILE_ID_PARAMETER_NAME+")");


      ICFile file = new ICFile(1);
      Link l = new Link(file.getName());
      l.setURL("#");
      l.setOnClick(ONCLICK_FUNCTION_NAME+"('"+file.getName()+"','"+file.getID()+"')");
      add(l);

/*
      link.maintainParameter(parameterName,iwc);
      add(viewer);
      viewer.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
      viewer.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
      viewer.setToMaintainParameter(VALUE_PARAMETER_NAME,iwc);

      set this on the link "use"
            proto.setOnClick(ONCLICK_FUNCTION_NAME+"('"+file.getName()+"','"+file.getID()+"')");

 getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME,"function "+ONCLICK_FUNCTION_NAME+"("+NodeNameParameterName+","+NodeIDParameterName+"){ }");


      Link prototype = new Link();
      viewer.setToUseOnClick();
      //sets the hidden input and textinput of the choosing File


      /*
      function treenodeselect(iw_node_name,iw_node_id){
        chooserSelect(iw_node_name,iw_node_id)
      }

      function chooserSelect(displaystring,value){window.opener.document.id6547410.ib_page_chooser_displaystring.value=displaystring;window.opener.document.id6547410.ib_page_chooser.value=value;window.close();return false;}



      */


    }


    catch(Exception e){
      e.printStackTrace();
    }

    /*Link link = new Link("tester");
    link.setURL("#");
    link.setOnClick(SELECT_FUNCTION_NAME+"('tester','tester')");
    add(link);*/
  }

}