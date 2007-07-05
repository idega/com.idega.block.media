package com.idega.block.media.presentation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.idega.block.media.business.VideoServices;
import com.idega.block.media.data.VideoService;
import com.idega.builder.business.BuilderLogic;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.content.business.ContentUtil;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Image;
import com.idega.presentation.Layer;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Shockwave;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.RadioButton;
import com.idega.presentation.ui.RadioGroup;
import com.idega.presentation.ui.TextInput;

public class VideoViewer extends Block {
	
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";
	
	private IWBundle iwb;
	private IWResourceBundle iwrb;
	
	private String videoId;
	private String serviceId;
	private Boolean hasUserValidRights = Boolean.FALSE;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getVideoId() {
		return videoId;
	}

	@Override
	public String getFamily() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void main(IWContext iwc) throws Exception {
		initialize(iwc);
		present(iwc);
	}
	
	private PresentationObject getEmbeddedVideoBlock(VideoServices videoServices) {
		try {
			VideoService service = videoServices.getVideoService(serviceId);
			if(service != null) {
				Shockwave player = new Shockwave();
				player.setClassId(null);
				player.setCodeBase(null);
				player.setPluginSpace(null);
				player.clearParams();
				
				Map objAttr = service.getObjectAttributes();
				Map objPar = service.getParameters();
				Map embAttr = service.getEmbedAttributes();
				Iterator it1 = objPar.keySet().iterator();
				while(it1.hasNext()) {
					String parName = (String) it1.next();
					
					String parValue = service.getParameter(parName);
					if(parName.equals(service.getObjectId())) {
						player.setParam(parName, parValue + videoId);
					} else {
						player.setParam(parName, parValue);
					}
				}
				Iterator it2 = embAttr.keySet().iterator();
				while(it2.hasNext()) {
					String parName = (String) it2.next();
					
					String parValue = service.getEmbedAttribute(parName);
					if(parName.equals(service.getEmbedId())) {
						player.setMarkupAttribute(parName, parValue + videoId);
					} else {
						player.setMarkupAttribute(parName, parValue);
					}
				}
				return player;
			}
		} catch(RemoteException re) {
			//TODO
		}
		return null;
	}
	
	private void addDwrScript(IWContext iwc, Layer obj) {
		obj.add("<script type=\"text/javascript\" src=\"/dwr/engine.js\" ><!--//--></script>");
		obj.add("<script type=\"text/javascript\" src=\"/dwr/interface/VideoServices.js\" ><!--//--></script>");
		obj.add("<script type=\"text/javascript\" src=\"" + getBundle().getVirtualPathWithFileNameString("javascript/VideoEmbed.js") + "\"><!--//--></script>");
	}
	
	public void present(IWContext iwc) throws Exception {
		hasUserValidRights = ContentUtil.hasContentEditorRoles(iwc);
		Layer rootSection = new Layer(Layer.DIV);
		rootSection.setStyleClass("embeddedVideoBlock");
		Layer section = new Layer(Layer.DIV);
		VideoServices videoServices = null;
		try {
			videoServices = (VideoServices) IBOLookup.getServiceInstance(iwc.getIWMainApplication().getIWApplicationContext(), VideoServices.class);
		} catch(IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
		if(hasUserValidRights) {
			section.setStyleClass("iwVideoViewerEditable");
			String instanceId = BuilderLogic.getInstance().getInstanceId(this);
			if(serviceId == null || "".equals(serviceId)) {
				Map services = videoServices.getVideoServices();
				Set ids = services.keySet();
				RadioGroup radioButtons = new RadioGroup("Select a Video Service");
				for(Iterator it = ids.iterator(); it.hasNext(); ) {
					VideoService service = videoServices.getVideoService((String) it.next());
					if(service != null) {
						RadioButton button = new RadioButton(service.getName(), service.getId());
						
						button.setOnClick("setVideoService(this.value, '" + instanceId + "', '" + section.getId() + "');");
						radioButtons.addRadioButton(button, new Text(service.getName()), false);
					}
				}
				section.add(radioButtons);
				section.setStyleClass("videoServiceSection");
			} else if(videoId == null || "".equals(videoId)) {
				Text header = new Text(getResourceBundle().getLocalizedString("iwblock.media.video.nosetup", "Enter ID of a video clip"));
				header.setStyleClass("videoIdHeader");
				section.add(header);
				TextInput idInput = new TextInput("videoId");
				idInput.setOnKeyUp("setVideoId(event, this.value, '" + instanceId + "', '" + section.getId() + "');");
				idInput.setOnFocus("");
				idInput.setOnBlur("");
				idInput.setId("videoId");
				section.add(idInput);
				section.setStyleClass("videoIdInputSection");
			} else {
				section.add(getEmbeddedVideoBlock(videoServices));
				section.setStyleClass("videoBlockSection");
			}
			Image setupButton = new Image();
			setupButton.setSrc("/idegaweb/bundles/com.idega.block.media.bundle/resources/images/pageIcons/preferences-system.png");
			setupButton.setStyleClass("videoSetupButton");
			setupButton.setOnClick("clearVideoViewer('" + instanceId + "', '" + section.getId() + "');return false;");
			setupButton.setToolTip(getResourceBundle().getLocalizedString("iwblock.media.video.setup", "Click to setup"));
			section.add(setupButton);
		} else {
			section.setStyleClass("iwVideoViewer");
			if(videoId == null || "".equals(videoId) || serviceId != null) {
				section.add(new Text(getResourceBundle().getLocalizedString("iwblock.media.video.nosetup", "Video parameters are not set")));
			} else {
				section.add(getEmbeddedVideoBlock(videoServices));
			}
		}
		if(hasUserValidRights) {
			addDwrScript(iwc, section);
		}
		rootSection.add(section);
		add(rootSection);
	}
	
	public String getBundleIdentifier() {
		return IW_BUNDLE_IDENTIFIER;
	}
	
	private void initialize(IWContext iwc) {
		setResourceBundle(getResourceBundle(iwc));
		setBundle(getBundle(iwc));
	}
	
	protected IWBundle getBundle() {
		return this.iwb;
	}

	protected IWResourceBundle getResourceBundle() {
		return this.iwrb;
	}
	
	private void setBundle(IWBundle iwb) {
		this.iwb = iwb;
	}

	private void setResourceBundle(IWResourceBundle iwrb) {
		this.iwrb = iwrb;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

}
