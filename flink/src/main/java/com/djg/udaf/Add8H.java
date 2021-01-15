package com.djg.udaf;

import org.apache.flink.table.functions.ScalarFunction;

import java.sql.Timestamp;

public class Add8H extends ScalarFunction {
    // 默认+8h
    public Timestamp eval(Timestamp s) {
        long timestamp = s.getTime() + 28800000;
        return new Timestamp(timestamp);
    }

    // 指定时区差值
    public Timestamp eval(Timestamp s, Integer diff) {
        long timestamp = s.getTime() + 3600000 * diff ;
        return new Timestamp(timestamp);
    }
}