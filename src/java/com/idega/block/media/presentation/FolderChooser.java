/*
 * Created on Nov 3, 2004
 *
 */
package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.ui.AbstractChooser;


/**
 * Allows the user to choose a folder that he has permission to view from <code>ICFileTree</code>. 
 * Pops up a FolderChooserWindow 
 * @author birna
 *
 */
public class FolderChooser extends AbstractChooser{
	
	private Image chooserButtonImage = null;
	private boolean submitForm = false;
	
	public FolderChooser() {
		addForm(false);
	}
	
	public FolderChooser(String chooserName) {
		this();
		setChooserParameter(chooserName);
	}
	
	public FolderChooser(String chooserName, String style) {
		this(chooserName);
		setInputStyle(style);
	}
	
	public FolderChooser(String chooserName, String style, Image chooserButtonImage) {
		this(chooserName);
		setInputStyle(style);
		setChooseButtonImage(chooserButtonImage);
	}
	
	public void main(IWContext iwc){
	 	empty();
	 	if(chooserButtonImage == null) {
	 		IWBundle iwb = iwc.getIWMainApplication().getBundle(MediaConstants.IW_BUNDLE_IDENTIFIER);
	 		setChooseButtonImage(iwb.getImage("magnifyingglass.gif", "Choose"));
	 	}
	}
	
	public Class getChooserWindowClass() {
		return FolderChooserWindow.class;
	}
	
	public void setSelectedNode(ICFileTreeNode fileTreeNode) {
		super.setChooserValue(fileTreeNode.getNodeName(), fileTreeNode.getNodeID());
	}
	
	public String getBundleIdentifier() {
		return MediaConstants.IW_BUNDLE_IDENTIFIER;
	}
	
/*	protected void addParametersToForm(Form form) {
		if(submitForm) {
			form.addParameter(FolderChooserWindow.SUBMIT_PARENT_FORM_AFTER_CHANGE,"true");
		}
	}
	
	protected void addParametersToLink(Link link) {
		if(submitForm) {
			link.addParameter(FolderChooserWindow.SUBMIT_PARENT_FORM_AFTER_CHANGE, "true");
		}
	}
	
	public void setToSubmitParentFormOnChange() {
		submitForm = true;
	}*/
}
