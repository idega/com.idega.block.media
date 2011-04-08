package com.idega.block.media.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jcr.RepositoryException;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.idega.block.media.data.VideoService;
import com.idega.builder.bean.BuilderEngine;
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBORuntimeException;
import com.idega.business.IBOServiceBean;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.business.BuilderServiceFactory;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWMainApplication;
import com.idega.util.FileUtil;

public class VideoServicesBean extends IBOServiceBean implements VideoServices {

	private static final long serialVersionUID = 1660826296410278238L;

	private static final String SLIDE_CONFIG_LOCATION = "/files/cms/settings/";
	private static final String SLIDE_CONFIG_FILENAME = "video-services.xml";
	private static final String VIDEO_SERVICE_PROPERTY = ":method:1:implied:void:setServiceId:java.lang.String:";
	private static final String VIDEO_ID_PROPERTY = ":method:1:implied:void:setVideoId:java.lang.String:";

	private Map<String, VideoService> services = new HashMap<String, VideoService>();
	private BuilderService builderService;
	private BuilderEngine builderEngine;

	@Override
	public void addVideoService(VideoService service) {
		if (service != null) {
			services.put(service.getId(), service);
		}
		else {
			throw new IllegalArgumentException("Parameter cannot be null");
		}
	}

	@Override
	public Document setVideoProperties(String serviceId, String videoId, String instanceId, String pageURI) throws RemoteException {
		if (builderService == null || builderEngine == null) {
			initialize();
		}
		if (serviceId == null || videoId == null || instanceId == null) {
			return null;
		}

		String pageKey = builderService.getPageKeyByURI(pageURI);
		if (videoId.equals("") && serviceId.equals("")) {
			builderService.removeProperty(IWMainApplication.getDefaultIWMainApplication(), pageKey, instanceId, VIDEO_SERVICE_PROPERTY, new String[] {serviceId});
			builderService.removeProperty(IWMainApplication.getDefaultIWMainApplication(), pageKey, instanceId, VIDEO_ID_PROPERTY, new String[] {videoId});
		}
		else if (!serviceId.equals("")) {
			builderService.setModuleProperty(pageKey, instanceId, VIDEO_SERVICE_PROPERTY, new String[] {serviceId});
		}
		else {
			builderService.setModuleProperty(pageKey, instanceId, VIDEO_ID_PROPERTY, new String[] {videoId});
		}

		log(Level.FINEST, "Setting VideoViewer properties 1: " + serviceId + " : " + videoId);
		Document document = builderEngine.reRenderObject(pageKey, instanceId);
		log(Level.FINEST, "Setting VideoViewer properties 2: " + document);

		return document;
	}

	@Override
	public VideoService getVideoService(String id) {
		if(services.keySet().size() == 0) {
			initialize();
		}
		return services.get(id);
	}

	@Override
	public Map<String, VideoService> getVideoServices() {
		if(services.keySet().size() == 0) {
			initialize();
		}
		return services;
	}

	private void initialize() {
		try {
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			try {
				if (!getRepositoryService().getExistence(SLIDE_CONFIG_LOCATION + SLIDE_CONFIG_FILENAME)) {
					IWBundle iwb = iwma.getBundle(MediaConstants.IW_BUNDLE_IDENTIFIER);
					File file = FileUtil.getFileAndCreateIfNotExists(iwb.getResourcesRealPath() + "/settings/" + SLIDE_CONFIG_FILENAME);
					FileInputStream fis = new FileInputStream(file);

					getRepositoryService().uploadFile(SLIDE_CONFIG_LOCATION, SLIDE_CONFIG_FILENAME, "text/xml", fis);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}

			builderService = BuilderServiceFactory.getBuilderService(iwma.getIWApplicationContext());
			builderEngine = (BuilderEngine) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), BuilderEngine.class);
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(getRepositoryService().getWebdavServerURL().toString() + SLIDE_CONFIG_LOCATION + SLIDE_CONFIG_FILENAME);
			Element root = doc.getRootElement();

			List serviceElements = root.getChildren("service");
			Iterator serviceIterator = serviceElements.iterator();
			while(serviceIterator.hasNext()) {
			   Element serviceElement = (Element)serviceIterator.next();
			   VideoService source = new VideoService();
			   String id = serviceElement.getChild("id").getTextTrim();
			   String index = serviceElement.getChild("index").getTextTrim();
			   String name = serviceElement.getChild("name").getTextTrim();
			   String icon = serviceElement.getChild("icon").getTextTrim();
			   source.setId(id);
			   source.setIndex(Integer.parseInt(index));
			   source.setName(name);
			   source.setIconURL(icon);

			   Element objectElement = serviceElement.getChild("object");
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
								   source.setIdPattern(objectParameter.getTextTrim());
							   }
						   }
					   }
				   }
			   }

			   Element embedElement = serviceElement.getChild("embed");
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
								   source.setIdPattern(embedAttribute.getTextTrim());
							   }
						   }
					   }
				   }
			   }

			   services.put(source.getId(), source);
			}
		}
		catch(JDOMException jde) {
			log(jde);
		}
		catch(IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		}
		catch(IOException ioe) {
			log(ioe);
		}
	}
}