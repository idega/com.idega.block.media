/*
 * Created on 28.3.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.media.presentation;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.ejb.FinderException;

import com.idega.core.business.ICTreeNodeLeafComparator;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.util.ListUtil;

/**
 * Title:		ICFileTreeNode
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICFileTreeNode implements ICTreeNode {

	private Collection _hiddenMimeTypes = new Vector();
	private Collection _visibleMimeTypes = new Vector();
	private ICFile _file;
	private String _orderBy=null;
	private ICFileTreeNode _parent=null;
	private List _children = null;
	private boolean _checkForLocalizationKey = false;
	
	protected boolean _sortLeafs = true;
	protected boolean _leafsFirst = false;
	
	public ICFileTreeNode(ICFile file){
		this(file,null);
	}
	
	/**
	 * 
	 */
	public ICFileTreeNode(ICFile file, ICFileTreeNode parent) {
		_file = file;
		if(parent != null){
			_parent = parent;
			this._orderBy = _parent._orderBy;
			this._hiddenMimeTypes = _parent._hiddenMimeTypes;
			this._visibleMimeTypes = _parent._visibleMimeTypes;
			this._sortLeafs = _parent._sortLeafs;
			this._leafsFirst = _parent._leafsFirst;
		} else {
			_orderBy = file.getEntityDefinition().findFieldByUniqueName(ICFile.UFN_NAME).getSQLFieldName();
			_sortLeafs = file.sortLeafs();
			_leafsFirst = file.leafsFirst();
		}
	}
	
	private ICFileHome getICFileHome() throws RemoteException {
		return ((ICFileHome) IDOLookup.getHome(ICFile.class));
	}


	public List getListOfChildren(){
		if(_children != null){
			return _children;
		} else {
			List l = new Vector();
			Collection coll = null;
			try {
				coll = getICFileHome().findChildren(_file, _visibleMimeTypes, _hiddenMimeTypes, _orderBy);
				
			} catch (RemoteException e) {
				System.err.println("There was an error in "+this.getClass().getName()+".getChildren() " + e.getMessage());
				e.printStackTrace(System.err);
			} catch (FinderException e) {
				System.err.println("There was an error in "+this.getClass().getName()+".getChildren() " + e.getMessage());
				e.printStackTrace(System.err);
			}		
	
			Iterator iter = coll.iterator();
			ICFileTreeNode node = null;
			while (iter.hasNext()) {
				ICFile item = (ICFile) iter.next();
				node = new ICFileTreeNode(item,this);
				l.add(node);
			}
			
			if (l != null) {
				if (_sortLeafs) {
					ICTreeNodeLeafComparator c = new ICTreeNodeLeafComparator(_leafsFirst);
					Collections.sort(l, c);
				}
				_children = l;
			} else {
				_children = ListUtil.getEmptyList();
			}
			
			
			return _children;
		}
	}
	

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildrenIterator()
	 */
	public Iterator getChildrenIterator() {
	    Iterator it = null;
	    Collection children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}	

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildren()
	 */
	public Collection getChildren() {
		return getListOfChildren();
	}


	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getAllowsChildren()
	 */
	public boolean getAllowsChildren() {
		return _file.getAllowsChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildAtIndex(int)
	 */
	public ICTreeNode getChildAtIndex(int childIndex) {
		return _file.getChildAtIndex(childIndex);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildCount()
	 */
	public int getChildCount() {
//		if(_children != null){
//			return _children.size();
//		} else {
			return this.getListOfChildren().size();
//		}
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getIndex(com.idega.core.data.ICTreeNode)
	 */
	public int getIndex(ICTreeNode node) {
		return _file.getIndex(node);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getParentNode()
	 */
	public ICTreeNode getParentNode() {
		return _parent;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#isLeaf()
	 */
	public boolean isLeaf() {
		return _file.isLeaf();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName()
	 */
	public String getNodeName() {
		return _file.getNodeName();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName(java.util.Locale)
	 */
	public String getNodeName(Locale locale) {
		return _file.getNodeName(locale);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName(java.util.Locale, com.idega.idegaweb.IWApplicationContext)
	 */
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		if(_checkForLocalizationKey){
			return _file.getNodeName(locale,iwac);
		} else {
			return _file.getNodeName(locale);
		}
		
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeID()
	 */
	public int getNodeID() {
		return _file.getNodeID();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getSiblingCount()
	 */
	public int getSiblingCount() {
		ICTreeNode parent = getParentNode();
		if (parent == null)
			return (0);

		return (parent.getChildCount() - 1);
	}
	
	public ICFile getICFile(){
		return _file;
	}
	
	public void addVisibleMimeType(String mimetype){
		_visibleMimeTypes.add(mimetype);
	}
	
	public void addHiddenMimeType(String mimetype){
		_hiddenMimeTypes.add(mimetype);
	}
	
	public void addVisibleMimeTypes(Collection mimetypes){
		_visibleMimeTypes.addAll(mimetypes);
	}
	
	public void addHiddenMimeTypes(Collection mimetypes){
		_hiddenMimeTypes.addAll(mimetypes);
	}

	public void removeVisibleMimeType(String mimetype){
		_visibleMimeTypes.remove(mimetype);
	}
	
	public void removeHiddenMimeType(String mimetype){
		_hiddenMimeTypes.remove(mimetype);
	}
	
	public void removeVisibleMimeTypes(Collection mimetypes){
		_visibleMimeTypes.removeAll(mimetypes);
	}
	
	public void removeHiddenMimeTypes(Collection mimetypes){
		_hiddenMimeTypes.removeAll(mimetypes);
	}


	public boolean checkForLocalizationKey() {
		return _checkForLocalizationKey;
	}

	public void setToCheckForLocalizationKey(boolean value) {
		_checkForLocalizationKey = value;
	}

}
