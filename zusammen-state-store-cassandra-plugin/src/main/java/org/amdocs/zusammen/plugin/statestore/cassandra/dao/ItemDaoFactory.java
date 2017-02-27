package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.zusammen.utils.facade.api.AbstractFactory;

public abstract class ItemDaoFactory extends AbstractComponentFactory<ItemDao> {
  public static ItemDaoFactory getInstance() {
    return AbstractFactory.getInstance(ItemDaoFactory.class);
  }

  public abstract ItemDao createInterface(SessionContext context);
}

