package com.idega.block.media;

import com.idega.block.media.presentation.FileChooser;
import com.idega.block.media.presentation.ImageInserter;
import com.idega.core.builder.business.BuilderFileChooser;
import com.idega.core.builder.business.BuilderImageInserter;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWBundleStartable;
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
public class IWBundleStarter implements IWBundleStartable {

	public void start(IWBundle starterBundle) {
		ImplementorRepository repository = ImplementorRepository.getInstance();
		repository.addImplementor(BuilderImageInserter.class, ImageInserter.class);
		repository.addImplementor(BuilderFileChooser.class, FileChooser.class);
	}
	
	public void stop(IWBundle starterBundle) {
		// nothing to do
	}
}
