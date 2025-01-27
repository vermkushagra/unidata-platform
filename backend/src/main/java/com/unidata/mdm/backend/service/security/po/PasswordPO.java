package com.unidata.mdm.backend.service.security.po;

import java.io.Serializable;

import com.unidata.mdm.backend.service.security.po.RolePO.Fields;

/**
 * The persistent class for the s_password database table.
 * 
 * @author ilya.bykov
 */
public class PasswordPO extends BaseSecurityPO implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant TABLE_NAME. */
	public static final String TABLE_NAME = "s_password";
	/** The id. */
	private Integer id;

	/** The password text. */
	private String passwordText;
	/** Is password active. */
	private Boolean active;
	// bi-directional one-to-one association to UserPO
	/** The S user. */
	private UserPO user;

	/**
	 * Instantiates a new password po.
	 */
	public PasswordPO() {
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Integer getId() {
		return this.id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id
	 *            the new id
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	/**
	 * Gets the password text.
	 *
	 * @return the password text
	 */
	public String getPasswordText() {
		return this.passwordText;
	}

	/**
	 * Sets the password text.
	 *
	 * @param passwordText
	 *            the new password text
	 */
	public void setPasswordText(String passwordText) {
		this.passwordText = passwordText;
	}

	/**
	 * Gets the active.
	 *
	 * @return the active
	 */
	public Boolean getActive() {
		return active;
	}

	/**
	 * Sets the active.
	 *
	 * @param active
	 *            the active to set
	 */
	public void setActive(Boolean active) {
		this.active = active;
	}

	/**
	 * Gets the s user.
	 *
	 * @return the s user
	 */
	public UserPO getUser() {
		return this.user;
	}

	/**
	 * Sets the s user.
	 *
	 * @param user
	 *            the new s user
	 */
	public void setUser(UserPO user) {
		this.user = user;
	}

	/**
	 * The Class Fields.
	 */
	public static final class Fields extends BaseSecurityPO.Fields {

		/**
		 * Instantiates a new fields.
		 */
		private Fields() {
			super();
		}

		/** The Constant PASSWORD_TEXT. */
		public static final String PASSWORD_TEXT = "PASSWORD_TEXT";

		/** The Constant ACTIVE. */
		public static final String ACTIVE = "ACTIVE";

		/** The Constant S_USER_ID. */
		public static final String S_USER_ID = "S_USER_ID";

		/** All fields combined. */
		public static final String ALL = String.join(DELIMETER,  PASSWORD_TEXT, ACTIVE, S_USER_ID, CREATED_AT,
				UPDATED_AT, CREATED_BY, UPDATED_BY);
		/** All fields combined. */
		public static final String ALL_WITH_TABLE_NAME = String.join(
				DELIMETER, 
				String.join(DOT, TABLE_NAME, ID),
				String.join(DOT, TABLE_NAME, PASSWORD_TEXT), 
				String.join(DOT, TABLE_NAME, ACTIVE),
				String.join(DOT, TABLE_NAME, S_USER_ID), 
				String.join(DOT, TABLE_NAME, CREATED_AT), 
				String.join(DOT, TABLE_NAME, UPDATED_AT),
				String.join(DOT, TABLE_NAME, CREATED_BY), 
				String.join(DOT, TABLE_NAME, UPDATED_BY));
		/** All fields combined, used for update queries. */
		public static final String ALL_TO_UPDATE = String.join(
				DELIMETER, 
				String.join(EQUALS,  PASSWORD_TEXT, PASSWORD_TEXT), 
				String.join(EQUALS, ACTIVE, ACTIVE),
				String.join(EQUALS, S_USER_ID, S_USER_ID), 
				String.join(EQUALS, CREATED_AT, CREATED_AT), 
				String.join(EQUALS, UPDATED_AT, UPDATED_AT),
				String.join(EQUALS,  CREATED_BY, CREATED_BY), 
				String.join(EQUALS, UPDATED_BY, UPDATED_BY));
		public static final String ALL_TO_INSERT = String.join(
				DELIMETER, 
				String.join("",DOTS,  PASSWORD_TEXT), 
				String.join("",DOTS,  ACTIVE),
				String.join("",DOTS,  S_USER_ID), 
				String.join("",DOTS,  CREATED_AT), 
				String.join("",DOTS,  UPDATED_AT),
				String.join("",DOTS,  CREATED_BY), 
				String.join("",DOTS,  UPDATED_BY));
	}

	/**
	 * The Class Queries.
	 */
	public static final class Queries {

		/**
		 * Instantiates a new queries.
		 */
		private Queries() {

		}

		/** The Constant SELECT_BY_ID. */
		public static final String SELECT_BY_ID = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME + " WHERE " + Fields.ID
				+ " = :" + Fields.ID;

		/** The Constant SELECT_BY_USER_ID. */
		public static final String SELECT_BY_USER_ID = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME + " WHERE "
				+ Fields.S_USER_ID + " = :" + Fields.S_USER_ID;
		/** The Constant SELECT_BY_USER_ID_ACTIVE_ONLY. */
		public static final String SELECT_BY_USER_ID_ACTIVE_ONLY = "SELECT " + Fields.ALL_WITH_TABLE_NAME + " FROM " + TABLE_NAME
				+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID + " AND " + Fields.ACTIVE + " IS TRUE";
		/** The Constant DELETE_BY_NAME. */
		public static final String DELETE_BY_ID = 
				"DELETE FROM "+TABLE_NAME
					+ " WHERE "+Fields.ID+" = :"+Fields.ID;
		/** The Constant DELETE_BY_USER_NAME. */
		public static final String DELETE_BY_USER_ID = 
				"DELETE FROM "+TABLE_NAME
					+ " WHERE "+Fields.S_USER_ID+" = :"+Fields.S_USER_ID;
		public static final String INSERT_NEW =
				"INSERT INTO "+TABLE_NAME+"("+Fields.ALL+") VALUES ("+Fields.ALL_TO_INSERT+")";
		public static final String UPDATE_BY_ID = 
				"UPDATE " +TABLE_NAME
					+ " SET "+ Fields.ALL_TO_UPDATE 
					+ " WHERE " + Fields.ID + " = :" + Fields.ID;
		public static final String DEACTIVATE_BY_USER_ID = 
				"UPDATE " +TABLE_NAME
					+ " SET "+ Fields.ACTIVE+" = FALSE" 
					+ " WHERE " + Fields.S_USER_ID + " = :" + Fields.S_USER_ID;
	}
}