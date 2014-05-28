package org.esgi.module.notification;

import org.esgi.web.action.AbstractAction;
import org.esgi.web.action.IContext;

public class Notification extends AbstractAction {

	@Override
	public void execute(IContext context) throws Exception {
		context.setPageTitle("MYGES");
	}
	
	@Override
	public String getRoute() { 
		return "/notification";
	}

}
