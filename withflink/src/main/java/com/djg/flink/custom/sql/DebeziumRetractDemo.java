//package djg.duoyi.custom.sql;
//
//import djg.duoyi.utils.FileUtils;
//import org.apache.flink.api.common.typeinfo.TypeHint;
//import org.apache.flink.api.common.typeinfo.TypeInformation;
//import org.apache.flink.api.java.tuple.Tuple2;
//import org.apache.flink.streaming.api.datastream.DataStream;
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
//import org.apache.flink.table.api.EnvironmentSettings;
//import org.apache.flink.table.api.StatementSet;
//import org.apache.flink.table.api.Table;
//import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
//
//import java.util.Objects;
////import org.apache.flink.table.api.java.StreamTableEnvironment;
//
//
//public class DebeziumRetractDemo {
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
//                .useBlinkPlanner()
//                .build();
//        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env, settings);
//        String filePath = "/Users/djg/private/教程/大数据/flink/多易教育Flink/flink_duoyi/src/main/resources/djg.sql";
//        String sql = FileUtils.getTemplateContent(filePath);
//        //
//        String[] sqls = sql.split(";");
//        tEnv.executeSql(sqls[0]);
//        tEnv.executeSql(sqls[1]);
//        tEnv.executeSql(sqls[2]);
//
////        Table table = tEnv.sqlQuery(insertDML);
////        tEnv.toRetractStream(table, TypeInformation.of(new TypeHint<Tuple2<Long, Long>>() {
////        }));
//
//        env.execute();
//
//    }
//}
