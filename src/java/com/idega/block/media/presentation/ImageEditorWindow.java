package com.idega.block.media.presentation;

import com.idega.jmodule.object.interfaceobject.Window;

/**
 * Title:        IdegaWeb
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      Idega Software
 * @author Eirikur S. Hrafnsson
 * @version 1.0
 */

public class ImageEditorWindow extends Window {

  public ImageEditorWindow() {
    ImageEditor editor = new ImageEditor();
    add(editor);


    setWidth(800);
    setHeight(600);
    setAllMargins(0);
  }
}