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
import com.blackberry.bdp.kaboom.api.KaBoomClient;
import com.blackberry.bdp.kontroller.resources.AuthenticationStatusResource;
import com.blackberry.bdp.kontroller.resources.KafkaTopicResource;
import com.blackberry.bdp.kontroller.resources.KafkaBrokerResource;
import com.blackberry.bdp.kontroller.resources.KaBoomTopicResource;
import com.blackberry.bdp.kontroller.resources.KaBoomRunningConfigResource;
import com.blackberry.bdp.kontroller.resources.KaBoomTopicConfigResource;
import com.blackberry.bdp.kontroller.resources.KaBoomClientResource;
import com.blackberry.bdp.kontroller.health.CuratorHealthCheck;
import com.blackberry.bdp.krackle.auth.AuthenticatedSocketBuilder;
import com.blackberry.bdp.krackle.meta.MetaData;
import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.CachingAuthenticator;
import io.dropwizard.auth.basic.BasicAuthFactory;
import io.dropwizard.auth.basic.BasicCredentials;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.security.auth.login.LoginContext;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KontrollerApplication extends Application<KontrollerConfiguration> {

	private static final Logger LOG = LoggerFactory.getLogger(KontrollerApplication.class);
	private CuratorFramework kaboomCurator;
	private MetaData kafkaMetaData;
	private List<KaBoomClient> kaboomClients;

	public static void main(String[] args) throws Exception {
		new KontrollerApplication().run(args);
	}

	@Override
	public String getName() {
		return "Kontroller v0.0.3-SNAPSHOT";
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

		AuthenticatedSocketBuilder sockBuilder = null;

		switch (config.getKafkaSecurityProtocol()) {
			case "SASL_PLAINTEXT": {
				LoginContext lc = new LoginContext(config.getJaasLoginContextName());
				lc.login();
				LOG.info("logged into security context: {}", config.getJaasLoginContextName());
				Map<String, Object> securityConfigs = new HashMap<>();
				securityConfigs.put("subject", lc.getSubject());
				securityConfigs.put("servicePrincipal", config.getKafkaServicePrincipal());
				sockBuilder = new AuthenticatedSocketBuilder(config.getKafkaSecurityProtocol(),
					 securityConfigs);
				break;
			}
			default:
				throw new SecurityException(String.format(
					 "security protocol %s is not recognized", config.getKafkaSecurityProtocol()));
		}

		if (sockBuilder == null) {
			throw new Exception("unable to configure a valid authenticated socket builder");
		}

		kaboomCurator = CuratorBuilder.build(config.getKaboomZkConnString(), true);

		kafkaMetaData = MetaData.getMetaData(sockBuilder,
			 config.getKafkaSeedBrokers(),
			 "kontroller");

		kaboomClients = KaBoomClient.getAll(KaBoomClient.class,
			 kaboomCurator,
			 config.getKaboomZkClientPath());

		// Public Resources
		final AuthenticationStatusResource authResource;
		authResource = new AuthenticationStatusResource(config);
		environment.jersey().register(authResource);

		final KafkaBrokerResource kafkaBrokerResource;
		kafkaBrokerResource = new KafkaBrokerResource(kafkaMetaData);
		environment.jersey().register(kafkaBrokerResource);

		final KafkaTopicResource kafkaTopicResource;
		kafkaTopicResource = new KafkaTopicResource(kafkaMetaData);
		environment.jersey().register(kafkaTopicResource);

		final KaBoomRunningConfigResource kaboomRunningConfigResource;
		kaboomRunningConfigResource = new KaBoomRunningConfigResource(
			 kaboomCurator,
			 config);
		environment.jersey().register(kaboomRunningConfigResource);

		final KaBoomTopicConfigResource kaboomTopicConfigResource;
		kaboomTopicConfigResource = new KaBoomTopicConfigResource(
			 kaboomCurator,
			 config);
		environment.jersey().register(kaboomTopicConfigResource);

		final KaBoomTopicResource kaboomTopicResource;
		kaboomTopicResource = new KaBoomTopicResource(kaboomCurator,
			 config,
			 kaboomClients,
			 kafkaMetaData);
		environment.jersey().register(kaboomTopicResource);

		final KaBoomClientResource kaboomClientResource;
		kaboomClientResource = new KaBoomClientResource(kaboomCurator, config);
		environment.jersey().register(kaboomClientResource);

		// Health Checks
		final CuratorHealthCheck kaboomZkHealthCheck = new CuratorHealthCheck(kaboomCurator);
		environment.healthChecks().register("kaboomCurator", kaboomZkHealthCheck);

		final LdapHealthCheck ldapHealthCheck = new LdapHealthCheck(ldapConnFactory);
		environment.healthChecks().register("ldap", ldapHealthCheck);
	}

}
