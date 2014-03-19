package org.esgi.orm.utils;

import java.util.ArrayList;
import java.util.HashMap;

public class JavaSqlTypeMap extends ArrayList<JavaSqlTypeBindItem>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7703732441537414245L;
	
	private HashMap<Class<?>, JavaSqlTypeBindItem> javaSearch = new HashMap<>();
	private HashMap<Integer, JavaSqlTypeBindItem> sqlSearch = new HashMap<>();
	
	
	@Override
	public boolean add(JavaSqlTypeBindItem e) {
		javaSearch.put(e.javaType, e);
		sqlSearch.put(e.sqlType, e);
		return super.add(e);
	}
	
	public JavaSqlTypeBindItem getSqlTypeInfo(Class<?> javaType){
		return this.javaSearch.get(javaType);
	}
	
	public Class<?> getJavaType(Integer sqlType){
		return this.sqlSearch.get(sqlType).javaType;
	}

}
