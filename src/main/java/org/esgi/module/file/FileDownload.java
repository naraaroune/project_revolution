package org.esgi.module.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.esgi.web.action.AbstractAction;
import org.esgi.web.action.IContext;


public class FileDownload extends AbstractAction{

	@Override
	public void execute(IContext context) /*throws Exception*/ {
		String path = (String) context.getParameter("path");
		File f = new File(context.getProperties().get("file.repository").toString() + "/" + path );
		System.out.println(f.getPath());
		if (!f.isDirectory()){
			/*byte buffer[] = new byte[16384];
			OutputStream outbin = context.getResponse().getOutputStream();
			InputStream inbin = Files.newInputStream(Paths.get(f.getAbsolutePath()));
			while(inbin.read(buffer) != -1){
				outbin.write(buffer);
			}
			inbin.close();
			outbin.close();*/
			
			/*** Fix for NUL chars in buffer (Romain) ***/
			InputStream inBin = null;
			OutputStream outBin = null;
			try {
				inBin = Files.newInputStream(Paths.get(f.getAbsolutePath()));
				outBin = context.getResponse().getOutputStream();
				byte[] buffer = new byte[4096];
				int sizeRead;
				while ((sizeRead = inBin.read(buffer)) > 0) {
					outBin.write(buffer, 0, sizeRead);
				}
				inBin.close();
				outBin.close();
			}
				
			catch(IOException e){
				System.out.println("Echec du transfert du fichier " + f.getAbsolutePath());
			}
			/***********************************/
			
		}
	}

	@Override
	public String getRoute() {
		return "/file/list/(.+[^/])$";
	}

	@Override
	public String[] getRewriteGroups() {
		return new String[]{"path"};
	}
	
	@Override
	public String getLayout(){
		return null;
	}
	

}
