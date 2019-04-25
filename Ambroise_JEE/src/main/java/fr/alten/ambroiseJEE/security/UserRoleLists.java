package fr.alten.ambroiseJEE.security;

import java.util.ArrayList;

/**
 * 
 * @author Kylian Gehier
 *
 */

public class UserRoleLists {

	private static UserRoleLists INSTANCE = null;

	private ArrayList<UserRole> adminUsers;
	private ArrayList<UserRole> nonAdminUsers;

	private UserRoleLists() {
		this.adminUsers = new ArrayList<>();
		this.nonAdminUsers = new ArrayList<>();
		fillLists();
	}

	public static UserRoleLists getInstance() {
		return (INSTANCE == null)? new UserRoleLists() : INSTANCE;
	}

	/*
	 * Fill both ArrayLists with UserRole.Enum enums
	 */
	private void fillLists() {
		this.adminUsers.add(UserRole.CDR_ADMIN);
		this.adminUsers.add(UserRole.MANAGER_ADMIN);
		this.nonAdminUsers.add(UserRole.CDR);
		this.nonAdminUsers.add(UserRole.MANAGER);
		this.nonAdminUsers.add(UserRole.CONSULTANT);
		this.nonAdminUsers.add(UserRole.DEACTIVATED);
	}

	/**
	 * 
	 * @return the list of admin UserRole
	 */
	public ArrayList<UserRole> getAdminUsers() {
		return this.adminUsers;
	}

	/**
	 * 
	 * @return the list of non-admin UserRole
	 */
	public ArrayList<UserRole> getNonAdminUsers() {
		return this.nonAdminUsers;
	}
	
	/**
	 * 
	 * @param role {@link UserRole} to test if he is admin
	 * @return true if admin, otherwise false
	 * @author Kylian Gehier
	 */
	public boolean isAdmin(UserRole role) {
		return (this.adminUsers.contains(role)) ? true : false;
	}

}