package com.idega.block.media.business;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.core.file.data.ICMimeType;
import com.idega.idegaweb.IWBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;

/**
 * Title: com.idega.block.media.business.SystemTypeHandler Description: A type
 * handler that handles idegaWeb system type files such as folders ( The Finder ;)
 * Copyright: Copyright (c) 2001 Company: idega software
 * 
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */
public class SystemTypeHandler extends FileTypeHandler {

	public static String[] LIST_VIEW_HEADERS = { "Select", "Name", "Date modified", "Size", "Mimetype" };//**@todo
																										 // localize**/
	//private static Hashtable _icFileIcons = null;
	private static final String _NODE_CLOSED = "_closed";
	private static final String _DEFAULT_ICON_PREFIX = "icfileicons/ui/";
	private static final String _DEFAULT_ICON_SUFFIX = ".gif";
	public static final String _UI_WIN = "win/";
	public static final String _UI_MAC = "mac/";
	public static final String _UI_IW = "iw/";
	private String _ui = _UI_IW;
	//private static final String _APP_DEFAULT_FILE_ICONS = "ic_filetree_icons";
	//private String _APP_FILE_ICONS = _APP_DEFAULT_FILE_ICONS;
	protected String iconWidth = "16";
	protected String iconHeight = "16";

	public PresentationObject getPresentationObject(int icFileId, IWContext iwc) {
		//ContentViewer listView = null;
		//try {
		Table table = new Table();
		// table.setColor("#ECECEC");
		table.setColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
		table.setWidth(Table.HUNDRED_PERCENT);
		table.setHeight(Table.HUNDRED_PERCENT);
		table.setCellpadding(2);
		table.setCellspacing(0);
		ICFile file = (ICFile) this.getCachedFileInfo(icFileId, iwc).getEntity();
		Iterator iter = file.getChildrenIterator();
		int x = 1;
		int y = 1;
		Text proto = new Text();
		proto.setFontSize(Text.FONT_SIZE_7_HTML_1);
		proto.setFontFace(Text.FONT_FACE_VERDANA);
		Text name = new Text("Name");
		name.setBold(true);
		name.setFontSize(Text.FONT_SIZE_10_HTML_2);
		Text date = new Text("Modified date");
		date.setBold(true);
		date.setFontSize(Text.FONT_SIZE_10_HTML_2);
		Text size = new Text("Size");
		size.setBold(true);
		size.setFontSize(Text.FONT_SIZE_10_HTML_2);
		Text mime = new Text("Mime type");
		mime.setBold(true);
		mime.setFontSize(Text.FONT_SIZE_10_HTML_2);
		table.add(name, 2, 1);
		table.add(date, 3, 1);
		table.add(size, 4, 1);
		table.add(mime, 5, 1);
		table.setHeight(1, iconHeight);
		table.setWidth(1, iconWidth);
		if (iter != null) {
			while (iter.hasNext()) {
				++y;
				ICFile item = (ICFile) iter.next();
				//table.add(new
				// CheckBox(Integer.toString(item.getID())),x++,y);
				table.add(getIcon(item, iwc), x++, y);
				Link view = MediaBusiness.getMediaViewerLink();
				view.setText(((item.getName() != null) ? item.getName() : ""));
				view.addParameter(MediaBusiness.getMediaParameterNameInSession(iwc), item.getPrimaryKey().toString());
				table.add(view, x++, y);
				table.add(((item.getModificationDate() != null) ? item.getModificationDate().toString()
						: item.getCreationDate().toString()), x++, y);
				table.add(((item.getFileSize() != null) ? item.getFileSize().toString() : ""), x++, y);
				table.add(((item.getMimeType() != null) ? item.getMimeType() : ""), x++, y);
				table.setRowVerticalAlignment(y, Table.VERTICAL_ALIGN_TOP);
				table.setHeight(y, "15");
				x = 1;
			}
		}
		table.add(Text.NON_BREAKING_SPACE, 1, ++y);
		table.setHeight(y, Table.HUNDRED_PERCENT);
		// table.setColumnColor(2,"#FCFCFC");
		//table.setColumnColor(4,"#FCFCFC");
		/*
		 * Vector V = new Vector();
		 * 
		 * if(!MediaBusiness.isFolder(file)) V.add(getContentObject(file));
		 * 
		 * Iterator iter = file.getChildren(); int i = 0; if( iter != null ){
		 * while (iter.hasNext()) { i++; ICFile item = (ICFile) iter.next();
		 * V.add(getContentObject(item)); } }
		 * 
		 * listView = new ContentViewer(LIST_VIEW_HEADERS,V); if( i>0 )
		 * listView.setDisplayNumber(i); listView.setAllowOrder(true);
		 * 
		 * listView.setWidth("100%");
		 * 
		 * 
		 * 
		 * 
		 *  } catch (Exception ex) { ex.printStackTrace(System.err); } return
		 * listView;
		 */
		return table;
	}

