package com.idega.block.media.servlet;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.idegaweb.IWMainApplication;
import com.idega.util.database.ConnectionBroker;


/**
 * Title: com.idega.block.media.business.MediaOutputWriter
 * Description: Writes media from database to outputstream
 * Copyright:    Copyright (c) 2001
 * Company:      idega software
 * @author Aron Birkir aron@idega.is
 * @version 1.0
 */

public class MediaOutputWriter {


  public void doPost(HttpServletRequest request, HttpServletResponse response,IWMainApplication iwma) throws IOException{

    Connection conn = null;
    Statement Stmt = null;
    ResultSet RS = null;

    String mmProp = iwma.getSettings().getProperty(MediaServlet.USES_OLD_TABLES);
    boolean usesOldTables = false;
    if(mmProp!=null) {
      usesOldTables = true;
    }

    String contentType=null;
    String sql = "select file_value,mime_type from ic_file where ic_file_id=";
    String mediaId = request.getParameter(MediaServlet.PARAMETER_NAME);

    /**
     *  @todo : remove temporary backward compatability when no longer needed
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
  try{
    if( (mediaId!=null) && (!mediaId.equalsIgnoreCase("-1")) ){
      conn = ConnectionBroker.getConnection();
      if( conn!=null ){
        Stmt = conn.createStatement();
        RS = Stmt.executeQuery(sql+mediaId);

        InputStream myInputStream = null;
        if( (RS!=null) &&  (RS.next()) ){
          contentType = RS.getString(2);
          myInputStream = RS.getBinaryStream(1);

        }
        // debug
        if(myInputStream!=null){

          if (!RS.wasNull()){
            if(contentType!=null)
              response.setContentType(contentType);
            DataOutputStream output = new DataOutputStream(response.getOutputStream() );
            byte buffer[]= new byte[1024];
            int	noRead	= 0;
            noRead = myInputStream.read( buffer, 0, 1024 );
            //Write out the file to the browser
            while ( noRead != -1 ){
              output.write( buffer, 0, noRead );
              noRead = myInputStream.read( buffer, 0, 1024 );
            }
            output.flush();
            output.close();
            myInputStream.close();
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
	// do not hide an existing exception
	try { 
		if (RS != null) {
			RS.close();
      	}
	}
    catch (SQLException resultCloseEx) {
     	System.err.println("MediaOutputWriter] result set could not be closed");
     	resultCloseEx.printStackTrace(System.err);
    }
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


}
