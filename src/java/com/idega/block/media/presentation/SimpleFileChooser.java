package com.idega.block.media.presentation;

import java.util.Iterator;
import java.util.List;

import com.idega.block.media.business.MediaBusiness;
import com.idega.core.data.ICFile;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
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
 * <p>Description: A simple to use persentation object to upload a file into the database</p>
 * <p>Copyright: Idega SoftwareCopyright (c) 2001</p>
 * <p>Company: Idega Software</p>
 * @author <a href="gummi@idega.is">Guðmundur Ágúst Sæmundsson</a>
 * @version 1.1
 */

public class SimpleFileChooser extends InterfaceObjectContainer {
  private boolean showChangeUploadedFileOption = true;
	//private Stringstyle;
  private String name;
  private Form form;
  private int selectedFileId = -1;
  private boolean deleteOnChange = true;
  
  private BusyBar busy = null;
  private List disabledObjects;

  private final static int ACTION_DELETE = 0;
  private final static int ACTION_NEWFILE = 1;
  private final static int ACTION_OLDFILE = 2;
  private final static int ACTION_MAINTAINFILE = 3;
  private int action = -1;
  
  private boolean showPreviewLink = true;

  private IWBundle coreBundle;
	private IWResourceBundle iwrb;
	/**
	 * @return
	 */
	public boolean isShowChangeUploadedFileOption() {
		return showChangeUploadedFileOption;
	}

	/**
	 * @param showChangeUploadedFileOption
	 */
	public void setShowChangeUploadedFileOption(boolean showChangeUploadedFileOption) {
		this.showChangeUploadedFileOption = showChangeUploadedFileOption;
	}

	public SimpleFileChooser(Form form, String chooserName) {
    this.form = form;
    name = chooserName;
    form.setMultiPart();
    busy = new BusyBar("busy_uploading");
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
		coreBundle = iwc.getApplication().getCoreBundle();
		iwrb = coreBundle.getResourceBundle(iwc);

    if(deleteOnChange && "true".equals(iwc.getParameter("change_file"))&&iwc.getParameter(name) != null){
      System.out.println("deleteFile: "+ iwc.getParameter(name));
      boolean del = false;
      try {
        del = MediaBusiness.deleteMedia(Integer.parseInt(iwc.getParameter(name)));
       	selectedFileId = -1;
      }
      catch (Exception ex) {
        del = false;
      } 
      finally{
        if(!del){
          System.err.println("media: "+iwc.getParameter(name)+" failed to delete");
        }
      }
    }

    UploadFile file = getUploadedFile(iwc);

    if(file == null &&selectedFileId == -1 || "false".equals(iwc.getParameter("change_file")) ){
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
       busy.addDisabledObject(confirm);
       busy.addBusyObject(confirm);
      } else {
       busy.setBusyOnChange();
       busy.addBusyObject(input);
      }
     busy.setBusyBarUrl(coreBundle.getImage("loading.gif").getURL());

      if(disabledObjects != null){
        Iterator iter = disabledObjects.iterator();
        while (iter.hasNext()) {
          InterfaceObject item = (InterfaceObject)iter.next();
         busy.addDisabledObject(item);
        }
      }

      table.add(busy,1,2);

      this.add(table);
    } 
    else if(file != null){//uploaded
			ICFile icFile = MediaBusiness.saveMediaToDBUploadFolder(file,iwc);

			if( showChangeUploadedFileOption){
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
	     
	      table.add(new HiddenInput(name,icFile.getPrimaryKey().toString()),1,2);
	      
				if( showPreviewLink){
			    Link preview = new Link("Preview");
			    preview.setURL(MediaBusiness.getMediaURL(icFile,iwc.getApplication()));
			    preview.setTarget(Link.TARGET_NEW_WINDOW);
			    table.add(preview,1,2);
				}
	      this.add(table);
			}
			else{
				add(new HiddenInput(name,icFile.getPrimaryKey().toString()));
			}
      //this.add(new Image(file.getWebPath(),file.getName()));
    } else if(selectedFileId != -1) {
    	
    	if( showChangeUploadedFileOption){
	      Table table = new Table(1,2);
	      table.setCellpadding(0);
	      table.setCellspacing(0);
	
	      ICFile icFile = ((com.idega.core.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(selectedFileId));
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
	      table.add(new HiddenInput(name,Integer.toString(selectedFileId)),1,2);
	      
	      if( showPreviewLink){
		      Link preview = new Link("Preview");
		      preview.setURL(MediaBusiness.getMediaURL(icFile,iwc.getApplication()));
		      preview.setTarget(Link.TARGET_NEW_WINDOW);
		      table.add(preview,1,2);
	      }
	      
	      this.add(table);
    	}
    	else{
    		add(new HiddenInput(name,Integer.toString(selectedFileId)));
    	}
    }
  }


	public UploadFile getUploadedFile(IWContext iwc) {
		return iwc.getUploadedFile();
	}

	public void setInputStyle(String style){
    this.setStyleAttribute(style);
  }

  public void setSelectedFile(int fileId){
   selectedFileId = fileId;
   deleteOnChange = false;
  }


	public boolean isShowingPreviewLink() {
		return showPreviewLink;
	}


	public void setToShowPreviewLink(boolean showPreviewLink) {
		this.showPreviewLink = showPreviewLink;
	}

}
