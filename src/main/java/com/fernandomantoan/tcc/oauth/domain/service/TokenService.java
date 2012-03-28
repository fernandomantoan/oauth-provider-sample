package com.fernandomantoan.tcc.oauth.domain.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation=Propagation.REQUIRED)
public interface TokenService
{
	@Transactional(readOnly=true)
	public OAuthProviderToken readToken(String token);
	
	public void storeToken(String tokenValue, OAuthProviderToken token);
	
	public void updateToken(String tokenValue, OAuthProviderToken token);
	
	public OAuthProviderToken removeToken(String tokenValue);
	
	public OAuthProviderToken getToken(String token) throws AuthenticationException;
	
	public OAuthProviderToken createUnauthorizedRequestToken(String consumerKey,
			String callbackUrl) throws AuthenticationException;
	
	public void authorizeRequestToken(String requestToken, String verifier, Authentication authentication) throws AuthenticationException;
	
	public OAuthAccessProviderToken createAccessToken(String requestToken) throws AuthenticationException;
}
