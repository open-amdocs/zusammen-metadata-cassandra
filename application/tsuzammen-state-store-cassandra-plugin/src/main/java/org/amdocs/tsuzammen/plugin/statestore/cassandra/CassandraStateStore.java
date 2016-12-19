/*
 * Copyright © 2016 Amdocs Software Systems Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.amdocs.tsuzammen.plugin.statestore.cassandra;


import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.impl.item.EntityInfo;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.item.Item;
import org.amdocs.tsuzammen.commons.datatypes.item.ItemVersion;
import org.amdocs.tsuzammen.commons.datatypes.item.Relation;
import org.amdocs.tsuzammen.commons.datatypes.workspace.WorkspaceInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDaoFactory;
import org.amdocs.tsuzammen.sdk.StateStore;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CassandraStateStore implements StateStore {

  private static final String PUBLIC_SPACE = "public";

  @Override
  public Collection<Item> listItems(SessionContext context) {
    return getItemDao(context).list(context);
  }

  @Override
  public Item getItem(SessionContext context, String itemId) {
    return getItemDao(context).get(context, itemId).get();
  }

  @Override
  public void createItem(SessionContext context, String itemId, Info itemInfo) {
    getItemDao(context).create(context, itemId, itemInfo);
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
  public Collection<ItemVersion> listItemVersions(SessionContext sessionContext, String s) {
    return null;
  }

  @Override
  public ItemVersion getItemVersion(SessionContext sessionContext, String s, String s1) {
    return null;
  }

  @Override
  public void createItemVersion(SessionContext context, String itemId, String baseVersionId,
                                String versionId, Info versionInfo) {
    String privateSpace = context.getUser().getUserName();
    getVersionDao(context)
        .create(context, privateSpace, itemId, versionId, baseVersionId, versionInfo);

    if (baseVersionId == null) {
      return;
    }

    copyRelationsFromBaseVersion(context, privateSpace, itemId, baseVersionId, versionId);
  }

  @Override
  public void publishItemVersion(SessionContext context, String itemId, String versionId) {
    String privateSpace = context.getUser().getUserName();

    copyVersionInfo(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
    copyVersionEntities(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
    copyVersionRelations(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
  }

  @Override
  public void syncItemVersion(SessionContext context, String itemId, String versionId) {

  }

  @Override
  public void createItemVersionEntity(SessionContext context, String itemId, String versionId,
                                      URI namespace, String entityId, EntityInfo entityInfo) {
    String privateSpace = context.getUser().getUserName();
    getEntityDao(context).create(context, privateSpace, itemId, versionId, namespace, entityId,
        entityInfo.getInfo());
  }

  @Override
  public void saveItemVersionEntity(SessionContext context, String itemId, String versionId,
                                    URI namespace, String entityId, EntityInfo entityInfo) {
    String privateSpace = context.getUser().getUserName();
    getEntityDao(context).save(context, privateSpace, itemId, versionId, namespace, entityId,
        entityInfo.getInfo());
  }

  @Override
  public void deleteItemVersionEntity(SessionContext context, String itemId, String versionId,
                                      URI namespace, String entityId) {
    String privateSpace = context.getUser().getUserName();
    getEntityDao(context).delete(context, privateSpace, itemId, versionId, namespace, entityId);
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

    Map<String, Relation> baseVersionRelations =
        relationDao.list(context, space, itemId, baseVersionId,
            StateStoreConstants.VERSION_PARENT_ENTITY_ID,
            StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
            StateStoreConstants.VERSION_ENTITY_ID);

    relationDao.save(context, space, itemId, versionId,
        StateStoreConstants.VERSION_PARENT_ENTITY_ID,
        StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
        StateStoreConstants.VERSION_ENTITY_ID, baseVersionRelations);
  }

  private void copyVersionInfo(SessionContext context, String sourceSpace, String targetSpace,
                               String itemId, String versionId) {
    Info versionInfo = getVersionDao(context).get(context, sourceSpace, itemId, versionId);
    getVersionDao(context).save(context, targetSpace, itemId, versionId, versionInfo);
  }

  private void copyVersionEntities(SessionContext context, String sourceSpace, String targetSpace,
                                   String itemId, String versionId) {
    // TODO: 12/14/2016
  }

  private void copyVersionRelations(SessionContext context, String sourceSpace, String targetSpace,
                                    String itemId, String versionId) {
    // TODO: 12/14/2016
  }

  private ItemDao getItemDao(SessionContext context) {
    return ItemDaoFactory.getInstance().createInterface(context);
  }

  private VersionDao getVersionDao(SessionContext context) {
    return VersionDaoFactory.getInstance().createInterface(context);
  }

  private EntityDao getEntityDao(SessionContext context) {
    return EntityDaoFactory.getInstance().createInterface(context);
  }

  private RelationDao getRelationDao(SessionContext context) {
    return RelationDaoFactory.getInstance().createInterface(context);
  }
}
