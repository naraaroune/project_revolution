package org.esgi.orm;

import java.util.List;
import java.util.Map;

public interface IORM {

	/** Create or update a record in db. */
	public Object _save(Object o);
	
	/** return an instance of clazz */
	public Object _load(Class<?> c, Object id);
	
	/** delete an record from clazz persistence layer */
	public boolean _remove(Class<?> c, Object id);
	
	public List<Object> _find(Class<?> c, String[] projections, Map<String, Object> where, String[] orderby, Integer limit, Integer offset);
	
}
