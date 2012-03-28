package com.fernandomantoan.tcc.oauth.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fernandomantoan.tcc.oauth.domain.entity.credentials.User;
import com.fernandomantoan.tcc.oauth.domain.repository.UserRepository;

@Service("userService")
public class UserServiceImpl implements UserService
{
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder encoder;
	
	@Override
	public User insert(User user)
	{
		String password = encoder.encodePassword(user.getPassword(), null);
		user.setPassword(password);
		return userRepository.insert(user);
	}

	@Override
	public User update(User user)
	{
		User oldUser = this.findById(user.getId());
		
		if (user.getPassword() == null || user.getPassword().equals(""))
			user.setPassword(oldUser.getPassword());
		else
			user.setPassword(encoder.encodePassword(user.getPassword(), null));
		
		return userRepository.update(user);
	}

	@Override
	public void remove(Long id)
	{
		User user = this.userRepository.findById(id);
		this.userRepository.remove(user);
	}

	@Override
	public User findById(Long id)
	{
		return userRepository.findById(id);
	}

	@Override
	public List<User> findAll()
	{
		return userRepository.findAll();
	}

	@Override
	public UserDetails loadUserByUsername(String username)
	{
		return userRepository.findByUsername(username);
	}
}