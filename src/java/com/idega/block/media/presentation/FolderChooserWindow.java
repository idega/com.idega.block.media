/*
 * Created on Nov 3, 2004
 *
 */
package com.idega.block.media.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.ejb.CreateException;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICMimeTypeBMPBean;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWLocation;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.StyledAbstractChooserWindow;
import com.idega.user.data.User;


/**
 * The window that pops up when using the <code>FolderChooser</code>.
 * Displays a <code>ICFileTree</code>
 * @author birna
 *
 */
public class FolderChooserWindow extends StyledAbstractChooserWindow{
	
	private String fileInSessionParameter = "";
  private IWCacheManager cm;
  private IWResourceBundle iwrb;
  private String mainStyleClass = "main";
  
  protected static final String SUBMIT_PARENT_FORM_AFTER_CHANGE = "submit_p_form";

	public FolderChooserWindow() {
		setWidth(280);
		setHeight(400);
		setScrollbar(true);
		this.getLocation().setApplicationClass(this.getClass());
		this.getLocation().isInPopUpWindow(true);
	}


	public void displaySelection(IWContext iwc) {
    iwrb = getResourceBundle(iwc);
    cm = iwc.getIWMainApplication().getIWCacheManager();
    fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);
    
    addTitle(iwrb.getLocalizedString("select_folder", "Select folder"), TITLE_STYLECLASS);
		setTitle(iwrb.getLocalizedString("select_group","Select group"));
		setName(iwrb.getLocalizedString("select_group","Select group"));

    Table T = new Table();
    T.setWidth(Table.HUNDRED_PERCENT);
    T.setCellpadding(2);
    T.setCellspacing(0);
    T.setStyleClass(mainStyleClass);

    ICFile publicRootNodeOld = (ICFile)cm.getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);

    ICFileTree tree = new ICFileTree();
    tree.setLocation((IWLocation) this.getLocation().clone()); 
    tree.getLocation().setSubID(1);
    tree.setToShowRootNodeTreeIcons(true);
    
    T.add(tree,1,2);
    add(T,iwc);
    
	  tree.setNodeActionParameter(fileInSessionParameter);
	  tree.setToMaintainParameter(SCRIPT_PREFIX_PARAMETER, iwc);
	  tree.setToMaintainParameter(SCRIPT_SUFFIX_PARAMETER,iwc);
		tree.setToMaintainParameter(DISPLAYSTRING_PARAMETER_NAME,iwc);
		tree.setToMaintainParameter(VALUE_PARAMETER_NAME,iwc);
		tree.setDefaultOpenLevel(1);	  
	  
		Link proto = new Link();
		proto.setURL("#");
		proto.setNoTextObject(true);
		tree.setToUseOnClick();
	  tree.setFileLinkPrototype(proto);
	  tree.setFolderLinkPrototype(proto);
	  
	  tree.setOnClick(SELECT_FUNCTION_NAME+"("+tree.ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME+","+tree.ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME+");");
	
		List firstLevelNodes = new ArrayList();
		if(publicRootNodeOld != null){
			ICFileTreeNode node = new ICFileTreeNode(publicRootNodeOld);
			node.setToCheckForLocalizationKey(true);
			node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
			firstLevelNodes.add(node);
		}
	
	// add user and group folders to publicRootNodeOld
	
		User user = iwc.getCurrentUser();
		if(user != null){
			ICFileTreeNode node = null;
			try {
				node = new ICFileTreeNode(MediaBusiness.getGroupHomeFolder(user,iwc));
			}
			catch (RemoteException e) {
				e.printStackTrace();
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
			node.setToCheckForLocalizationKey(true);
			node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
			firstLevelNodes.add(node);
			
			
			List userGroups = user.getParentGroups();
			Collection groupFolders = null;
			try {
				groupFolders = MediaBusiness.getGroupHomeFolders(userGroups,iwc);
			}
			catch (RemoteException e1) {
				e1.printStackTrace();
			}
			catch (CreateException e1) {
				e1.printStackTrace();
			}
			for (Iterator iter = groupFolders.iterator(); iter.hasNext();) {
				ICFile folder = (ICFile)iter.next();
				node = new ICFileTreeNode(folder);
				node.setToCheckForLocalizationKey(false);
				node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
				firstLevelNodes.add(node);
			}
		}
	
		Iterator it = firstLevelNodes.iterator();
		if(it!=null) tree.setFirstLevelNodes(it);
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}
