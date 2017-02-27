package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDaoFactory;

public class ItemCassandraDaoFactory extends ItemDaoFactory {

  private static final ItemDao INSTANCE = new ItemCassandraDao();

  @Override
  public ItemDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
