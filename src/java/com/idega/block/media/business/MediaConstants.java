package com.idega.block.media.business;

import com.idega.block.media.presentation.MediaFolderEditorWindow;
import com.idega.block.media.presentation.MediaTreeViewer;
import com.idega.block.media.presentation.MediaUploaderWindow;
import com.idega.block.media.presentation.MediaViewerWindow;
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

  public static final String MEDIA_ID = "me_id";

  public static final String TARGET_MEDIA_TREE = "tree";
  public static final String TARGET_MEDIA_VIEWER = "viewer";

  public static final String MEDIA_CHOOSER_PARAMETER_NAME = "me_ch";
  public static final String MEDIA_CHOOSER_IMAGE = "me_im_ch";
  public static final String MEDIA_CHOOSER_FILE = "me_f_ch";
  public static final String MEDIA_CHOOSER_FOLDER = "me_ch_fol";
  
  public static final String MEDIA_CHOOSER_FOLDER_CHOOSER_NAME = "me_fol_ch_name";


  public static final String MEDIA_ACTION_VIEW = "me_act_v";
  public static final String MEDIA_ACTION_FOLDER_SAVE = "me_act_fs";
  public static final String MEDIA_ACTION_SAVE = "me_act_s";
  public static final String MEDIA_ACTION_RELOAD = "me_act_r";
  public static final String MEDIA_ACTION_DELETE = "me_act_d";
  public static final String MEDIA_ACTION_DELETE_CONFIRM = "me_act_d_c";
  public static final String MEDIA_ACTION_NEW = "me_act_n";
  public static final String MEDIA_ACTION_EDIT = "me_act_e";
  public static final String MEDIA_ACTION_USE = "me_act_u";
  public static final String MEDIA_ACTION_RENAME = "me_act_rn";
  public static final String MEDIA_ACTION_MOVE = "me_act_mv";
  public static final String MEDIA_ACTION_SAVE_MOVE = "me_act_save_mv";


  public static final String MEDIA_ACTION_PARAMETER_NAME = "me_cho_act";
  public static final String MEDIA_MIME_TYPE_PARAMETER_NAME = "me_mime_type";
  public static final String MEDIA_MIME_TYPE_DESCRIPTION_PARAMETER_NAME = "me_mime_type_desc";
  public static final String MEDIA_FILE_TYPE_PARAMETER_NAME = "me_file_type";
  public static final String MEDIA_FILE_TYPE_DESCRIPTION_PARAMETER_NAME = "me_file_type_desc";

  public static final String MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME = "me_prop_in_sess_n";
  public static final String FILE_IN_SESSION_PARAMETER_NAME = "me_file_in_sess_n";
  public static final String MEDIA_FOLDER_NAME_PARAMETER_NAME = "me_fol_n";

  public static final String MEDIA_ID_IN_SESSION = "me_id_in_sess";

  //public static final String MEDIA_VIEWER_BACKGROUND_COLOR = "#E4E0D8";
  public static final String MEDIA_VIEWER_BACKGROUND_COLOR = "#ECECEC";
  public static final String MEDIA_TREE_VIEWER_BACKGROUND_COLOR = "#B0B29D";

  public static final int FILE_UPLOAD_MAXIMUM_SIZE = 20*1024*1024;//20MB

  public static final Class MEDIA_VIEWER_CLASS = MediaViewerWindow.class;
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
