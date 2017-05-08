package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.zusammen.utils.facade.api.AbstractFactory;

public abstract class KeepAliveDaoFactory extends AbstractComponentFactory<KeepAliveDao> {
  public static KeepAliveDaoFactory getInstance() {
    return AbstractFactory.getInstance(KeepAliveDaoFactory.class);
  }

  public abstract KeepAliveDao createInterface(SessionContext context);
}

