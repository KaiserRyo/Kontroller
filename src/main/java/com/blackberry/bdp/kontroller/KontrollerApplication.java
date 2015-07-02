/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blackberry.bdp.kontroller;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.blackberry.bdp.kontroller.ldap.LdapAuthenticator;
import com.blackberry.bdp.kontroller.ldap.LdapConnectionFactory;
import com.blackberry.bdp.kontroller.ldap.User;
import com.blackberry.bdp.kontroller.ldap.LdapConfiguration;
import com.blackberry.bdp.kontroller.resources.KafkaTopicResource;
import com.blackberry.bdp.kontroller.resources.KafkaBrokerResource;
import com.blackberry.bdp.kontroller.resources.KaBoomTopicResource;
import com.blackberry.bdp.kontroller.resources.KaBoomRunningConfigResource;
import com.blackberry.bdp.kontroller.resources.KaBoomTopicConfigResource;
import com.blackberry.bdp.kontroller.resources.KaBoomClientResource;
import com.blackberry.bdp.kontroller.health.CuratorHealthCheck;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.auth.basic.BasicCredentials;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author dariens
 */
public class KontrollerApplication extends Application<KontrollerConfiguration> {

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
		bootstrap.addBundle(new ConfiguredAssetsBundle("/assets", "/", "index.html", "static-content"));
	}

	@Override
	public void run(KontrollerConfiguration configuration, Environment environment) throws AuthenticationException {

		LdapConfiguration ldapConfiguration = configuration.getLdapConfiguration();
		
		LdapConnectionFactory ldapConnFactory = new LdapConnectionFactory(
			 ldapConfiguration.getUri().toString(),
			 389,
			 ldapConfiguration.getUsername(),
			 ldapConfiguration.getPassword()
		);
		
		LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(
			 ldapConnFactory,
			 ldapConfiguration.getUserFilter(),
			 ldapConfiguration.getGroupFilter()
		);
		
		Authenticator<BasicCredentials, User> cachedAuthenticator = new CachingAuthenticator<>(
			 environment.metrics(),
			 ldapAuthenticator,
			 configuration.getLdapConfiguration().getCachePolicy()
		);

		environment.jersey().register(AuthFactory.binder(new BasicAuthFactory<>(cachedAuthenticator, "realm", User.class)));
		
		//environment.healthChecks().register("ldap", new LdapHealthCheck<>(new ResourceAuthenticator(new LdapCanAuthenticate(ldapConfiguration))));

		kaboomCurator = CuratorBuilder.build(configuration.getKaboomZkConnString(), true);
		kafkaCurator = CuratorBuilder.build(configuration.getKafkaZkConnString(), true);

		// Publically Accesible Resources	
		final KafkaBrokerResource kafkaBrokerResource = new KafkaBrokerResource(
			 kafkaCurator,
			 configuration.getKafkaZkBrokerPath());

		final KafkaTopicResource kafkaTopicResource = new KafkaTopicResource(
			 configuration.getKafkaSeedBrokers());

		final KaBoomRunningConfigResource kaboomRunningResource = new KaBoomRunningConfigResource(
			 kaboomCurator,
			 configuration.getKaboomZkConfigPath());

		final KaBoomTopicConfigResource kaboomTopicConfigResource = new KaBoomTopicConfigResource(
			 kaboomCurator,
			 configuration.getKaboomZkTopicPath());

		final KaBoomTopicResource kaboomTopicResource = new KaBoomTopicResource(
			 kaboomCurator,
			 configuration.getKaboomZkTopicPath(),
			 configuration.getKaboomZkAssignmentPath());

		final KaBoomClientResource kaboomClientResource = new KaBoomClientResource(
			 kaboomCurator,
			 configuration.getKaboomZkClientPath());

		// Health Checks
		final CuratorHealthCheck kaboomZkHealthCheck = new CuratorHealthCheck(kaboomCurator);
		final CuratorHealthCheck kafkaZkHealthCheck = new CuratorHealthCheck(kafkaCurator);

		// Registrations
		environment.jersey().register(kafkaTopicResource);
		environment.jersey().register(kafkaBrokerResource);
		environment.jersey().register(kaboomTopicResource);
		environment.jersey().register(kaboomClientResource);
		environment.jersey().register(kaboomRunningResource);
		environment.jersey().register(kaboomTopicConfigResource);

		environment.healthChecks().register("kaboomCurator", kaboomZkHealthCheck);
		environment.healthChecks().register("kafkaCurator", kafkaZkHealthCheck);
	}

}
