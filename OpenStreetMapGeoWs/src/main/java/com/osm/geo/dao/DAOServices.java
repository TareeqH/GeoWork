package com.osm.geo.dao;

import java.util.Date;
import java.util.List;

public interface DAOServices {

	public void saveEntity(Object entity);

	public void deleteEntity(Object entity);

	public void mergeEntity(Object entity);

	public void updateEntity(Object entity);

	public <T> List<T> getEntitiesByProperty(Class<T> type,
			String propertyName, Object value);

	public <T> T getEntityByRef(Class<T> type, String ref);

	public <T> List<T> searchByProperty(Class<T> type, String propertyName,
			Object propertyValue);

	public <T> List<T> searchByPropertyId(Class<T> type, String propertyName,
			int value);

	public void saveEntities(List<?> entities);

	public <T> List<T> getEntities(Class<T> type);

	public <T> T getEntityById(Class<T> type, Integer id);

	public <T> Object getEntityById2(Class<T> type, Integer id);

	public <T> List<T> searchByBollProperty(Class<T> type, String propertyName,
			boolean value);

	public <T> List<T> searchByAttPropetry(Class<T> type, String attrName,
			String propertyName, String value);
	
	public <T> List<T> fetchByPropertyDate(Class<T> type,
			String propertyName, Date begin,Date end);

}
