package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDaoFactory;

public class RelationCassandraDaoFactory extends RelationDaoFactory {

  private static final RelationDao INSTANCE = new RelationCassandraDao();

  @Override
  public RelationDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
