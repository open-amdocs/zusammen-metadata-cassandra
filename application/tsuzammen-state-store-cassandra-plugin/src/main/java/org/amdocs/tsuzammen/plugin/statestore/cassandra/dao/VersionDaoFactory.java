package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class VersionDaoFactory extends AbstractComponentFactory<VersionDao> {
  public static VersionDaoFactory getInstance() {
    return AbstractFactory.getInstance(VersionDaoFactory.class);
  }

  public abstract VersionDao createInterface(SessionContext context);
}

