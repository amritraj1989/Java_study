package com.mikealbert.data.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnitUtil;

import com.mikealbert.exception.MalException;

@SuppressWarnings("unchecked")
public abstract class GenericDAOImpl<T, ID extends Serializable> implements GenericDAO<T, ID> {

	private static final long serialVersionUID = 1L;

	private Class<T> persistentClass;

	@PersistenceContext
	protected EntityManager entityManager;

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public GenericDAOImpl() {
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public PersistenceUnitUtil getPersistenceUnitUtil(){		
			return entityManager.getEntityManagerFactory().getPersistenceUnitUtil();	
	}
	
	public Class<T> getPersistentClass() {
		return persistentClass;
	}

	public T getReference(ID id) {
		return entityManager.getReference(persistentClass, id);
	}

	public T loadById(ID id) {
		return entityManager.find(persistentClass, id);
	}

	public void persist(T entity) throws MalException {
		try {
			entityManager.persist(entity);			
		} catch (Exception ex) {
			ex.printStackTrace();			
			throw new MalException("generic.error.occured.while", new String[]{"persisting " + entity.getClass().getName()}, ex);
		}
	}

	public void update(T entity) throws MalException{
		try {
			entityManager.merge(entity);
		} catch (Exception ex) {
			ex.printStackTrace();			
			throw new MalException("generic.error.occured.while", new String[]{"updating " + entity.getClass().getName()}, ex);
		}
	}

//	public void delete(ID id) throws MalException{
//		T dbEntity = null;
//		try {
//			dbEntity = entityManager.getReference(persistentClass, id);
//			entityManager.remove(dbEntity);
//		} catch (Exception ex) {
//			ex.printStackTrace();			
//			throw new MalException("generic.error.occured.while", new String[]{"delete"}, ex);
//		}
//	}

	public List<T> loadAll() {
		return entityManager.createQuery("Select t from " + persistentClass.getSimpleName() + " t").getResultList();
	}

}
