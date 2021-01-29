package com.djg.kerberos;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface JaasModule {
    /**
     * get the login module name
     * @return moduleName
     */
    String getLogInModuleName();

    /**
     * get the auth options
     * @return auth config
     */
    Map<String,Object> getOptions();

    /**
     * create jaas config
     * @return jaasConf
     */
    String createJaasConf();

    default String configToString(boolean beautified){
        StringBuilder builder = new StringBuilder();
        if(beautified){
            builder.append("   ");
        }
        builder.append( getLogInModuleName()).append(" required \n");
        Map<String,Object> options = getOptions();
        int totalSize = options.size();
        int i = 0;

        for (Map.Entry<String, Object> entry : options.entrySet()) {
            if(beautified){
                builder.append("    ");
            }
            builder.append(entry.getKey()).append("=");
            Object val = entry.getValue();
            if(val instanceof Boolean){
                builder.append(val);
            }else {
                builder.append("\"").append(val).append("\"");
            }
            if(++i == totalSize){
                if(beautified){
                    builder.append(";\n");
                }else{
                    builder.append(";");
                }

            }else{
                builder.append("\n");
            }
        }
        return builder.toString();
    }



    default String toJaasConf(String contextName, List<JaasModule> modules){
        return String.format("%s { \n %s};", contextName,modules.stream().map( t-> t.configToString(true))
                .collect(Collectors.joining("\n")));
    }
}
