package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.idegaweb.IWApplicationContext;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWConstants;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.FrameSet;
import com.idega.presentation.IWContext;
import com.idega.presentation.Page;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.user.business.UserBusiness;
/**
 * Title: com.idega.block.media.presentation.MediaChooserWindow
 * Description: The frame window that displays the filesystem
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

//public class MediaChooserWindow extends FrameSet {
public class MediaChooserWindow extends AbstractChooserWindow {

	private static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";
	
	private static final String HELP_TEXT_KEY = "media_chooser_help";

	private IWBundle iwb;
	//  public static String prmReloadParent = "simple_upl_wind_rp";
	private String fileInSessionParameter = "ic_file_id";
	private FrameSet frame = null;

	public MediaChooserWindow() {
		super();
		//frameset fixes
		setEmpty(); //for IWAdminWindow
		setOnlyScript(true); //for AbstractChooserWindow
		//
		setWidth(640);
		setHeight(480);
		setResizable(true);

		frame = new FrameSet();

		frame.add(Top.class);
		frame.add(BottomFrameSet.class);
		frame.setSpanPixels(1, 50);
		frame.setScrollbar(false);
		frame.setScrolling(1, false);
		frame.setScrolling(2, false);
		frame.setSpanAdaptive(2);
		frame.setResizable(true);
	}

	public void displaySelection(IWContext iwc) {
		//store the parameter in session
		//MediaBusiness.getMediaParameterNameInSession(iwc);
//		IWContext iwc = IWContext.getInstance();
		IWResourceBundle iwrb = getResourceBundle(iwc);
		setTitle(iwrb.getLocalizedString("media_chooser_window.media_chooser","Media chooser"));

		MediaBusiness.saveMediaIdToSession(iwc, MediaBusiness.getMediaId(iwc));

		String chooserType = iwc.getParameter(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME);
		if (chooserType != null) {
			iwc.setSessionAttribute(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME, chooserType);
		}

		if (MediaBusiness.reloadOnClose(iwc)) {
			frame.setParentToReload();
		}

		add(frame);
	}

	public String getBundleIdentifier() {
		return MediaConstants.IW_BUNDLE_IDENTIFIER;
	}

	public static class FileTree extends Page {
		public FileTree() {
			setAllMargins(0);
			setStyleClass("main");
			Table table = new Table();
			table.setHeight(Table.HUNDRED_PERCENT);
			table.setWidth(Table.HUNDRED_PERCENT);
			table.setVerticalAlignment(1,1,Table.VERTICAL_ALIGN_TOP);
			table.setVerticalAlignment(1,2,Table.VERTICAL_ALIGN_TOP);
			table.setCellpadding(1,2,7);
			table.add(new MediaTreeViewer(),1,1);
			table.add(getHelp(HELP_TEXT_KEY),1,2);
			//    setBackgroundColor(MediaConstants.MEDIA_TREE_VIEWER_BACKGROUND_COLOR);
			add(table);
		}
	}

	public static class FileViewer extends MediaViewerWindow {
		public FileViewer() {
			setAllMargins(0);
			setStyleClass("main");
		}
	}

	public static class BottomFrameSet extends FrameSet {
		public BottomFrameSet() {
			add(FileTree.class);
			add(FileViewer.class);
			setFrameName(1, MediaConstants.TARGET_MEDIA_TREE);
			setFrameName(2, MediaConstants.TARGET_MEDIA_VIEWER);

			this.setSpanPixels(1, 210);
			this.setHorizontal();
			// this.setSpanPixels(3,35);
			this.setScrollbar(false);
			this.setScrolling(1, true);
			this.setScrolling(2, true);
		}
	}

	public static class Top extends Page {
		private UserBusiness userBusiness = null;
		private String styleSrc = "";

		public Top() {
			setAllMargins(0);
		}

		public void main(IWContext iwc) throws Exception {
			IWResourceBundle iwrb = getResourceBundle(iwc);

			Page parentPage = null;
			Table headerTable = new Table();
			headerTable.setCellpadding(0);
			headerTable.setCellspacing(0);
			headerTable.setStyleClass("banner");
			headerTable.setWidth("100%");
			headerTable.setHeight("100%");
			Table t = new Table();
			t.setCellpadding(10);
			t.setCellspacing(0);
			Text text = new Text(iwrb.getLocalizedString("media_chooser_window.media_chooser","Media chooser"));
			text.setStyleAttribute(IWConstants.BUILDER_FONT_STYLE_TITLE);
			t.add(text,1,1);
			headerTable.setAlignment(2,1,Table.HORIZONTAL_ALIGN_RIGHT);
			headerTable.add(t,2,1);
			//			headerTable.addText(iwrb.getLocalizedString("media_chooser_window.media_chooser", "Media chooser"), IWConstants.BUILDER_FONT_STYLE_TITLE);
			//   headerTable.add(getBundle(iwc).getImage(this.getBundle(iwc).getProperty("logo_image_name","top.gif")));

			parentPage = this.getParentPage();
			userBusiness = getUserBusiness(iwc);
			styleSrc = userBusiness.getUserApplicationStyleSheetURL();
			parentPage.addStyleSheetURL(styleSrc);

			//   headerTable.add(iwc.getApplication().getCoreBundle().getImage("/editorwindow/idegaweb.gif","idegaWeb"),1,1);
			add(headerTable);
		}
		protected UserBusiness getUserBusiness(IWApplicationContext iwc) {
			if (userBusiness == null) {
				try {
					userBusiness = (UserBusiness)com.idega.business.IBOLookup.getServiceInstance(iwc, UserBusiness.class);
				} catch (java.rmi.RemoteException rme) {
					throw new RuntimeException(rme.getMessage());
				}
			}
			return userBusiness;
		}

	}

}
