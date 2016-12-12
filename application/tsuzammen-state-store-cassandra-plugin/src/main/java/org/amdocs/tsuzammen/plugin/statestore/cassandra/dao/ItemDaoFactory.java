package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class ItemDaoFactory extends AbstractComponentFactory<ItemDao> {
  public static ItemDaoFactory getInstance() {
    return AbstractFactory.getInstance(ItemDaoFactory.class);
  }

  public abstract ItemDao createInterface(SessionContext context);
}

