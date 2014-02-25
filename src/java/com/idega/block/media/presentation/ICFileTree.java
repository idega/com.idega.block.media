package com.idega.block.media.presentation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.idega.block.media.business.MediaBusiness;
import com.idega.core.file.data.ICMimeType;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Script;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.AbstractTreeViewer;
import com.idega.util.CoreConstants;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class ICFileTree extends AbstractTreeViewer<ICFileTreeNode> {

  private static final String _APP_DEFAULT_FILE_ICONS = "ic_filetree_icons";
  private String _APP_FILE_ICONS = _APP_DEFAULT_FILE_ICONS;
  private static final String _NODE_OPEN = "_open";
  private static final String _NODE_CLOSED = "_closed";
  private static final String _DEFAULT_ICON_PREFIX = "icfileicons/ui/";

	public static final String ONCLICK_FUNCTION_NAME = "treenodeselect";
	public static final String ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME = "iw_node_id";
	public static final String ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME = "iw_node_name";


  private static final String _DEFAULT_ICON_SUFFIX = ".gif";
  private Link _fileLink = new Link();
  private Link _folderLink = new Link();

  private String nodeNameTarget = null;
  private String nodeActionPrm = null;

  private boolean _usesOnClick = false;

  public ICFileTree(){
    super();
    this.setColumns(2);
  }

  public static ICFileTree getICFileTreeInstance(ICFileTreeNode[] nodes){
    ICFileTree fileTree = new ICFileTree();
    fileTree.setFirstLevelNodes(nodes);
    return fileTree;
  }

	@Override
	public void setFirstLevelNodes(ICFileTreeNode[] nodes) {
		super.setFirstLevelNodes(nodes);
	}

	@Override
	public void setFirstLevelNodes(Iterator<ICFileTreeNode> nodes) {
		super.setFirstLevelNodes((ICFileTreeNode[]) null);
		if (nodes != null) {
			while (nodes.hasNext()) {
				ICFileTreeNode node = nodes.next();
				this.addFirstLevelNode(node);
			}
		}
	}

	@Override
	public void addFirstLevelNode(ICFileTreeNode node) {
		super.addFirstLevelNode(node);
	}

  public Image getIcon(Map<String, Image> _icFileIcons, ICFileTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode){
    String mimeType = node.getICFile().getMimeType();
    if(mimeType != null){
      mimeType = mimeType.replace('\\','_');
      mimeType = mimeType.replace('/','_');
      mimeType = mimeType.replace(':','_');
      mimeType = mimeType.replace('*','_');
      mimeType = mimeType.replace('?','_');
      mimeType = mimeType.replace('<','_');
      mimeType = mimeType.replace('>','_');
      mimeType = mimeType.replace('|','_');
      mimeType = mimeType.replace('\"','_');
      if(!node.isLeaf()){
        Object obj = _icFileIcons.get(mimeType+((nodeIsOpen)?_NODE_OPEN:_NODE_CLOSED));
        if(obj == null){
          this.updateFileIcon(_icFileIcons,mimeType,iwc,false);
          obj = _icFileIcons.get(mimeType+((nodeIsOpen)?_NODE_OPEN:_NODE_CLOSED));
        }
        return (Image)obj;
      }else {
        Object obj = _icFileIcons.get(mimeType);
        if(obj == null){
          this.updateFileIcon(_icFileIcons,mimeType,iwc,true);
          obj = _icFileIcons.get(mimeType);
        }
        return (Image)obj;
      }
    }else {
      return null;
    }
  }

  @Override
  public PresentationObject getObjectToAddToColumn(int colIndex, ICFileTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode) {
      switch (colIndex) {
        case 1:
          return getIcon(this.getIcons(iwc), node, iwc, nodeIsOpen, nodeHasChild, isRootNode);
        case 2:
          if(!node.isLeaf()){
            Link l = this.getFolderLinkClone(node.getNodeName(iwc.getCurrentLocale(),iwc));

            this.setLinkToOpenOrCloseNode(l,node,nodeIsOpen);
            if( this.nodeNameTarget != null ){
             l.setTarget(this.nodeNameTarget);
            }
            if( this.nodeActionPrm!=null ){
              l.addParameter(this.nodeActionPrm,node.getId());
            }
            if (this._usesOnClick) {
    					String nodeName = node.getNodeName();
    					l.setURL(CoreConstants.HASH);
    					l.setOnClick(ONCLICK_FUNCTION_NAME + "('" + nodeName + "','" +  node.getId() + "')");
    				}

            return l;
          }
          else {
            Link l = this.getFileLinkClone(node.getNodeName(iwc.getCurrentLocale()));
            this.setLinkToMaintainOpenAndClosedNodes(l);

            if( this.nodeNameTarget != null ){
             l.setTarget(this.nodeNameTarget);
            }
            if( this.nodeActionPrm!=null ){
              l.addParameter(this.nodeActionPrm,node.getId());
            }
            if (this._usesOnClick) {
    					String nodeName = node.getNodeName();
    					l.setURL(CoreConstants.HASH);
    					l.setOnClick(ONCLICK_FUNCTION_NAME + "('" + nodeName + "','" + node.getId() + "')");
    				}

            return l;
          }
      }
//    }
    return null;
  }


  private Link getFileLinkClone(){
    return (Link)this._fileLink.clone();
  }
  private Link getFolderLinkClone(){
    return (Link)this._folderLink.clone();
  }

  private Link getFileLinkClone(String text){
    Link l = getFileLinkClone();
    l.setText(text);
    return l;
  }

  private Link getFolderLinkClone(String text){
    Link l = getFolderLinkClone();
    l.setText(text);
    return l;
  }

  public void setNodeActionParameter(String prm){
    this.nodeActionPrm = prm;
  }

  public void setTarget(String target){
    this.nodeNameTarget = target;
  }

  public void setFileLinkPrototype(Link link){
    this._fileLink = link;
  }

  public void setFolderLinkPrototype(Link link){
    this._folderLink = link;
  }


  protected void updateIconDimensions(Map<String, Image> _icFileIcons){
    //super.updateIconDimensions();

    if(_icFileIcons != null && _icFileIcons.values() != null){
      Iterator<Image> iter = _icFileIcons.values().iterator();
      while (iter.hasNext()) {
        Image item = iter.next();
        if(item != null){
          item.setHeight(this.iconHeight);
        }
      }
    }
  }

  public Map<String, Image> getIcons(IWContext iwc){
  //public void initIcons(IWContext iwc){
    //super.initIcons(iwc);

    //Object obj = iwc.getApplicationAttribute(_APP_FILE_ICONS + getUI());
    //if(obj == null){
      IWBundle bundle = this.getBundle(iwc);
      Map<String, Image> tmp = new HashMap<String, Image>();

      @SuppressWarnings("unchecked")
	HashMap<String, ICMimeType> mimeMap = (HashMap<String, ICMimeType>) MediaBusiness.getICMimeTypeMap(iwc);

      if(mimeMap != null){
        Iterator<String> iter = mimeMap.keySet().iterator();
        while (iter.hasNext()) {
          ICMimeType item = (mimeMap.get(iter.next()));
          String mimeType = item.getMimeType();
          tmp.put(mimeType,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_DEFAULT_ICON_SUFFIX));
        }
      }

      iwc.setApplicationAttribute(this._APP_FILE_ICONS + getUI(),tmp);
      //this._icFileIcons = tmp;
    //} else {
    //  this._icFileIcons = (Hashtable)obj;
    //}

    updateIconDimensions(tmp);
    return tmp;
  }

  public void updateFileIcon(Map<String, Image> _icFileIcons,String mimeType, IWContext iwc, boolean isLeaf){
    IWBundle bundle = this.getBundle(iwc);
    if(isLeaf){
      _icFileIcons.put(mimeType,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_DEFAULT_ICON_SUFFIX));
    } else {
      _icFileIcons.put(mimeType+_NODE_OPEN,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_NODE_OPEN+_DEFAULT_ICON_SUFFIX));
      _icFileIcons.put(mimeType+_NODE_CLOSED,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_NODE_CLOSED+_DEFAULT_ICON_SUFFIX));
    }
  }

	public void setToUseOnClick() {
		setToUseOnClick(ONCLICK_DEFAULT_NODE_NAME_PARAMETER_NAME, ONCLICK_DEFAULT_NODE_ID_PARAMETER_NAME);
	}
  public void setToUseOnClick(String NodeNameParameterName, String NodeIDParameterName) {
		this._usesOnClick = true;
		Script associatedScript = getParentPage().getAssociatedScript();
		if(associatedScript != null) {
			getAssociatedScript().addFunction(ONCLICK_FUNCTION_NAME, "function " + ONCLICK_FUNCTION_NAME + "(" + NodeNameParameterName + "," + NodeIDParameterName + "){ }");
		}
	}

	@Override
	public void setOnClick(String action) {
		Script associatedScript = getParentPage().getAssociatedScript();
		if(associatedScript != null) {
			this.getAssociatedScript().addToFunction(ONCLICK_FUNCTION_NAME, action);
		}
	}

/*
  public void main(IWContext iwc) throws Exception {
    super.main(iwc);

    if(_icFileIcons != null && _icFileIcons.keySet() != null){
      Iterator iter = _icFileIcons.keySet().iterator();
      while (iter.hasNext()) {
        String mimeType = (String)iter.next();
        mimeType = mimeType.replace('/','_');
        mimeType = mimeType.replace('.','_');
        mimeType = mimeType.replace(',','_');
        mimeType = mimeType.replace(';','_');
        mimeType = mimeType.replace('?','_');
        this.print(mimeType+_DEFAULT_ICON_SUFFIX+"\n");
      }
    } else {
      System.out.print("ICFileTree: _icFileIcons or _icFileIcons.keySet() = null");
    }

  }
*/

///**
// *
// * @param fileType An ArrayList of ICFileType entities
// */
//  public void setICFileTypeFilterArrayList(ArrayList icFileTypeArrayList){
//    this.icFileTypeArrayList = icFileTypeArrayList;
//  }
//
//  protected boolean filter(ICTreeNode node){
//    //store mimetype result
//    //check in store
//    //else get type from cache
//    //return if not of filter type
//return true;
//    //return ((ICFile)node).getMimeType().equals(com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
//  }

}
