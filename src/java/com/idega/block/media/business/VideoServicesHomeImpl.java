package com.idega.block.media.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHomeImpl;

public class VideoServicesHomeImpl extends IBOHomeImpl implements VideoServicesHome {
	@Override
	public Class<VideoServices> getBeanInterfaceClass() {
		return VideoServices.class;
	}

	public VideoServices create() throws CreateException {
		return (VideoServices) super.createIBO();
	}
}