package com.idega.block.media.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.block.media.business.MediaBusiness;
import com.idega.core.data.ICFile;
import com.idega.idegaweb.presentation.BusyBar;
import com.idega.io.UploadFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.FileInput;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.InterfaceObject;
import com.idega.presentation.ui.InterfaceObjectContainer;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextInput;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2001</p>
 * <p>Company: idega</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.0
 */

public class SimpleFileChooser extends InterfaceObjectContainer {
  //private String _style;
  private String _name;
  private Form _form;
  private int _selectedFileId = -1;
  private boolean _deleteOnChange = true;
  private BusyBar _busy = null;
  private List disabledObjects;

  private final static int _ACTION_DELETE = 0;
  private final static int _ACTION_NEWFILE = 1;
  private final static int _ACTION_OLDFILE = 2;
  private final static int _ACTION_MAINTAINFILE = 3;
  private int _action = -1;

  public SimpleFileChooser(Form form, String chooserName) {
    _form = form;
    _name = chooserName;
    _form.setMultiPart();
    _busy = new BusyBar("busy_uploading");
  }

  public SimpleFileChooser(Form form, String chooserName,String style) {
    this(form, chooserName);
    setInputStyle(style);
  }


  public void addDisabledObjectWhileLoading(InterfaceObject obj){
    if(disabledObjects==null)
      disabledObjects = new java.util.Vector();
    disabledObjects.add(obj);
  }


  public void main(IWContext iwc) throws Exception{
    //IWBundle iwb = iwc.getApplication().getBundle(BuilderLogic.IW_BUNDLE_IDENTIFIER);

    if(_deleteOnChange && "true".equals(iwc.getParameter("change_file"))&&iwc.getParameter(_name) != null){
      System.out.println("deleteFile: "+ iwc.getParameter(_name));
      boolean del = false;
      try {
        del = MediaBusiness.deleteMedia(Integer.parseInt(iwc.getParameter(_name)));
        _selectedFileId = -1;
      }
      catch (Exception ex) {
        del = false;
      } finally{
        if(!del){
          System.err.println("media: "+iwc.getParameter(_name)+" faild to delete");
        }
      }
    }

    UploadFile file = iwc.getUploadedFile();

    if(file == null && _selectedFileId == -1 || "false".equals(iwc.getParameter("change_file")) ){
      this.empty();

      //Image busy = iwc.getApplication().getCoreBundle().getImage("transparentcell.gif");



      Table table = new Table(1,2);
      table.setCellpadding(0);
      table.setCellspacing(0);
      FileInput input = new FileInput();
      SubmitButton confirm = new SubmitButton("Confirm");
      String style = this.getStyleAttribute();
      if(style != null){
        input.setStyleAttribute(style);
        confirm.setStyleAttribute(style);
      }
      table.add(input,1,1);

      if(!iwc.isIE()){
        table.add(confirm,1,1);
        _busy.addDisabledObject(confirm);
        _busy.addBusyObject(confirm);
      } else {
        _busy.setBusyOnChange();
        _busy.addBusyObject(input);
      }
      _busy.setBusyBarUrl(iwc.getApplication().getCoreBundle().getImage("loading.gif").getURL());

      if(disabledObjects != null){
        Iterator iter = disabledObjects.iterator();
        while (iter.hasNext()) {
          InterfaceObject item = (InterfaceObject)iter.next();
          _busy.addDisabledObject(item);
        }
      }

      table.add(_busy,1,2);
      //input.setOnChange("document.forms['"+_form.getID()+"'].submit();document.images['"+busy.getID()+"'].src='"+iwc.getApplication().getCoreBundle().getImage("loading.gif").getURL()+"';");
      //input.setOnChange("this.form.submit();document.images['"+busy.getID()+"'].src='"+iwc.getApplication().getCoreBundle().getImage("loading.gif").getURL()+"';");

      //input.setOnChange("this.form.submit()");

      //f.setOnSubmit("javascript:document.images['"+busy.getID()+"'].src='"+iwc.getApplication().getCoreBundle().getImage("loading.gif").getURL()+"';return true");

      this.add(table);
    } else if(file != null){


      Table table = new Table(1,2);
      table.setCellpadding(0);
      table.setCellspacing(0);

      TextInput tInput = new TextInput("ic_uploaded_file",file.getName());
      tInput.setDisabled(true);
      SubmitButton change = new SubmitButton("Change...","change_file","true");

      String style = this.getStyleAttribute();
      if(style != null){
        tInput.setStyleAttribute(style);
        change.setStyleAttribute(style);
      }

      table.add(tInput,1,1);
      table.add(change,1,1);
      //table.add(busy,1,2);
      ICFile icFile = MediaBusiness.saveMediaToDBUploadFolder(file,iwc);
      table.add(new HiddenInput(_name,Integer.toString(icFile.getID())),1,2);
      Link preview = new Link("Preview");
      preview.setURL(MediaBusiness.getMediaURL(icFile,iwc.getApplication()));
      preview.setTarget(Link.TARGET_NEW_WINDOW);
      table.add(preview,1,2);
      this.add(table);
      //this.add(new Image(file.getWebPath(),file.getName()));
    } else if(_selectedFileId != -1) {
      Table table = new Table(1,2);
      table.setCellpadding(0);
      table.setCellspacing(0);

      ICFile icFile = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class)).findByPrimaryKeyLegacy(_selectedFileId);
      TextInput tInput = new TextInput("ic_uploaded_file",icFile.getName());
      tInput.setDisabled(true);
      SubmitButton change = new SubmitButton("Change...","change_file","true");

      String style = this.getStyleAttribute();
      if(style != null){
        tInput.setStyleAttribute(style);
        change.setStyleAttribute(style);
      }

      table.add(tInput,1,1);
      table.add(change,1,1);
      //table.add(busy,1,2);
      table.add(new HiddenInput(_name,Integer.toString(_selectedFileId)),1,2);
      Link preview = new Link("Preview");
      preview.setURL(MediaBusiness.getMediaURL(icFile,iwc.getApplication()));
      preview.setTarget(Link.TARGET_NEW_WINDOW);
      table.add(preview,1,2);
      this.add(table);
    }
  }

  public void setInputStyle(String style){
    //_style = style;
    this.setStyleAttribute(style);
  }

  public void setSelectedFile(int fileId){
    _selectedFileId = fileId;
    _deleteOnChange = false;
  }

}
