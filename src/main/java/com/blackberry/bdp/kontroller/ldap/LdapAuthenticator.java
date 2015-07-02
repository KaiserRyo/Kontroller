/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.ldap;

import com.google.common.base.Optional;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.x509.X500Name;

/**
 *
 * @author dariens
 */
public class LdapAuthenticator implements Authenticator<BasicCredentials, User> {

	/**
	 * Creates a new authenticator
	 *
	 * @param connectionFactory
	 * @param searchDN the base DN in which to perform a search for the user's DN by uid.
	 * @param rolesDN the base DN in which to lookup roles for a user.
	 */
	public LdapAuthenticator(LdapConnectionFactory connectionFactory, String searchDN, String rolesDN) {
		this.connectionFactory = connectionFactory;
		this.searchDN = searchDN;
		this.rolesDN = rolesDN;
	}

	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {		
		try {
			String username = sanitizeUsername(credentials.getUsername());
			String userDN = dnFromUsername(username);

			verifyCredentials(credentials, userDN);

			Set<String> roles = rolesFromDN(userDN);

			return Optional.fromNullable(new User(username, roles));
		} catch (LDAPException le) {
			if (invalidCredentials(le)) {
				throw new AuthenticationException("Could not connect to LDAP server", le);
			} else {
				logger.error("Error logging in: ", le);
				return Optional.absent();
			}
		}
	}

	private boolean invalidCredentials(LDAPException le) {
		return le.getResultCode() != ResultCode.INVALID_CREDENTIALS;
	}

	private void verifyCredentials(BasicCredentials credentials, String userDN) throws LDAPException {
		LDAPConnection authenticatedConnection
			 = connectionFactory.getLDAPConnection(userDN, credentials.getPassword());
		authenticatedConnection.close();
	}

	private String dnFromUsername(String username) throws LDAPException {
		LDAPConnection connection = connectionFactory.getLDAPConnection();
		logger.info("We're here to get the DN");
		try {
			SearchRequest searchRequest = new SearchRequest(searchDN, SearchScope.SUB, "(sAMAccountName=" + username + ")");

			SearchResult sr = connection.search(searchRequest);
			logger.info("There were {} search results getting the DN", sr.getEntryCount());
			if (sr.getEntryCount() == 0) {
				throw new LDAPException(ResultCode.INVALID_CREDENTIALS);
			}

			logger.info("user DN for {} is {}", username, sr.getSearchEntries().get(0).getDN());
			return sr.getSearchEntries().get(0).getDN();
		} catch (Exception e) {
			logger.error("Failed to get the DN for {}", username, e);
			throw e;
		} finally {
			connection.close();
		}
	}

	private Set<String> rolesFromDN(String userDN) throws LDAPException {
		LDAPConnection connection = connectionFactory.getLDAPConnection();
		SearchRequest searchRequest = new SearchRequest(rolesDN,
			 SearchScope.SUB, Filter.createEqualityFilter("uniqueMember", userDN));
		Set<String> applicationAccessSet = new LinkedHashSet<String>();

		try {
			SearchResult sr = connection.search(searchRequest);

			for (SearchResultEntry sre : sr.getSearchEntries()) {
				try {
					X500Name myName = new X500Name(sre.getDN());
					applicationAccessSet.add(myName.getCommonName());
				} catch (IOException e) {
					logger.error("Could not create X500 Name for role:" + sre.getDN(), e);
				}
			}
		} catch (Exception e) {
			logger.error("Failed to get roles for user DN for {}", userDN, e);
			throw e;
		} finally {
			connection.close();
		}

		return applicationAccessSet;
	}

	private String sanitizeUsername(String username) {
		return username.replaceAll("[^A-Za-z0-9-_.]", "");
	}

	private static final Logger logger = LoggerFactory.getLogger(LdapAuthenticator.class);

	private final LdapConnectionFactory connectionFactory;
	private String searchDN;
	private String rolesDN;

}
