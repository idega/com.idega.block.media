package com.idega.block.media.business;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
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
import com.idega.business.IBOLookup;
import com.idega.business.IBOLookupException;
import com.idega.business.IBOServiceBean;
import com.idega.idegaweb.IWMainApplication;
import com.idega.slide.business.IWSlideService;
import com.idega.util.bundles.BundleResourceResolver;

public class VideoServicesBean extends IBOServiceBean implements VideoServices {
	
	private static final String SLIDE_CONFIG_LOCATION = "/files/cms/settings/";
	private static final String SLIDE_CONFIG_FILENAME = "video-services.xml";
	
	private Map services = new HashMap();

	public void addVideoService(VideoService service) {
		if(service != null) {
			services.put(service.getId(), service);
		} else {
			throw new IllegalArgumentException("Parameter cannot be null");
		}
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
	
	public void uploadConfigFile(String bundleId, String path) {
		IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
		try {
			IWSlideService slide = (IWSlideService) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), IWSlideService.class);
			String config_uri_string = 
				new StringBuffer("bundle://")
				.append(bundleId)
				.append(path)
				.toString();
			
			BundleResourceResolver resolver = new BundleResourceResolver(iwma);
			URI config_uri = URI.create(config_uri_string);
			
			InputStream resource = resolver.resolve(config_uri).getInputStream();
			slide.uploadFileAndCreateFoldersFromStringAsRoot(SLIDE_CONFIG_LOCATION, SLIDE_CONFIG_FILENAME, resource, null, true);
			resource.close();
			
			initialize();
		} catch (IBOLookupException ile) {
			//TODO
//			throw new IBORuntimeException(ile);
		} catch(RemoteException re) {
			//TODO handling
		} catch(IOException ioe) {
			//TODO handling
		}
	}
	
	private void initialize() {
		try {
			IWMainApplication iwma = IWMainApplication.getDefaultIWMainApplication();
			IWSlideService slide = (IWSlideService) IBOLookup.getServiceInstance(iwma.getIWApplicationContext(), IWSlideService.class);
			
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(slide.getWebdavServerURL().toString() + SLIDE_CONFIG_LOCATION + SLIDE_CONFIG_FILENAME);
			Element root = doc.getRootElement();
		
			List serviceElements = root.getChildren("service");
			Iterator serviceIterator = serviceElements.iterator();
			while(serviceIterator.hasNext()) {
			//for (int i = 0; i < serviceElements.size(); i++) {
			   Element serviceElement = (Element)serviceIterator.next();
			   //System.out.println("Service: " + serviceElement.toString());
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
					   //System.out.println("Object attributes: " + objectAttributes.size());
					   Iterator objAttrIterator = objectAttributes.iterator();
					   while(objAttrIterator.hasNext()) {
						   Element objectAttribute = (Element)objAttrIterator.next();
//						   System.out.println(objectAttribute.getAttributeValue("name"));
//						   System.out.println(objectAttribute.getTextTrim());
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
//					   System.out.println("Object parameters: " + objectParameters.size());
					   Iterator objParIterator = objectParameters.iterator();
					   while(objParIterator.hasNext()) {
						   Element objectParameter = (Element)objParIterator.next();
//						   System.out.println(objectParameter.getAttributeValue("name"));
//						   System.out.println(objectParameter.getTextTrim());
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
//					   System.out.println("Embed parameters: " + embedAttributes.size());
					   Iterator embAttrIterator = embedAttributes.iterator();
					   while(embAttrIterator.hasNext()) {
						   Element embedAttribute = (Element)embAttrIterator.next();
//						   System.out.println(embedAttribute.getAttributeValue("name"));
//						   System.out.println(embedAttribute.getTextTrim());
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
		} catch(IOException ioe) {
			//TODO
		}
	}
}
