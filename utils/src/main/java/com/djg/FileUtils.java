package com.djg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {
    public static void generateTempFlinkConf(Class tClass){
        InputStream fis = tClass.getResourceAsStream("/flink-conf.yaml");
        File tmpConfigurationFile = null;
        try {
            tmpConfigurationFile = Files.createFile(Paths.get("/tmp","flink-conf.yaml")).toFile();
            tmpConfigurationFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tmpConfigurationFile);
            int d = -1;
            while((d=fis.read())!=-1) {
                fos.write(d);
            }
            fis.close();
            fos.close();
        } catch (IOException e) {
            throw new RuntimeException("copy flink-conf.yaml failed", e);
        }


    }
}
