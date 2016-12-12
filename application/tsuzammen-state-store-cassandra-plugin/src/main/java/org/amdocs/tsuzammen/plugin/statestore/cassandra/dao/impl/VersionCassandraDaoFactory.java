package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDaoFactory;

public class VersionCassandraDaoFactory extends VersionDaoFactory {

  private static final VersionDao INSTANCE = new VersionCassandraDao();

  @Override
  public VersionDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
