package com.idega.block.media.business;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.idega.block.media.data.VideoService;
import com.idega.block.media.presentation.VideoViewer;
import com.idega.builder.bean.BuilderEngine;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.IWContext;
import com.idega.slide.business.IWSlideService;

public class VideoServicesBean extends IBOServiceBean implements VideoServices {
	
	private static final String SLIDE_CONFIG_LOCATION = "/files/cms/settings/";
	private static final String SLIDE_CONFIG_FILENAME = "video-services.xml";
	private static final String VIDEO_SERVICE_PROPERTY = ":method:1:implied:void:setServiceId:java.lang.String:";
	private static final String VIDEO_ID_PROPERTY = ":method:1:implied:void:setVideoId:java.lang.String:";
	
	private Map services = new HashMap();
	private BuilderService builderService;
	private BuilderEngine builderEngine;

	public void addVideoService(VideoService service) {
		if(service != null) {
			services.put(service.getId(), service);
		} else {
			throw new IllegalArgumentException("Parameter cannot be null");
		}
	}
	
	public void clearVideoViewer(String instanceId) {
		
	}
	
//	public Document setServiceIdProperty(String serviceId, String instanceId) throws RemoteException {
//		if(builderService == null) {
//			return null;
//		}
//		if(builderEngine == null) {
//			return null;
//		}
//		String pageKey = builderService.getCurrentPageKey(IWContext.getInstance());
//		builderService.setModuleProperty(pageKey, instanceId, VIDEO_SERVICE_PROPERTY, new String[] {serviceId});
//		return builderEngine.reRenderObject(pageKey, instanceId);
//	}
//	
//	public Document setVideoIdProperty(String videoId, String instanceId) throws RemoteException {
//		if(builderService == null) {
//			return null;
//		}
//		if(builderEngine == null) {
//			return null;
//		}
//		String pageKey = builderService.getCurrentPageKey(IWContext.getInstance());
//		builderService.setModuleProperty(pageKey, instanceId, VIDEO_ID_PROPERTY, new String[] {videoId});
//		return builderEngine.reRenderObject(pageKey, instanceId);
//	}
//	
//	public Document clearVideoProperties(String instanceId) throws RemoteException {
//		if(builderService == null) {
//			return null;
//		}
//		if(builderEngine == null) {
//			return null;
//		}
//		String pageKey = builderService.getCurrentPageKey(IWContext.getInstance());
//		List<String> moduleIds = builderService.getModuleId(pageKey, VideoViewer.class.getName());
//		builderService.setModuleProperty(pageKey, (String) moduleIds.get(0), VIDEO_SERVICE_PROPERTY, new String[] {""});
//		builderService.setModuleProperty(pageKey, (String) moduleIds.get(0), VIDEO_ID_PROPERTY, new String[] {""});
//		return builderEngine.reRenderObject(pageKey, instanceId);
//	}
	
	public Document setVideoProperties(String serviceId, String videoId, String instanceId) throws RemoteException {
		if(builderService == null || builderEngine == null) {
			initialize();
		}
		if(serviceId == null || videoId == null || instanceId == null) {
			return null;
		}
		String pageKey = builderService.getCurrentPageKey(IWContext.getInstance());
		boolean updateResult = false;
		if(videoId.equals("") && serviceId.equals("")) {
			updateResult = builderService.removeProperty(IWMainApplication.getDefaultIWMainApplication(), pageKey, instanceId, VIDEO_SERVICE_PROPERTY, new String[] {serviceId});
			updateResult = builderService.removeProperty(IWMainApplication.getDefaultIWMainApplication(), pageKey, instanceId, VIDEO_ID_PROPERTY, new String[] {videoId});
		} else if(!serviceId.equals("")) {
			updateResult = builderService.setModuleProperty(pageKey, instanceId, VIDEO_SERVICE_PROPERTY, new String[] {serviceId});
		} else {
			updateResult = builderService.setModuleProperty(pageKey, instanceId, VIDEO_ID_PROPERTY, new String[] {videoId});
		}
		return builderEngine.reRenderObject(pageKey, instanceId);
	}

	public VideoService getVideoService(String id) {
		if(services.keySet().size() == 0) {
			initialize();
		}
		return (VideoService) services.get(id);
	}
	
