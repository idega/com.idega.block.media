/*
 * $Id: MediaFileSystemHomeImpl.java,v 1.2 2004/12/15 16:02:07 palli Exp $
 * Created on Dec 15, 2004
 *
 * Copyright (C) 2004 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.media.business;

import com.idega.business.IBOHomeImpl;


/**
 * 
 *  Last modified: $Date: 2004/12/15 16:02:07 $ by $Author: palli $
 * 
 * @author <a href="mailto:palli@idega.com">palli</a>
 * @version $Revision: 1.2 $
 */
public class MediaFileSystemHomeImpl extends IBOHomeImpl implements MediaFileSystemHome {

	protected Class getBeanInterfaceClass() {
		return MediaFileSystem.class;
	}

	public MediaFileSystem create() throws javax.ejb.CreateException {
		return (MediaFileSystem) super.createIBO();
	}
}
