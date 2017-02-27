package org.amdocs.zusammen.plugin.statestore.cassandra.dao;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.utils.facade.api.AbstractComponentFactory;
import org.amdocs.zusammen.utils.facade.api.AbstractFactory;

public abstract class WorkspaceDaoFactory extends AbstractComponentFactory<WorkspaceDao> {
  public static WorkspaceDaoFactory getInstance() {
    return AbstractFactory.getInstance(WorkspaceDaoFactory.class);
  }

  public abstract WorkspaceDao createInterface(SessionContext context);
}

