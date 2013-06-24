package me.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author VivekMadurai
 *
 */
public interface Authentication {
	
	public String getLoginUrl(HttpServletRequest request, HttpServletResponse response);
	
	public User authenticate(HttpServletRequest request, HttpServletResponse response);
	
	public String getLogoutUrl(String redirectUrl);
	
	public void onSuccess(HttpServletRequest request, HttpServletResponse response);
	
	public void onFailure(HttpServletRequest request, HttpServletResponse response);
}
