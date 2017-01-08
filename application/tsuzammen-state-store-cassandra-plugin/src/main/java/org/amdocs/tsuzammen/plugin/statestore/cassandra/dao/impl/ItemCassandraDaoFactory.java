package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDaoFactory;

public class ItemCassandraDaoFactory extends ItemDaoFactory {

  private static final ItemDao INSTANCE = new ItemCassandraDao();

  @Override
  public ItemDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
