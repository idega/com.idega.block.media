
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

public void doGet( HttpServletRequest _req, HttpServletResponse _res) throws IOException{
  doPost(_req,_res);
}

public void doPost( HttpServletRequest request, HttpServletResponse response) throws IOException{

  Connection conn = null;
  Statement Stmt = null;
  ResultSet RS;

  String contentType=null;
  String mediaId = request.getParameter("media_id");

  try{
    if( mediaId!=null){

        conn = ConnectionBroker.getConnection();

        if( conn!=null ){
          Stmt = conn.createStatement();

          RS = Stmt.executeQuery("select file_value,mime_type from ic_file where ic_file_id='"+mediaId+"'");

          InputStream myInputStream = null;

          while(RS.next()){
            contentType = RS.getString("mime_type");
            myInputStream = RS.getBinaryStream("file_value");
          }



          response.setContentType(contentType);

          if(myInputStream!=null){
            DataOutputStream output = new DataOutputStream( response.getOutputStream() );

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

    ConnectionBroker.freeConnection(conn);
  }


}//end service


}//end
