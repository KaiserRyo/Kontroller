/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kontroller.core.KafkaBrokers;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import com.blackberry.bdp.kaboom.api.KafkaBroker;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kafka-broker") @Produces(MediaType.APPLICATION_JSON)
public class KafkaBrokerResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaBrokerResource.class);
	private final AtomicLong counter;
	private final CuratorFramework curator;
	private final String kafkaZkBrokerPath;

	public KafkaBrokerResource(CuratorFramework curator, String kafkaZkBrokerPath) {
		this.curator = curator;
		this.kafkaZkBrokerPath = kafkaZkBrokerPath;
		this.counter = new AtomicLong();
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public KafkaBrokers getAll() throws Exception {
		final List<KafkaBroker> clients = KafkaBroker.getAll(curator, kafkaZkBrokerPath);
		return new KafkaBrokers(counter.incrementAndGet(), clients);
	}
}
