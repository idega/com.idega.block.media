
package com.idega.block.media.servlet;


/**
 * Title: MediaServlet
 * Description: A servlet for streaming data from the blob field of the ic_file table.
 * Copyright: Idega software Copyright (c) 2001
 * Company: idega
 * @author <a href = "mailto:eiki@idega.is">Eirdebikur Hrafnsson</a>
 * @version 1.0
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.database.ConnectionBroker;
import com.idega.idegaweb.IWMainApplication;
import com.idega.presentation.ui.Parameter;

public class MediaServlet extends IWCoreServlet{

public static final String PARAMETER_NAME = "media_id";
public static final String USES_OLD_TABLES = "IW_USES_OLD_MEDIA_TABLES";
private boolean usesOldTables = false;
private static IWMainApplication iwma;
public static boolean debug = false;


public static String getMediaURL(int iFileId){
    StringBuffer URIBuffer = new StringBuffer(com.idega.idegaweb.IWMainApplication.MEDIA_SERVLET_URL);
    URIBuffer.append(iFileId);
    URIBuffer.append("media?");
    URIBuffer.append(PARAMETER_NAME);
    URIBuffer.append("=");
    URIBuffer.append(iFileId);
    return URIBuffer.toString();
}

public static Parameter getParameter(int FileId){
	return new Parameter(PARAMETER_NAME,String.valueOf(FileId));
}

public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{
  doPost(_req,_res);
}

public void doPost( HttpServletRequest request, HttpServletResponse response) throws IOException{

  Connection conn = null;
  Statement Stmt = null;
  ResultSet RS;

  if( iwma == null ) iwma = IWMainApplication.getIWMainApplication(getServletContext());

  String mmProp = iwma.getSettings().getProperty(MediaServlet.USES_OLD_TABLES);
  if(mmProp!=null) {
    usesOldTables = true;
  }



  String contentType=null;
  String sql = "select file_value,mime_type from ic_file where ic_file_id=";
  String mediaId = request.getParameter(PARAMETER_NAME);

  /**@todo : remove temporary backward compatability when no longer needed
   *
   */
  if( mediaId == null){
     mediaId = request.getParameter("image_id");
     if( mediaId != null){
      //sql = "select image_value,content_type from image where image_id=";
      sql = "select image_value from image where image_id=";
     }
     else{
      mediaId = request.getParameter("file_id");
      if(mediaId!=null) sql = "select file_value,content_type from file_ where file_id=";
     }
  }
  else if( usesOldTables ){//special case for the Image object
    sql = "select image_value from image where image_id=";
  }
  //

  try{
    if( (mediaId!=null) && (!mediaId.equalsIgnoreCase("-1")) ){

        conn = ConnectionBroker.getConnection();

        if( conn!=null ){
					if(debug)
						System.out.println("Mediaservlet debug:"+sql+mediaId);

          Stmt = conn.createStatement();
          RS = Stmt.executeQuery(sql+mediaId);

          InputStream myInputStream = null;

         // while(RS.next()){
          if( (RS!=null) &&  (RS.next()) ){
						contentType = RS.getString(2);
						System.err.println("MediaServlet: contenttype: "+contentType);
            myInputStream = RS.getBinaryStream(1);

          }
          // debug
          if(myInputStream!=null){

            if (!RS.wasNull()){

							if(contentType!=null)
							  response.setContentType(contentType);

							/* // Using BytArrayOutputStream
							ByteArrayOutputStream baos = new ByteArrayOutputStream();

								// Read the entire contents of the file.
							while (myInputStream.available() > 0)
							{
									baos.write(myInputStream.read());
							}

							// write ByteArrayOutputStream to the ServletOutputStream
							response.setContentLength(baos.size());
							ServletOutputStream out = response.getOutputStream();
							baos.writeTo(out);
							out.flush();
							*/
							/// using DataOutputStream
							DataOutputStream output = new DataOutputStream( response.getOutputStream() );

              byte buffer[]= new byte[1024];
              int	noRead	= 0;

           // check something!!!

              noRead = myInputStream.read( buffer, 0, 1024 );

              //Write out the file to the browser
              while ( noRead != -1 ){
                output.write( buffer, 0, noRead );
                noRead = myInputStream.read( buffer, 0, 1024 );
              }

              output.flush();
              output.close();
							myInputStream.close();

              RS.close();

            }
            else System.err.println("MediaServlet: Was null");

          }
          else System.err.println("InputStream is null");
      }

  }


  }
  catch (Exception E) {
      E.printStackTrace(System.err);
  }
  finally{
    try{
     if(Stmt != null){
      Stmt.close();
      }
    }
    catch(SQLException ex){
      System.err.println("Exception in "+this.getClass().getName()+" streaming data to browser "+ex.getMessage());
      ex.printStackTrace(System.err);
    }
    if(conn!=null){
      ConnectionBroker.freeConnection(conn);
    }
  }


}//end service


}//end
