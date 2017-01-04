package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDaoFactory;

public class EntityCassandraDaoFactory extends EntityDaoFactory {

  private static final EntityDao INSTANCE = new EntityCassandraDao();

  @Override
  public EntityDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
