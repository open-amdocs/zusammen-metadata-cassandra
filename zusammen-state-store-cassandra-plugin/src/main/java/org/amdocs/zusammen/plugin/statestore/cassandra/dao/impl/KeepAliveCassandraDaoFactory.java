package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.*;
public class KeepAliveCassandraDaoFactory extends KeepAliveDaoFactory {

  private static final KeepAliveDao INSTANCE = new KeepAliveCassandraDao();

  @Override
  public KeepAliveDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
