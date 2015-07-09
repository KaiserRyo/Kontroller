/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KafkaBroker;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kafka-broker") @Produces(MediaType.APPLICATION_JSON)
public class KafkaBrokerResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaBrokerResource.class);
	private final KontrollerConfiguration config;
	private final CuratorFramework curator;
	
	public KafkaBrokerResource(CuratorFramework curator, KontrollerConfiguration config) {
		this.curator = curator;
		this.config = config;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KafkaBroker> getAll() throws Exception {
		return KafkaBroker.getAll(curator, config.getKafkaZkBrokerPath());
	}
}
