package com.spatialanalytics.servlet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileCleaningTracker;
import org.json.JSONObject;
import javax.servlet.ServletConfig;
import com.spatialanalytics.config.ConstantConfig;
import com.spatialanalytics.config.URLConfig;
import com.spatialanalytics.model.DBController;
import com.spatialanalytics.model.MyExceptionHandler;

/**
 * Servlet implementation class UploadServlet
 * handle image upload request
 */
public class UploadImageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * File upload set up & constraits
	 */
	private boolean isMultipart;
	private String filePath;
	private int maxFileSize = 500000 * 1024;
	private int maxMemSize = 40 * 1024;
	private File file;
	private static String root;
	private String rootFolder=ConstantConfig.KEY_PROFILE_IMAGE_FOLDER;//the folder store the uploaded fuel image. it has to be created in disk





	private List <String>fileNameList=new ArrayList<String>();

	/**
	 * for error control
	 */
	private final  String TAG="UploadImageServlet";
	private final  MyExceptionHandler myExceptinHandler=new MyExceptionHandler(TAG);

	private Logger logger = LogManager.getLogger(UploadImageServlet.class.getSimpleName());



	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadImageServlet() {
		super();

		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
		root = config.getServletContext().getRealPath("/");
		filePath=root+rootFolder;
		createDir(new File(filePath));	
		logger.debug("File Folder PATH : " +root);

	}


	/**
	 * create folder if the path doesn't exist
	 * @param file
	 */

	public void createDir(File file){
		if (!file.exists()) {
			if (file.mkdir()) {
				logger.debug("Directory is created!");
			} else {
				logger.debug("Failed to create directory!");
			}
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		logger.debug("-----UploadImageServlet---");
	
		String reply=myExceptinHandler.getJsonOK();

		//		root = getServletContext().getRealPath("/");

		isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter( );
		if( !isMultipart )
		{
			out.println(myExceptinHandler.getJsonError("File not uploaded!"));

			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(maxMemSize);
		factory.setRepository(new File(filePath));
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setSizeMax( maxFileSize );

		try
		{
			//Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);
			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			while ( i.hasNext () ) 
			{
				FileItem fi = (FileItem)i.next();
				if ( !fi.isFormField () )	
				{
					//Get the uploaded file parameters
					String fieldName = fi.getFieldName();
					String fileName = fi.getName();


					if(fieldName.equals("file"))
						if(fileName.toLowerCase().endsWith("jpg") || fileName.toLowerCase().endsWith("png") ||fileName.toLowerCase().endsWith("jpeg"))
						{

							/**
							 * Get the file and store it locally 
							 */

							fileNameList.add(fileName);
							//createDir(new File(filePath));//if the folder doesn't exist, then create it. Might be removed cause only need to run once
							file = new File( filePath + fileName) ;
							logger.debug(file.toString());
							fi.write( file );
							logger.debug("write image file to server success");


						}



				}else {


					/**
					 * Get string data usually encoded in JSON
					 */
					JSONObject json = new JSONObject(fi.getString());
					logger.debug(json.toString());
					String doc=json.getString("_id");
					reply=DBController.performPUT(doc, json.toString());
					logger.debug(reply);
					

				}
			}


			out.print(reply);
			logger.debug("server run success (doesn't mean image file upload success)");


		}
		catch(Exception ex) 
		{
			out.println(myExceptinHandler.getJsonError(ex.toString()));
			return;
		}

	}





}


