package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao;

import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.workspace.WorkspaceInfo;

import java.util.List;

public interface WorkspaceDao {

  void create(SessionContext context, Id workspaceId, Info workspaceInfo);

  void save(SessionContext context, Id workspaceId, Info workspaceInfo);

  void delete(SessionContext context, Id workspaceId);

  List<WorkspaceInfo> list(SessionContext context);
}
