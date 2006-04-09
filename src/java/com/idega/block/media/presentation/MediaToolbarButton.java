package com.idega.block.media.presentation;



import com.idega.block.media.business.MediaConstants;

import com.idega.builder.app.IBToolbarButton;

import com.idega.idegaweb.IWBundle;

import com.idega.presentation.Image;

import com.idega.presentation.text.Link;





/**

 * @author    <a href="mail:eiki@idega.is">Eirikur Hrafnsson</a>

 * @version   1.0

 */

public class MediaToolbarButton implements IBToolbarButton {



  private Link _link = null;

  private boolean _isSeparator = false;



  /**

   * Constructor for the MediaToolbarButton object

   *

   * @param iwb          The bundle

   * @param isSeparator  boolean is this a seperator

   */

  public MediaToolbarButton(IWBundle iwb, boolean isSeparator) {

    if (!isSeparator) {

      Image image = iwb.getImage("open.gif", "open1.gif", "Open my storage", 20, 20);

      image.setHorizontalSpacing(2);

      this._link = new Link(image);

      this._link.setWindowToOpen(MediaChooserWindow.class);

    }



    this._isSeparator = isSeparator;

  }



  /**

   * Gets the link attribute of the MediaToolbarButton object

   *

   * @return   The link value

   */

  public Link getLink() {

    return (this._link);

  }



  /**

   * Gets the isSeparator attribute of the MediaToolbarButton object

   *

   * @return   The isSeparator value

   */

  public boolean getIsSeparator() {

    return (this._isSeparator);

  }



  /**

   * @return   The bundleIdentifier value

   */

  public String getBundleIdentifier() {

    return MediaConstants.IW_BUNDLE_IDENTIFIER;

  }

}

