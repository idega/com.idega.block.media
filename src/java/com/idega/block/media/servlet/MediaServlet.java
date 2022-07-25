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
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.FactoryFinder;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.faces.webapp.FacesServlet;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import com.idega.core.file.business.FileSystemConstants;
import com.idega.idegaweb.IWMainApplication;
import com.idega.io.MediaWritable;
import com.idega.io.MemoryFileBufferWriter;
import com.idega.presentation.IWContext;
import com.idega.presentation.ui.Parameter;
import com.idega.repository.data.RefactorClassRegistry;
import com.idega.servlet.IWCoreServlet;
import com.idega.util.CoreConstants;

@SuppressWarnings("deprecation")
public class MediaServlet extends IWCoreServlet implements Servlet {

	private static final long serialVersionUID = 6029625854933532862L;

	public static final String PARAMETER_NAME = FileSystemConstants.PARAM_FILE_ID;
	public static final String USES_OLD_TABLES = "IW_USES_OLD_MEDIA_TABLES";
	public static final String PRM_WRITABLE_CLASS = "wrcls";
	public static final String PRM_SESSION_MEMORY_BUFFER = MemoryFileBufferWriter.PRM_SESSION_BUFFER;

	public static boolean debug = false;

	private static IWMainApplication iwma;

	private ServletConfig servletConfig = null;
	private FacesContextFactory facesContextFactory = null;
	private Lifecycle lifecycle = null;

	public static Parameter getParameter(int FileId){
		return new Parameter(PARAMETER_NAME, String.valueOf(FileId));
	}

	@Override
  	public void init(ServletConfig servletConfig)	throws ServletException {
  		this.servletConfig = servletConfig;

  		facesContextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
  		LifecycleFactory lifecycleFactory = (LifecycleFactory)FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
  		lifecycle = lifecycleFactory.getLifecycle(getLifecycleId());
  	}

  	private String getLifecycleId() {
  		String lifecycleId = servletConfig.getServletContext().getInitParameter(FacesServlet.LIFECYCLE_ID_ATTR);
  		return lifecycleId != null ? lifecycleId : LifecycleFactory.DEFAULT_LIFECYCLE;
    }

  	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
	    if (iwma == null) {
			iwma = IWMainApplication.getIWMainApplication(servletConfig.getServletContext());
		}

	    if (request.getParameter(PARAMETER_NAME) != null || request.getParameter("image_id") != null) {
	    	new MediaOutputWriter().doPost(request, response, iwma);

	    } else if (request.getParameter(PRM_SESSION_MEMORY_BUFFER) != null) {
	    	new MemoryFileBufferWriter().doPost(request, response);

	    } else if (request.getParameter(MediaWritable.PRM_WRITABLE_CLASS) != null || request.getParameter("amp;" + MediaWritable.PRM_WRITABLE_CLASS) != null) {
	    	IWContext iwc = null;
	    	try {
		    	FacesContext facesContext = facesContextFactory.getFacesContext(servletConfig.getServletContext(), request, response, lifecycle);
	    		iwc = IWContext.getIWContext(facesContext);
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    	}

	    	MediaWritable mw = null;
	    	try {
	    		if (iwc == null) {
	    			iwc = new IWContext(request, response, servletConfig.getServletContext());
	    		}

	    		String mediaWriter = request.getParameter(MediaWritable.PRM_WRITABLE_CLASS);
	    		if (StringUtils.isBlank(mediaWriter)) {
	    			mediaWriter = request.getParameter("amp;" + MediaWritable.PRM_WRITABLE_CLASS);
	    		}
	    		mw = (MediaWritable) RefactorClassRegistry.forName(IWMainApplication.decryptClassName(mediaWriter)).newInstance();
		        mw.init(request, iwc);
	    		response.setContentType(mw.getMimeType());
		        ServletOutputStream out = response.getOutputStream();
		        mw.writeTo(out);
		        out.flush();
		    } catch(Exception ex) {
		    	Logger.getLogger(getClass().getName()).log(
		    			Level.WARNING,
		    			"Error getting or writing media" + (mw == null ? CoreConstants.EMPTY : " from " + mw.getClass().getName()),
		    			ex
		    	);
	    	}
	    }
	}
}
