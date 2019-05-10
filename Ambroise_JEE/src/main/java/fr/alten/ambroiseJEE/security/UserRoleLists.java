package fr.alten.ambroiseJEE.security;

import java.util.ArrayList;

/**
 *
 * @author Kylian Gehier
 *
 */

public class UserRoleLists {

	private static UserRoleLists INSTANCE = null;

	public static UserRoleLists getInstance() {
		return UserRoleLists.INSTANCE == null ? new UserRoleLists() : UserRoleLists.INSTANCE;
	}

	private final ArrayList<UserRole> adminUsers;

	private final ArrayList<UserRole> nonAdminUsers;

	private UserRoleLists() {
		this.adminUsers = new ArrayList<>();
		this.nonAdminUsers = new ArrayList<>();
		fillLists();
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
	public boolean isAdmin(final UserRole role) {
		return this.adminUsers.contains(role);
	}

	/**
	 *
	 * @param role {@link UserRole} to test if he is Cdr or Manager
	 * @return true if Cdr or Manager, otherwise false
	 * @author Kylian Gehier
	 */
	public boolean isManagerOrCdr(final UserRole role) {
		return role == UserRole.CDR || role == UserRole.MANAGER;
	}
	
	/**
	 *
	 * @param role {@link UserRole} to test if he is Consultant or Deactivated
	 * @return false if Consultant or Deactivated, otherwise true
	 * @author Kylian Gehier
	 */
	public boolean isNot_ConsultantOrDeactivated(final UserRole role) {
		return role != UserRole.DEACTIVATED && role != UserRole.CONSULTANT; 
	}
	
	/**
	 * 
	 * @param role {@link UserRole} the current logged user's role
	 * @return true if it's a CDR or a Manager (Admin or not), otherwise false
	 * @author Lucas Royackkers
	 */
	public boolean isManagerOrCdrAdmin(final UserRole role) {
		return role == UserRole.CDR_ADMIN || role == UserRole.MANAGER || role == UserRole.MANAGER_ADMIN;
	}

}