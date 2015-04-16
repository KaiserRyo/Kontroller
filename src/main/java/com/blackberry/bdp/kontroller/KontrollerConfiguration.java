/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *
 * @author dariens
 */
public class KontrollerConfiguration extends Configuration {
	
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

	// The path in kaboom's zk for where assignments are kept
	@NotEmpty
	private String kaboomZkAssignmentPath;

	@JsonProperty
	public String getKaboomZkAssignmentPath() {
		return kaboomZkAssignmentPath;
	}

	@JsonProperty
	public void setKaboomZkAssignmentPath(String kaboomZkAssignmentPath) {
		this.kaboomZkAssignmentPath = kaboomZkAssignmentPath;
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

}
