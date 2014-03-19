package org.esgi.orm.utils;

import java.lang.reflect.Field;
import java.util.HashMap;

public class JavaSqlTypeBindItem {
	
	public Class<?> javaType;
	public Integer sqlType;
	public Integer defaultSize;
	
	public static HashMap<Integer, String> sqlTypesStrings;
	
	static{
		sqlTypesStrings = getJdbcTypeName();
	}
	
	public JavaSqlTypeBindItem(Class<?> _javaType, Integer _sqlType, Integer _defaultSize) {
		javaType = _javaType;
		sqlType = _sqlType;
		defaultSize = _defaultSize;
	}
	
	public String getSQLTypeName(){
		return sqlTypesStrings.get(this.sqlType);
	}
	
	public static HashMap<Integer, String> getJdbcTypeName() {
		HashMap<Integer, String> out = new HashMap<>();
		for (Field sqlField : java.sql.Types.class.getFields()) {
			try {
				out.put((Integer) sqlField.get(null), sqlField.getName());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return out;
	}
}
