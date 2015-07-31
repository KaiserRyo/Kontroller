package com.blackberry.bdp.kontroller;

import com.bazaarvoice.dropwizard.assets.ConfiguredAssetsBundle;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import com.blackberry.bdp.dwauth.ldap.AccessDeniedHandler;
import com.blackberry.bdp.dwauth.ldap.LdapAuthenticator;
import com.blackberry.bdp.dwauth.ldap.LdapConnectionFactory;
import com.blackberry.bdp.dwauth.ldap.User;
import com.blackberry.bdp.dwauth.ldap.LdapConfiguration;
import com.blackberry.bdp.dwauth.ldap.LdapHealthCheck;
import com.blackberry.bdp.kontroller.resources.AuthenticationStatusResource;
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
	public void run(KontrollerConfiguration config, Environment environment) throws AuthenticationException, Exception {
		
		LdapConfiguration ldapConfiguration = config.getLdapConfiguration();
		LdapConnectionFactory ldapConnFactory = new LdapConnectionFactory(ldapConfiguration);
		LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(ldapConnFactory, ldapConfiguration);
		Authenticator<BasicCredentials, User> cachedAuthenticator = new CachingAuthenticator<>(
			 environment.metrics(),
			 ldapAuthenticator,
			 config.getLdapConfiguration().getCachePolicy());

		environment.jersey().register(AuthFactory.binder(new BasicAuthFactory<>(cachedAuthenticator, "Kontroller", User.class)));
		environment.jersey().register(new AccessDeniedHandler());

		kaboomCurator = CuratorBuilder.build(config.getKaboomZkConnString(), true);
		kafkaCurator = CuratorBuilder.build(config.getKafkaZkConnString(), true);
		
		// Public Resources

		final AuthenticationStatusResource authResource;
		authResource = new AuthenticationStatusResource(config);
		environment.jersey().register(authResource);
		
		final KafkaBrokerResource kafkaBrokerResource;
		kafkaBrokerResource = new KafkaBrokerResource(kafkaCurator, config);
		environment.jersey().register(kafkaBrokerResource);

		final KafkaTopicResource kafkaTopicResource;
		kafkaTopicResource = new KafkaTopicResource(config);
		environment.jersey().register(kafkaTopicResource);

		final KaBoomRunningConfigResource kaboomRunningConfigResource;		
		kaboomRunningConfigResource = new KaBoomRunningConfigResource(kaboomCurator, config);
		environment.jersey().register(kaboomRunningConfigResource);

		final KaBoomTopicConfigResource kaboomTopicConfigResource;
		kaboomTopicConfigResource = new KaBoomTopicConfigResource(kaboomCurator, config);
		environment.jersey().register(kaboomTopicConfigResource);

		final KaBoomTopicResource kaboomTopicResource;
		kaboomTopicResource = new KaBoomTopicResource(kaboomCurator, config);
		environment.jersey().register(kaboomTopicResource);

		final KaBoomClientResource kaboomClientResource;
		kaboomClientResource = new KaBoomClientResource(kaboomCurator, config);
		environment.jersey().register(kaboomClientResource);

		// Health Checks
		
		final CuratorHealthCheck kaboomZkHealthCheck = new CuratorHealthCheck(kaboomCurator);
		environment.healthChecks().register("kaboomCurator", kaboomZkHealthCheck);
		
		final CuratorHealthCheck kafkaZkHealthCheck = new CuratorHealthCheck(kafkaCurator);		
		environment.healthChecks().register("kafkaCurator", kafkaZkHealthCheck);

		final LdapHealthCheck ldapHealthCheck = new LdapHealthCheck(ldapConnFactory);
		environment.healthChecks().register("ldap", ldapHealthCheck);
	}
}
