package org.esgi.orm.model;

import java.util.Date;

import org.esgi.orm.annotations.ORM_AUTO_INCREMENT;
import org.esgi.orm.annotations.ORM_PK;
import org.esgi.orm.annotations.ORM_TABLE;

@ORM_TABLE("users")
public class User {
	
	
	@ORM_PK
	@ORM_AUTO_INCREMENT
	public Integer id;
	public String login;
	public String password;
	public volatile Date connectedAt;
	
	@Override
	public String toString() {
		return "User [login=" + login + ", password=" + password
				+ ", connectedAt=" + connectedAt + "]";
	}
}
