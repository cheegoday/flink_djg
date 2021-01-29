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

package org.apache.flink.streaming.connectors.redis;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.typeutils.RowTypeInfo;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.connectors.redis.common.mapper.RedisCommand;
import org.apache.flink.streaming.connectors.redis.descriptor.Redis;
import org.apache.flink.streaming.connectors.redis.descriptor.RedisValidator;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.table.descriptors.Schema;
import org.apache.flink.types.Row;
import org.junit.Before;
import org.junit.Test;

import static org.apache.flink.table.api.Expressions.$;

public class RedisStandaloneDescriptorTest extends RedisStandaloneITCaseBase {

    private static final String REDIS_KEY = "TEST_KEY";
    private static final Long NUM_ELEMENTS = 2000L;


    StreamExecutionEnvironment env;

    @Before
    public void setUp() {
        env = StreamExecutionEnvironment.getExecutionEnvironment();
    }

    @Test
    public void testRedisDescriptor() throws Exception {
        DataStreamSource<Row> source = (DataStreamSource<Row>) env.addSource(new TestSourceFunctionString())
                .returns(new RowTypeInfo(TypeInformation.of(String.class), TypeInformation.of(Long.class)));

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useOldPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env, settings);
        tableEnvironment.createTemporaryView("t1", source, $("k"), $("v"));

        Redis redis = new Redis()
                .mode(RedisValidator.REDIS_MODE_STANDALONE)
                .command(RedisCommand.SET.name())
                .ttl(100000)
                .property(RedisValidator.REDIS_SERVER_IP, REDIS_HOST + ":" + REDIS_PORT)
                .property(RedisValidator.REDIS_SERVER_PORT, String.valueOf(REDIS_PORT));

        tableEnvironment
                .connect(redis).withSchema(new Schema()
                .field("k", TypeInformation.of(String.class))
                .field("v", TypeInformation.of(Long.class)))
                .createTemporaryTable("redis");

        tableEnvironment.executeSql("insert into redis select k, v from t1");
        env.execute();
    }

    @Test
    public void testRedisDescriptorUsingDDL() throws Exception {
        DataStreamSource<Row> source = (DataStreamSource<Row>) env.addSource(new TestSourceFunctionString())
                .returns(new RowTypeInfo(TypeInformation.of(String.class), TypeInformation.of(Long.class)));

        EnvironmentSettings settings = EnvironmentSettings
                .newInstance()
                .useOldPlanner()
                .inStreamingMode()
                .build();
        StreamTableEnvironment tableEnvironment = StreamTableEnvironment.create(env, settings);
        tableEnvironment.createTemporaryView("t1", source, $("k"), $("v"));


        String ddl =
                "CREATE TABLE redis (\n" +
                        "    `k` STRING,\n" +
                        "    `v` BIGINT\n" +
                        ") WITH (\n" +
                        "    'connector.type' = 'redis',\n" +
                        "    'redis-mode' = 'standalone',\n" +
                        "    'server.ip' = '127.0.0.1',\n" +
                        "    'server.port' = '53393',\n" +
                        "    'command' = 'SET',\n" +
                        "    'key.ttl' = '10000'\n" +
                        ")";

        tableEnvironment.executeSql(ddl);

        tableEnvironment.executeSql("insert into redis select k, v from t1");
        env.execute();
    }


    private static class TestSourceFunctionString implements SourceFunction<Row> {
        private static final long serialVersionUID = 1L;

        private volatile boolean running = true;

        @Override
        public void run(SourceContext<Row> ctx) throws Exception {

            for (Long i = 0L; i < NUM_ELEMENTS && running; i++) {
                Thread.sleep(1000);
                Row row = new Row(2);
                row.setField(0, REDIS_KEY);
                row.setField(1, i);
                ctx.collect(row);
            }
        }

        @Override
        public void cancel() {
            running = false;
        }
    }

}
