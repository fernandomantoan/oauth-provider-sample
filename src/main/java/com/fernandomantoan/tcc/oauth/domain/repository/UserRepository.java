package com.fernandomantoan.tcc.oauth.domain.repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;

import com.fernandomantoan.tcc.oauth.domain.entity.credentials.User;

@Repository
public class UserRepository extends AbstractRepository<User>
{
	public User findByUsername(String username)
	{
		try {
			TypedQuery<User> query = this.entityManager.createQuery("select u from User u where u.username = :username", User.class);
			query.setParameter("username", username);
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
