/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.common.versioned.MissingConfigurationException;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomTopicConfig;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-topic-config")
@Produces(MediaType.APPLICATION_JSON)
public class KaBoomTopicConfigResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomTopicConfigResource.class);
	private final CuratorFramework curator;
	private final String kaboomZkTopicPath;

	public KaBoomTopicConfigResource(CuratorFramework curator, String kaboomZkTopicPath) {
		this.curator = curator;
		this.kaboomZkTopicPath = kaboomZkTopicPath;
	}

	/**
	 * Fetches the current KaBoom RunningConfig from ZK if present or 
	 * a default initialized KaBoomTopicConfig if none is found in ZK
	 *
	 * @return
	 * @throws Exception
	 */
	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KaBoomTopicConfig> getAll() throws Exception {
		return KaBoomTopicConfig.getAll(curator, kaboomZkTopicPath);
	}
	
	@PUT @Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public KaBoomTopicConfig save(KaBoomTopicConfig topicConfig) throws Exception {
		LOG.info("We're here in save()");
		// Objects we get back in from our external API won't have 
		// curator/zkPath set, so let's set them from our resource config
		// Note that we need to change the path to reflect the topic name
		// which is the same as the ID.
		topicConfig.setCurator(curator);
		topicConfig.setZkPath(String.format("%s/%s", kaboomZkTopicPath, topicConfig.id));
		// save, updates the version, so just return it.
		topicConfig.save();
		return topicConfig;
	}
}
