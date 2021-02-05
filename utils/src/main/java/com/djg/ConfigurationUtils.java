package com.djg;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.GlobalConfiguration;

import java.io.File;

public class ConfigurationUtils {

    public static Configuration getConfigurationByFlinkConf(String flinkConfDir){
        if(flinkConfDir == null ){
            if( !System.getenv().containsKey("FLINK_HOME")){
                throw new RuntimeException("no flink conf and 'FLINK_HOME' found");
            }
            flinkConfDir = System.getenv().get("FLINK_HOME") + File.separator + "conf";
        }
        return GlobalConfiguration.loadConfiguration(flinkConfDir);
    }
}
