package com.idega.block.media.presentation;

import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.core.data.ICFile;
import com.idega.data.EntityFinder;
import com.idega.idegaweb.IWCacheManager;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

/**
 * Title: com.idega.block.media.presentation.MediaTreeViewer
 * Description: The tree viewer for the ic_file table. it can be customized to show only certain file types or mime types
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaTreeViewer extends Block {

  private String fileInSessionParameter = "";
  private IWCacheManager cm;
  private IWResourceBundle iwrb;

  public void  main(IWContext iwc){
    iwrb = getResourceBundle(iwc);
    cm = iwc.getApplication().getIWCacheManager();
    fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);

    Table T = new Table(1,2);
    T.setWidth("100%");
    T.setCellpadding(2);
    T.setCellspacing(0);

    Link proto = new Link(MediaViewerWindow.class);
    proto.setTarget(MediaConstants.TARGET_MEDIA_VIEWER);
    ICFile rootNode = (ICFile)cm.getCachedEntity(ICFile.IC_ROOT_FOLDER_CACHE_KEY);

    ICFileTree tree = new ICFileTree();

    Iterator it = rootNode.getChildren();
    if(it!=null) tree.setFirstLevelNodes(it);

    tree.setNodeActionParameter(fileInSessionParameter);
    tree.setFileLinkPrototype(proto);

    tree.setFolderLinkPrototype(proto);


    //tree.setUI(tree._UI_MAC);

    T.add(tree,1,2);


    /**@todo: localize
    *
    */

    Link upload = MediaBusiness.getNewFileLink();
    upload.setText(iwrb.getLocalizedString("mv.upload","upload"));
    upload.setAsImageButton(true);
    add(upload);


    Link folder = MediaBusiness.getNewFolderLink();
    folder.setText(iwrb.getLocalizedString("mv.folder","folder"));
    folder.setAsImageButton(true);
    add(folder);

    add(T);

  }


  public Link getMediaLink(ICFile file,String target){
    Link L = new Link(file.getName(),MediaViewer.class);
    L.setFontSize(1);
    //L.setOnClick("top.iImageId = "+file.getID() );
    L.addParameter(fileInSessionParameter,file.getID());

    L.setTarget(target);
    return L;
  }

  public List listOfMedia(){
    List L = null;
    try {
      ICFile file = new ICFile();
      L = EntityFinder.findAllDescendingOrdered(file,file.getIDColumnName());
    }
    catch (SQLException ex) {
      L = null;
    }
    return L;
  }

  public Text formatText(String s){
    Text T= new Text();
    if(s!=null){
      T= new Text(s);

      T.setFontColor("#000000");
      T.setFontSize(1);
    }
    return T;
  }
  public Text formatText(int i){
    return formatText(String.valueOf(i));
  }

  public String getBundleIdentifier(){
    return MediaConstants.IW_BUNDLE_IDENTIFIER ;
  }

}