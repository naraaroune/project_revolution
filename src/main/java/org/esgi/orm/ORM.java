package org.esgi.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.esgi.orm.annotations.*;
import org.esgi.orm.utils.JavaSQLTypeImpl;
import org.esgi.orm.utils.JavaSqlTypeBindItem;

public class ORM implements IORM {

	static ORM instance;
	static String mysqlHost;
	static String mysqlDatabase;
	static String mysqlUser;
	static String mysqlPassword;
	static Set<Class<?>> alreadyChecked;
	

	static {
		instance = new ORM();
		mysqlHost = "localhost";
		mysqlDatabase = "esgi";
		mysqlUser = "root";
		mysqlPassword = "";
		alreadyChecked = new HashSet<>();
	}

	public static Object save(Object o) {
		return instance._save(o);
	}

	public static Object load(Class<?> c, Object id) {
		return instance._load(c, id);
	}

	public static boolean remove(Class<?> c, Object id) {
		return instance._remove(c, id);
	}

	public static String createConnectionString() {
		return "jdbc:mysql://" + mysqlHost + "/" + mysqlDatabase;
	}

	public static Connection createConnectionObject() {
		Connection connection;
		try {
			Class.forName("org.gjt.mm.mysql.Driver").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			connection = DriverManager.getConnection(createConnectionString(),
					mysqlUser, mysqlPassword);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return connection;
	}

	@Override
	public Object _load(Class<?> c, Object obj) {
		if(obj == null){
			return null;
		}
		Connection connection = createConnectionObject();
		// Check if PK exists
		Field pkField = getPrimaryKey(obj);
		Object pkObj = getPrimaryKeyObj(pkField,obj);
		if(pkObj != null){
			Map<String, Object> whereClause = new HashMap<>();
			whereClause.put(getPrimaryKey(obj).getName(), pkObj);
			List<Object> rid = makeSelect(c, new String[] { pkField
				.getName().toString() }, whereClause, new String[] {}, 1,
				0, connection);
			if (rid.size() != 0) {
				return rid.get(0);
			}
		}
		
		return null;
	}

	@Override
	public boolean _remove(Class<?> c, Object id) {
		return false;
	}
	private Object getPrimaryKeyObj(Field f,Object o){
		if (f != null) {
			try {
				return f.get(o);
			} catch (IllegalArgumentException | IllegalAccessException e) {
			}
		}
		return null;
	}
	@Override
	public Object _save(Object o) {
		if (null == o) {
			System.err.println("Warning, object to save is a nullptr");
			return null;
		}
		Connection connection = createConnectionObject();
		
		try {
			checkAndSyncSchema(o.getClass(), connection);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		// Check if PK exists
		Field pkField = getPrimaryKey(o);
		Object pkObj = getPrimaryKeyObj(pkField,o);
		if (pkObj != null) {
			// PK exists: make upsert
			Map<String, Object> whereClause = new HashMap<>();
			whereClause.put(getPrimaryKey(o).getName(), pkObj);
			// Check if the object exists in database
			List<Object> rid = makeSelect(o.getClass(), new String[] { pkField
					.getName().toString() }, whereClause, new String[] {}, 1,
					0, connection);
			if (rid.size() != 0) {
				// Object already exists: UPDATE
				return makeUpdate(o, connection);
			} else {
				// Object doesn't exists: INSERT
				return makeInsert(o, connection);
			}

		} else {
			// PK doesn't exists, make directly an insert
			return makeInsert(o, connection);
		}
	}

	private static String getTableName(Class<?> c) {
		if (null != c.getAnnotation(ORM_TABLE.class)) {
			return c.getAnnotation(ORM_TABLE.class).value();
		} else {
			return c.getSimpleName().toLowerCase();
		}
	}

	private static String getFieldName(Field inF) {
		return inF.getName().toLowerCase();
	}

	private Object makeInsert(Object o, Connection connection) {
		StringBuilder query = new StringBuilder();

		query.append("INSERT INTO `" + getTableName(o.getClass()) + "` (");
		Boolean first = true;
		for (Field objectField : o.getClass().getFields()) {
			// The field must be a persistent data
			if (Modifier.isVolatile(objectField.getModifiers()))
				continue;

			if (first == true) {
				// Add the field name
				query.append("`" + getFieldName(objectField) + "` ");
				first = false;
			} else {
				query.append(",`" + getFieldName(objectField) + "` ");
			}
		}

		query.append(") VALUES (");
		first = true;
		for (Field objectField : o.getClass().getFields()) {
			// The field must be a persistent data
			if (Modifier.isVolatile(objectField.getModifiers()))
				continue;

			if (first == true) {
				query.append("?");
				first = false;
			} else {
				query.append(",?");
			}
		}
		query.append(")");

		PreparedStatement insertStatement;
		try {
			insertStatement = connection.prepareStatement(query.toString(),
					Statement.RETURN_GENERATED_KEYS);
			int fieldn = 1;
			for (Field objectField : o.getClass().getFields()) {
				// The field must be a persistent data
				if (Modifier.isVolatile(objectField.getModifiers()))
					continue;
				
				insertStatement.setObject(fieldn++, objectField.get(o),
						JavaSQLTypeImpl.typesMapping.getSqlTypeInfo(objectField
								.getType()).sqlType);
			}

			insertStatement.executeUpdate();
			ResultSet generatedRowField = insertStatement.getGeneratedKeys();
			ResultSetMetaData meta = generatedRowField.getMetaData();
			do {
				for (int i = 1; i <= meta.getColumnCount(); i++) {
					for (Field objectField : o.getClass().getFields()) {
						if (getFieldName(objectField).equals(
								meta.getColumnLabel(i))) {
							objectField.set(o, generatedRowField.getObject(i));
						}
					}
				}
			} while (generatedRowField.next());

		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private Boolean makeCreateTable(Class<?> c, Connection connection) throws SQLException {

		StringBuilder query = new StringBuilder();

		query.append("CREATE TABLE `" + mysqlDatabase + "` . `"
				+ ORM.getTableName(c) + "` ( ");

		Boolean first = true;
		for (Field objectField : c.getFields()) {
			// The field must be a persistent data
			if (Modifier.isVolatile(objectField.getModifiers()))
				continue;

			// Add the field name
			if (first) {
				query.append("`" + getFieldName(objectField) + "` ");
				first = false;
			} else {
				query.append(",`" + getFieldName(objectField) + "` ");
			}

			// Get the field type
			JavaSqlTypeBindItem sqlConvertedTypeInfo = JavaSQLTypeImpl.typesMapping
					.getSqlTypeInfo(objectField.getType());
			if (sqlConvertedTypeInfo.defaultSize != null) {
				query.append(sqlConvertedTypeInfo.getSQLTypeName() + "("
						+ sqlConvertedTypeInfo.defaultSize + ")");
			} else {
				query.append(sqlConvertedTypeInfo.getSQLTypeName());
			}
			// typesMapping.getSqlType(objectField.getType());

			if(null != objectField.getAnnotation(ORM_AUTO_INCREMENT.class)){
				query.append(" AUTO_INCREMENT");
			}
			if (null != objectField.getAnnotation(ORM_NOT_NULL.class)) {
				query.append(" NOT NULL");
			}
			if (null != objectField.getAnnotation(ORM_PK.class)) {
				query.append(" PRIMARY KEY");
			}
			if (null != objectField.getAnnotation(ORM_UNIQUE.class)) {
				query.append(" UNIQUE");
			}
			// query.append("NOT NULL ");

		}

		query.append(" )");

		System.out.println(query.toString());

		
		Statement st = connection.createStatement();
		st.execute(query.toString());
		st.close();

		return true;
	}
	
	private Boolean checkAndSyncSchema(Class<?> c, Connection connection) throws SQLException{
		
		if(!alreadyChecked.contains(c)){
			try {
				makeCreateTable(c, connection);
			} catch (SQLException e) {
				if(e.getMessage().indexOf("already exists") != -1){
					makeAlterTable(c, connection);
				}else{
					throw e;
				}
			}
		}
		alreadyChecked.add(c);
		return true;
	}
	
	// TODO : modify  
	private void makeAlterTable(Class<?> c, Connection connection) throws SQLException {

		StringBuilder query = new StringBuilder();

		query.append("ALTER TABLE `" + mysqlDatabase + "` . `"
				+ ORM.getTableName(c) + "` ");

		Boolean first = true;
		for (Field objectField : c.getFields()) {
			// The field must be a persistent data
			if (Modifier.isVolatile(objectField.getModifiers()))
				continue;

			// Add the field name
			if (first) {
				query.append("CHANGE `" + getFieldName(objectField) + "` `" + getFieldName(objectField) + "` ");
				first = false;
			} else {
				query.append(", CHANGE `" + getFieldName(objectField) + "` `" + getFieldName(objectField) + "` ");
			}

			// Get the field type
			JavaSqlTypeBindItem sqlConvertedTypeInfo = JavaSQLTypeImpl.typesMapping
					.getSqlTypeInfo(objectField.getType());
			if (sqlConvertedTypeInfo.defaultSize != null) {
				query.append(sqlConvertedTypeInfo.getSQLTypeName() + "("
						+ sqlConvertedTypeInfo.defaultSize + ")");
			} else {
				query.append(sqlConvertedTypeInfo.getSQLTypeName());
			}
			// typesMapping.getSqlType(objectField.getType());

			if(null != objectField.getAnnotation(ORM_AUTO_INCREMENT.class)){
				query.append(" AUTO_INCREMENT");
			}
			if (null != objectField.getAnnotation(ORM_NOT_NULL.class)) {
				query.append(" NOT NULL");
			}
			if (null != objectField.getAnnotation(ORM_PK.class)) {
				query.append(" PRIMARY KEY");
			}
			if (null != objectField.getAnnotation(ORM_UNIQUE.class)) {
				query.append(" UNIQUE");
			}
			// query.append("NOT NULL ");

		}

		System.out.println(query.toString());

		Statement st = connection.createStatement();
		st.execute(query.toString());
		st.close();
		
	}


	private Object makeUpdate(Object o, Connection connection) {
		StringBuilder query = new StringBuilder();

		query.append("UPDATE `" + getTableName(o.getClass()) + "` SET ");

		Boolean first = true;
		for (Field objectField : o.getClass().getFields()) {

			if (Modifier.isVolatile(objectField.getModifiers()))
				continue;
			if (objectField.getAnnotation(ORM_PK.class) != null)
				continue;

			if (first) {
				query.append("`" + getFieldName(objectField) + "` = ?");
				first = false;
			} else {
				query.append(",`" + getFieldName(objectField) + "` = ?");
			}
		}
		query.append(" WHERE `" + getFieldName(getPrimaryKey(o)) + "` = ?");

		try {
			PreparedStatement updateStatement = connection
					.prepareStatement(query.toString());
			int fieldn = 1;
			for (Field objectField : o.getClass().getFields()) {
				if (Modifier.isVolatile(objectField.getModifiers()))
					continue;
				if (objectField.getAnnotation(ORM_PK.class) != null)
					continue;
				
				updateStatement.setObject(fieldn++, objectField.get(o),
						JavaSQLTypeImpl.typesMapping.getSqlTypeInfo(objectField
								.getType()).sqlType);
			}

			return o;

		} catch (SQLException | IllegalArgumentException
				| IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private Field getPrimaryKey(Object o) {
		for (Field f : o.getClass().getFields()) {
			if (f.getAnnotation(ORM_PK.class) != null)
				return f;
		}
		return null;
	}

	public List<Object> makeSelect(Class<?> c, String[] projections,
			Map<String, Object> where, String[] orderby, Integer limit,
			Integer offset, Connection connection) {
		StringBuilder query = new StringBuilder();

		query.append("SELECT ");
		for (int i = 0; i < projections.length; i++) {
			String projection = projections[i];
			if (i != projections.length - 1) {
				query.append(projection + ",");
			} else {
				query.append(projection);
			}
		}
		query.append(" FROM " + getTableName(c) + " WHERE ");

		Iterator<Entry<String, Object>> it = where.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, Object> whereEntry = it.next();
			query.append("`" + whereEntry.getKey() + "` = ?");
			if (it.hasNext()) {
				query.append(" AND ");
			}
		}
		if (limit != null) {
			query.append(" LIMIT " + limit);
			if (offset != null)
				query.append(" OFFSET " + offset);
		}

		try {
			PreparedStatement selectStatement = connection
					.prepareStatement(query.toString());
			for (int i = 0; i < where.values().toArray().length; i++) {
				selectStatement.setObject(i, where.values().toArray()[i],
						JavaSQLTypeImpl.typesMapping.getSqlTypeInfo(where.values()
								.toArray()[i].getClass()).sqlType);
			}
			ResultSet resultSet = selectStatement.executeQuery();
			List<Object> out = new ArrayList<>();
			do {
				Object rowObj = c.newInstance();
				for (String p : projections) {
					Field mapField = c.getField(p);
					mapField.set(rowObj, resultSet.getObject(
							getFieldName(mapField), mapField.getClass()));
				}
				out.add(rowObj);
			} while (resultSet.next());
			return out;
		} catch (SQLException | InstantiationException | IllegalAccessException
				| NoSuchFieldException | SecurityException e) {
			System.err.println("Failed to prepare/execute : "
					+ query.toString());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Object> _find(Class<?> c, String[] projections,
			Map<String, Object> where, String[] orderby, Integer limit,
			Integer offset) {
		// TODO Auto-generated method stub
		return null;
	}
}
