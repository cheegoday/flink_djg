package com.djg.utils;


import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

public class EnvUtil {
    public  static Tuple2<StreamExecutionEnvironment, StreamTableEnvironment> getEnv(Integer parallelism, String plannerVersion, TimeCharacteristic timeAttribute, CheckpointingMode checkpointingMode){
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useOldPlanner().inStreamingMode().build();
        if (plannerVersion.equals("blink")) {
            settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        }
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(timeAttribute);
        env.setParallelism(parallelism);
        env.enableCheckpointing(5000);
        env.setStateBackend(new FsStateBackend("hdfs:///djg/checkpoint"));
        env.getCheckpointConfig().enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        if (checkpointingMode.equals(CheckpointingMode.AT_LEAST_ONCE)){
            env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.AT_LEAST_ONCE);
        }

        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        return new Tuple2<>(env, tableEnv);
    }


}
