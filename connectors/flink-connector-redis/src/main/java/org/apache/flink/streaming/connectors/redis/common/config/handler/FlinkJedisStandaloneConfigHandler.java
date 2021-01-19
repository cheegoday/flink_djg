/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.redis.common.config.handler;

import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisConfigBase;
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig;
import org.apache.flink.streaming.connectors.redis.common.hanlder.FlinkJedisConfigHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator.REDIS_MODE;
import static org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator.REDIS_MODE_STANDALONE;
import static org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator.REDIS_SERVER_IP;
import static org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator.REDIS_SERVER_PASSWORD;
import static org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator.REDIS_SERVER_PORT;

/**
 * @author: daijiguo
 * Version: 1.0
 */
public class FlinkJedisStandaloneConfigHandler implements FlinkJedisConfigHandler {
	private static final Logger log = LoggerFactory.getLogger(FlinkJedisStandaloneConfigHandler.class);

	@Override
	public FlinkJedisConfigBase createFlinkJedisConfig(Map<String, String> properties) {
		log.info("create flink jedis config, req:{} ", properties.toString());
		String hostIp = properties.get(REDIS_SERVER_IP);
		Objects.requireNonNull(hostIp, "Redis单机模式下的主机地址不能为空");
		if (hostIp.split(":").length > 1) {
			hostIp = hostIp.split(":")[0];
		}

		Integer redisPort = Integer.parseInt(properties.getOrDefault(REDIS_SERVER_PORT, "6379"));
		String password = properties.get(REDIS_SERVER_PASSWORD);
		if (password != null && password.trim().isEmpty()) {
			password = null;
		}

		FlinkJedisPoolConfig jedisPoolConfig = new FlinkJedisPoolConfig.Builder()
				.setHost(hostIp).setPort(redisPort).setPassword(password)
				.build();
		return jedisPoolConfig;
	}

	@Override
	public Map<String, String> requiredContext() {
		Map<String, String> require = new HashMap<>();
		require.put(REDIS_MODE, REDIS_MODE_STANDALONE);
		return require;
	}

	public FlinkJedisStandaloneConfigHandler(){

	}
}
