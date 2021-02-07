package com.djg.localStreamEnvironment;

import com.djg.ConfigurationUtils;
import com.djg.FileUtils;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.LocalStreamEnvironment;
import org.apache.flink.util.Collector;

public class StreamWordCount {



    public static void main(String[] args) throws Exception {

        // 在/tmp目录下创建临时flink-conf.yaml文件
        FileUtils.generateTempFlinkConf(StreamWordCount.class);
        Configuration configuration = ConfigurationUtils.getConfigurationByFlinkConf("/tmp");
        configuration.setString("metrics.reporter.promgateway.jobName","国际戴专属JOB");
        configuration.setString("yarn.application.type","国际戴专属任务类型");
        configuration.setString("metrics.reporter.promgateway.groupingKey","name=daijiguo;sex=male");

        LocalStreamEnvironment env = new LocalStreamEnvironment(configuration);

        DataStream<String> lines = env.socketTextStream(args[0], Integer.parseInt(args[1]));
        SingleOutputStreamOperator<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String line, Collector<String> out) throws Exception {
                //切分
                String[] words = line.split(" ");
                for (String word : words) {
                    //输出
                    out.collect(word);
                }
            }
        });

        //将单词和一组合
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOne = words.map(new MapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public Tuple2<String, Integer> map(String word) throws Exception {
                return Tuple2.of(word, 1);
            }
        });


        SingleOutputStreamOperator<Tuple2<String, Integer>> summed = wordAndOne.keyBy(0).sum(1);
        summed.print();

        //启动
        env.execute("StreamWordCount");

    }
}
