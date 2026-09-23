package io.github.irfanghat.spark.opcua;

import java.io.Serializable;

import org.apache.spark.sql.util.CaseInsensitiveStringMap;

public record OpcUaOptions(
                String endpoint,
                int maxNodes) implements Serializable {

        public static final int DEFAULT_MAX_NODES = 10;

        public static OpcUaOptions from(
                        CaseInsensitiveStringMap options) {

                return new OpcUaOptions(
                                options.get("endpoint"),
                                options.getInt(
                                                "maxNodes",
                                                DEFAULT_MAX_NODES));
        }
}