package org.esgi.orm;

import org.esgi.orm.model.User;


public class Application {
	public static void main(String[] args) throws NoSuchFieldException, SecurityException {
		
		ORM.mysqlHost 		= "localhost";
		ORM.mysqlDatabase 	= "test";
		ORM.mysqlUser 		= "test";
		ORM.mysqlPassword 	= "PrJuHXpQn6XSntuN";
		
		User u = new User();
		ORM.save(u);
		System.out.println(u);
		//u = (User) ORM.load(User.class, 5);
	}
}
