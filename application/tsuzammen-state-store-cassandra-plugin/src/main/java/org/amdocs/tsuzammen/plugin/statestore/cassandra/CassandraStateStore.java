package org.amdocs.tsuzammen.plugin.statestore.cassandra;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.item.RelationInfo;
import org.amdocs.tsuzammen.commons.datatypes.workspace.WorkspaceInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDaoFactory;
import org.amdocs.tsuzammen.sdk.StateStore;

import java.util.List;
import java.util.Map;

public class CassandraStateStore implements StateStore {

  @Override
  public void createItem(SessionContext context, String itemId, Info itemInfo) {
    saveItem(context, itemId, itemInfo);
  }

  @Override
  public void saveItem(SessionContext context, String itemId, Info itemInfo) {
    getItemDao(context).save(context, itemId, itemInfo);
  }

  @Override
  public void deleteItem(SessionContext context, String itemId) {
    getItemDao(context).delete(context, itemId);
  }

  @Override
  public void createItemVersion(SessionContext context, String itemId, String baseVersionId, String versionId,
                                Info versionInfo) {
    String space = context.getUser().getUserName();
    getVersionDao(context).create(context, space, itemId, versionId, baseVersionId, versionInfo);
    copyRelationsFromBaseVersion(context, space, itemId, baseVersionId, versionId);
  }

  @Override
  public void createWorkspace(SessionContext context, String workspaceId, Info workspaceInfo) {

  }

  @Override
  public void saveWorkspace(SessionContext context, String workspaceId, Info workspaceInfo) {

  }

  @Override
  public void deleteWorkspace(SessionContext context, String workspaceId) {

  }

  @Override
  public List<WorkspaceInfo> listWorkspaces(SessionContext context) {
    return null;
  }

  private void copyRelationsFromBaseVersion(SessionContext context, String space, String itemId,
                                            String baseVersionId, String versionId) {
    RelationDao relationDao = getRelationDao(context);

    Map<String, RelationInfo> baseVersionRelations =
        relationDao.list(context, space, itemId, baseVersionId,
            StateStoreConstants.VERSION_PARENT_ENTITY_ID,
            StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
            StateStoreConstants.VERSION_ENTITY_ID);

    relationDao.save(context, space, itemId, versionId,
        StateStoreConstants.VERSION_PARENT_ENTITY_ID,
        StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
        StateStoreConstants.VERSION_ENTITY_ID, baseVersionRelations);
  }

  private ItemDao getItemDao(SessionContext context) {
    return ItemDaoFactory.getInstance().createInterface(context);
  }

  private VersionDao getVersionDao(SessionContext context) {
    return VersionDaoFactory.getInstance().createInterface(context);
  }

  private RelationDao getRelationDao(SessionContext context) {
    return RelationDaoFactory.getInstance().createInterface(context);
  }
}
