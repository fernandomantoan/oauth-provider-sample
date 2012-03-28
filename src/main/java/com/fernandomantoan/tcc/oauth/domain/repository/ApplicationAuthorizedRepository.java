package com.fernandomantoan.tcc.oauth.domain.repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.fernandomantoan.tcc.oauth.domain.entity.applications.ApplicationAuthorized;

@Repository
public class ApplicationAuthorizedRepository extends AbstractRepository<ApplicationAuthorized>
{
	public ApplicationAuthorized findByTokenValue(String token)
	{
		try {
			TypedQuery<ApplicationAuthorized> query = this.entityManager.createQuery("select a from ApplicationAuthorized a where a.value = :token", ApplicationAuthorized.class);
			query.setParameter("token", token);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
