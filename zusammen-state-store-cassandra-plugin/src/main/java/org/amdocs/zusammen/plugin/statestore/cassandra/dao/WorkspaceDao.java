package org.amdocs.zusammen.plugin.statestore.cassandra.dao;


import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.workspace.WorkspaceInfo;

import java.util.List;

public interface WorkspaceDao {

  void create(SessionContext context, String workspaceId, Info workspaceInfo);

  void save(SessionContext context, String workspaceId, Info workspaceInfo);

  void delete(SessionContext context, String workspaceId);

  List<WorkspaceInfo> list(SessionContext context);
}
