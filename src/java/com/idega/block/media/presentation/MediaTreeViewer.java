package com.idega.block.media.presentation;

import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.text.Link;
import com.idega.presentation.IWContext;
import com.idega.core.data.ICFile;
import com.idega.util.idegaTimestamp;
import com.idega.data.EntityFinder;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.business.MediaBusiness;
import java.sql.SQLException;
import java.util.List;

import com.idega.presentation.PresentationObjectContainer;

/**
 * Title: com.idega.block.media.presentation.MediaTreeViewer
 * Description: The tree viewer for the ic_file table. it can be customized to show only certain file types or mime types
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class MediaTreeViewer extends PresentationObjectContainer {

    private String fileInSessionParameter = "ic_file_id";

    public void  main(IWContext iwc){
      getParentPage().setAllMargins(0);
      List L = listOfMedia();

      fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession(iwc);

      if(L!= null){
        Table Frame = new Table();
          Frame.setWidth("100%");
        Frame.setCellpadding(0);
        Frame.setCellspacing(0);
        Table T = new Table();
          T.setWidth("100%");
        int len = L.size();
        int row = 1;
        T.add(formatText("Media Trainus"),1,row++);

        for (int i = 0; i < len; i++) {
          ICFile file = (ICFile) L.get(i);
          T.add(getMediaLink(file,MediaConstants.TARGET_MEDIA_VIEWER),1,row);
          /**@todo: localize
           *
           */
          T.add(formatText(new idegaTimestamp(file.getCreationDate() ).getISLDate()),2,row);
          row++;
        }
        T.setCellpadding(2);
        T.setCellspacing(0);

        T.setHorizontalZebraColored("#CBCFD3","#ECEEF0");
        Frame.add(T,1,1);
        add(Frame);
      }
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