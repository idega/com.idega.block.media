package com.idega.block.media.presentation;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Map;

import com.idega.block.media.business.VideoServices;
import com.idega.block.media.data.VideoService;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Layer;
import com.idega.presentation.Shockwave;
import com.idega.presentation.text.Text;

public class VideoViewer extends Block {
	
	private static String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";
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
		if(videoId != null && !videoId.equals("") && serviceId != null) {
			try {
				VideoServices videoServices = (VideoServices) IBOLookup.getServiceInstance(iwc.getIWMainApplication().getIWApplicationContext(), VideoServices.class);
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
					
					section.add(player);
				}
//				player.setTransparent();
//				player.setURL(YOUTUBE_WWW_PREFIX + videoId);
//				player.setMarkupAttribute(YOUTUBE_PARAMETER_TYPE, YOUTUBE_VALUE_TYPE);
//				player.setMarkupAttribute(YOUTUBE_PARAMETER_PLAY, "");
				
				
			} catch (IBOLookupException ile) {
				throw new IBORuntimeException(ile);
			} catch (RemoteException re) {
				//TODO
			}
		} else {
			section.add(new Text(getResourceBundle().getLocalizedString("iwblock.video.noid", "Video parameters are not set")));
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
