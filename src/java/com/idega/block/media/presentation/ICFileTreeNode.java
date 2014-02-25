/*
 * Created on 28.3.2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.idega.block.media.presentation;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.ejb.FinderException;

import com.idega.core.business.ICTreeNodeLeafComparator;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICFileHome;
import com.idega.data.IDOLookup;
import com.idega.idegaweb.IWApplicationContext;

/**
 * Title:		ICFileTreeNode
 * Description:
 * Copyright:	Copyright (c) 2004
 * Company:		idega Software
 * @author		2004 - idega team - <br><a href="mailto:gummi@idega.is">Gudmundur Agust Saemundsson</a><br>
 * @version		1.0
 */
public class ICFileTreeNode implements ICTreeNode<ICFileTreeNode> {

	private Collection<String> _hiddenMimeTypes = new ArrayList<String>();
	private Collection<String> _visibleMimeTypes = new ArrayList<String>();
	private ICFile _file;
	private String _orderBy=null;
	private ICFileTreeNode _parent = null;
	private List<ICFileTreeNode> _children = null;
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
		this._file = file;
		if(parent != null){
			this._parent = parent;
			this._orderBy = this._parent._orderBy;
			this._hiddenMimeTypes = this._parent._hiddenMimeTypes;
			this._visibleMimeTypes = this._parent._visibleMimeTypes;
			this._sortLeafs = this._parent._sortLeafs;
			this._leafsFirst = this._parent._leafsFirst;
		} else {
			this._orderBy = file.getEntityDefinition().findFieldByUniqueName(ICFile.UFN_NAME).getSQLFieldName();
			this._sortLeafs = file.sortLeafs();
			this._leafsFirst = file.leafsFirst();
		}
	}

	private ICFileHome getICFileHome() throws RemoteException {
		return ((ICFileHome) IDOLookup.getHome(ICFile.class));
	}


	public List<ICFileTreeNode> getListOfChildren(){
		if(this._children != null){
			return this._children;
		} else {
			List<ICFileTreeNode> l = new ArrayList<ICFileTreeNode>();
			Collection<ICFile> coll = null;
			try {
				coll = getICFileHome().findChildren(this._file, this._visibleMimeTypes, this._hiddenMimeTypes, this._orderBy);
			} catch (RemoteException e) {
				System.err.println("There was an error in "+this.getClass().getName()+".getChildren() " + e.getMessage());
				e.printStackTrace(System.err);
			} catch (FinderException e) {
				System.err.println("There was an error in "+this.getClass().getName()+".getChildren() " + e.getMessage());
				e.printStackTrace(System.err);
			}

			ICFileTreeNode node = null;
			for (Iterator<ICFile> iter = coll.iterator(); iter.hasNext();) {
				ICFile item = iter.next();
				node = new ICFileTreeNode(item,this);
				l.add(node);
			}

			if (l != null) {
				if (this._sortLeafs) {
					Collections.sort(l, new ICTreeNodeLeafComparator<ICFileTreeNode>(this._leafsFirst));
				}
				this._children = l;
			}

			return this._children;
		}
	}


	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildrenIterator()
	 */
	@Override
	public Iterator<ICFileTreeNode> getChildrenIterator() {
	    Iterator<ICFileTreeNode> it = null;
	    Collection<ICFileTreeNode> children = getChildren();
	    if (children != null) {
	        it = children.iterator();
	    }
	    return it;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildren()
	 */
	@Override
	public Collection<ICFileTreeNode> getChildren() {
		return getListOfChildren();
	}


	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getAllowsChildren()
	 */
	@Override
	public boolean getAllowsChildren() {
		return this._file.getAllowsChildren();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildAtIndex(int)
	 */
	@Override
	public ICFileTreeNode getChildAtIndex(int childIndex) {
		ICTreeNode<?> node = this._file.getChildAtIndex(childIndex);
		if (node instanceof ICFileTreeNode) {
			return (ICFileTreeNode) node;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getChildCount()
	 */
	@Override
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
	@Override
	public int getIndex(ICFileTreeNode node) {
		ICFile file = node.getICFile();
		return this._file.getIndex(file);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getParentNode()
	 */
	@Override
	public ICFileTreeNode getParentNode() {
		return this._parent;
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#isLeaf()
	 */
	@Override
	public boolean isLeaf() {
		return this._file.isLeaf();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName()
	 */
	@Override
	public String getNodeName() {
		return this._file.getNodeName();
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName(java.util.Locale)
	 */
	@Override
	public String getNodeName(Locale locale) {
		return this._file.getNodeName(locale);
	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getNodeName(java.util.Locale, com.idega.idegaweb.IWApplicationContext)
	 */
	@Override
	public String getNodeName(Locale locale, IWApplicationContext iwac) {
		if(this._checkForLocalizationKey){
			return this._file.getNodeName(locale,iwac);
		} else {
			return this._file.getNodeName(locale);
		}

	}

	/* (non-Javadoc)
	 * @see com.idega.core.data.ICTreeNode#getSiblingCount()
	 */
	@Override
	public int getSiblingCount() {
		ICFileTreeNode parent = getParentNode();
		if (parent == null) {
			return (0);
		}

		return (parent.getChildCount() - 1);
	}

	public ICFile getICFile(){
		return this._file;
	}

	public void addVisibleMimeType(String mimetype){
		this._visibleMimeTypes.add(mimetype);
	}

	public void addHiddenMimeType(String mimetype){
		this._hiddenMimeTypes.add(mimetype);
	}

	public void addVisibleMimeTypes(Collection<String> mimetypes){
		this._visibleMimeTypes.addAll(mimetypes);
	}

	public void addHiddenMimeTypes(Collection<String> mimetypes){
		this._hiddenMimeTypes.addAll(mimetypes);
	}

	public void removeVisibleMimeType(String mimetype){
		this._visibleMimeTypes.remove(mimetype);
	}

	public void removeHiddenMimeType(String mimetype){
		this._hiddenMimeTypes.remove(mimetype);
	}

	public void removeVisibleMimeTypes(Collection<String> mimetypes){
		this._visibleMimeTypes.removeAll(mimetypes);
	}

	public void removeHiddenMimeTypes(Collection<String> mimetypes){
		this._hiddenMimeTypes.removeAll(mimetypes);
	}

	public boolean checkForLocalizationKey() {
		return this._checkForLocalizationKey;
	}

	public void setToCheckForLocalizationKey(boolean value) {
		this._checkForLocalizationKey = value;
	}

	@Override
	public int getNodeID() {
		return new Integer(this._file.getPrimaryKey().toString()).intValue();
	}

	@Override
	public String getId(){
		return this._file.getId().toString();
	}

}