package com.djg.flink.custom.kafkaKerberos;

import lombok.Data;

import java.io.Serializable;

@Data
public class RegistryRecord implements Serializable {
    private static final long serialVersionUID = 66687827L;
    private String user;
    private Long price;
    private String hotel;
}