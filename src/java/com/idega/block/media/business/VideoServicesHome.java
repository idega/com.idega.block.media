package com.idega.block.media.business;


import javax.ejb.CreateException;
import com.idega.business.IBOHome;
import java.rmi.RemoteException;

public interface VideoServicesHome extends IBOHome {
	public VideoServices create() throws CreateException, RemoteException;
}