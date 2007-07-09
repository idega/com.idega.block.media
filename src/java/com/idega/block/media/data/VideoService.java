package com.idega.block.media.data;

import java.util.HashMap;
import java.util.Map;

public class VideoService {
	
	private String id;
	private int index;
	private String name;
	private Map objectAttributes = new HashMap();
	private Map embedAttributes = new HashMap();
	private Map parameters = new HashMap();
	private String embedId;
	private String objectId;
	private String iconURL;
	private String idPattern;
	
	public String getIdPattern() {
		return idPattern;
	}
	public void setIdPattern(String idPattern) {
		this.idPattern = idPattern;
	}
	public String getIconURL() {
		return iconURL;
	}
	public void setIconURL(String iconURL) {
		this.iconURL = iconURL;
	}
	public String getEmbedId() {
		return embedId;
	}
	public void setEmbedId(String embedId) {
		this.embedId = embedId;
	}
	public String getObjectId() {
		return objectId;
	}
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map getEmbedAttributes() {
		return embedAttributes;
	}
	public void setEmbedAttributes(Map embedAttributes) {
		this.embedAttributes = embedAttributes;
	}
	public Map getObjectAttributes() {
		return objectAttributes;
	}
	public void setObjectAttributes(Map objectAttributes) {
		this.objectAttributes = objectAttributes;
	}
	public Map getParameters() {
		return parameters;
	}
	public void setParameters(Map parameters) {
		this.parameters = parameters;
	}
	public void addObjectAttribute(String key, String value) {
		objectAttributes.put(key, value);
	}
	public void addEmbedAttribute(String key, String value) {
		embedAttributes.put(key, value);
	}
	public void addParameter(String key, String value) {
		parameters.put(key, value);
	}
	public String getObjectAttribute(String key) {
		return (String) objectAttributes.get(key);
	}
	public String getEmbedAttribute(String key) {
		return (String) embedAttributes.get(key);
	}
	public String getParameter(String key) {
		return (String) parameters.get(key);
	}
}
