package com.idega.block.media;

import com.idega.block.media.business.MediaBundleStarter;
import com.idega.block.media.business.MediaFileSystemBean;
import com.idega.block.media.presentation.FileChooser;
import com.idega.block.media.presentation.IBImageInserterImpl;
import com.idega.builder.business.IBFileChooser;
import com.idega.builder.business.IBImageInserter;
import com.idega.business.IBOLookup;
import com.idega.core.file.business.ICFileSystem;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
import com.idega.idegaweb.include.GlobalIncludeManager;
import com.idega.repository.data.ImplementorRepository;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jun 10, 2004
 */
public class IWBundleStarter extends MediaBundleStarter implements IWBundleStartable {
	
	public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";

	public void start(IWBundle starterBundle) {
		super.start(starterBundle);
		// implementors
		ImplementorRepository repository = ImplementorRepository.getInstance();
		repository.addImplementor(IBImageInserter.class, IBImageInserterImpl.class);
		repository.addImplementor(IBFileChooser.class, FileChooser.class);
		
		// services
		IBOLookup.registerImplementationForBean(ICFileSystem.class, MediaFileSystemBean.class);
		GlobalIncludeManager.getInstance().addBundleStyleSheet(IW_BUNDLE_IDENTIFIER, "/style/media.css");
	}
	
	public void stop(IWBundle starterBundle) {
		super.stop(starterBundle);
		// nothing to do
	}
}
