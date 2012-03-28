package com.fernandomantoan.tcc.oauth.domain.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fernandomantoan.tcc.oauth.domain.entity.credentials.User;

@Transactional(propagation=Propagation.REQUIRED)
public interface UserService extends UserDetailsService
{
	public User insert(User user);
	
	public User update(User user);
	
	public void remove(Long id);
	
	@Transactional(readOnly=true)
	public User findById(Long id);
	
	@Transactional(readOnly=true)
	public List<User> findAll();
	
	@Transactional(readOnly=true)
	public UserDetails loadUserByUsername(String username);
}