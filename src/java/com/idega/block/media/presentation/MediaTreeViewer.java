package com.idega.block.media.presentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.FinderException;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.core.file.data.ICMimeTypeBMPBean;
import com.idega.idegaweb.IWCacheManager;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.user.data.Group;
import com.idega.user.data.User;

/**
 * Title: com.idega.block.media.presentation.MediaTreeViewer
 * Description: The tree viewer for the ic_file table. it can be customized to show only certain file types or mime types
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaTreeViewer extends Block {

  private String fileInSessionParameter = "";
  private IWCacheManager cm;

  @Override
public void  main(IWContext iwc) throws Exception{
    this.cm = iwc.getIWMainApplication().getIWCacheManager();
    this.fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);

    Table T = new Table(1,2);
    T.setWidth("100%");
    T.setCellpadding(2);
    T.setCellspacing(0);

    Link proto = new Link(MediaViewerWindow.class);
    proto.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    ICFile publicRootNodeOld = (ICFile)this.cm.getCachedEntity(com.idega.core.file.data.ICFileBMPBean.IC_ROOT_FOLDER_CACHE_KEY);

    ICFileTree tree = new ICFileTree();
    tree.getLocation().setApplicationClass(MediaTreeViewer.class);
    tree.getLocation().setTarget("legacy_mediaviewer");
    tree.setToShowRootNodeTreeIcons(true);


//    Iterator it = publicRootNodeOld.getChildren();
//    if(it!=null) tree.setFirstLevelNodes(it);

	List<ICFileTreeNode> firstLevelNodes = new ArrayList<ICFileTreeNode>();
	if(publicRootNodeOld != null){
		ICFileTreeNode node = new ICFileTreeNode(publicRootNodeOld);
		node.setToCheckForLocalizationKey(true);
		node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		firstLevelNodes.add(node);
	}

	// add user and group folders to publicRootNodeOld

	User user = iwc.getCurrentUser();
	if(user != null){
		ICFileTreeNode node = new ICFileTreeNode(MediaBusiness.getGroupHomeFolder(user,iwc));
		node.setToCheckForLocalizationKey(true);
		node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
		firstLevelNodes.add(node);


		List<Group> userGroups = user.getParentGroups();
		Collection<ICFile> groupFolders = MediaBusiness.getGroupHomeFolders(userGroups,iwc);
		for (Iterator<ICFile> iter = groupFolders.iterator(); iter.hasNext();) {
			ICFile folder = iter.next();
			node = new ICFileTreeNode(folder);
			node.setToCheckForLocalizationKey(false);
			node.addVisibleMimeType(ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
			firstLevelNodes.add(node);
		}
	}

	Iterator<ICFileTreeNode> it = firstLevelNodes.iterator();
	if(it!=null) {
		tree.setFirstLevelNodes(it);
	}

    tree.setNodeActionParameter(this.fileInSessionParameter);
    tree.setFileLinkPrototype(proto);

    tree.setFolderLinkPrototype(proto);

    T.add(tree,1,2);

    /**@todo: localize
    *
    */

//    Link upload = MediaBusiness.getNewFileLink();
//    upload.setText(iwrb.getLocalizedString("mv.upload","upload"));
//    upload.setAsImageButton(true);
//    add(upload);


//    Link folder = MediaBusiness.getNewFolderLink();
//    folder.setText(iwrb.getLocalizedString("mv.folder","folder"));
//    folder.setAsImageButton(true);
//    add(folder);

    add(T);

  }


  public Link getMediaLink(ICFile file,String target){
    Link L = new Link(file.getName(),MediaViewer.class);
    L.setFontSize(1);
    //L.setOnClick("top.iImageId = "+file.getID() );
    L.addParameter(this.fileInSessionParameter,file.getPrimaryKey().toString());

    L.setTarget(target);
    return L;
  }

  public List<ICFile> listOfMedia(){
    try {
      ICFileHome fileHome = (com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHomeLegacy(ICFile.class);
      Collection<ICFile> files = fileHome.findAllDescendingOrdered();
      if (files != null) {
    	  return new ArrayList<ICFile>(files);
      }
    } catch (FinderException e) {
		e.printStackTrace();
	}
    return Collections.emptyList();
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

  @Override
public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }

  @Override
protected String getCacheState(IWContext iwc, String cacheStatePrefix) {
    /**@todo: Override this com.idega.presentation.Block method*/
    return super.getCacheState( iwc,  cacheStatePrefix);
  }



}
