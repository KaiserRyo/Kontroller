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
import com.unboundid.ldap.sdk.LDAPSearchException;
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

/**
 *
 * @author dariens
 */
public class LdapAuthenticator implements Authenticator<BasicCredentials, User> {

	private static final Logger LOG = LoggerFactory.getLogger(LdapAuthenticator.class);
	private final LdapConnectionFactory connectionFactory;
	private final LdapConfiguration config;

	public LdapAuthenticator(LdapConnectionFactory connectionFactory, LdapConfiguration config) {
		this.connectionFactory = connectionFactory;
		this.config = config;
	}

	@Override
	public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
		try {
			String username = sanitizeUsername(credentials.getUsername());
			String userDN = dnFromUsername(username);
			verifyCredentials(credentials, userDN);
			Set<String> roles = membershipsFromBaseDNs(userDN);
			return Optional.fromNullable(new User(username, roles));
		} catch (LDAPException le) {
			if (invalidCredentials(le)) {
				throw new AuthenticationException("Could not connect to LDAP server", le);
			} else {
				LOG.error("Error logging in: ", le);
				return Optional.absent();
			}
		} catch (IOException ioe) {
			LOG.error("Error logging in: ", ioe);
			return Optional.absent();
		}
	}

	private boolean invalidCredentials(LDAPException le) {
		return le.getResultCode() != ResultCode.INVALID_CREDENTIALS;
	}

	private void verifyCredentials(BasicCredentials credentials, String userDN)
		 throws LDAPException, AuthenticationException {
		LDAPConnection authenticatedConnection
			 = connectionFactory.getLDAPConnection(userDN, credentials.getPassword());
		authenticatedConnection.close();
	}

	private String dnFromUsername(String username) throws LDAPException, AuthenticationException {
		LDAPConnection connection = connectionFactory.getLDAPConnection();
		try {
			SearchRequest searchRequest = new SearchRequest(config.getUserFilter(),
				 SearchScope.SUB,
				 String.format("(sAMAccountName=%s)", username));
			SearchResult sr = connection.search(searchRequest);
			if (sr.getEntryCount() == 0) {
				throw new LDAPException(ResultCode.INVALID_CREDENTIALS);
			}
			LOG.info("user DN for {} is {}", username, sr.getSearchEntries().get(0).getDN());
			return sr.getSearchEntries().get(0).getDN();
		} catch (Exception e) {
			LOG.error("Failed to get the DN for {}", username, e);
			throw e;
		} finally {
			connection.close();
		}
	}

	private Set<String> membershipsFromBaseDNs(String userDN)
		 throws LDAPException, LDAPSearchException, AuthenticationException, IOException {
		Set<String> groupMembershipSet = new LinkedHashSet<>();
		LDAPConnection connection = connectionFactory.getLDAPConnection();
		try {
			LOG.info("Search baseDN {} for membership", config.getGroupBaseDN());
			recursiveMemberShipSetBuilder(
				 connection,
				 groupMembershipSet,
				 config.getGroupBaseDN(),
				 userDN);
		} finally {
			connection.close();
		}
		return groupMembershipSet;
	}

	private void recursiveMemberShipSetBuilder(
		 LDAPConnection connection,
		 Set<String> membershipSet,
		 String baseDN,
		 String nodeDN) throws LDAPSearchException, IOException {
		SearchRequest searchRequest = new SearchRequest(
			 baseDN,
			 SearchScope.SUB,
			 Filter.createEqualityFilter("member", nodeDN));
		SearchResult sr = connection.search(searchRequest);
		LOG.info("There are {} memberships associated to {}", sr.getEntryCount(), nodeDN);
		for (SearchResultEntry sre : sr.getSearchEntries()) {
			if (!membershipSet.contains(sre.getDN())) {
				LOG.info("{} is a member of {}", nodeDN, sre.getDN());
				membershipSet.add(sre.getDN());
				recursiveMemberShipSetBuilder(connection, membershipSet, baseDN, sre.getDN());
			}
		}
	}

	private String sanitizeUsername(String username) {
		return username.replaceAll("[^A-Za-z0-9-_.]", "");
	}

}
