package com.idega.block.media.presentation;



import com.idega.block.media.business.FileTypeHandler;
import com.idega.block.media.business.MediaBusiness;
import com.idega.block.media.business.MediaConstants;
import com.idega.block.media.data.MediaProperties;
import com.idega.core.file.data.ICFile;
import com.idega.idegaweb.IWResourceBundle;
import com.idega.presentation.Block;
import com.idega.presentation.IWContext;
import com.idega.presentation.Table;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.AbstractChooserWindow;
import com.idega.util.CoreConstants;
import com.idega.util.caching.Cache;



/**

 *  Title: com.idega.block.media.presentation.MediaToolbar

 *  Description: The the media toolbar for selecting deleting or editing files<br>

 *  it can be extended by registering extra MediaToolbarItem(s) your bundle. The button<br>

 *  will get the icfile_id of the media from the db that is being viewed or a <br>

 *  MediaProperties object for an uploaded file.

 *  @todo move some action to mediaviewerwindow?

 *

 * @author     Eirikur S. Hrafnsson eiki@idega.is

 * @created    16. mars 2002

 * @version    1.0

 */



public class MediaToolbar extends Block {

  /*

   *  these are used for creating a chooser function that has a unique name for this chooser

   */

  private final static String ONCLICK_FUNCTION_NAME = "fileselect";

  private final static String FILE_ID_PARAMETER_NAME = "media_file_id";

  private final static String FILE_NAME_PARAMETER_NAME = "media_file_name";



  private String fileInSessionParameter = "";

  private MediaProperties props = null;

  private IWResourceBundle iwrb;



  private int mediaId = -1;

  private boolean choosingImage = false;





  /**

   *  Constructor for the MediaToolbar object

   */

  public MediaToolbar() { }



  /**

   *  Constructor for the MediaViewer object

   */

  public MediaToolbar(int mediaId) {

    this.mediaId = mediaId;

  }





  /**

   *  Constructor for the MediaToolbar object

   *

   * @param  props  MediaProperties

   */

  public MediaToolbar( MediaProperties props ) {

    this();

    this.props = props;

    this.mediaId = props.getId();

  }





  /**

   *  The main method were mediaid's and props get sent to the toolbaritems

   *

   * @param  iwc            The IWContext

   * @exception  Exception  A random exception ;)

   */

  @Override
public void main( IWContext iwc ) throws Exception {

    this.iwrb = getResourceBundle(iwc);

    //get the mediaId parameter name

    this.fileInSessionParameter = MediaBusiness.getMediaParameterNameInSession( iwc );

    String action = iwc.getParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME);



/**@ is this necessery**///    MediaBusiness.saveMediaIdToSession( iwc, mediaId );





    //use for filtering

    String chooserType = (String)iwc.getSessionAttribute(MediaConstants.MEDIA_CHOOSER_PARAMETER_NAME);

    this.choosingImage = ( (chooserType!=null) && (chooserType.equals(MediaConstants.MEDIA_CHOOSER_IMAGE)) );



    if(action==null) {
			action = "";
		}



//for extra toolbar items

//    List extension = (List)iwc.getApplicationAttribute(TOOLBAR_ITEMS);

//	if (extension != null) {

//	  Iterator it = extension.iterator();

//	  while (it.hasNext()) {

//	    IBToolbarButton b = (IBToolbarButton)it.next();

//	    xpos++;

//	    if (b.getIsSeparator())

//	      toolbarTable.add(separator, xpos, 1);

//	    else

//	      toolbarTable.add(b.getLink(), xpos, 1);

//	  }

//

    if( (this.mediaId==-1) && (this.props==null) ) {

      this.mediaId = Integer.valueOf(MediaBusiness.getMediaId(iwc));

    }



