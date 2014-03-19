package org.esgi.orm.utils;

import java.sql.Types;
import java.util.Date;

public class JavaSQLTypeImpl {
	
	public static JavaSqlTypeMap typesMapping;
	
	static{
		typesMapping = new JavaSqlTypeMap();
		
		typesMapping.add(new JavaSqlTypeBindItem(Integer.class, Types.INTEGER, 11));
		typesMapping.add(new JavaSqlTypeBindItem(String.class, Types.VARCHAR, 300));
		typesMapping.add(new JavaSqlTypeBindItem(Date.class, Types.DATE, null));
	}

}
