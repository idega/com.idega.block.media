package com.idega.block.media.business;


import com.idega.block.media.data.MediaProperties;
import com.idega.block.reports.business.Content;
import com.idega.core.file.data.ICFile;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import java.util.Iterator;


/**
 * Title: com.idega.block.media.business.SystemTypeHandler
 * Description: A type handler that handles idegaWeb system type files such as folders ( The Finder ;)
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Eirikur S. Hrafnsson eiki@idega.is
 * @version 1.0
 */

public class SystemTypeHandler extends FileTypeHandler {

public static String[] LIST_VIEW_HEADERS = {"Select","Name","Date modified","Size","Mimetype"};//**@todo localize**/


  public PresentationObject getPresentationObject(int icFileId, IWContext iwc){
    //ContentViewer listView = null;
    //try {
      Table table = new Table();
     // table.setColor("#ECECEC");
      table.setColor(MediaConstants.MEDIA_VIEWER_BACKGROUND_COLOR);
      table.setWidth(Table.HUNDRED_PERCENT);
      table.setHeight(Table.HUNDRED_PERCENT);
      table.setCellpadding(2);
      table.setCellspacing(0);

      ICFile file = (ICFile)this.getCachedFileInfo(icFileId,iwc).getEntity();

      Iterator iter = file.getChildren();
      int x = 1;
      int y = 1;

      Text proto = new Text();
      proto.setFontSize(Text.FONT_SIZE_7_HTML_1);
      proto.setFontFace(Text.FONT_FACE_VERDANA);


      Text name = new Text("Name");
      name.setBold(true);
      name.setFontSize(Text.FONT_SIZE_10_HTML_2);
      Text date = new Text("Modified date");
      date.setBold(true);
      date.setFontSize(Text.FONT_SIZE_10_HTML_2);
      Text size = new Text("Size");
      size.setBold(true);
      size.setFontSize(Text.FONT_SIZE_10_HTML_2);
      Text mime = new Text("Mime type");
      mime.setBold(true);
      mime.setFontSize(Text.FONT_SIZE_10_HTML_2);

      table.add(name,1,1);
      table.add(date,2,1);
      table.add(size,3,1);
      table.add(mime,4,1);

      table.setHeight(1,"15");


      if( iter != null ){
        while (iter.hasNext()) {
          ++y;
          ICFile item = (ICFile) iter.next();
          //table.add(new CheckBox(Integer.toString(item.getID())),x++,y);
          Link view = MediaBusiness.getMediaViewerLink();
          view.setText(((item.getName() != null ) ? item.getName() : ""));
          view.addParameter(MediaBusiness.getMediaParameterNameInSession(iwc),item.getPrimaryKey().toString());
          table.add(view,x++,y);
          table.add( ((item.getModificationDate() != null ) ? item.getModificationDate().toString() : item.getCreationDate().toString()),x++,y);
          table.add( ((item.getFileSize() != null ) ? item.getFileSize().toString() : ""),x++,y);
          table.add( ((item.getMimeType() != null ) ? item.getMimeType() : ""),x++,y);
          table.setRowVerticalAlignment(y,Table.VERTICAL_ALIGN_TOP);
          table.setHeight(y,"15");
          x=1;
        }
      }

      table.add(Text.NON_BREAKING_SPACE,1,++y);
      table.setHeight(y,Table.HUNDRED_PERCENT);

     // table.setColumnColor(2,"#FCFCFC");
      //table.setColumnColor(4,"#FCFCFC");
      table.setAlignment(Table.HORIZONTAL_ALIGN_LEFT);





      /*Vector V = new Vector();

      if(!MediaBusiness.isFolder(file)) V.add(getContentObject(file));

      Iterator iter = file.getChildren();
      int i = 0;
      if( iter != null ){
        while (iter.hasNext()) {
          i++;
          ICFile item = (ICFile) iter.next();
          V.add(getContentObject(item));
        }
      }

      listView = new ContentViewer(LIST_VIEW_HEADERS,V);
      if( i>0 ) listView.setDisplayNumber(i);
      listView.setAllowOrder(true);

      listView.setWidth("100%");





    }
    catch (Exception ex) {
      ex.printStackTrace(System.err);
    }
    return listView;
*/
return table;
  }

  public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc){
    return new Table();
  }

  private Content getContentObject(ICFile item){
    Object[] objs = new Object[5];
    objs[0] = new CheckBox(item.getPrimaryKey().toString());
    objs[1] = (item.getName() != null ) ? item.getName() : "";
    objs[2] = (item.getModificationDate() != null ) ? item.getModificationDate().toString() : item.getCreationDate().toString();
    objs[3] = (item.getFileSize() != null ) ? item.getFileSize().toString() : "";
    objs[4] = (item.getMimeType() != null ) ? item.getMimeType() : "";

    Content C = new Content(objs);

    return C;
  }

}
