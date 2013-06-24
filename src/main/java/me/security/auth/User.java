package me.security.auth;

/**
 * @author VivekMadurai
 *
 */
public interface User {
	
	public String getUserId();
	
	public String getEmail();
	
	public String getFullName();
	
	public String getFirstName();
	
	public String getLastName();
	
	public String getTenantId();
	
}
