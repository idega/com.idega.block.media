package com.idega.block.media.presentation;

import com.idega.builder.business.IBImageInserter;

/**
 * <p>Title: idegaWeb</p>
 * <p>Description:
 * This class implements IBImageInserter by extending ImageInserter.
 * In order to avoid a reference to the builder bundle ImageInserter should not 
 * declare that it implements IBImageInserter.
 * In this way other classes from other bundles can use ImageInserter without being
 * dependent on the builder bundle (simplified dependencies).  
 *    
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: idega Software</p>
 * @author <a href="thomas@idega.is">Thomas Hilbig</a>
 * @version 1.0
 * Created on Jul 14, 2004
 */
public class IBImageInserterImpl extends ImageInserter implements 	IBImageInserter {
	// nothing to declare
}
