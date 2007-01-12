package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.idegaweb.IWBundle;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Window;

/**
 * Title: com.idega.block.media.presentation.MediaViewerWindow
 * Description: A simple window container for the MediaViewer
 * Copyright:    Copyright (c) 2002
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaViewerWindow extends Window{

private IWBundle iwb;
private IWResourceBundle iwrb;

  public MediaViewerWindow(){
    setBackgroundColor( MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR );
    setAllMargins( 0 );
  }


  public void main(IWContext iwc) throws Exception{
  	//System.out.println("MEDIA param = "+MediaBusiness.getMediaParameterNameInSession(iwc));
    int mediaId = MediaBusiness.getMediaId(iwc);
    
    //System.out.println("MEDIA ID = "+mediaId);
    
    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);
    if(action==null) {
		action = "";
	}

    add(new MediaToolbar(mediaId));
    if( !action.equals(MediaConstants.MEDIA_ACTION_USE) && !action.equals(MediaConstants.MEDIA_ACTION_DELETE) && !action.equals(MediaConstants.MEDIA_ACTION_DELETE_CONFIRM) ){
      add(new MediaViewer(mediaId));
    }
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }
}
