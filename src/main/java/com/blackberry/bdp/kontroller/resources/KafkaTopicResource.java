/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kaboom.api.KafkaBroker;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

import com.blackberry.bdp.kaboom.api.KafkaTopic;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;

@Path("/kafka-topic") @Produces(MediaType.APPLICATION_JSON)
public class KafkaTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicResource.class);
	private final KontrollerConfiguration config;
	private final List<KafkaBroker> kafkaBrokers;

	public KafkaTopicResource(KontrollerConfiguration config, List<KafkaBroker> kafkaBrokers) {		
		this.config = config;
		this.kafkaBrokers = kafkaBrokers;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KafkaTopic> getAll() throws Exception {
		return KafkaTopic.getAll(config.getKafkaSeedBrokers(), "API", kafkaBrokers);
	}
}