	public Map getVideoServices() {
		if(services.keySet().size() == 0) {
			initialize();
		}
		return services;
	}
	
	private void initialize() {
		try {
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			IWSlideService slide = (IWSlideService) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), IWSlideService.class);
			builderService = BuilderServiceFactory.getBuilderService(iwma.getIWApplicationContext());
			builderEngine = (BuilderEngine) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), BuilderEngine.class);
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(slide.getWebdavServerURL().toString() + SLIDE_CONFIG_LOCATION + SLIDE_CONFIG_FILENAME);
			Element root = doc.getRootElement();
		
			List serviceElements = root.getChildren("service");
			Iterator serviceIterator = serviceElements.iterator();
			while(serviceIterator.hasNext()) {
			   Element serviceElement = (Element)serviceIterator.next();
			   VideoService source = new VideoService();
			   String id = serviceElement.getChild("id").getTextTrim();
			   String index = serviceElement.getChild("index").getTextTrim();
			   String name = serviceElement.getChild("name").getTextTrim();
			   source.setId(id);
			   source.setIndex(Integer.parseInt(index));
			   source.setName(name);
			   
			   Element objectElement = (Element) serviceElement.getChild("object");
			   if(objectElement != null) {
				   Element objAttr = objectElement.getChild("attributes");
				   if(objAttr != null) {
					   List objectAttributes = objAttr.getChildren("attribute");
					   Iterator objAttrIterator = objectAttributes.iterator();
					   while(objAttrIterator.hasNext()) {
						   Element objectAttribute = (Element)objAttrIterator.next();
						   source.addObjectAttribute(objectAttribute.getAttributeValue("name"), objectAttribute.getTextTrim());
						   if(objectAttribute.getAttributeValue("embed") != null) {
							   if(objectAttribute.getAttributeValue("embed").equalsIgnoreCase("true")) {
								   source.addEmbedAttribute(objectAttribute.getAttributeValue("name"), objectAttribute.getTextTrim());
							   }
						   }
					   }
				   }
				   Element objPar = objectElement.getChild("parameters");
				   if(objPar != null) {
					   List objectParameters = objPar.getChildren("parameter");
					   Iterator objParIterator = objectParameters.iterator();
					   while(objParIterator.hasNext()) {
						   Element objectParameter = (Element)objParIterator.next();
						   source.addParameter(objectParameter.getAttributeValue("name"), objectParameter.getTextTrim());
						   if(objectParameter.getAttributeValue("embed") != null) {
							   if(objectParameter.getAttributeValue("embed").equalsIgnoreCase("true")) {
								   source.addEmbedAttribute(objectParameter.getAttributeValue("name"), objectParameter.getTextTrim());
							   }
						   }
						   if(objectParameter.getAttributeValue("id") != null) {
							   if(objectParameter.getAttributeValue("id").equalsIgnoreCase("true")) {
								   source.setObjectId(objectParameter.getAttributeValue("name"));
							   }
						   }
					   }
				   }
			   }
			   
			   Element embedElement = (Element) serviceElement.getChild("embed");
			   if(embedElement != null) {
				   Element embedAttr = embedElement.getChild("attributes");
				   if(embedAttr != null) {
					   List embedAttributes = embedAttr.getChildren("attribute");
					   Iterator embAttrIterator = embedAttributes.iterator();
					   while(embAttrIterator.hasNext()) {
						   Element embedAttribute = (Element)embAttrIterator.next();
						   source.addEmbedAttribute(embedAttribute.getAttributeValue("name"), embedAttribute.getTextTrim());
						   if(embedAttribute.getAttributeValue("id") != null) {
							   if(embedAttribute.getAttributeValue("id").equalsIgnoreCase("true")) {
								   source.setEmbedId(embedAttribute.getAttributeValue("name"));
							   }
						   }
					   }
				   }
			   }
			   
			   services.put(source.getId(), source);
			}
		} catch(JDOMException jde) {
			//TODO
		} catch(IBOLookupException ile) {
			//TODO
			throw new IBORuntimeException(ile);
		} catch(IOException ioe) {
			//TODO
		}
	}
}
