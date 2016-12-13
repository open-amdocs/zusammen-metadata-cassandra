package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.workspace.WorkspaceInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.WorkspaceDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.util.List;
import java.util.stream.Collectors;

public class WorkspaceCassandraDao implements WorkspaceDao {

  private static final class Field {
    private static final String WORKSPACE_ID = "workspace_id";
    private static final String WORKSPACE_INFO = "workspace_info";
  }

  @Override
  public void create(SessionContext context, String workspaceId, Info workspaceInfo) {
    save(context, workspaceId, workspaceInfo);
  }

  @Override
  public void save(SessionContext context, String workspaceId, Info workspaceInfo) {
    getAccessor(context).save(context.getUser().getUserName(), workspaceId,
        JsonUtil.object2Json(workspaceInfo));
  }

  @Override
  public void delete(SessionContext context, String workspaceId) {
    getAccessor(context).delete(context.getUser().getUserName(), workspaceId);
  }

  @Override
  public List<WorkspaceInfo> list(SessionContext context) {
    ResultSet rows = getAccessor(context).list(context.getUser().getUserName());
    return rows.all().stream().map(this::createWorkspaceInfo).collect(Collectors.toList());
  }

  private WorkspaceInfo createWorkspaceInfo(Row row) {
    WorkspaceInfo workspaceInfo = new WorkspaceInfo();
    workspaceInfo.setId(new String(row.getString(Field.WORKSPACE_ID)));
    workspaceInfo.setInfo(JsonUtil.json2Object(row.getString(Field.WORKSPACE_INFO), Info.class));
    return workspaceInfo;
  }

  private UserWorkspacesAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, UserWorkspacesAccessor.class);
  }

  @Accessor
  interface UserWorkspacesAccessor {

    @Query("insert into user_workspaces (user, workspace_id, workspace_info) values (?, ?, ?)")
    void save(String user, String workspaceId, String workspaceInfo);

    @Query("delete from user_workspaces where user=? and workspace_id=?")
    void delete(String user, String workspaceId);

    @Query("select workspace_id, workspace_info from user_workspaces where user=?")
    ResultSet list(String user);
  }
}
