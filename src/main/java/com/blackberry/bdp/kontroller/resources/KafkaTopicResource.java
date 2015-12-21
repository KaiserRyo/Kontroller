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

import com.blackberry.bdp.krackle.meta.MetaData;
import com.blackberry.bdp.krackle.meta.Topic;
import java.util.Map;

@Path("/kafka-topic") @Produces(MediaType.APPLICATION_JSON)
public class KafkaTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicResource.class);
	private final MetaData kafkaMetaData;

	public KafkaTopicResource(MetaData kafkaMetaData) {
		this.kafkaMetaData = kafkaMetaData;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public Map<String, Topic> getAll() throws Exception {
		return kafkaMetaData.getTopics();
	}
}
