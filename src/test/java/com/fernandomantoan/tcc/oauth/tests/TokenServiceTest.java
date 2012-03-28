package com.fernandomantoan.tcc.oauth.tests;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.fernandomantoan.tcc.oauth.domain.service.TokenService;

public class TokenServiceTest extends AbstractTest
{
	@Autowired
	private TokenService service;
	
	@Test
	public void testInsertToken() throws Exception
	{
		this.service.createUnauthorizedRequestToken("bills-consumer-key", "http://test");
	}
}