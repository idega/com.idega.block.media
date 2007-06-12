package com.idega.block.media.presentation;

import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Shockwave;
import com.idega.presentation.text.Text;

public class VideoViewer extends Block {
	
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.video";
	private static final String YOUTUBE_WWW_PREFIX = "http://www.youtube.com/v/";
	private static final String YOUTUBE_VALUE_TYPE = "application/x-shockwave-flash";
	private static final String YOUTUBE_PARAMETER_TYPE = "type";
	private static final String YOUTUBE_PARAMETER_PLAY = "play";
	private static final int YOUTUBE_WIDTH =  425;
	private static final int YOUTUBE_HEIGHT =  350;
	
	private IWBundle iwb;
	private IWResourceBundle iwrb;
	
	private String videoId;
	private String serviceId;

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
	
	public void present(IWContext iwc) throws Exception {
		Layer section = new Layer(Layer.DIV);
		String temp = videoId;
		String temp2 = serviceId;
		if(videoId != null && !videoId.equals("")) {
			Shockwave player = new Shockwave(YOUTUBE_WWW_PREFIX + videoId, YOUTUBE_WIDTH, YOUTUBE_HEIGHT);
			player.setClassId(null);
			player.setCodeBase(null);
			player.setPluginSpace(null);
			player.clearParams();
			player.setTransparent();
			player.setURL(YOUTUBE_WWW_PREFIX + videoId);
			player.setMarkupAttribute(YOUTUBE_PARAMETER_TYPE, YOUTUBE_VALUE_TYPE);
			player.setMarkupAttribute(YOUTUBE_PARAMETER_PLAY, "");
			section.add(player);
		} else {
			section.add(new Text(getResourceBundle().getLocalizedString("iwblock.video.noid", "ID of YouTube video not set")));
		}
		
		add(section);
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
