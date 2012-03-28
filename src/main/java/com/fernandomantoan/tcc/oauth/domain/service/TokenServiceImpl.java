package com.fernandomantoan.tcc.oauth.domain.service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth.provider.token.ExpiredOAuthTokenException;
import org.springframework.security.oauth.provider.token.InvalidOAuthTokenException;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.security.oauth.provider.token.OAuthTokenLifecycleListener;
import org.springframework.security.oauth.provider.token.OAuthTokenLifecycleRegistry;
import org.springframework.security.oauth.provider.token.RandomValueProviderTokenServices;
import org.springframework.stereotype.Service;

import com.fernandomantoan.tcc.oauth.domain.entity.applications.ApplicationAuthorized;
import com.fernandomantoan.tcc.oauth.domain.repository.ApplicationAuthorizedRepository;

/**
 * This implementation would use the database to store tokens
 * It's incomplete
 * TODO
 * 
 * @see RandomValueProviderTokenServices
 * @author fernando
 */
@Service("databaseTokenService")
public class TokenServiceImpl implements TokenService, OAuthProviderTokenServices, InitializingBean, OAuthTokenLifecycleRegistry
{
	private Random random;
	private int requestTokenValiditySeconds = 60 * 10;
	private int accessTokenValiditySeconds = 60 * 60 * 12;
	private int tokenSecretLengthBytes = 80;
	private final Collection<OAuthTokenLifecycleListener> lifecycleListeners = new HashSet<OAuthTokenLifecycleListener>();
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private ApplicationAuthorizedRepository repository;

	/**
	* Read the token and application data from the database.
	*
	* @param token
	* @return
	*/
	@Override
	public OAuthProviderToken readToken(String token)
	{
		log.debug("Reading the token " + token);
		OAuthProviderToken tokenImpl = this.repository.findByTokenValue(token);
		
		if (((ApplicationAuthorized)tokenImpl).getUser() != null && 
				((ApplicationAuthorized)tokenImpl).getUserAuthentication() == null) {
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					((ApplicationAuthorized)tokenImpl).getUser(), null, 
					((ApplicationAuthorized)tokenImpl).getUser().getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authentication);
			((ApplicationAuthorized)tokenImpl).setUserAuthentication(authentication);
		}
				
