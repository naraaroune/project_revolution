package org.esgi.module.member;

import java.util.Properties;

import org.esgi.web.action.AbstractAction;
import org.esgi.web.action.IContext;

public class General extends AbstractAction {

	public static String NAMEPAGE = "account";
	public static Properties CONFIG;
	
	@Override
	public void execute(IContext context) throws Exception {
		
		CONFIG = new Properties();
		 
		if(context.getRequest().getSession().getAttribute("account") != null) {
			// BIND CONTEXT ATTRIBUTES 
			context.setDescription(General.CONFIG.getProperty("description"));
			context.getVelocityContext().put("title", General.CONFIG.getProperty("title"));
			
			context.getVelocityContext().put("imgDataAccount", General.CONFIG.getProperty("imgDataAccount"));
			context.getVelocityContext().put("imgFacePicture", General.CONFIG.getProperty("imgFacePicture"));
			context.getVelocityContext().put("imgStudentTrombinoscope", General.CONFIG.getProperty("imgStudentTrombinoscope"));
			context.getVelocityContext().put("imgTeacherTrombinoscope", General.CONFIG.getProperty("imgTeacherTrombinoscope"));
			
			context.getVelocityContext().put("descDataAccount", General.CONFIG.getProperty("descDataAccount"));
			context.getVelocityContext().put("descFacePicture", General.CONFIG.getProperty("descFacePicture"));
			context.getVelocityContext().put("descStudentTrombinoscope", General.CONFIG.getProperty("descStudentTrombinoscope"));
			context.getVelocityContext().put("descTeacherTrombinoscope", General.CONFIG.getProperty("descTeacherTrombinoscope"));
			
			context.getVelocityContext().put("linkDataAccount", General.CONFIG.getProperty("linkDataAccount"));
			context.getVelocityContext().put("linkFacePicture", General.CONFIG.getProperty("linkFacePicture"));
			context.getVelocityContext().put("linkStudentTrombinoscope", General.CONFIG.getProperty("linkStudentTrombinoscope"));
			context.getVelocityContext().put("linkTeacherTrombinoscope", General.CONFIG.getProperty("linkTeacherTrombinoscope"));
			
		} else {
			context.getResponse().sendRedirect(context.getRequest().getContextPath());;
		}
		
	}

	@Override
	public String getRoute() {
		return "/" + General.NAMEPAGE + "/(.+[^/])$";
	}
	
}
