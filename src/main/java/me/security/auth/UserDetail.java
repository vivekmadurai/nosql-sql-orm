package me.security.auth;

import me.util.Constant;


public class UserDetail implements User{
	
	private String userId;
	private String companyId;

	public UserDetail(String userId) {
		this.userId = userId;
		this.companyId = Constant.ANONYMOUS;
	}
	
	public UserDetail(String userId, String companyId) {
		this.userId = userId;
		this.companyId = companyId;
	}
	
	@Override
	public String getUserId() {
		return this.userId;
	}
	
	@Override
	public String getEmail() {
		return this.userId;
	}

	@Override
	public String getFullName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirstName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getLastName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTenantId() {
		return this.companyId;
	}

}