		return tokenImpl;
	}
	/**
	 * Store a new token in the database
	 * 
	 * @param tokenValue
	 * @param token
	 */
	@Override
	public void storeToken(String tokenValue, OAuthProviderToken token)
	{
		log.debug("Storing the token " + tokenValue);
		try {
			this.repository.insert((ApplicationAuthorized)token);
		} catch (Exception e) {
			log.error("Error to store the token " + tokenValue);
			e.printStackTrace();
		}
	}
	
	@Override
	public void updateToken(String tokenValue, OAuthProviderToken token)
	{
		log.debug("Updating the token " + tokenValue);
		try {
			this.repository.update((ApplicationAuthorized)token);
		} catch (Exception e) {
			log.error("Error to store the token " + tokenValue);
			e.printStackTrace();
		}
	}
	/**
	 * Remove a token from the database
	 * 
	 * @param tokenValue
	 * @return
	 */
	@Override
	public OAuthProviderToken removeToken(String tokenValue)
	{
		log.debug("Removing the token " + tokenValue);
		ApplicationAuthorized token = (ApplicationAuthorized)this.readToken(tokenValue);
		this.repository.remove(token);
		return token;
	}
	/**
	 * Initialize the token services.
	 * 
	 * @throws Exception
	 * @see {@link RandomValueProviderTokenServices}
	 */
	@Override
	public void afterPropertiesSet() throws Exception
	{
		if (random == null)
			random = new SecureRandom();
	}
	/**
	 * 
	 */
	@Override
	public OAuthProviderToken getToken(String token) throws AuthenticationException
	{
		OAuthProviderToken tokenImpl = this.readToken(token);
		
		if (tokenImpl == null) {
			throw new InvalidOAuthTokenException("Invalid token getToken: " + token);
		} else if (isExpired(tokenImpl)) {
			removeToken(token);
			onTokenRemoved(tokenImpl);
			throw new ExpiredOAuthTokenException("Expired token.");
		}
		return tokenImpl;
	}
	/**
	 * Verify if the authentication token is expired
	 * 
	 * @param authToken
	 * @return
	 */
	protected boolean isExpired(OAuthProviderToken authToken)
	{
		long timestamp = ((ApplicationAuthorized)authToken).getTimestamp();
		if (authToken.isAccessToken()) {
			
			if ((timestamp + (getAccessTokenValiditySeconds() * 1000L)) < System.currentTimeMillis()) {
				return true;
			}
		} else {
			if ((timestamp + (getRequestTokenValiditySeconds() * 1000L)) < System.currentTimeMillis()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public OAuthProviderToken createUnauthorizedRequestToken(String consumerKey,
			String callbackUrl) throws AuthenticationException
	{
		String tokenValue = UUID.randomUUID().toString();
		byte[] secretBytes = new byte[getTokenSecretLengthBytes()];
		this.getRandom().nextBytes(secretBytes);
		String secret = new String(Base64.encodeBase64(secretBytes));
		ApplicationAuthorized token = new ApplicationAuthorized();
		token.setAccessToken(false);
		token.setConsumerKey(consumerKey);
		token.setCallbackUrl(callbackUrl);
		token.setUserAuthentication(null);
		token.setSecret(secret);
		token.setValue(tokenValue);
		token.setTimestamp(System.currentTimeMillis());
		this.onTokenCreated(token);
		this.storeToken(tokenValue, token);
		return token;
	}

	public void authorizeRequestToken(String requestToken, String verifier, Authentication authentication) throws AuthenticationException
	{
		ApplicationAuthorized tokenImpl = (ApplicationAuthorized) readToken(requestToken);
		
		if (tokenImpl == null) {
			throw new InvalidOAuthTokenException("Invalid token authorizeRequest: " + requestToken);
		} else if (isExpired(tokenImpl)) {
			removeToken(requestToken);
			onTokenRemoved(tokenImpl);
			throw new ExpiredOAuthTokenException("Expired token.");
		} else if (tokenImpl.isAccessToken()) {
			throw new InvalidOAuthTokenException("Request to authorize an access token.");
		}
		
		tokenImpl.setUserAuthentication(authentication);
		tokenImpl.setTimestamp(System.currentTimeMillis());//reset the expiration.
		tokenImpl.setVerifier(verifier);
		this.updateToken(requestToken, tokenImpl);
	}

	public OAuthAccessProviderToken createAccessToken(String requestToken) throws AuthenticationException
	{
		ApplicationAuthorized tokenImpl = (ApplicationAuthorized) readToken(requestToken);
		
		if (tokenImpl == null) {
			throw new InvalidOAuthTokenException("Invalid token createAccess: " + requestToken);
		} else if (isExpired(tokenImpl)) {
			removeToken(requestToken);
			onTokenRemoved(tokenImpl);
			throw new ExpiredOAuthTokenException("Expired token.");
		} else if (tokenImpl.isAccessToken()) {
			throw new InvalidOAuthTokenException("Not a request token.");
		} else if (tokenImpl.getUser() == null) {
			throw new InvalidOAuthTokenException("Request token has not been authorized.");
		}
		
		OAuthProviderToken requestTokenImpl = removeToken(requestToken);
		
		if (requestTokenImpl != null) {
			onTokenRemoved(requestTokenImpl);
		}
		
		String tokenValue = UUID.randomUUID().toString();
		byte[] secretBytes = new byte[getTokenSecretLengthBytes()];
		this.getRandom().nextBytes(secretBytes);
		String secret = new String(Base64.encodeBase64(secretBytes));
		ApplicationAuthorized token = new ApplicationAuthorized();
	    token.setAccessToken(true);
	    token.setConsumerKey(tokenImpl.getConsumerKey());
	    token.setUser(tokenImpl.getUser());
	    token.setSecret(secret);
	    token.setValue(tokenValue);
	    token.setTimestamp(System.currentTimeMillis());
	    onTokenCreated(token);
	    storeToken(tokenValue, token);
	    return token;
	}
	

	protected void onTokenRemoved(OAuthProviderToken token)
	{
		for (OAuthTokenLifecycleListener listener : getLifecycleListeners()) {
			listener.tokenExpired(token);
		}
	}
	
	protected void onTokenCreated(OAuthProviderToken token)
	{
		for (OAuthTokenLifecycleListener listener : getLifecycleListeners()) {
			listener.tokenCreated(token);
		}
	}
	
	public int getTokenSecretLengthBytes()
	{
		return tokenSecretLengthBytes;
	}
	/**
	 * The length of the token secret in bytes, before being base64-encoded.
	 * 
	 * @param tokenSecretLengthBytes The length of the token secret in bytes, before being base64-encoded.
	 */
	public void setTokenSecretLengthBytes(int tokenSecretLengthBytes)
	{
		this.tokenSecretLengthBytes = tokenSecretLengthBytes;
	}
	/**
	 * The random value generator used to create token secrets.
	 * 
	 * @return The random value generator used to create token secrets.
	 */
	public Random getRandom()
	{
		return random;
	}
	/**
	 * The random value generator used to create token secrets.
	 * 
	 * @param random The random value generator used to create token secrets.
	 */
	public void setRandom(Random random)
	{
		this.random = random;
	}
	/**
	 * The validity (in seconds) of the unauthenticated request token.
	 * 
	 * @return The validity (in seconds) of the unauthenticated request token.
	 */
	public int getRequestTokenValiditySeconds()
	{
		return requestTokenValiditySeconds;
	}
	/**
	 * The validity (in seconds) of the unauthenticated request token.
	 * 
	 * @param requestTokenValiditySeconds The validity (in seconds) of the unauthenticated request token.
	 */
	public void setRequestTokenValiditySeconds(int requestTokenValiditySeconds)
	{
		this.requestTokenValiditySeconds = requestTokenValiditySeconds;
	}
	/**
	 * The validity (in seconds) of the access token.
	 * 
	 * @return The validity (in seconds) of the access token.
	 */
	public int getAccessTokenValiditySeconds()
	{
		return accessTokenValiditySeconds;
	}
	/**
	 * The validity (in seconds) of the access token.
	 * 
	 * @param accessTokenValiditySeconds The validity (in seconds) of the access token.
	 */
	public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds)
	{
		this.accessTokenValiditySeconds = accessTokenValiditySeconds;
	}
	
	@Override
	public Collection<OAuthTokenLifecycleListener> getLifecycleListeners()
	{
		return lifecycleListeners;
	}

	@Autowired(required=false)
	@Override
	public void register(OAuthTokenLifecycleListener... lifecycleListeners)
	{
		if (lifecycleListeners != null)
			this.lifecycleListeners.addAll(Arrays.asList(lifecycleListeners));
	}
}