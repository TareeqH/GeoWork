package com.osm.geo.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository("daoServices")
public class DAOServicesImpl implements DAOServices {
	@Autowired
	private SessionFactory sessionFactory;

	public DAOServicesImpl() {
		super();
	}

	private List getCriteria(Criteria crit) {

		List res = new ArrayList();
		if (crit != null) {
			res = crit.list();
			if (res == null || res.isEmpty()) {
				res = null;
			}
		}

		return res;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public void saveEntity(Object entity) {
		try {
			sessionFactory.getCurrentSession().save(entity);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteEntity(Object entity) {

		try {

			Session session = sessionFactory.getCurrentSession();
			session.delete(entity);

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	public void mergeEntity(Object entity) {

		try {
			Session session = sessionFactory.getCurrentSession();
			session.merge(entity);

		} catch (Exception e) {

			e.printStackTrace();

		}
	}

	public void updateEntity(Object entity) {

		try {

			Session session = sessionFactory.getCurrentSession();
			session.update(entity);

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public <T> List<T> getEntitiesByProperty(Class<T> type,
			String propertyName, Object value) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		crit.add(Restrictions.eq(propertyName, value));

		return this.getCriteria(crit);
	}

	public <T> T getEntityByRef(Class<T> type, String ref) {

		return getEntitiesByProperty(type, "ref", ref).get(0);
	}

	public <T> T getEntityById(Class<T> type, Integer id) {
		return getEntitiesByProperty(type, "id", id).get(0);
	}

	public <T> List<T> searchByProperty(Class<T> type, String propertyName,
			Object propertyValue) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		crit.add(Restrictions.like(propertyName, "%" + propertyValue + "%"));

		return this.getCriteria(crit);
	}

	public void saveEntities(List<?> entities) {

		@SuppressWarnings("unchecked")
		List<Object> entitiesList = (List<Object>) entities;

		for (Object entity : entitiesList) {
			saveEntity(entity);
		}

	}

	public <T> List<T> getEntities(Class<T> type) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);

		return this.getCriteria(crit);
	}

	public void init() {
		System.out.println(sessionFactory != null);
		System.out.println("test Spring DAOServices !! ");
	}

	public <T> Object getEntityById2(Class<T> type, Integer id) {
		Object object = sessionFactory.getCurrentSession().load(type, id);
		Hibernate.initialize(object);
		return object;
	}

	@Override
	public <T> List<T> searchByPropertyId(Class<T> type, String propertyName,
			int value) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		crit.add(Restrictions.eq(propertyName, value));

		return this.getCriteria(crit);
	}

	@Override
	public <T> List<T> searchByBollProperty(Class<T> type, String propertyName,
			boolean value) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		crit.add(Restrictions.eq(propertyName, value));
		return this.getCriteria(crit);
	}

	@Override
	public <T> List<T> searchByAttPropetry(Class<T> type, String attrName,
			String propertyName, String value) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type);
		crit.createCriteria(attrName).add(Restrictions.eq(propertyName, value));

		return this.getCriteria(crit);
	}

	@Override
	public <T> List<T> fetchByPropertyDate(Class<T> type, String propertyName,
			Date begin, Date end) {
		Criteria crit = sessionFactory.getCurrentSession().createCriteria(type)
				.add(Restrictions.between(propertyName, begin, end));

		return this.getCriteria(crit);
	}
}