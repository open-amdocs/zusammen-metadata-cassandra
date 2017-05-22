package com.amdocs.zusammen.plugin.statestore.cassandra.dao;


import com.amdocs.zusammen.datatypes.SessionContext;

public interface HealthHelper {
    public static final String CASSANDERA_MODEL_NAME = "Cassandera";
    default KeepAliveDao getKeepAliveDao(SessionContext context) {;
        return KeepAliveDaoFactory.getInstance().createInterface(context);
    }
}
