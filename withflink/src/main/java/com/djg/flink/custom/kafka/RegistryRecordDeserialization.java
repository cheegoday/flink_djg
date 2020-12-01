package com.djg.flink.custom.kafka;

import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class RegistryRecordDeserialization implements DeserializationSchema<RegistryRecord> {

    @Override
    public RegistryRecord deserialize(byte[] message) throws IOException {
        return (RegistryRecord) (new ObjectMapper()).readValue(message, RegistryRecord.class);
    }

    @Override
    public boolean isEndOfStream(RegistryRecord nextElement) {
        return false;
    }

    @Override
    public TypeInformation<RegistryRecord> getProducedType() {
        return TypeInformation.of(new TypeHint<RegistryRecord>() {
            @Override
            public TypeInformation<RegistryRecord> getTypeInfo() {
                return super.getTypeInfo();
            }
        });
    }
}
