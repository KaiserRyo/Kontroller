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
package com.blackberry.bdp.kontroller;

import org.apache.curator.RetryPolicy;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.CuratorFramework;

public class CuratorBuilder
{
	/**
	 * Builds a curator object from zkConnString
	 * @param zkConnString in the format of host1.com,host2.com,host3.com:/namespaceRoot
	 * @param start start the curator framework after building it
	 * @return
	 */
	public static CuratorFramework build(String zkConnString, boolean start) {
		String[] connStringAndPrefix = zkConnString.split("/", 2);
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);		
		CuratorFramework newCurator;
		
		if (connStringAndPrefix.length == 1) {
			newCurator = CuratorFrameworkFactory.newClient(zkConnString, retryPolicy);
		} 
		else {
			newCurator = CuratorFrameworkFactory.builder()
				 .namespace(connStringAndPrefix[1])
				 .connectString(connStringAndPrefix[0]).retryPolicy(retryPolicy)
				 .build();
		}
		
		if (start) {
			newCurator.start();
		}		
		
		return newCurator;		
	}

}
