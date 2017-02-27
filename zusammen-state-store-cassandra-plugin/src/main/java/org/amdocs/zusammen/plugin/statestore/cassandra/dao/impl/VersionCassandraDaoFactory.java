package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.VersionDaoFactory;

public class VersionCassandraDaoFactory extends VersionDaoFactory {

  private static final VersionDao INSTANCE = new VersionCassandraDao();

  @Override
  public VersionDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
