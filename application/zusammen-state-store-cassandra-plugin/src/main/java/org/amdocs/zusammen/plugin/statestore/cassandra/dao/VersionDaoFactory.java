package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.zusammen.utils.facade.api.AbstractFactory;

public abstract class VersionDaoFactory extends AbstractComponentFactory<VersionDao> {
  public static VersionDaoFactory getInstance() {
    return AbstractFactory.getInstance(VersionDaoFactory.class);
  }

  public abstract VersionDao createInterface(SessionContext context);
}

