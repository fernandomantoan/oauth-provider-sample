package com.fernandomantoan.tcc.oauth.controller;

import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AccessConfirmationController
{
	@Autowired
	private OAuthProviderTokenServices tokenServices;
	
	@Autowired
	private ConsumerDetailsService consumerDetailsService;
	
	
	@RequestMapping("/oauth/confirm_access")
	public ModelAndView accessConfirmation(HttpServletRequest request, HttpServletResponse response)
		throws Exception
	{
		String token = request.getParameter("oauth_token");
		if (token == null) {
			throw new IllegalArgumentException("Request token not defined");
		}
		
		OAuthProviderToken providerToken = tokenServices.getToken(token);
		ConsumerDetails consumer = consumerDetailsService.loadConsumerByConsumerKey(providerToken.getConsumerKey());
		String callback = request.getParameter("oauth_callback");
		TreeMap<String, Object> model = new TreeMap<String, Object>();
		
		model.put("oauth_token", token);
		if (callback != null) {
			model.put("oauth_callback", callback);
		}
		
		model.put("consumer", consumer);
		return new ModelAndView("access_confirmation/confirm", model);
	}
	
	@RequestMapping("/oauth/revoke/{tokenValue}")
	public ModelAndView accessRevoked(@PathVariable String tokenValue)
	{
		((OAuthProviderTokenImpl)this.tokenServices.getToken(tokenValue)).setTimestamp(0);
		
		return new ModelAndView("access_confirmation/revoked");
	}
}