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


import org.amdocs.tsuzammen.datatypes.FetchCriteria;
import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.item.ElementContext;
import org.amdocs.tsuzammen.datatypes.item.ElementInfo;
import org.amdocs.tsuzammen.datatypes.item.Info;
import org.amdocs.tsuzammen.datatypes.item.Item;
import org.amdocs.tsuzammen.datatypes.item.ItemVersion;
import org.amdocs.tsuzammen.datatypes.workspace.WorkspaceInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementInfoRepositoryFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ItemDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDaoFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.sdk.StateStore;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CassandraStateStore implements StateStore {

  private static final String PUBLIC_SPACE = "public";

  @Override
  public Collection<Item> listItems(SessionContext context) {
    return getItemDao(context).list(context);
  }

  @Override
  public boolean isItemExist(SessionContext context, Id itemId) {
    return false;
  }

  @Override
  public Item getItem(SessionContext context, Id itemId) {
    return getOptionalItem(context, itemId).orElseThrow(() ->
        new RuntimeException(String.format(StateStoreMessages.ITEM_NOT_EXIST, itemId)));
  }

  @Override
  public void createItem(SessionContext context, Id itemId, Info itemInfo) {
    getItemDao(context).create(context, itemId, itemInfo);
  }

  @Override
  public void saveItem(SessionContext context, Id itemId, Info itemInfo) {
    getItemDao(context).save(context, itemId, itemInfo);
  }

  @Override
  public void deleteItem(SessionContext context, Id itemId) {
    getItemDao(context).delete(context, itemId);
  }

  @Override
  public Collection<ItemVersion> listItemVersions(SessionContext context, Id itemId) {
    String privateSpace = context.getUser().getUserName();
    return getVersionDao(context).list(context, privateSpace, itemId);
  }

  @Override
  public boolean isItemVersionExist(SessionContext context, Id itemId, Id versionId) {
    return true;
  }

  @Override
  public ItemVersion getItemVersion(SessionContext context, Id itemId, Id versionId) {
    String privateSpace = context.getUser().getUserName();
    return getOptionalItemVersion(context, privateSpace, itemId, versionId)
        .orElseThrow(() -> new RuntimeException(
            String.format(StateStoreMessages.ITEM_VERSION_NOT_EXIST,
                itemId, versionId, privateSpace)));
  }

  @Override
  public void createItemVersion(SessionContext context, Id itemId, Id baseVersionId,
                                Id versionId, Info versionInfo) {
    String privateSpace = context.getUser().getUserName();
    getVersionDao(context)
        .create(context, privateSpace, itemId, versionId, baseVersionId, versionInfo);

    if (baseVersionId == null) {
      return;
    }

    copyRelationsFromBaseVersion(context, privateSpace, itemId, baseVersionId, versionId);
  }

  @Override
  public void publishItemVersion(SessionContext context, Id itemId, Id versionId) {
    String privateSpace = context.getUser().getUserName();

    copyVersionInfo(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
    copyVersionEntities(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
    copyVersionRelations(context, privateSpace, PUBLIC_SPACE, itemId, versionId);
  }

  @Override
  public void syncItemVersion(SessionContext context, Id itemId, Id versionId) {

  }

  @Override
  public Namespace getElementNamespace(SessionContext context,
                                       ElementContext elementContext, Id elementId) {
    return getElementRepository(context)
        .get(context, StateStoreUtils.getElementEntity(
            context.getUser().getUserName(), elementContext, null, elementId))
        .getNamespace();
  }

  @Override
  public boolean isElementExist(SessionContext context, ElementContext elementContext,
                                Id elementId) {
    return getElementRepository(context)
        .get(context, StateStoreUtils.getElementEntity(
            context.getUser().getUserName(), elementContext, null, elementId)) != null;
  }

  @Override
  public ElementInfo getElement(SessionContext context, ElementContext elementContext,
                                Id elementId, FetchCriteria fetchCriteria) {
    ElementEntity elementEntity =
        getElementRepository(context)
            .get(context, StateStoreUtils.getElementEntity(context.getUser()
                .getUserName(), elementContext, null, elementId));
    return StateStoreUtils.getElementInfo(elementEntity);
  }


  @Override
  public void createElement(SessionContext context, ElementContext elementContext,
                            Namespace namespace, ElementInfo elementInfo) {
    getElementRepository(context).create(context,
        StateStoreUtils.getElementEntity(context.getUser().getUserName(), elementContext,
            namespace, elementInfo));
  }

  @Override
  public void saveElement(SessionContext context, ElementContext elementContext,
                          ElementInfo elementInfo) {
    getElementRepository(context).update(context,
        StateStoreUtils
            .getElementEntity(context.getUser().getUserName(), elementContext, null, elementInfo));
  }

  @Override
  public void deleteElement(SessionContext context, ElementContext elementContext,
                            ElementInfo elementInfo) {
    ElementEntity elementEntity = StateStoreUtils
        .getElementEntity(context.getUser().getUserName(), elementContext, null, elementInfo);
    deleteElementHierarchy(context, elementContext, elementEntity);


  }

  private void deleteElementHierarchy(SessionContext context, ElementContext elementContext,
                                      ElementEntity elementEntity) {
    ElementRepository elementRepository = getElementRepository(context);

    Set<Id> subElementIds = elementRepository.get(context, elementEntity).getSubElementIds();
    subElementIds.stream()
        .map(subElementId -> StateStoreUtils
            .getElementEntity(context.getUser().getUserName(), elementContext, null, subElementId))
        .forEach(subElementEntity ->
            deleteElementHierarchy(context, elementContext, subElementEntity));

    elementRepository.delete(context, elementEntity);
  }

  @Override
  public void createWorkspace(SessionContext context, Id workspaceId, Info workspaceInfo) {

  }

  @Override
  public void saveWorkspace(SessionContext context, Id workspaceId, Info workspaceInfo) {

  }

  @Override
  public void deleteWorkspace(SessionContext context, Id workspaceId) {

  }

  @Override
  public List<WorkspaceInfo> listWorkspaces(SessionContext context) {
    return null;
  }

  private void copyRelationsFromBaseVersion(SessionContext context, String space, Id itemId,
                                            Id baseVersionId, Id versionId) {
    RelationDao relationDao = getRelationDao(context);

/*    Map<String, Relation> baseVersionRelations =
        relationDao.list(context, space, itemId, baseVersionId,
            StateStoreConstants.VERSION_PARENT_ENTITY_ID,
            StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
            StateStoreConstants.VERSION_ENTITY_ID);

    relationDao.update(context, space, itemId, versionId,
        StateStoreConstants.VERSION_PARENT_ENTITY_ID,
        StateStoreConstants.VERSION_PARENT_CONTENT_NAME,
        StateStoreConstants.VERSION_ENTITY_ID, baseVersionRelations);*/
  }

  private void copyVersionInfo(SessionContext context, String sourceSpace, String targetSpace,
                               Id itemId, Id versionId) {
/*    Optional<ItemVersion> itemVersion =
        getOptionalItemVersion(context, sourceSpace, itemId, versionId);
    getVersionDao(context).update(context, targetSpace, itemId, versionId, versionInfo);*/
  }

  private void copyVersionEntities(SessionContext context, String sourceSpace, String targetSpace,
                                   Id itemId, Id versionId) {
    // TODO: 12/14/2016
  }

  private void copyVersionRelations(SessionContext context, String sourceSpace, String targetSpace,
                                    Id itemId, Id versionId) {
    // TODO: 12/14/2016
  }

  private Optional<Item> getOptionalItem(SessionContext context, Id itemId) {
    return getItemDao(context).get(context, itemId);
  }


  private Optional<ItemVersion> getOptionalItemVersion(SessionContext context, String space,
                                                       Id itemId, Id versionId) {
    return getVersionDao(context).get(context, space, itemId, versionId);
  }

  private ItemDao getItemDao(SessionContext context) {
    return ItemDaoFactory.getInstance().createInterface(context);
  }

  private VersionDao getVersionDao(SessionContext context) {
    return VersionDaoFactory.getInstance().createInterface(context);
  }

  private ElementRepository getElementRepository(SessionContext context) {
    return ElementInfoRepositoryFactory.getInstance().createInterface(context);
  }

  private RelationDao getRelationDao(SessionContext context) {
    return RelationDaoFactory.getInstance().createInterface(context);
  }
}
