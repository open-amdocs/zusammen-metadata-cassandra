package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.WorkspaceDao;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.WorkspaceDaoFactory;

public class WorkspaceCassandraDaoFactory extends WorkspaceDaoFactory {

  private static final WorkspaceDao INSTANCE = new WorkspaceCassandraDao();

  @Override
  public WorkspaceDao createInterface(SessionContext context) {
    return INSTANCE;
  }
}
