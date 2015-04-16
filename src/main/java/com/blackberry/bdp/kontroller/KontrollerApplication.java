/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.blackberry.bdp.kontroller.resources.KafkaTopicResource;
import com.blackberry.bdp.kontroller.resources.KafkaBrokerResource;
import com.blackberry.bdp.kontroller.resources.KaBoomTopicResource;
import com.blackberry.bdp.kontroller.resources.KaBoomClientResource;
import com.blackberry.bdp.kontroller.health.CuratorHealthCheck;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dariens
 */
public class KontrollerApplication extends Application<KontrollerConfiguration>
{
	private static final Logger LOG = LoggerFactory.getLogger(KontrollerApplication.class);
	private CuratorFramework kaboomCurator;
	private CuratorFramework kafkaCurator;

	public static void main(String[] args) throws Exception {
		new KontrollerApplication().run(args);
	}

	@Override
	public String getName() {
		return "hello-world";
	}

	@Override
	public void initialize(Bootstrap<KontrollerConfiguration> bootstrap) {
		// nothing to do yet
	}

	@Override
	public void run(KontrollerConfiguration configuration, Environment environment) {
		
		kaboomCurator = CuratorBuilder.build(configuration.getKaboomZkConnString(), true);
		kafkaCurator = CuratorBuilder.build(configuration.getKafkaZkConnString(), true);
		
		// Resources	
		
		final KafkaBrokerResource kafkaBrokerResource = new KafkaBrokerResource(kafkaCurator, 
			 configuration.getKafkaZkBrokerPath());
		environment.jersey().register(kafkaBrokerResource);

		final KafkaTopicResource kafkaTopicResource = new KafkaTopicResource(configuration.getKafkaSeedBrokers());
		environment.jersey().register(kafkaTopicResource);

		final KaBoomTopicResource kaboomTopicResource = new KaBoomTopicResource(kaboomCurator, 
			 configuration.getKaboomZkTopicPath(),
			 configuration.getKaboomZkAssignmentPath());
		environment.jersey().register(kaboomTopicResource);
		
		final KaBoomClientResource kaboomClientResource = new KaBoomClientResource(kaboomCurator,
			 configuration.getKaboomZkClientPath());		
		environment.jersey().register(kaboomClientResource);

		// Health Checks
		
		final CuratorHealthCheck kaboomZkHealthCheck = new CuratorHealthCheck(kaboomCurator);
		environment.healthChecks().register("kaboomCurator", kaboomZkHealthCheck);
		
		final CuratorHealthCheck kafkaZkHealthCheck = new CuratorHealthCheck(kafkaCurator);
		environment.healthChecks().register("kafkaCurator", kafkaZkHealthCheck);		
	}
}
