package com.djg.stateProcessorAPI;

import lombok.Data;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.GroupReduceFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.state.api.BootstrapTransformation;
import org.apache.flink.state.api.OperatorTransformation;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
import org.apache.flink.util.Collector;

import java.util.Iterator;

/**
 * 参考：
 * https://developer.aliyun.com/article/740203
 * https://www.cnblogs.com/029zz010buct/p/11900302.html
 */
public class SateProcessorAPIWriterDemo {
    @Data
    public static class KeyedValueState {
        Tuple1<String> key;
        Long countNum;
    }


    private static class KeyedValueStateBootstrapper extends KeyedStateBootstrapFunction<Tuple1<String>, KeyedValueState> {

        private static final long serialVersionUID = 1893716139133502118L;
        private ValueState<Long> currentCount = null;

        @Override
        public void open(Configuration parameters) throws Exception {
            ValueStateDescriptor currentCountDescriptor = new ValueStateDescriptor("currentCountState", Long.class, 0L);
            currentCount = getRuntimeContext().getState(currentCountDescriptor);
        }

        @Override
        public void processElement(KeyedValueState value, Context ctx) throws Exception {
            currentCount.update(value.countNum);
        }
    }

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //获取外部离线数据源
        DataSource<String> textSource =  env.readTextFile("/Users/djg/Downloads/checkpoints/data.txt");
        DataSet<Tuple2<String, Integer>> sourceDataSet = textSource.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {

            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] strArr = value.split(",");
                for (String str : strArr) {
                    Tuple2<String, Integer> worldTuple = new Tuple2<>(str, 1);
                    out.collect(worldTuple);
                }
            }
        });

        //计算出需要的历史状态
        DataSet<KeyedValueState> dataSet = sourceDataSet
                .groupBy(0)
                .reduceGroup(new GroupReduceFunction<Tuple2<String, Integer>, KeyedValueState>() {
                    @Override
                    public void reduce(Iterable<Tuple2<String, Integer>> values, Collector<KeyedValueState> out) throws Exception {

                        Iterator iterator = values.iterator();
                        Long countNum = 0L;
                        String worldkey = null;
                        while(iterator.hasNext()){
                            Tuple2<String, Integer> info = (Tuple2<String, Integer>) iterator.next();
                            if(worldkey == null){
                                worldkey = info.f0;
                            }
                            countNum++;
                        }

                        KeyedValueState keyedValueState = new KeyedValueState();
                        keyedValueState.key = new Tuple1<>(worldkey);
                        keyedValueState.countNum = countNum;

                        out.collect(keyedValueState);
                    }
                });

        //将历史状态转换为state 并转换为savepoint 写入hdfs上
        BootstrapTransformation<KeyedValueState> transformation = OperatorTransformation
                .bootstrapWith(dataSet)
                .keyBy(new KeySelector<KeyedValueState, Tuple1<String>>() {
                    @Override
                    public Tuple1<String> getKey(KeyedValueState value) throws Exception {
                        return value.key;
                    }
                })
                .transform(new KeyedValueStateBootstrapper());

        String uid = "keyby_summarize";
        String savePointPath = "file:///Users/djg/Downloads/checkpoints/djgck";
        StateBackend fsStateBackend = new FsStateBackend("file:///Users/djg/Downloads/checkpoints");
        Savepoint.create(fsStateBackend, 128)
                .withOperator(uid, transformation)
                .write(savePointPath);


        env.execute("batch build save point");
        System.out.println("-------end------------");
    }




}
