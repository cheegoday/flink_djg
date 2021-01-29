package com.djg.stateProcessorAPI;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple1;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.StateBackend;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.state.api.BootstrapTransformation;
import org.apache.flink.state.api.ExistingSavepoint;
import org.apache.flink.state.api.OperatorTransformation;
import org.apache.flink.state.api.Savepoint;
import org.apache.flink.state.api.functions.KeyedStateBootstrapFunction;
import org.apache.flink.state.api.functions.KeyedStateReaderFunction;
import org.apache.flink.util.Collector;

public class StateProcessorAPIReaderDemo {

    public static void main(String[] args) throws Exception {
        ExecutionEnvironment bEnv = ExecutionEnvironment.getExecutionEnvironment();
        String savePointPath = "file:///Users/djg/Downloads/checkpoints/djgck";
        StateBackend fsStateBackend = new FsStateBackend("file:///Users/djg/Downloads/checkpoints");

        ExistingSavepoint savepoint = Savepoint.load(bEnv, savePointPath, fsStateBackend);

        //读取
        String uid = "keyby_summarize";
        DataSet<KeyedValueState> keyState = savepoint.readKeyedState(uid, new StateReaderFunc());

        //修改
        DataSet<KeyedValueState> dataSet = keyState.flatMap((FlatMapFunction<KeyedValueState, KeyedValueState>) (value, out) -> {
            value.countNum = value.countNum * 2;
            out.collect(value);
        }).returns(KeyedValueState.class);

//        dataSet.print();
        BootstrapTransformation<KeyedValueState> transformation = OperatorTransformation
                .bootstrapWith(dataSet)
                //注意keyby操作的key一定要和原来的相同
                .keyBy(new KeySelector<KeyedValueState, Tuple1<String>>() {
                    @Override
                    public Tuple1<String> getKey(KeyedValueState value) throws Exception {
                        return value.key;
                    }
                })
                .transform(new KeyedValueStateBootstrapper());

        Savepoint.create(fsStateBackend, 128)
                .withOperator(uid, transformation)
                .write("file:///Users/djg/Downloads/checkpoints/djgck2");


        bEnv.execute("read the list state");
        System.out.println("-----end------------");
    }

    public static class StateReaderFunc extends KeyedStateReaderFunction<Tuple1<String>, KeyedValueState> {

        private static final long serialVersionUID = -3616180524951046897L;
        private transient ValueState<Long> state;

        @Override
        public void open(Configuration parameters) {
            ValueStateDescriptor currentCountDescriptor = new ValueStateDescriptor("currentCountState", Long.class);
            state = getRuntimeContext().getState(currentCountDescriptor);
        }

        @Override
        public void readKey(Tuple1<String> key, Context ctx, Collector<KeyedValueState> out) throws Exception {
            System.out.println(key.f0 +":" + state.value());

            KeyedValueState keyedValueState = new KeyedValueState();
            keyedValueState.key = new Tuple1<>(key.f0);
            keyedValueState.countNum = state.value();

            out.collect(keyedValueState);
        }
    }

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
}
