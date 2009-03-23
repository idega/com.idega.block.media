package com.idega.block.media.business;


import java.util.Map;
import com.idega.business.IBOService;
import org.jdom.Document;
import java.rmi.RemoteException;
import com.idega.block.media.data.VideoService;

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
	public Map getVideoServices() throws RemoteException;
}