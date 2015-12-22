/*
 * Copyright 2015 dariens.
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

import com.blackberry.bdp.kaboom.api.KaBoomClient;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.blackberry.bdp.kaboom.api.KaBoomTopic;
import com.blackberry.bdp.kontroller.KontrollerConfiguration;
import com.blackberry.bdp.kontroller.core.MetaDataCache;

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
	private final MetaDataCache kafkaMetaDataCache;

	public KaBoomTopicResource(CuratorFramework curator,
		 KontrollerConfiguration config,
		 List<KaBoomClient> kaboomClients,
		 MetaDataCache kafkaMetaDataCache) {
		this.curator = curator;
		this.config = config;
		this.kaboomClients = kaboomClients;
		this.kafkaMetaDataCache = kafkaMetaDataCache;
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public List<KaBoomTopic> getAll() throws Exception {
		return KaBoomTopic.getAll(
			 kaboomClients,
			 kafkaMetaDataCache.getMetaData(),
			 curator,
			 config.getKaboomZkTopicPath(),
			 config.getKaboomZkPartitionAssignmentPath(),
			 config.getKaboomZkFlagAssignmentPath());
	}
}
