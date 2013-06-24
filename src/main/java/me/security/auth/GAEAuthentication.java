package me.security.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * @author VivekMadurai
 *
 */
public class GAEAuthentication  implements Authentication {

	private UserService userService;
	
	public GAEAuthentication() {
		userService = UserServiceFactory.getUserService();
	}

	public String getLoginUrl(HttpServletRequest request, HttpServletResponse response) {
		String loginUrl = userService.createLoginURL("/process_login");
		return "redirect:".concat(loginUrl);
	}

	public User authenticate(HttpServletRequest request, HttpServletResponse response) {
		com.google.appengine.api.users.User currentUser = userService.getCurrentUser();
		String emailId = currentUser.getEmail();
		return new UserDetail(emailId);
	}
	
	public String getLogoutUrl(String redirectUrl) {
		com.google.appengine.api.users.User currentUser = userService.getCurrentUser();
		if (currentUser != null) {
			return userService.createLogoutURL(redirectUrl, currentUser.getAuthDomain());
		} else {
			return userService.createLogoutURL(redirectUrl);
		}
	}

	@Override
	public void onSuccess(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFailure(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		
	}

}

