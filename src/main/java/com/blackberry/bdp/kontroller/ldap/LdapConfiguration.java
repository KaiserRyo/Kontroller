/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.ldap;

import com.google.common.cache.CacheBuilderSpec;
import com.google.common.collect.Sets;
import io.dropwizard.util.Duration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.Set;

@SuppressWarnings("unused")
public class LdapConfiguration {

	/************************************************************************************************************
	* URI 
	************************************************************************************************************/
	@NotNull @Valid private String ldapServers = "ldap://www.example.com:389";

	public LdapConfiguration setLdapServers(String ldapServers) {
		this.ldapServers = ldapServers;
		return this;
	}
	
	public String getLdapServers() {
		return ldapServers;
	}	

	/************************************************************************************************************
	* Trust any secure host?
	************************************************************************************************************/
	@NotNull @Valid private boolean trustAnySecuredHost = false;

	public LdapConfiguration setTrustAnySecuredHost (boolean trustAnySecuredHost) {
		this.trustAnySecuredHost = trustAnySecuredHost;
		return this;
	}
	
	public boolean getTrustAnySecuredHost() {
		return trustAnySecuredHost;
	}	
	
	/************************************************************************************************************
	* Base DNs for looking up memberships
	************************************************************************************************************/
	@NotNull @Valid private String  groupBaseDN;

	public LdapConfiguration setGroupBaseDN (String groupBaseDNs) {
		this.groupBaseDN = groupBaseDNs;
		return this;
	}
	
	public String getGroupBaseDN() {
		return groupBaseDN;
	}	
	
	/************************************************************************************************************
	* Cache Builder
	************************************************************************************************************/
	@NotNull @Valid private CacheBuilderSpec cachePolicy = CacheBuilderSpec.disableCaching();
	
	public CacheBuilderSpec getCachePolicy() {
		return cachePolicy;
	}

	public LdapConfiguration setCachePolicy(CacheBuilderSpec cachePolicy) {
		this.cachePolicy = cachePolicy;
		return this;
	}
	
	/************************************************************************************************************
	* User Filter
	************************************************************************************************************/
	@NotNull @NotEmpty private String userFilter = "ou=people,dc=example,dc=com";

	public String getUserFilter() {
		return userFilter;
	}

	public LdapConfiguration setUserFilter(String userFilter) {
		this.userFilter = userFilter;
		return this;
	}
	
	
	/************************************************************************************************************
	* Group FIllter
	************************************************************************************************************/
	/*
	@NotNull @NotEmpty private String groupFilter = "ou=groups,dc=example,dc=com";
	
	public String getGroupFilter() {
		return groupFilter;
	}

	public LdapConfiguration setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
		return this;
	}
	*/
	
	/************************************************************************************************************
	* Username
	************************************************************************************************************/
	@NotNull @NotEmpty private String username = "ldapreader";
	
	public String getUsername() {
		return username;
	}

	public LdapConfiguration setUsername(String username) {
		this.username = username;
		return this;
	}	

	/************************************************************************************************************
	* Password
	************************************************************************************************************/	
	@NotNull @NotEmpty private String password = "changeme";
	
	public String getPassword() {
		return password;
	}

	public LdapConfiguration setPassword(String password) {
		this.password = password;
		return this;
	}

	/************************************************************************************************************
	* Connect Timeout
	************************************************************************************************************/
 	//@NotNull 
	@Valid 	private Duration connectTimeout = Duration.milliseconds(500);
	
	public Duration getConnectTimeout() {
		return connectTimeout;
	}

	public LdapConfiguration setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	/************************************************************************************************************
	* Read Timeout
	************************************************************************************************************/
	//@NotNull
	@Valid private Duration readTimeout = Duration.milliseconds(500);

	public Duration getReadTimeout() {
		return readTimeout;
	}

	public LdapConfiguration setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

}