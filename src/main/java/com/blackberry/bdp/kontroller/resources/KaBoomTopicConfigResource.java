/*
 * Copyright 2015 BlackBerry Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.dwauth.ldap.AccessDeniedException;
import com.blackberry.bdp.dwauth.ldap.User;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomTopicConfig;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;
import io.dropwizard.auth.Auth;
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
	public List<KaBoomTopicConfig> getAll(@Auth User user) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
		return KaBoomTopicConfig.getAll(KaBoomTopicConfig.class, curator, config.getKaboomZkTopicPath());
	}

	/**
	 * Saves a new KaBoomTopicConfiguration
	 * @param topicConfig
	 * @return
	 * @throws Exception
	 */
	@POST @Timed @Produces(value = MediaType.APPLICATION_JSON)
	public KaBoomTopicConfig create(@Auth User user, KaBoomTopicConfig topicConfig) throws Exception {		
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
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
	public KaBoomTopicConfig save(@Auth User user, KaBoomTopicConfig topicConfig) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
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
	public void delete(@Auth User user, @PathParam("id") String id) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
		String path = String.format("%s/%s", config.getKaboomZkTopicPath(), id);
		KaBoomTopicConfig.delete(curator, path);
		LOG.info("Deleted object at path {}", path);
	}
}