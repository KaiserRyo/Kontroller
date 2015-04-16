/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller.resources;

import com.blackberry.bdp.kontroller.core.KaBoomTopics;
import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;

import com.blackberry.bdp.kaboom.api.KaBoomTopic;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/kaboom-topic") @Produces(MediaType.APPLICATION_JSON)
public class KaBoomTopicResource {

	private static final Logger LOG = LoggerFactory.getLogger(KaBoomTopicResource.class);
	private final AtomicLong counter;
	private final CuratorFramework curator;
	private final String kaboomZkTopicPath;
	private final String kaboomZkAssignmentPath;

	public KaBoomTopicResource(CuratorFramework curator, 
		 String kaboomZkTopicPath,
		 String kaboomZkAssignmentPath) {
		this.curator = curator;
		this.kaboomZkTopicPath = kaboomZkTopicPath;
		this.kaboomZkAssignmentPath = kaboomZkAssignmentPath;
		this.counter = new AtomicLong();
	}

	@GET 	@Timed @Produces(value = MediaType.APPLICATION_JSON)
	public KaBoomTopics getAll() throws Exception {
		final List<KaBoomTopic> topics = KaBoomTopic.getAll(curator, 
			 kaboomZkTopicPath,
			 kaboomZkAssignmentPath);
		return new KaBoomTopics(counter.incrementAndGet(), topics);
	}
}
