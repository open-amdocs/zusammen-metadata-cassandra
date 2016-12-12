package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class EntityDaoFactory extends AbstractComponentFactory<EntityDao> {
  public static EntityDaoFactory getInstance() {
    return AbstractFactory.getInstance(EntityDaoFactory.class);
  }

  public abstract EntityDao createInterface(SessionContext context);
}

