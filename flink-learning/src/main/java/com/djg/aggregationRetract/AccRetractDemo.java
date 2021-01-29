//package djg.duoyi.custom.sql;
//
//import org.apache.flink.api.common.typeinfo.TypeHint;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.table.api.EnvironmentSettings;
//import org.apache.flink.table.api.Table;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
////import org.apache.flink.table.api.java.StreamTableEnvironment;
//
//
//public class AccRetractDemo {
//
//
//    public static void main(String[] args) throws Exception {
//
//
//        // set up execution environment
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setParallelism(1);
//        // use blink planner in streaming mode
//        EnvironmentSettings settings = EnvironmentSettings.newInstance()
//                .inStreamingMode()
//                .useOldPlanner()
//                .inStreamingMode()
//                .build();
//        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
//
//
//        DataStream<Tuple2<String, Integer>> dataStream = env.fromElements(new Tuple2<>("hello", 1), new Tuple2<>("hello", 1), new Tuple2<>("hello", 1));
//        tEnv.createTemporaryView("tmpTable", dataStream, "word, num");
//        Table table = tEnv.sqlQuery("select cnt, count(word) as freq from (select word, count(num) as cnt from tmpTable group by word) group by cnt");
////        Table table = tEnv.sqlQuery("select word, count(num) as cnt from tmpTable group by word");
//        tEnv.toRetractStream(table, TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
//        })).print();
//
//        env.execute();
//
//    }
//}
