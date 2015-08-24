/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

//import com.blackberry.bdp.kontroller.core.KaBoomClients;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomClient;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-client") @Produces(MediaType.APPLICATION_JSON)
public class KaBoomClientResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomClientResource.class);	
	private final CuratorFramework curator;
	private final KontrollerConfiguration config;

	public KaBoomClientResource(CuratorFramework curator, KontrollerConfiguration config) {
		this.curator = curator;
		this.config = config;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KaBoomClient> getAll() throws Exception {
		return KaBoomClient.getAll(KaBoomClient.class, curator, config.getKaboomZkClientPath());
	}
}
