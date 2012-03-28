package com.fernandomantoan.tcc.oauth.domain.service;

import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;

public class ConsumerDetailsServiceImpl implements ConsumerDetailsService
{
	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String arg0)
			throws OAuthException {
		return null;
	}
}