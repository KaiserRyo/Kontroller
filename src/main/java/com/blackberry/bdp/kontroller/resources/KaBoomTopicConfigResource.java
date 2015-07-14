/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

//import com.blackberry.bdp.common.versioned.MissingConfigurationException;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomTopicConfig;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-topic-config")
@Produces(MediaType.APPLICATION_JSON)
public class KaBoomTopicConfigResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomTopicConfigResource.class);
	private final CuratorFramework curator;
	private final KontrollerConfiguration config;

	/**
	 * Creates a new KaBoomTopicConfigResource 
	 * @param curator
	 * @param config
	 */
	public KaBoomTopicConfigResource(CuratorFramework curator, KontrollerConfiguration config) {
		this.curator = curator;
		this.config = config;
	}

	/**
	 * Fetches the current KaBoom RunningConfig from ZK if present or 
	 * a default initialized KaBoomTopicConfig if none is found in ZK
	 * @return
	 * @throws Exception
	 */
	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KaBoomTopicConfig> getAll() throws Exception {
		return KaBoomTopicConfig.getAll(KaBoomTopicConfig.class, curator, config.getKaboomZkTopicPath());
	}

	/**
	 * Saves a new KaBoomTopicConfiguration
	 * @param topicConfig
	 * @return
	 * @throws Exception
	 */
	@POST @Timed @Produces(value = MediaType.APPLICATION_JSON)
	public KaBoomTopicConfig create(KaBoomTopicConfig topicConfig) throws Exception {
		topicConfig.setCurator(curator);
		topicConfig.setZkPath(String.format("%s/%s", config.getKaboomZkTopicPath(), topicConfig.getId()));
		topicConfig.save();
		return topicConfig;
	}	
	
	/**
	 * Saves an existing KaBoomTopicConfig
	 * @param topicConfig
	 * @return
	 * @throws Exception
	 */
	@PUT @Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public KaBoomTopicConfig save(KaBoomTopicConfig topicConfig) throws Exception {
		topicConfig.setCurator(curator);
		topicConfig.setZkPath(String.format("%s/%s", config.getKaboomZkTopicPath(), topicConfig.getId()));
		topicConfig.save();
		return topicConfig;
	}
	
	/**
	 * Deletes a KaBoomTopicConfig
	 * @param id
	 * @throws Exception
	 */
	@DELETE @Path("{id}")
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})	
	public void delete(@PathParam("id") String id) throws Exception {
		String path = String.format("%s/%s", config.getKaboomZkTopicPath(), id);
		KaBoomTopicConfig.delete(curator, path);
		LOG.info("Deleted object at path {}", path);
	}
}