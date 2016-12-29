package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementDaoFactory;

public class ElementCassandraDaoFactory extends ElementDaoFactory {

  private static final ElementDao INSTANCE = new ElementCassandraDao();

  @Override
  public ElementDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
