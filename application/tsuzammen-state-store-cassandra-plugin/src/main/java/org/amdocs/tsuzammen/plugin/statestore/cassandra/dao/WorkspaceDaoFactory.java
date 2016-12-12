package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.tsuzammen.utils.facade.api.AbstractFactory;

public abstract class WorkspaceDaoFactory extends AbstractComponentFactory<WorkspaceDao> {
  public static WorkspaceDaoFactory getInstance() {
    return AbstractFactory.getInstance(WorkspaceDaoFactory.class);
  }

  public abstract WorkspaceDao createInterface(SessionContext context);
}