    if( this.mediaId!=-1 ) {



      if( action.equals( MediaConstants.MEDIA_ACTION_USE ) ){

        /*selecting and closing the window only reaches this state in the builder selecting images*/

        MediaBusiness.saveMediaIdToSession( iwc, String.valueOf(this.mediaId) );

        getParentPage().setOnLoad( "top.window.close()" );

      }

      else if( action.equals( MediaConstants.MEDIA_ACTION_DELETE ) ) {

        /*displaying confirm delete*/

        confirmDeleteMedia( this.mediaId, iwc );

        getParentPage().setOnUnLoad( "parent.frames['" + MediaConstants.TARGET_MEDIA_TREE + "'].location.reload()" );

      }

      else if( action.equals( MediaConstants.MEDIA_ACTION_DELETE_CONFIRM ) ) {

        /*deleting*/

        MediaBusiness.deleteMedia( this.mediaId );

        MediaBusiness.removeMediaIdFromSession( iwc );//not really necessary

        addBreak();

        add( new Text(this.iwrb.getLocalizedString("mv.file.deleted","The file was deleted")) );

      }
      else{

        /*viewing from db*/

        viewFileFromDB( iwc, this.mediaId );

      }



    }

    else if( this.props != null ) {

      viewFileFromDisk( iwc, this.props );

    }

  }









  /**

   *  Displays a confirm message

   *

   * @param  mediaId  The media to delete

   * @param  iwc      The IWContext

   */

  protected void confirmDeleteMedia( int id, IWContext iwc ) {
	ICFile tmp = FileTypeHandler.getFile(id);
	Cache cache = FileTypeHandler.getCachedFileInfo(iwc, tmp.getUniqueId(), tmp.getToken());

    ICFile file = ( ICFile ) cache.getEntity();

    Table T = new Table( 1, 3 );

    T.setAlignment( 1, 3, "center" );



    Link confirm = new Link( this.iwrb.getLocalizedString("mv.delete","delete")  );

    confirm.setAsImageButton( true );

    confirm.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE_CONFIRM );

    confirm.addParameter( this.fileInSessionParameter, this.mediaId );

    T.add( confirm, 1, 1 );



    Link cancel = MediaBusiness.getMediaViewerLink();

    cancel.setText(this.iwrb.getLocalizedString("mv.cancel","cancel") );

    cancel.setAsImageButton( true );

    cancel.addParameter( this.fileInSessionParameter, this.mediaId );

    T.add( cancel, 1, 1 );



    Text warning = new Text(this.iwrb.getLocalizedString("mv.file.are.you.sure","Are you sure you want to delete this file"));

    warning.setFontSize( 4 );

    warning.setFontColor( "FF0000" );

    warning.setBold();



    T.add( file.getName(), 1, 2 );

    T.add( Text.getBreak(), 1, 3 );

    T.add( warning, 1, 3 );



    add( T );

  }





  /**

   *  Finds the right filehandler and displays the media from disk

   *

   * @param  iwc            Description of the Parameter

   * @param  props          Description of the Parameter

   * @exception  Exception  Description of the Exception

   */

  protected void viewFileFromDisk( IWContext iwc, MediaProperties props ) {



    Link submitSave = new Link(this.iwrb.getLocalizedString("mv.save","save"));

    submitSave.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_SAVE);

    submitSave.setAsImageButton(true);

    //submitSave.addParameter(fileInSessionParameter,(String)props.getParameterMap().get(fileInSessionParameter));

    /**@todo fix to use iwc**/

    submitSave.addParameter(this.fileInSessionParameter,iwc.getParameter(this.fileInSessionParameter));

    Link submitNew = new Link(this.iwrb.getLocalizedString("mv.cancel","cancel"));

    submitNew.addParameter(MediaConstants.MEDIA_ACTION_PARAMETER_NAME,MediaConstants.MEDIA_ACTION_NEW);

    submitNew.addParameter(this.fileInSessionParameter,(String)props.getParameterMap().get(this.fileInSessionParameter));

    submitNew.setAsImageButton(true);



    add(submitNew);

    add(submitSave);

  }





  /**

   *  Finds a correct filehandler and displays the media

   *

   * @param  iwc      the IWContext

   * @param  mediaId  The media id

   */

  protected void viewFileFromDB( IWContext iwc, int mediaId ) {
	  ICFile tmp = FileTypeHandler.getFile(mediaId);
    Cache cache = FileTypeHandler.getCachedFileInfo(iwc, tmp.getUniqueId(), tmp.getToken());

    ICFile file = ( ICFile ) cache.getEntity();




    Table T = new Table( 1, 1 );

    T.setVerticalAlignment( 1, 1, Table.VERTICAL_ALIGN_TOP );

    T.setColumnAlignment( 1, Table.HORIZONTAL_ALIGN_LEFT );

    T.setHeight( 1, "15" );

    T.setCellpaddingAndCellspacing(0);



    getAssociatedScript().addFunction( ONCLICK_FUNCTION_NAME, "function " + ONCLICK_FUNCTION_NAME + "(" + FILE_NAME_PARAMETER_NAME + "," + FILE_ID_PARAMETER_NAME + "){ }" );

    getAssociatedScript().addToFunction( ONCLICK_FUNCTION_NAME, "top." + AbstractChooserWindow.SELECT_FUNCTION_NAME + "(" + FILE_NAME_PARAMETER_NAME + "," + FILE_ID_PARAMETER_NAME + ")" );



    Link use = MediaBusiness.getUseImageLink();

    use.setTextOnLink( this.iwrb.getLocalizedString("mv.use","use") );

    use.setAsImageButton( true );

    use.addParameter( this.fileInSessionParameter, mediaId );



    if(this.choosingImage){

      use.setOnClick( "top.window.opener.setImageId('" + file.getPrimaryKey() + "','" + this.fileInSessionParameter + "');");

    }

    else{

      use.setURL( CoreConstants.HASH );

      use.setOnClick( ONCLICK_FUNCTION_NAME + "('" + file.getName() + "','" + file.getPrimaryKey() + "');top.window.close()" );

    }



    T.add( use, 1, 1 );



    /**

     * @todo    use mediabusiness*

     */

    Link newLink = new Link(this.iwrb.getLocalizedString("mv.upload","upload"), MediaUploaderWindow.class );

    newLink.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );

    newLink.setAsImageButton( true );

    newLink.addParameter( this.fileInSessionParameter, mediaId );

    newLink.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_NEW );

    T.add( newLink, 1, 1 );



    Link delete = new Link(this.iwrb.getLocalizedString("mv.delete","delete"), MediaViewerWindow.class );

    delete.setTarget( MediaConstants.TARGET_MEDIA_VIEWER );

    delete.setAsImageButton( true );

    delete.addParameter( this.fileInSessionParameter, mediaId );

    delete.addParameter( MediaConstants.MEDIA_ACTION_PARAMETER_NAME, MediaConstants.MEDIA_ACTION_DELETE );

    T.add( delete, 1, 1 );



    if( MediaBusiness.isFolder( file ) ) {

      Link folder = MediaBusiness.getNewFolderLink();

      folder.setText(this.iwrb.getLocalizedString("mv.folder","folder"));

      folder.setAsImageButton( true );

      folder.addParameter( this.fileInSessionParameter, mediaId );

      T.add( folder, 1, 1 );

    }

    Link rename = MediaBusiness.getRenameFileLink();
	rename.setText(this.iwrb.getLocalizedString("mv.properties","properties"));
	rename.setAsImageButton(true);
	rename.addParameter(this.fileInSessionParameter,mediaId);
	T.add(rename,1,1);

	Link move = MediaBusiness.getMoveLink();
	move.setText(this.iwrb.getLocalizedString("mv.move","move"));
	move.setAsImageButton(true);
	move.addParameter(this.fileInSessionParameter,mediaId);
	T.add(move, 1, 1);

    add( T );





  }





  /**

   *  Gets the bundleIdentifier attribute of the MediaToolbar object

   *

   * @return    The bundleIdentifier value

   */

  @Override
public String getBundleIdentifier() {

    return MediaConstants.IW_BUNDLE_IDENTIFIER;

  }



}

