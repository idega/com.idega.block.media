package com.idega.block.media.business;

import com.idega.block.media.presentation.MediaUploaderWindow;
import com.idega.block.media.presentation.MediaViewer;
import com.idega.block.media.presentation.MediaTreeViewer;
import com.idega.block.media.presentation.MediaFolderEditorWindow;

import com.idega.presentation.text.Link;
/**
 * Title: com.idega.block.media.business.MediaConstants
 * Description: A handy class with shared constants (static final) used by all the Media classes
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaConstants {

  public static final String IW_BUNDLE_IDENTIFIER = "com.idega.block.media";

  public static final String TARGET_MEDIA_TREE = "tree";
  public static final String TARGET_MEDIA_VIEWER = "viewer";

  public static final String target3 = "buttons";

  public static final String MEDIA_ACTION_VIEW = "media_action_view";
  public static final String MEDIA_ACTION_SAVE = "media_action_save";
  public static final String MEDIA_ACTION_RELOAD = "media_action_reload";
  public static final String MEDIA_ACTION_DELETE = "media_action_delete";
  public static final String MEDIA_ACTION_DELETE_CONFIRM = "media_action_delete_confirm";
  public static final String MEDIA_ACTION_NEW = "media_action_new";
  public static final String MEDIA_ACTION_EDIT = "media_action_edit";

  public static final String MEDIA_ACTION_PARAMETER_NAME = "media_chooser_action";
  public static final String MEDIA_MIME_TYPE_PARAMETER_NAME = "media_mime_type";
  public static final String MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME = "media_mime_type_description";
  public static final String MEDIA_FILE_TYPE_PARAMETER_NAME = "media_file_type";
  public static final String MEDIA_FILE_TYPE_DESCRIPTION_PARAMETER_NAME = "media_file_type_description";
  public static final String FILE_IN_SESSION_PARAMETER_NAME = "media_file_in_session_name";
  public static final String MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME = "media_properties_in_session_name";
  public static final String MEDIA_FOLDER_NAME_PARAMETER_NAME = "media_folder_name";

  public static final String MEDIA_ID_IN_SESSION = "media_id_in_session";

  public static final int FILE_UPLOAD_MAXIMUM_SIZE = 20*1024*1024;//20MB

  public static final Class MEDIA_VIEWER_CLASS = MediaViewer.class;
  public static final Class MEDIA_UPLOADER_CLASS = MediaUploaderWindow.class;
  public static final Class MEDIA_TREE_VIEWER_CLASS = MediaTreeViewer.class;
  public static final Class MEDIA_FOLDER_EDITOR_CLASS = MediaFolderEditorWindow.class;

  public static final Link MEDIA_VIEWER_LINK = new Link(MEDIA_VIEWER_CLASS);
  public static final Link MEDIA_UPLOADER_LINK = new Link(MEDIA_UPLOADER_CLASS);
  public static final Link MEDIA_TREE_VIEWER_LINK = new Link(MEDIA_TREE_VIEWER_CLASS);
  public static final Link MEDIA_FOLDER_EDITOR_LINK = new Link(MEDIA_FOLDER_EDITOR_CLASS);

  public static final String SAVE_IMAGE_FUNCTION_NAME = "saveImageId()";

  public static String IMAGE_CHANGE_JS_FUNCTION = null;
  public static String IMAGE_SAVE_JS_FUNCTION = null;



  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }

}