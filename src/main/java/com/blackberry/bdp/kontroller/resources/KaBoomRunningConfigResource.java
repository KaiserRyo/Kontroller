/** Copyright 2015 BlackBerry, Limited.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package com.blackberry.bdp.kontroller.resources;

//import com.blackberry.bdp.kontroller.core.KaBoomClients;
import com.blackberry.bdp.common.exception.MissingConfigurationException;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.dwauth.ldap.User;
import com.blackberry.bdp.dwauth.ldap.AccessDeniedException;
import com.blackberry.bdp.kaboom.api.KaBoomTopicConfig;

import com.blackberry.bdp.kaboom.api.RunningConfig;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;

import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;

import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-config")
@Produces(MediaType.APPLICATION_JSON)
public class KaBoomRunningConfigResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomRunningConfigResource.class);
	private final CuratorFramework curator;
	private final KontrollerConfiguration config;	

	public KaBoomRunningConfigResource(CuratorFramework curator, 
		 KontrollerConfiguration config) {		
		this.curator = curator;
		this.config = config;
	}

	/**
	 * Fetches the current KaBoom RunningConfig from ZK if present or a default initialized RunningConfig if none is found in ZK
	 *
	 * @param user
	 * @return
	 * @throws Exception
	 */
	@GET @Timed @Produces(value = MediaType.APPLICATION_JSON)
	public RunningConfig get(@Auth User user) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
		try {			
			return RunningConfig.get(RunningConfig.class, curator, config.getKaboomZkConfigPath());
		} catch (MissingConfigurationException mce) {
			LOG.info("Missing configuration at {}, returning default {}",
				 config.getKaboomZkConfigPath(),
				 RunningConfig.class);
			return new RunningConfig();
		}
	}

	@POST
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public RunningConfig save(@Auth User user, RunningConfig runningConfig) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}				
		// Objects we get back in from our external API won't have 
		// curator/zkPath set, so let's set them from our resource config
		runningConfig.setCurator(curator);
		runningConfig.setZkPath(config.getKaboomZkConfigPath());
		// save, updates the version, so just return it.
		runningConfig.save();
		return runningConfig;
	}
	
	/**
	 * Deletes the KaBoomRunningConfig
	 * @throws Exception
	 */
	@DELETE
	@Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})	
	public void delete(@Auth User user) throws Exception {
		if (!user.getMemberships().contains(config.getAdminGroupDn())) {
			LOG.error("User {} is not a member of group {}", user.getName(), config.getAdminGroupDn());
			throw new AccessDeniedException();
		}						
		RunningConfig.delete(curator, config.getKaboomZkConfigPath());
		LOG.info("Deleted object at path {}", config.getKaboomZkConfigPath());
	}	
}
