package com.idega.block.media.presentation;

/**
 * Title: ImageInserter Description: Copyright: Copyright (c) 2001 Company:
 * idega
 * 
 * @author Eirikur Hrafnsson, eiki@idega.is
 * @version 1.0
 */

import com.idega.block.media.business.MediaConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.idegaweb.IWUserContext;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Page;
import com.idega.presentation.Script;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.Window;

public class ImageInserter extends Block  {

	private final static String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";
	private int imageId = -1;
	private boolean nullImageIDDefault = false;
	private String imSessionImageName = null;
	private String sHiddenInputName = null;
	private String adminURL = null;
	private String nameOfWindow;
	private String sUseBoxString;
	private int maxImageWidth = 140;
	private int imageWidth = 0;
	private int imageHeight = 0;
	private boolean hasUseBox = true;
	private boolean selected = false;
	private boolean openInWindow = false;
	private Class windowClass = MediaChooserWindow.class;
	private Image setImage;
	private boolean limitWidth = true;
	public final String sessionImageParameterName = MediaConstants.FILE_IN_SESSION_PARAMETER_NAME;
	private String prmUseBox = "insertImage";
	private boolean maintainSessionParameter = false;
	private boolean setWindowToReloadParent = false;

	public static int IM_BROWSER_WIDTH = 800;
	public static int IM_BROWSER_HEIGHT = 600;
	public static int IM_MAX_WIDTH = 140;
	private String contextPath = "";

	private IWBundle iwb;
	private IWResourceBundle iwrb;

	public ImageInserter() {
		this.imSessionImageName = "image_id";
		this.sHiddenInputName = "image_id";
	}

	public ImageInserter(Image setImage) {
		this();
		this.setImage = setImage;
	}

	public ImageInserter(int imageId) {
		this.imageId = imageId;
		this.imSessionImageName = "image_id";
		this.sHiddenInputName = "image_id";
	}

	public ImageInserter(String imSessionImageName) {
		this.imSessionImageName = imSessionImageName;
		this.sHiddenInputName = imSessionImageName;
	}

	public ImageInserter(String imSessionImageName, boolean hasUseBox) {
		this(imSessionImageName);
		setHasUseBox(hasUseBox);
	}

	public ImageInserter(int imageId, String imSessionImageName) {
		this.imageId = imageId;
		this.imSessionImageName = imSessionImageName;
		this.sHiddenInputName = imSessionImageName;
	}

	public ImageInserter(Class WindowToOpen) {
		this.sHiddenInputName = imSessionImageName;
		windowClass = WindowToOpen;
		openInWindow = true;
	}

	public void main(IWContext iwc) throws Exception {
		this.empty();
		iwb = getBundle(iwc);
		iwrb = getResourceBundle(iwc);

		contextPath = iwb.getApplication().getApplicationContextURI();

		if (contextPath == null)
			contextPath = "";
		else if (!contextPath.endsWith("/"))
			contextPath += "/";

		nameOfWindow = iwrb.getLocalizedString("new_image", "New image");
		sUseBoxString = iwrb.getLocalizedString("use_image", "Use image");

		String imageSessionId = (String) iwc.getSessionAttribute(imSessionImageName);

		if (imageSessionId != null) {
			imageId = Integer.parseInt(imageSessionId);
			if (!maintainSessionParameter) {
				iwc.removeSessionAttribute(imSessionImageName);
			}
		}

		Image image = setImage;
		if (image == null) {
			if (imageId == -1) {
				image = iwrb.getImage("picture.gif", iwrb.getLocalizedString("new_image", "Newimage"), 138, 90);
			}
			else {
				image = new Image(imageId);
			}
			if (limitWidth) {
				image.setMaxImageWidth(this.maxImageWidth);
				image.setHeight(90);
			}
			if (imageWidth > 0) {
				image.setWidth(imageWidth);
			}
			if (imageHeight > 0) {
				image.setHeight(imageHeight);
			}
			image.setNoImageLink();
		}
		image.setName("im" + imSessionImageName);
		image.setBorder(1);

		Link imageAdmin = null;
		if (adminURL == null) {
			imageAdmin = new Link(image);
			imageAdmin.setWindowToOpen(windowClass);
		}
		else {
			Window insertNewsImageWindow = new Window(nameOfWindow, IM_BROWSER_WIDTH, IM_BROWSER_HEIGHT, adminURL);
			imageAdmin = new Link(image, insertNewsImageWindow);
		}

		if (setWindowToReloadParent) {
			imageAdmin.addParameter(MediaConstants.MEDIA_ACTION_RELOAD, "true");
		}

		imageAdmin.addParameter("submit", "new");
		imageAdmin.addParameter(sessionImageParameterName, imSessionImageName);
		//filter only images
		imageAdmin.addParameter(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME, MediaConstants.MEDIA_CHOOSER_IMAGE);

		if (imageId != -1)
			imageAdmin.addParameter(imSessionImageName, imageId);

		String stringImageID = null;
		if (nullImageIDDefault && imageId==-1) {
			stringImageID = "";
		}
		else {
			stringImageID = Integer.toString(imageId);
		}
		HiddenInput hidden = new HiddenInput(sHiddenInputName, stringImageID);
		hidden.keepStatusOnAction();

		Page P = getParentPage();
		if (P != null) {
			Script S = P.getAssociatedScript();
			if (S != null)
				S.addFunction("imchange", getImageChangeJSFunction(hidden.getID()));
		}

		CheckBox insertImage = new CheckBox(prmUseBox, "Y");
		insertImage.setChecked(selected);

		Text imageText = new Text(sUseBoxString + ":&nbsp;");
		imageText.setFontSize(1);

		Table imageTable = new Table(1, 2);
		imageTable.setAlignment(1, 2, "right");
		imageTable.add(imageAdmin, 1, 1);
		if (hasUseBox) {
			imageTable.add(imageText, 1, 2);
			imageTable.add(insertImage, 1, 2);
		}

		imageTable.add(hidden, 1, 2);

		add(imageTable);
	}

