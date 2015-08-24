/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kaboom.api.KaBoomClient;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomTopic;
import com.blackberry.bdp.kaboom.api.KafkaTopic;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-topic") @Produces(MediaType.APPLICATION_JSON)
public class KaBoomTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomTopicResource.class);
	
	private final CuratorFramework curator;
	private final KontrollerConfiguration config;
	private final List<KaBoomClient> kaboomClients;
	private final List<KafkaTopic> kafkaTopics;
	
	//private final String kaboomZkTopicPath;
	//private final String kaboomZkAssignmentPath;

	public KaBoomTopicResource(CuratorFramework curator, 
		 KontrollerConfiguration config, 
		 List<KaBoomClient> kaboomClients, 
		 List<KafkaTopic> kafkaTopics) {
		this.curator = curator;
		this.config = config;
		this.kaboomClients = kaboomClients;
		this.kafkaTopics = kafkaTopics;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KaBoomTopic> getAll() throws Exception {
		return KaBoomTopic.getAll(
			 kaboomClients,
			 kafkaTopics,
			 curator, 
			 config.getKaboomZkTopicPath(),
			 config.getKaboomZkPartitionAssignmentPath(), 
			 config.getKaboomZkFlagAssignmentPath());
	}
}
