package com.idega.block.media.presentation;

import java.text.DateFormat;
import java.util.Iterator;
import java.util.Map;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.ICFileType;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.HorizontalRule;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.presentation.ui.TextArea;
import com.idega.presentation.ui.TextInput;
import com.idega.presentation.ui.Window;

/**
 * Title: com.idega.block.media.presentation.MediaFolderEditorWindow
 * Description:  This class handles creating and editing of folders.
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaFolderEditorWindow extends Window {
  private IWResourceBundle iwrb;
  private String fileInSessionParameter = "";

  public MediaFolderEditorWindow() {
  }


  public void main(IWContext iwc) throws Exception {
    super.main(iwc);
    iwrb = getResourceBundle(iwc);
    handleEvents(iwc);
  }

  private void handleEvents(IWContext iwc) throws Exception {
    setBackgroundColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
    setAllMargins(0);

    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession( iwc );

    int mediaId = MediaBusiness.getMediaId(iwc);

    if(action != null) {
      if( action.equals(MediaConstants.MEDIA_ACTION_NEW) ) {
      		add(new MediaToolbar(mediaId));
        Form form = new Form();
        Table table = new Table(1,3);
        table.setWidth(300);
        table.setHeight(120);
        table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
        table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
        table.setVerticalAlignment(1,3,Table.VERTICAL_ALIGN_TOP);

        TextInput folderName = new TextInput(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
        //Link save = new Link("Save");
        //save.setAsImageButton(true);
        //save.setToFormSubmit(form);
        Text add = new Text(iwrb.getLocalizedString("mediafoldereditwindow.name.the.folder","Name the folder"));
        add.setStyle(Text.FONT_FACE_ARIAL);
        add.setFontSize(Text.FONT_SIZE_10_HTML_2);
        add.setBold();

        table.add(add,1,1);
        table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_FOLDER_SAVE),1,2);
        table.add(folderName,1,3);
        table.add(new SubmitButton(iwrb.getLocalizedString("mv.save","save")),1,3);


        form.add(table);

        if( mediaId != -1){
          form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),String.valueOf(mediaId)));
        }
        else{
          ICFile rootNode = (ICFile)iwc.getIWMainApplication().getIWCacheManager().getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);
          form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),rootNode.getPrimaryKey().toString()));
        }

        add(form);
      }
      else if( action.equals(MediaConstants.MEDIA_ACTION_EDIT) ){
       /**TODO add edit code**/
       	// done  by Aron (aron@idega.is)
  	 	addFileProperties(iwc,mediaId);
      }
	    	else if(action.equals(MediaConstants.MEDIA_ACTION_MOVE)) {
	    		addFileMove(iwc, mediaId);
	    	}
      else if( action.equals(MediaConstants.MEDIA_ACTION_FOLDER_SAVE) ){
        String folderName = iwc.getParameter(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
        if( (folderName!=null) && !(folderName.equalsIgnoreCase("")) ){


          ICFile folder = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
          folder.setName(folderName);
          folder.setMimeType(com.idega.core.file.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);

          folder = MediaBusiness.saveMediaToDB(folder,mediaId,iwc);
          setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
          add(new MediaToolbar(((Integer)folder.getPrimaryKey()).intValue()));
          add(new MediaViewer(((Integer)folder.getPrimaryKey()).intValue()));

//          Text created = new Text(iwrb.getLocalizedString("mediafoldereditwindow.folder.saved","Folder created"));
//          created.setStyle(Text.FONT_FACE_ARIAL);
//          created.setFontSize(Text.FONT_SIZE_10_HTML_2);


        }
      }
		  	else if( action.equals(MediaConstants.MEDIA_ACTION_RENAME) ){
		  		String newFileName = iwc.getParameter(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
		  		String newDescription = iwc.getParameter("me_fol_desc");
		  		
		  		if(mediaId>0){
		  			ICFile file = ( (ICFileHome) IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(mediaId));
		  			// Keeping same file ending :
		  			String oldFileName = file.getName();
		  			boolean store = false;
		  			// Check for new name or description
		  			if( (newFileName!=null)  &&!(newFileName.equalsIgnoreCase(""))  ){
		  				//	keeping same file ending 
		  				if(oldFileName!=null&& !oldFileName.equals(newFileName)){
		  					int lastPeriod = oldFileName.lastIndexOf(".");
		  					if(lastPeriod>0){
		  						String postfix = oldFileName.substring(lastPeriod);
		  						if(newFileName.lastIndexOf(".")==-1){
		  							newFileName +=postfix;
		  						}
		  					}
		  				}
		  				file.setName(newFileName);
		  				store = true;
		  			}			
		  					
		  			// Check for new description
		  			if(newDescription!=null && !newDescription.equals(file.getDescription()) ){
		  				file.setDescription(newDescription);
		  				store = true;
		  			}
		  					
		  			// Check for new metadata
		  			if(iwc.isParameterSet("me_fol_mkey") && iwc.isParameterSet("me_fol_mval")){
		  				String key = iwc.getParameter("me_fol_mkey");
		  				String val = iwc.getParameter("me_fol_mval");
		  				file.setMetaData(key,val);
		  				store = true;
		  				//System.out.println("we shall add metadata !");
		  			}
		  			if(iwc.isParameterSet("me_fol_mdel")){
		  				String[] deleteMeta = iwc.getParameterValues("me_fol_mdel");
		  				if(deleteMeta!=null){
		  					for (int i = 0; i < deleteMeta.length; i++) {
		  						file.removeMetaData(deleteMeta[i]);
		  					}
		  					file.updateMetaData();				
		  				}
		  			}
		  			if(store){
		  				file.store();
		  			}
		  				
		  			setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
		  			addFileProperties(iwc,mediaId);
		  				//add(new MediaToolbar(file.getID()));
		  				//add(new MediaViewer(file.getID()));
		  			
		  		}
		  	}
		  	else if(action.equals(MediaConstants.MEDIA_ACTION_SAVE_MOVE)) {
		  		String newFolder = iwc.getParameter(MediaConstants.MEDIA_CHOOSER_FOLDER_CHOOSER_NAME);
		  		if(newFolder != null && !newFolder.equals("")) {
		    		MediaBusiness.moveMedia(mediaId,Integer.parseInt(newFolder));
		  		}
				setOnLoad("parent.frames['"+MediaConstants.TARGET_MEDIA_TREE+"'].location.reload()");
		  	}
    }
  }
  
  private void addFileProperties(IWContext iwc,int mediaId)throws Exception{
		add(new MediaToolbar(mediaId));
		Form form = new Form();
		Table table = new Table();
		//table.setWidth(300);
		//table.setHeight(120);
		//table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
		//table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
		//table.setVerticalAlignment(1,3,Table.VERTICAL_ALIGN_TOP);
	

		TextInput inputName = new TextInput(MediaConstants.MEDIA_FOLDER_NAME_PARAMETER_NAME);
		TextArea inputDescription = new TextArea("me_fol_desc");
		if(mediaId>0){
			ICFile file = ( (ICFileHome) IDOLookup.getHome(ICFile.class)).findByPrimaryKey(new Integer(mediaId));
			//ICFile file = (ICFile)MediaBusiness.getCachedFileInfo(mediaId,iwc.getApplication()).getEntity();
			if(file.getName()!=null){
				inputName.setContent(file.getName());
			}
			if(file.getDescription()!=null){
				inputDescription.setContent(file.getDescription());
			}
		int row = 1;
		Text props = new Text(iwrb.getLocalizedString("mediafoldereditwindow.properties","Properties"));
		props.setStyle(Text.FONT_FACE_ARIAL);
		props.setFontSize(Text.FONT_SIZE_10_HTML_2);
		props.setBold();
		table.add(Text.getBreak(),1,row);
		table.mergeCells(1,row,2,row);
		table.add(props,1,row++);
		table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_RENAME),1,2);
		table.add(Text.getBreak(),1,row++);
		Text name = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.filename","Name"));
		table.add(name,1,row);
		table.add(inputName,2,row++);
		
		Text description = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.description","Description"));
		table.add(description,1,row);
		table.add(inputDescription,2,row++);
		
		SubmitButton save = new SubmitButton(iwrb.getLocalizedString("mv.save","save"));
		table.add(save,2,row++);
	
		String mimeType = (file.getMimeType() != null ) ? file.getMimeType() : "";
		ICFileType fileType = MediaBusiness.getFileType(iwc,mimeType);
		Text type = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.type","Type")+":");
		table.add(type,1,row);
		table.add(fileType.getDisplayName(),2,row);
		row++;
		Text size = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.size","Size")+":");;
		table.add(size,1,row);
		table.add((file.getFileSize() != null ) ? file.getFileSize().toString() : "",2,row);
		row++;
		Text location = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.location","Location")+":");
		table.add(location,1,row);
		table.add(getFileLocation(file,"/"),2,row);
		row++;
		Text mimetype = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.mimetype","Mimetype")+":");
		table.add(mimetype,1,row);
		table.add(mimeType,2,row);
		row++;
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.LONG,DateFormat.LONG,iwc.getCurrentLocale());
		Text created = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.created","Created")+":");
		table.add(created,1,row);
		table.add(file.getCreationDate()!=null?df.format(file.getCreationDate()):"",2,row);
		row++;
		Text modified = getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.modified","Modified")+":");
		table.add(modified,1,row);
		table.add(file.getModificationDate()!=null?df.format(file.getModificationDate()):"",2,row);
		row++;
		
		table.add(Text.getBreak(),1,row++);
		
		table.mergeCells(1,row,2,row);
		table.add(new HorizontalRule(),1,row++);
		table.setAlignment(1,row,table.HORIZONTAL_ALIGN_CENTER);
		table.setColumnVerticalAlignment(1,table.VERTICAL_ALIGN_TOP);
		table.setColumnAlignment(1,table.HORIZONTAL_ALIGN_RIGHT);
		table.setAlignment(1,1,table.HORIZONTAL_ALIGN_CENTER);
		
		
		Table metaTable = new Table();
		int mrow = 1;
		metaTable.add(Text.getBreak(),1,mrow);
		metaTable.add(getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.metadata","Metadata")),1,mrow);
		metaTable.mergeCells(1,mrow,3,mrow);
		mrow++;
		metaTable.add(Text.getBreak(),1,mrow++);
		metaTable.add(getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.metadata.key","Key")),1,mrow);
		metaTable.add(getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.metadata.value","Value")),2,mrow);
		metaTable.add(getHeaderText(iwrb.getLocalizedString("mediafoldereditwindow.properties.metadata.remove","Remove")),3,mrow);
		mrow++;
		file.getMetaData("test");	// to fetch the metadata !! //TODO do it in a better way	
		if(file.getMetaDataAttributes()!=null){			
			Iterator iter = file.getMetaDataAttributes().entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry) iter.next();
				Text key = getHeaderText((String)entry.getKey());
				metaTable.add(key,1,mrow);
				metaTable.add((String)entry.getValue(),2,mrow);
				metaTable.add(new CheckBox("me_fol_mdel",(String)entry.getKey()),3,mrow++);
			}
			table.add(metaTable,1,row);
			table.mergeCells(1,row,2,row);
		}
		
		TextInput inputKey = new TextInput("me_fol_mkey");
		TextInput inputValue = new TextInput("me_fol_mval");
		metaTable.add(inputKey,1,mrow);
		metaTable.add(inputValue,2,mrow);
		metaTable.add(save,3,mrow);
		metaTable.setAlignment(1,1,metaTable.HORIZONTAL_ALIGN_CENTER);
	
			 form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),String.valueOf(mediaId)));
		
		}
		
		form.add(table);
		add(form);

  }
  private void addFileMove(IWContext iwc,int mediaId)throws Exception {
  		add(new MediaToolbar(mediaId));
  		Form form = new Form();
  		form.add(new HiddenInput(MediaBusiness.getMediaParameterNameInSession(iwc),String.valueOf(mediaId)));
  		Table table = new Table();
  		table.setCellpadding(0);
  		table.setCellspacing(0);
  		table.setWidth(Table.HUNDRED_PERCENT);
  		table.add(Text.BREAK,1,1);
  		table.add(new HiddenInput(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE_MOVE),1,2);
  		
  		Text move = new Text(iwrb.getLocalizedString("mediafoldereditwindow.choose_folder_to_move_to","Choose a folder to move current file/folder to"));
  		move.setStyle(Text.FONT_FACE_ARIAL);
  		move.setFontSize(Text.FONT_SIZE_10_HTML_2);
  		move.setBold();
  		table.setAlignment(1,1,Table.HORIZONTAL_ALIGN_CENTER);
  		table.add(move,1,1);
  		
  		FolderChooser folderChooser = new FolderChooser(MediaConstants.MEDIA_CHOOSER_FOLDER_CHOOSER_NAME);
  		table.setAlignment(1,2,Table.HORIZONTAL_ALIGN_CENTER);
  		table.add(Text.BREAK,1,2);
  		table.add(folderChooser,1,2);
  		
  		SubmitButton submit = new SubmitButton(iwrb.getLocalizedString("mv.save","save"));
  		table.setAlignment(1,3,Table.HORIZONTAL_ALIGN_CENTER);
  		table.add(Text.BREAK,1,3); 
  		table.add(submit,1,3);
  		
  		form.add(table);
  		add(form);
  }
  
	private Text getHeaderText(String string){
		Text  text= new Text(string);
		text.setStyle(Text.FONT_FACE_ARIAL);
		text.setFontSize(Text.FONT_SIZE_10_HTML_2);
		text.setBold();
		return text;
  }
  
  	private String getFileLocation(ICTreeNode node,String delimiter){
		ICTreeNode parent = node.getParentNode();
		if(parent!=null  ){
			// we dont print the name of the leaf , only parents
			if(!node.isLeaf())
				return  getFileLocation(parent,delimiter)+delimiter+node.getNodeName();
			else 
				return getFileLocation(parent,delimiter)+delimiter;
		}
		// dont print the root ( ICROOT ) 
		return "";  	
  	}
  

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }


}
