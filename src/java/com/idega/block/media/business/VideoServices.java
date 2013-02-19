package com.idega.block.media.business;


import java.rmi.RemoteException;
import java.util.Map;

import org.jdom2.Document;

import com.idega.block.media.data.VideoService;
import com.idega.business.IBOService;

public interface VideoServices extends IBOService {
	/**
	 * @see com.idega.block.media.business.VideoServicesBean#addVideoService
	 */
	public void addVideoService(VideoService service) throws RemoteException;

	/**
	 * @see com.idega.block.media.business.VideoServicesBean#setVideoProperties
	 */
	public Document setVideoProperties(String serviceId, String videoId, String instanceId, String pageURI) throws RemoteException, RemoteException;

	/**
	 * @see com.idega.block.media.business.VideoServicesBean#getVideoService
	 */
	public VideoService getVideoService(String id) throws RemoteException;

	/**
	 * @see com.idega.block.media.business.VideoServicesBean#getVideoServices
	 */
	public Map<String, VideoService> getVideoServices() throws RemoteException;
}