/*
 * Created on Oct 25, 2003
 *
 */
package com.idega.block.media.business;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;

import com.idega.block.media.data.MediaProperties;
import com.idega.block.media.presentation.MediaViewerWindow;
import com.idega.core.data.ICTreeNode;
import com.idega.core.file.data.ICFile;
import com.idega.data.IDOLookupException;
import com.idega.event.IWPageEventListener;
import com.idega.idegaweb.IWException;
import com.idega.presentation.IWContext;
import com.idega.presentation.PresentationObject;
import com.idega.presentation.Table;
import com.idega.presentation.text.Text;
import com.idega.presentation.ui.CheckBox;
import com.idega.presentation.ui.Form;
import com.idega.presentation.ui.HiddenInput;
import com.idega.presentation.ui.SubmitButton;
import com.idega.util.caching.Cache;

/**
 * ZipTypeHandler handles zip files, decompresses them to the database, creates directory structure
 * @author Aron Birkir aron@idega.is
 * @version 1.0
 */
public class ZipTypeHandler extends FileTypeHandler implements IWPageEventListener {
	/* (non-Javadoc)
	 * @see com.idega.block.media.business.FileTypeHandler#getPresentationObject(int, com.idega.presentation.IWContext)
	 */
	public PresentationObject getPresentationObject(int icFileId, IWContext iwc) {
		
		
		try {
			int id = -1;
			Cache cache =  this.getCachedFileInfo(icFileId,iwc);
			String filePath =cache.getRealPathToFile();
			
			ICFile file = (ICFile) cache.getEntity();
			 ICTreeNode parent = file.getParentNode();
			 if(parent!=null)
			 id = parent.getNodeID();
			
			return getZipFileContent(filePath,new Integer(id));
		}
		catch (ZipException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see com.idega.block.media.business.FileTypeHandler#getPresentationObject(com.idega.block.media.data.MediaProperties, com.idega.presentation.IWContext)
	 */
	public PresentationObject getPresentationObject(MediaProperties props, IWContext iwc) {
		try {
			int parentID = MediaBusiness.getMediaId(iwc);
			return getZipFileContent(props.getRealPath(),new Integer(parentID));
		}
		catch (ZipException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private PresentationObject getZipFileContent(String zipFilePath,Integer parentID) throws ZipException,IOException{
		ZipFile zipFile = new ZipFile(new File(zipFilePath));
		Form form = new Form(MediaViewerWindow.class);
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		Table table = new Table();
		Text fileName = new Text("Filename");
			fileName.setBold();
		Text fileSize = new Text("Filesize");
			fileSize.setBold();
		Text fileContentType = new Text("Content type");
			fileContentType.setBold();
		table.add(fileName,1,1);
		table.add(fileSize,2,1);
		table.add(fileContentType,3,1);
		int row = 2;
		Enumeration entries = zipFile.entries();
		String name,contentType;
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();
			name = entry.getName();
			table.add(name,1,row);
			table.add(String.valueOf(entry.getSize()),2,row);
			contentType  = fileNameMap.getContentTypeFor(name);				
			if(contentType!=null)
				table.add(contentType,3,row);
			else
				table.add("unknown , please update you resources",3,row);
						
			row++;
		}
		CheckBox createDirectoryStructure = new CheckBox("create_dirs","true");
		createDirectoryStructure.setChecked(true);
		SubmitButton uncompress = new SubmitButton("uncompress","Uncompress");
		uncompress.setToolTip("Files will be inflated to chosen directory");
		Table buttons = new Table();
		buttons.add(new Text("Create directory structure"),1,1);
		buttons.add(createDirectoryStructure,2,1);
		buttons.add(uncompress,3,1);
		
		form.add(Text.getBreak());
		form.add(buttons);
		form.add(table);
		
		form.setEventListener(this.getClass());
		form.add(new HiddenInput("zip_path",zipFilePath));
		form.add(new HiddenInput("parent_folder",parentID.toString()));
		
		
		
		
		return form;
		
	}
	/* (non-Javadoc)
	 * @see com.idega.event.IWPageEventListener#actionPerformed(com.idega.presentation.IWContext)
	 */
	public boolean actionPerformed(IWContext iwc) throws IWException {
		boolean createDirectoryStructure  = iwc.isParameterSet("create_dirs");
		String zipFilePath = iwc.getParameter("zip_path");
		Integer parentID = Integer.valueOf(iwc.getParameter("parent_folder"));
		int id = parentID.intValue();
		//System.out.println("Create directories "+createDirectoryStructure +" path: "+zipFilePath+" parent "+parentID);	
	
		
			try {
//				if no error occur we want to view the parent directory content
				if( iwc.getSessionAttribute( MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME )!=null)
				(( MediaProperties ) iwc.getSessionAttribute( MediaConstants.MEDIA_PROPERTIES_IN_SESSION_PARAMETER_NAME )).setId(id);
				MediaBusiness.saveMediaIdToSession(iwc,id);
				uncompressZipToDB(zipFilePath,createDirectoryStructure,parentID);
				
			}
			catch (ZipException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (CreateException e) {
				e.printStackTrace();
			}
		
		return false;
	}
	
	private void uncompressZipToDB(String zipFilePath,boolean createDirectories,Integer parentID)throws ZipException,IOException,CreateException{
		ZipFile zipFile = new ZipFile(new File(zipFilePath));
		Enumeration enumer = zipFile.entries();
		Map folderMap = new Hashtable();
		FileNameMap fileNameMap = URLConnection.getFileNameMap();
		String file,folder;
		while (enumer.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) enumer.nextElement();
			
			String name = entry.getName();
			int i = name.lastIndexOf("/");
			if(i>0){
				file = name.substring(i+1);
				folder = "/"+ name.substring(0,i);
			}
			else{
				file = name;
				folder = null;
			}
			//System.out.println("folder: "+folder+" file: "+file);
			int parentFolderID = parentID.intValue();
			if(file.length()>0){
				if(createDirectories && folder!=null ){
					if(!folderMap.containsKey(folder)){
				
						int parent = parentFolderID;
						StringTokenizer tokener = new StringTokenizer(folder,"/");
						String folderPath = "";
						while(tokener.hasMoreTokens()){
							String folderName = tokener.nextToken();
							folderPath += "/"+folderName;
							if(!folderMap.containsKey(folderPath)){
								parent = createSubFolder(folderName,parent);
								folderMap.put(folderPath,new Integer(parent));
							}
							else{
								parent = ((Integer)folderMap.get(folderPath)).intValue();
							}
						}
						parentFolderID = parent;
					}
					else{
						parentFolderID = ((Integer) folderMap.get(folder)).intValue();
						//System.out.println("map contains folder "+ folder +" id "+parentFolderID);
					}
				}
				
				ICFile zfile = ((com.idega.core.file.data.ICFileHome)com.idega.data.IDOLookup.getHome(ICFile.class)).create();
				zfile.setName(file);
				zfile.setFileSize((int)entry.getSize());
				zfile.setFileValue(zipFile.getInputStream(entry));
				String mimeType = fileNameMap.getContentTypeFor(file);
				if(mimeType == null)
					mimeType = "application/octet";
				//System.out.println("Save file "+file+ " of type "+mimeType+" under folder "+parentFolderID);
				zfile.setMimeType(mimeType);
				zfile = MediaBusiness.saveMediaToDB(zfile, parentFolderID);
			
			}
			
		}
	}
	
	private int createSubFolder(String name, int parent){
		try {
			ICFile folder = MediaBusiness.createSubFolder(parent,name);
			return Integer.valueOf(folder.getPrimaryKey().toString()).intValue();
		}
		catch (IDOLookupException e) {
			e.printStackTrace();
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		catch (EJBException e) {
			e.printStackTrace();
		}
		catch (FinderException e) {
			e.printStackTrace();
		}
		catch (CreateException e) {
			e.printStackTrace();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return parent;
	}

}
