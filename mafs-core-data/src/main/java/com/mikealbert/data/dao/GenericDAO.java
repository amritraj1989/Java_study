package com.mikealbert.data.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;

import com.mikealbert.exception.MalException;

public interface GenericDAO<T, ID> extends Serializable {

	T loadById(ID id);

	void persist(T entity) throws MalException;

	void update(T entity) throws MalException;

//	void delete(ID id) throws MalException;

	List<T> loadAll();

	T getReference(ID id);

	public void setEntityManager(EntityManager entityManager);
	
	public PersistenceUnitUtil getPersistenceUnitUtil();

}
