/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller;

import com.bazaarvoice.dropwizard.assets.AssetsBundleConfiguration;
import com.bazaarvoice.dropwizard.assets.AssetsConfiguration;
import com.blackberry.bdp.dwauth.ldap.LdapConfiguration;
import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dariens
 */
public class KontrollerConfiguration extends Configuration implements AssetsBundleConfiguration {

	@Valid
	@NotNull
	@JsonProperty
	private final AssetsConfiguration assets = new AssetsConfiguration();

	@Override
	public AssetsConfiguration getAssetsConfiguration() {
		return assets;
	}

	// The path in kafka's zk for where brokers are kept
	@NotEmpty
	private String kafkaZkBrokerPath;

	@JsonProperty
	public String getKafkaZkBrokerPath() {
		return kafkaZkBrokerPath;
	}

	@JsonProperty
	public void setKafkaZkBrokerPath(String kafkaZkBrokerPath) {
		this.kafkaZkBrokerPath = kafkaZkBrokerPath;
	}

	// The path in kaboom's zk for where the RunningConfiguration is kept
	@NotEmpty
	private String kaboomZkConfigPath;

	@JsonProperty
	public String getKaboomZkConfigPath() {
		return kaboomZkConfigPath;
	}

	@JsonProperty
	public void setKaboomZkConfigPath(String kaboomZkConfigPath) {
		this.kaboomZkConfigPath = kaboomZkConfigPath;
	}

	// The path in kaboom's zk for where partition assignments are kept
	@NotEmpty
	private String kaboomZkPartitionAssignmentPath;

	@JsonProperty
	public String getKaboomZkPartitionAssignmentPath() {
		return kaboomZkPartitionAssignmentPath;
	}

	@JsonProperty
	public void setKaboomZkPartitionAssignmentPath(String kaboomZkPartitionAssignmentPath) {
		this.kaboomZkPartitionAssignmentPath = kaboomZkPartitionAssignmentPath;
	}

	// The path in kaboom's zk for where flag assignments are kept
	@NotEmpty
	private String kaboomZkFlagAssignmentPath;

	@JsonProperty
	public String getKaboomZkFlagAssignmentPath() {
		return kaboomZkFlagAssignmentPath;
	}

	@JsonProperty
	public void setKaboomZkFlagAssignmentPath(String kaboomZkFlagAssignmentPath) {
		this.kaboomZkFlagAssignmentPath = kaboomZkFlagAssignmentPath;
	}

	// The path in kaboom's zk for where clients are kept
	@NotEmpty
	private String kaboomZkClientPath;

	@JsonProperty
	public String getKaboomZkClientPath() {
		return kaboomZkClientPath;
	}

	@JsonProperty
	public void setKaboomZkClientPath(String kaboomZkClientPath) {
		this.kaboomZkClientPath = kaboomZkClientPath;
	}

	// The path in kaboom's zk for where topics are kept
	@NotEmpty
	private String kaboomZkTopicPath;

	@JsonProperty
	public String getKaboomZkTopicPath() {
		return kaboomZkTopicPath;
	}

	@JsonProperty
	public void setKaboomZkTopicPath(String kaboomZkTopicPath) {
		this.kaboomZkTopicPath = kaboomZkTopicPath;
	}

	// The connection string for kaboom's curator instance
	@NotEmpty
	private String kaboomZkConnString;

	@JsonProperty
	public String getKaboomZkConnString() {
		return kaboomZkConnString;
	}

	@JsonProperty
	public void setKaboomZkConnString(String kaboomZkConnString) {
		this.kaboomZkConnString = kaboomZkConnString;
	}

	// The seed brokers to be used for the Kafka API
	@NotEmpty
	private String kafkaSeedBrokers;

	@JsonProperty
	public String getKafkaSeedBrokers() {
		return kafkaSeedBrokers;
	}

	@JsonProperty
	public void setkafkaSeedBrokers(String kafkaSeedBrokers) {
		this.kafkaSeedBrokers = kafkaSeedBrokers;
	}

	// The connection string for kafka's curator instance
	@NotEmpty
	private String kafkaZkConnString;

	@JsonProperty
	public String getKafkaZkConnString() {
		return kafkaZkConnString;
	}

	@JsonProperty
	public void setKafkaZkConnString(String kafkaZkConnString) {
		this.kafkaZkConnString = kafkaZkConnString;
	}

	// The LdapConfiguration for Authentication/Authorization
	//@NotEmpty
	private LdapConfiguration ldapConfiguration;

	//@JsonProperty
	public LdapConfiguration getLdapConfiguration() {
		return ldapConfiguration;
	}

	//@JsonProperty
	public void setLdapConfiguration(LdapConfiguration ldapConfiguration) {
		this.ldapConfiguration = ldapConfiguration;
	}

	// The full LDAP distinguished name of the administrative group (write access)
	@NotEmpty
	private String adminGroupDn;

	@JsonProperty
	public String getAdminGroupDn() {
		return adminGroupDn;
	}

	@JsonProperty
	public void setAdminGroupDn(String adminGroupDn) {
		this.adminGroupDn = adminGroupDn;
	}
}
