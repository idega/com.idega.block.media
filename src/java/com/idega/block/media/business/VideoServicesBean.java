package com.idega.block.media.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.jcr.RepositoryException;

import org.jdom2.Document;
import org.jdom2.Element;

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
import com.idega.util.xml.XmlUtil;

public class VideoServicesBean extends IBOServiceBean implements VideoServices {

	private static final long serialVersionUID = 1660826296410278238L;

	private static final String CONFIG_LOCATION = "/files/cms/settings/";
	private static final String CONFIG_FILENAME = "video-services.xml";
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
				if (!getRepositoryService().getExistence(CONFIG_LOCATION + CONFIG_FILENAME)) {
					IWBundle iwb = iwma.getBundle(MediaConstants.IW_BUNDLE_IDENTIFIER);
					File file = FileUtil.getFileAndCreateIfNotExists(iwb.getResourcesRealPath() + "/settings/" + CONFIG_FILENAME);
					FileInputStream fis = new FileInputStream(file);

					getRepositoryService().uploadFile(CONFIG_LOCATION, CONFIG_FILENAME, "text/xml", fis);
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}

			builderService = BuilderServiceFactory.getBuilderService(iwma.getIWApplicationContext());
			builderEngine = (BuilderEngine) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), BuilderEngine.class);
			InputStream stream = null;
			try {
			stream = getRepositoryService().getInputStreamAsRoot(
					getRepositoryService().getWebdavServerURL().toString() + CONFIG_LOCATION + CONFIG_FILENAME);
			} catch (Exception e) {
				log(Level.WARNING, "Error getting settings for video services", e);
			}
			Document doc = XmlUtil.getJDOMXMLDocument(stream);
			Element root = doc.getRootElement();

			List<Element> serviceElements = root.getChildren("service");
			for (Element serviceElement: serviceElements) {
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
					   List<Element> objectAttributes = objAttr.getChildren("attribute");
					   for (Element objectAttribute: objectAttributes) {
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
					   List<Element> objectParameters = objPar.getChildren("parameter");
					   for (Element objectParameter: objectParameters) {
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
					   List<Element> embedAttributes = embedAttr.getChildren("attribute");
					   for (Element embedAttribute: embedAttributes) {
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
		} catch(IBOLookupException ile) {
			throw new IBORuntimeException(ile);
		} catch(IOException ioe) {
			log(ioe);
		}
	}
}