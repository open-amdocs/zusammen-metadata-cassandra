package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class RelationDaoFactory extends AbstractComponentFactory<RelationDao> {
  public static RelationDaoFactory getInstance() {
    return AbstractFactory.getInstance(RelationDaoFactory.class);
  }

  public abstract RelationDao createInterface(SessionContext context);
}
