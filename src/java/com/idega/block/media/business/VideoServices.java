package com.idega.block.media.business;


import java.rmi.RemoteException;
import java.util.Map;

import com.idega.block.media.data.VideoService;
import com.idega.business.IBOService;

public interface VideoServices extends IBOService {
	/**
	 * @see com.idega.block.video.business.VideoServicesBean#addVideoService
	 */
	public void addVideoService(VideoService service) throws RemoteException;

	/**
	 * @see com.idega.block.video.business.VideoServicesBean#getVideoService
	 */
	public VideoService getVideoService(String id) throws RemoteException;

	/**
	 * @see com.idega.block.media.business.VideoServicesBean#getVideoServices
	 */
	public Map getVideoServices() throws RemoteException;

	/**
	 * @see com.idega.block.media.business.VideoServicesBean#uploadConfigFile
	 */
	public void uploadConfigFile(String bundleId, String path) throws RemoteException;
}