/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kontroller.core.MetaDataCache;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.krackle.meta.Broker;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kafka-broker") @Produces(MediaType.APPLICATION_JSON)
public class KafkaBrokerResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaBrokerResource.class);
	private final MetaDataCache kafkaMetaDataCache;

	public KafkaBrokerResource(MetaDataCache kafkaMetaDataCache) {
		this.kafkaMetaDataCache = kafkaMetaDataCache;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public Collection<Broker> getAll() throws Exception {
		return kafkaMetaDataCache.getMetaData().getBrokers().values();
	}
}