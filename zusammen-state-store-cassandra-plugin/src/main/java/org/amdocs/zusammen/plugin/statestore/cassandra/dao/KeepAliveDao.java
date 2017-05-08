package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;

import java.util.Optional;

public interface KeepAliveDao {

  boolean get(SessionContext context);
}