	public Map initIcons(IWContext iwc) {
		//Object obj = iwc.getApplicationAttribute(_APP_FILE_ICONS + getUI());
		//if (obj == null) {
			IWBundle bundle = this.getBundle(iwc);
			Hashtable tmp = new Hashtable();
			HashMap mimeMap = (HashMap) MediaBusiness.getICMimeTypeMap(iwc);
			if (mimeMap != null) {
				Iterator iter = mimeMap.keySet().iterator();
				while (iter.hasNext()) {
					ICMimeType item = (ICMimeType) (mimeMap.get(iter.next()));
					String mimeType = item.getMimeType();
					tmp.put(mimeType, bundle.getImage(_DEFAULT_ICON_PREFIX + getUI() + mimeType + _DEFAULT_ICON_SUFFIX));
				}
			}
			//iwc.setApplicationAttribute(_APP_FILE_ICONS + getUI(), tmp);
			//SystemTypeHandler._icFileIcons = tmp;
			return tmp;
		//}
		//else {
		//	SystemTypeHandler._icFileIcons = (Hashtable) obj;
		//}
		//updateIconDimensions();
	}
/*
	protected void updateIconDimensions() {
		if (_icFileIcons != null && _icFileIcons.values() != null) {
			Iterator iter = this._icFileIcons.values().iterator();
			while (iter.hasNext()) {
				Image item = (Image) iter.next();
				if (item != null) {
					item.setHeight(iconHeight);
				}
			}
		}
	}*/

	/**
	 * @param iwc
	 * @return
	 */
	private IWBundle getBundle(IWContext iwc) {
		return iwc.getIWMainApplication().getCoreBundle();
	}

	public Image getIcon(ICFile file, IWContext iwc) {
		Map _icFileIcons=initIcons(iwc);
		/*if (_icFileIcons == null) {
			initIcons(iwc);
		}*/
		String mimeType = file.getMimeType();
		if (mimeType != null) {
			mimeType = mimeType.replace('\\', '_');
			mimeType = mimeType.replace('/', '_');
			mimeType = mimeType.replace(':', '_');
			mimeType = mimeType.replace('*', '_');
			mimeType = mimeType.replace('?', '_');
			mimeType = mimeType.replace('<', '_');
			mimeType = mimeType.replace('>', '_');
			mimeType = mimeType.replace('|', '_');
			mimeType = mimeType.replace('\"', '_');
			if (!file.isLeaf()) {
				Object obj = _icFileIcons.get(mimeType + _NODE_CLOSED);
				if (obj == null) {
					this.updateFileIcon(_icFileIcons,mimeType, iwc, false);
					obj = _icFileIcons.get(mimeType + _NODE_CLOSED);
				}
				return (Image) obj;
			}
			else {
				Object obj = _icFileIcons.get(mimeType);
				if (obj == null) {
					this.updateFileIcon(_icFileIcons,mimeType, iwc, true);
					obj = _icFileIcons.get(mimeType);
				}
				return (Image) obj;
			}
		}
		else {
			return null;
		}
	}

	
	public void updateFileIcon(Map _icFileIcons,String mimeType, IWContext iwc, boolean isLeaf) {
		IWBundle bundle = this.getBundle(iwc);
		if (isLeaf) {
			_icFileIcons.put(mimeType,
					bundle.getImage(_DEFAULT_ICON_PREFIX + getUI() + mimeType + _DEFAULT_ICON_SUFFIX));
		}
		else {
			_icFileIcons.put(mimeType + _NODE_CLOSED, bundle.getImage(_DEFAULT_ICON_PREFIX + getUI() + mimeType
					+ _NODE_CLOSED + _DEFAULT_ICON_SUFFIX));
		}
	}

	public String getUI() {
		return _ui;
	}

	public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc) {
		return new Table();
	}
}