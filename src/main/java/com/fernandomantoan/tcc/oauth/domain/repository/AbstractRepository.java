package com.fernandomantoan.tcc.oauth.domain.repository;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractRepository<T>
{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@PersistenceContext
	protected EntityManager entityManager;
	
	protected Class<T> persistentClass;
	
	@SuppressWarnings("unchecked")
	public AbstractRepository()
	{
		this.persistentClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).
				getActualTypeArguments()[0];
	}
	
	public AbstractRepository(Class<T> persistentClass)
	{
		this.persistentClass = persistentClass;
	}
	
	public T insert(T entity)
	{
		this.entityManager.persist(entity);
		log.debug("Persisted successfully");
		return entity;
	}
	
	public T update(T entity)
	{
		log.debug("Updated successfully");
		return this.entityManager.merge(entity);
	}
	
	public void remove(T entity)
	{
		this.entityManager.remove(entity);
	}
	
	public T findById(Long id)
	{
		return this.entityManager.find(this.persistentClass, id);
	}
	
	public List<T> findAll()
	{
		CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(this.persistentClass);
		criteria.from(this.persistentClass);
		TypedQuery<T> query = this.entityManager.createQuery(criteria);
		return query.getResultList();
	}
}