package com.fernandomantoan.tcc.oauth.domain.entity.applications;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;

import com.fernandomantoan.tcc.oauth.domain.entity.credentials.User;

@Entity
public class ApplicationAuthorized implements OAuthAccessProviderToken
{
	private static final long serialVersionUID = 1L;

	@ManyToOne(cascade=CascadeType.MERGE)
	private User user;
	
	@Id
	@Column
	private String value;
	
	@Column
	private String callbackUrl;
	
	@Column
	private String verifier;
	
	@Column
	private String secret;
	
	@Column
	private String consumerKey;
	
	@Column
	private boolean accessToken;
	
	@Column
	private long timestamp;
	
	@Transient
	private Authentication authentication;

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}

	@Override
	public String getCallbackUrl()
	{
		return this.callbackUrl;
	}
	
	public void setCallbackUrl(String callbackUrl)
	{
		this.callbackUrl = callbackUrl;
	}

	@Override
	public String getConsumerKey()
	{
		return this.consumerKey;
	}
	
	public void setConsumerKey(String consumerKey)
	{
		this.consumerKey = consumerKey;
	}

	@Override
	public String getSecret()
	{
		return this.secret;
	}
	
	public void setSecret(String secret)
	{
		this.secret = secret;
	}

	@Override
	public String getValue()
	{
		return this.value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String getVerifier()
	{
		return this.verifier;
	}
	
	public void setVerifier(String verifier)
	{
		this.verifier = verifier;
	}

	@Override
	public boolean isAccessToken()
	{
		return this.accessToken;
	}
	
	public void setAccessToken(boolean accessToken)
	{
		this.accessToken = accessToken;
	}
	
	public long getTimestamp()
	{
		return this.timestamp;
	}
	
	public void setTimestamp(long timestamp)
	{
		this.timestamp = timestamp;
	}

	public void setUserAuthentication(Authentication authentication)
	{
		this.authentication = authentication;
		
		if (this.authentication != null && this.authentication.getPrincipal() != null)
			this.setUser((User)this.authentication.getPrincipal());
	}
	
	@Override
	public Authentication getUserAuthentication()
	{
		return this.authentication;
	}
}