package com.idega.block.media.business;

/**
 * Title: com.idega.block.media.business.MediaConstants
 * Description: A handy class with shared constants (static final) used by all the Media classes
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaConstants {
  public static final String TARGET_MEDIA_TREE = "tree";
  public static final String TARGET_MEDIA_VIEWER = "viewer";

  public static final String target3 = "buttons";

  public static final String MEDIA_ACTION_VIEW = "media_action_view";
  public static final String MEDIA_ACTION_SAVE = "media_action_save";
  public static final String MEDIA_ACTION_DELETE = "media_action_delete";
  public static final String MEDIA_ACTION_DELETE_CONFIRM = "media_action_delete_confirm";


  public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";
  public static final String MEDIA_ACTION_PARAMETER_NAME = "media_chooser_action";
  public static final String FILE_IN_SESSION_PARAMETER_NAME = "media_file_in_session_name";
  public static final String MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME = "media_properties_in_session_name";
  public static final int FILE_UPLOAD_MAXIMUM_SIZE = 10*1024*1024;//10MB


  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }

}