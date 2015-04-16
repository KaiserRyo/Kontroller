/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import com.blackberry.bdp.kaboom.api.KafkaTopic;
import com.blackberry.bdp.kontroller.core.KafkaTopics;

@Path("/kafka-topic") @Produces(MediaType.APPLICATION_JSON)
public class KafkaTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicResource.class);
	private final AtomicLong counter;
	private final String kafkaSeedBrokers;	

	public KafkaTopicResource(String kafkaSeedBrokers) {		
		this.kafkaSeedBrokers = kafkaSeedBrokers;
		this.counter = new AtomicLong();
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public KafkaTopics getAll() throws Exception {
		final List<KafkaTopic> topics = KafkaTopic.getAll(kafkaSeedBrokers, "API");
		return new KafkaTopics(counter.incrementAndGet(), topics);
	}
}
