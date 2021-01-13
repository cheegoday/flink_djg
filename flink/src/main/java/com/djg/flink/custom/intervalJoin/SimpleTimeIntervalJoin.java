package com.djg.flink.custom.intervalJoin;

import org.apache.flink.api.common.eventtime.*;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.EnvironmentSettings;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.TableResult;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;
import org.apache.flink.types.Row;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.ArrayList;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 参考：
 * https://developer.aliyun.com/article/683681?spm=a2c6h.13262185.0.0.513c7e18aCjUnt
 */
public class SimpleTimeIntervalJoin {
    public static void main(String[] args) throws Exception {

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        EnvironmentSettings settings = EnvironmentSettings.newInstance().useBlinkPlanner().inStreamingMode().build();
        StreamTableEnvironment tableEnv = StreamTableEnvironment.create(env, settings);
        env.setParallelism(1);
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);


        // 订单
        ArrayList<Tuple3<String, String, Timestamp>> ordersData = new ArrayList<>();
        ordersData.add(new Tuple3<>("001", "iphone", new Timestamp(1545800002000L)));
        ordersData.add(new Tuple3<>("002", "mac", new Timestamp(1545800003000L)));
        ordersData.add(new Tuple3<>("003", "book", new Timestamp(1545800004000L)));
        ordersData.add(new Tuple3<>("004", "cup", new Timestamp(1545800018000L)));
        SingleOutputStreamOperator<Tuple3<String, String, Timestamp>> orders = env
                .fromCollection(ordersData)
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Tuple3<String, String, Timestamp>>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner((event, timestamp) -> event.f2.getTime()));
        tableEnv.createTemporaryView("Orders", orders, $("orderId"), $("productName"), $("orderTime").rowtime());

        // 汇率
        ArrayList<Tuple3<String, String, Timestamp>> paymentData = new ArrayList<>();
        paymentData.add(new Tuple3<>("001", "alipay", new Timestamp(1545803501000L)));
        paymentData.add(new Tuple3<>("002", "card", new Timestamp(1545803602000L)));
        paymentData.add(new Tuple3<>("003", "card", new Timestamp(1545803610000L)));
        paymentData.add(new Tuple3<>("004", "alipay", new Timestamp(1545803611000L)));

        SingleOutputStreamOperator<Tuple3<String, String, Timestamp>> ratesHistory = env
                .fromCollection(paymentData)
                .assignTimestampsAndWatermarks(WatermarkStrategy
                        .<Tuple3<String, String, Timestamp>>forBoundedOutOfOrderness(Duration.ofSeconds(10))
                        .withTimestampAssigner((event, timestamp) -> event.f2.getTime()));

        tableEnv.createTemporaryView("Payment", ratesHistory, $("orderId"), $("payType"), $("payTime").rowtime());

        String sql =
                "SELECT o.orderId, o.productName, p.payType, o.orderTime, cast(payTime as timestamp) as payTime\n" +
                        "FROM Orders AS o\n" +
                        "         JOIN Payment AS p\n" +
                        "              ON o.orderId = p.orderId AND p.payTime BETWEEN o.orderTime AND o.orderTime + INTERVAL '1' HOUR";


        TableResult result = tableEnv.sqlQuery(sql).execute();
        result.print();


    }
}
