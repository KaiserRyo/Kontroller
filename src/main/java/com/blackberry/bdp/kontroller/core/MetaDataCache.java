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
package com.blackberry.bdp.kontroller.core;

import com.blackberry.bdp.kontroller.KontrollerConfiguration;
import com.blackberry.bdp.krackle.auth.AuthenticatedSocketBuilder;
import com.blackberry.bdp.krackle.auth.AuthenticationException;
import com.blackberry.bdp.krackle.meta.MetaData;
import java.util.HashMap;
import java.util.Map;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetaDataCache {

	private final KontrollerConfiguration config;
	private final AuthenticatedSocketBuilder sockBuilder;
	private final AuthenticatedSocketBuilder.Protocol kafkaSecurityProtocol;
	private long lastMetaDataRefresh;
	private MetaData kafkaMetaData;

	private static final Logger LOG = LoggerFactory.getLogger(MetaDataCache.class);

	public MetaDataCache(KontrollerConfiguration config)
		 throws LoginException, AuthenticationException, Exception {
		this.config = config;
		kafkaSecurityProtocol = AuthenticatedSocketBuilder.Protocol
			 .valueOf(config.getKafkaSecurityProtocol());

		switch (kafkaSecurityProtocol) {
			case SASL_PLAINTEXT: {
				LoginContext lc = new LoginContext(config.getJaasLoginContextName());
				lc.login();
				LOG.info("logged into security context: {}", config.getJaasLoginContextName());
				Map<String, Object> securityConfigs = new HashMap<>();
				securityConfigs.put("subject", lc.getSubject());
				securityConfigs.put("servicePrincipal", config.getKafkaServicePrincipal());
				sockBuilder = new AuthenticatedSocketBuilder(kafkaSecurityProtocol, securityConfigs);
				break;
			}
			case PLAINTEXT: {
				Map<String, Object> securityConfigs = new HashMap<>();
				sockBuilder = new AuthenticatedSocketBuilder(kafkaSecurityProtocol, securityConfigs);
				break;
			}
			default:
				throw new SecurityException(String.format(
					 "security protocol %s is not recognized", config.getKafkaSecurityProtocol()));
		}

		if (sockBuilder == null) {
			throw new Exception("unable to configure a valid authenticated socket builder");
		}

		this.kafkaMetaData = getMetaData();
	}

	public final MetaData getMetaData() {
		if (lastMetaDataRefresh == 0
			 || (System.currentTimeMillis() - lastMetaDataRefresh) / 1000
			 > config.getMetaDataCacheTtlSec()) {
			kafkaMetaData = MetaData.getMetaData(sockBuilder,
				 config.getKafkaSeedBrokers(),
				 "kontroller-metadata");
			lastMetaDataRefresh = System.currentTimeMillis();
			LOG.info("refreshed expired metadata");
		}
		return kafkaMetaData;
	}

}