	public static String getFunction(int id) {
		return "setImageId(" + id + ")";
	}

	public String getImageChangeJSFunction(String hiddenInputID) {
		StringBuffer function = new StringBuffer("");
		function.append("function setImageId(imageId,imagename) { \n \t");
		function.append("document.getElementById('im'+imagename).src = \"");
		function.append(contextPath + "servlet/MediaServlet");
		function.append("?media_id=\"+imageId; \n\t");
		function.append("document.getElementById('").append(hiddenInputID).append("').value = imageId; \n}\n");
		function.append("function getElementIndex(elementname){ \n \t");
		function.append("len = document.forms[0].length \n \t");
		function.append("for(i=0; i<len; i++){ \n \t \t");
		function.append("if(document.forms[0].elements[i].name == elementname.toString()){ \n\t\t ");
		function.append("return i; \n \t \t} \n  \t} \n }\n");

		return function.toString();
	}

	public void setHasUseBox(boolean useBox) {
		this.hasUseBox = useBox;
	}

	public void setHasUseBox(boolean useBox, String prmUseBox) {
		this.hasUseBox = useBox;
		this.prmUseBox = prmUseBox;
	}

	public void setUseBoxParameterName(String prmUseBox) {
		this.prmUseBox = prmUseBox;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void setUseBoxString(String sUseBoxString) {
		this.sUseBoxString = sUseBoxString;
	}

	public void setHiddenInputName(String name) {
		sHiddenInputName = name;
	}

	public String getHiddenInputName() {
		return sHiddenInputName;
	}

	public void setMaxImageWidth(int maxWidth) {
		this.maxImageWidth = maxWidth;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public void setHeight(String imageHeight) {
		try {
			this.imageHeight = Integer.parseInt(imageHeight);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setWidth(String imageWidth) {
		try {
			this.imageWidth = Integer.parseInt(imageWidth);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public void setAdminURL(String adminURL) {
		this.adminURL = adminURL;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	public void setImSessionImageName(String imSessionImageName) {
		this.imSessionImageName = imSessionImageName;
		this.sHiddenInputName = imSessionImageName;
	}

	public String getImSessionImageName() {
		return this.imSessionImageName;
	}

	public void maintainSessionParameter() {
		maintainSessionParameter = true;
	}

	public void setWindowToReload(boolean reload) {
		setWindowToReloadParent = reload;
	}

	public void setWindowClassToOpen(Class WindowClass) {
		windowClass = WindowClass;
		openInWindow = true;
	}

	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}

	public void limitImageWidth(boolean limitWidth) {
		this.limitWidth = limitWidth;
	}

	/**
	 * Sets the instance so it has an empty string instead of the default -1 as
	 * the ImageID when nothing is selected
	 */
	public void setNullImageIDDefault() {
		nullImageIDDefault = true;
	}

  public String getBuilderName(IWUserContext iwc) {
    return iwc.getApplicationContext().getIWMainApplication().getCoreBundle().getComponentName(Image.class,iwc.getCurrentLocale());
  }
}