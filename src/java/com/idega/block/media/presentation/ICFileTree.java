package com.idega.block.media.presentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import com.idega.block.media.business.MediaBusiness;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICMimeType;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.text.Link;
import com.idega.presentation.ui.AbstractTreeViewer;

/**
 * Title:        IW Project
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      idega
 * @author <a href="gummi@idega.is">Gu�mundur �g�st S�mundsson</a>
 * @version 1.0
 */

public class ICFileTree extends AbstractTreeViewer {

  private static final String _APP_DEFAULT_FILE_ICONS = "ic_filetree_icons";
  private String _APP_FILE_ICONS = _APP_DEFAULT_FILE_ICONS;
  private static final String _NODE_OPEN = "_open";
  private static final String _NODE_CLOSED = "_closed";
  private static final String _DEFAULT_ICON_PREFIX = "icfileicons/ui/";

  private static final String _DEFAULT_ICON_SUFFIX = ".gif";
  private String iconFolder = _DEFAULT_ICON_PREFIX;
  private String icon_suffix = _DEFAULT_ICON_SUFFIX;
  private static Hashtable _icFileIcons = null;

  private Link _fileLink = new Link();
  private Link _folderLink = new Link();

  private String nodeNameTarget = null;
  private String nodeActionPrm = null;

  private ArrayList icFileTypeArrayList = null;

  public ICFileTree(){
    super();
    this.setColumns(2);
  }

  public static ICFileTree getICFileTreeInstance(ICTreeNode[] nodes){
    ICFileTree fileTree = new ICFileTree();
    fileTree.setFirstLevelNodes(nodes);
    return fileTree;
  }

  public Image getIcon(ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode){
    String mimeType = ((ICFile)node).getMimeType();
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
          this.updateFileIcon(mimeType,iwc,false);
          obj = _icFileIcons.get(mimeType+((nodeIsOpen)?_NODE_OPEN:_NODE_CLOSED));
        }
        return (Image)obj;
      }else {
        Object obj = _icFileIcons.get(mimeType);
        if(obj == null){
          this.updateFileIcon(mimeType,iwc,true);
          obj = _icFileIcons.get(mimeType);
        }
        return (Image)obj;
      }
    }else {
      return null;
    }
  }

  public PresentationObject getObjectToAddToColumn(int colIndex, ICTreeNode node, IWContext iwc, boolean nodeIsOpen, boolean nodeHasChild, boolean isRootNode) {
    if( filter(node) ){//return null if this
      switch (colIndex) {
        case 1:
          return getIcon(node, iwc, nodeIsOpen, nodeHasChild, isRootNode );
        case 2:

          if(!node.isLeaf()){
            Link l = this.getFolderLinkClone(node.getNodeName());

            this.setLinkToOpenOrCloseNode(l,node,nodeIsOpen);
            if( nodeNameTarget != null ){
             l.setTarget(nodeNameTarget);
            }
            if( nodeActionPrm!=null ){
              l.addParameter(nodeActionPrm,node.getNodeID());
            }

            return l;
          }
          else {
            Link l = this.getFileLinkClone(node.getNodeName());
            this.setLinkToMaintainOpenAndClosedNodes(l);

            if( nodeNameTarget != null ){
             l.setTarget(nodeNameTarget);
            }
            if( nodeActionPrm!=null ){
              l.addParameter(nodeActionPrm,node.getNodeID());
            }

            return l;
          }
      }
    }
    return null;
  }


  private Link getFileLinkClone(){
    return (Link)_fileLink.clone();
  }
  private Link getFolderLinkClone(){
    return (Link)_folderLink.clone();
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
    nodeActionPrm = prm;
  }

  public void setTarget(String target){
    nodeNameTarget = target;
  }

  public void setFileLinkPrototype(Link link){
    _fileLink = link;
  }

  public void setFolderLinkPrototype(Link link){
    _folderLink = link;
  }

  protected void updateIconDimensions(){
    super.updateIconDimensions();

    if(_icFileIcons != null && _icFileIcons.values() != null){
      Iterator iter = this._icFileIcons.values().iterator();
      while (iter.hasNext()) {
        Image item = (Image)iter.next();
        if(item != null){
          item.setHeight(iconHeight);
        }
      }
    }
  }

  public void initIcons(IWContext iwc){
    super.initIcons(iwc);

    Object obj = iwc.getApplicationAttribute(_APP_FILE_ICONS + getUI());
    if(obj == null){
      IWBundle bundle = this.getBundle(iwc);
      Hashtable tmp = new Hashtable();

      HashMap mimeMap = (HashMap)MediaBusiness.getICMimeTypeMap(iwc);

      if(mimeMap != null){
        Iterator iter = mimeMap.keySet().iterator();
        while (iter.hasNext()) {
          ICMimeType item = (ICMimeType)(mimeMap.get((String)iter.next()));
          String mimeType = item.getMimeType();
          tmp.put(mimeType,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_DEFAULT_ICON_SUFFIX));
        }
      }

      iwc.setApplicationAttribute(_APP_FILE_ICONS + getUI(),tmp);
      this._icFileIcons = tmp;
    } else {
      this._icFileIcons = (Hashtable)obj;
    }

    updateIconDimensions();

  }

  public void updateFileIcon(String mimeType, IWContext iwc, boolean isLeaf){
    IWBundle bundle = this.getBundle(iwc);
    if(isLeaf){
      _icFileIcons.put(mimeType,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_DEFAULT_ICON_SUFFIX));
    } else {
      _icFileIcons.put(mimeType+_NODE_OPEN,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_NODE_OPEN+_DEFAULT_ICON_SUFFIX));
      _icFileIcons.put(mimeType+_NODE_CLOSED,bundle.getImage(_DEFAULT_ICON_PREFIX+getUI()+mimeType+_NODE_CLOSED+_DEFAULT_ICON_SUFFIX));
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

/**
 *
 * @param fileType An ArrayList of ICFileType entities
 */
  public void setICFileTypeFilterArrayList(ArrayList icFileTypeArrayList){
    this.icFileTypeArrayList = icFileTypeArrayList;
  }

  protected boolean filter(ICTreeNode node){
    //store mimetype result
    //check in store
    //else get type from cache
    //return if not of filter type
return true;
    //return ((ICFile)node).getMimeType().equals(com.idega.core.data.ICMimeTypeBMPBean.IC_MIME_TYPE_FOLDER);
  }

}
