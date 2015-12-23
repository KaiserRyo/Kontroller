/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kontroller.core.MetaDataCache;
import com.codahale.metrics.annotation.Timed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.krackle.meta.Topic;
import java.util.Collection;

@Path("/kafka-topic") @Produces(MediaType.APPLICATION_JSON)
public class KafkaTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicResource.class);
	private final MetaDataCache kafkaMetaDataCache;

	public KafkaTopicResource(MetaDataCache kafkaMetaDataCache) {
		this.kafkaMetaDataCache = kafkaMetaDataCache;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public Collection<Topic> getAll() throws Exception {
		return kafkaMetaDataCache.getMetaData().getTopics().values();
	}
}
