
package com.idega.block.media.servlet;


/**
 * Title: MediaServlet
 * Description: A servlet for streaming data from the blob field of the ic_file table.
 * Copyright: Idega software Copyright (c) 2001
 * Company: idega
 * @author <a href = "mailto:eiki@idega.is">Eirikur Hrafnsson</a>
 * @version 1.0
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.database.ConnectionBroker;

public class MediaServlet extends IWCoreServlet{

public static final String PARAMETER_NAME = "media_id";
public static final String USES_OLD_TABLES = "IW_USES_OLD_MEDIA_TABLES";


public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{
  doPost(_req,_res);
}

public void doPost( HttpServletRequest request, HttpServletResponse response) throws IOException{

  Connection conn = null;
  Statement Stmt = null;
  ResultSet RS;

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
  //

  try{
    if( (mediaId!=null) && (!mediaId.equalsIgnoreCase("-1")) ){

        conn = ConnectionBroker.getConnection();

        if( conn!=null ){
          Stmt = conn.createStatement();

          RS = Stmt.executeQuery(sql+mediaId);
System.err.println("Mediaservlet debug:"+sql+mediaId);
          InputStream myInputStream = null;

         // while(RS.next()){
          if( (RS!=null) &&  (RS.next()) ){
            myInputStream = RS.getBinaryStream(1);
          // debug
           // contentType = RS.getString(2);
          }

          // debug
          //response.setContentType(contentType);

          if(myInputStream!=null){

    //      System.err.println("FileSize: "+myInputStream.available());

            if (!RS.wasNull()){
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
             // myInputStream.close();
            }
            else System.err.println("MediaServlet: Was null");

          }
          else System.err.println("InputStream is null");

          RS.close();

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
