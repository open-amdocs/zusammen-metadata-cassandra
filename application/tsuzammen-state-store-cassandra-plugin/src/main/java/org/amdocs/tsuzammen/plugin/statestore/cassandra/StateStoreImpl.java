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
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;
import org.amdocs.tsuzammen.sdk.StateStore;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class StateStoreImpl implements StateStore {

  private final ItemStateStore itemStateStore = new ItemStateStore();
  private final VersionStateStore versionStateStore = new VersionStateStore();
  private final ElementStateStore elementStateStore = new ElementStateStore();

  @Override
  public Collection<Item> listItems(SessionContext context) {
    return itemStateStore.listItems(context);
  }

  @Override
  public boolean isItemExist(SessionContext context, Id itemId) {
    return itemStateStore.isItemExist(context, itemId);
  }

  @Override
  public Item getItem(SessionContext context, Id itemId) {
    return itemStateStore.getItem(context, itemId);
  }

  @Override
  public void createItem(SessionContext context, Id itemId, Info itemInfo) {
    itemStateStore.createItem(context, itemId, itemInfo);
  }

  @Override
  public void saveItem(SessionContext context, Id itemId, Info itemInfo) {
    itemStateStore.saveItem(context, itemId, itemInfo);
  }

  @Override
  public void deleteItem(SessionContext context, Id itemId) {
    itemStateStore.deleteItem(context, itemId);
  }

  @Override
  public Collection<ItemVersion> listItemVersions(SessionContext context, Id itemId) {
    return versionStateStore.listItemVersions(context, itemId);
  }

  @Override
  public boolean isItemVersionExist(SessionContext context, Id itemId, Id versionId) {
    return versionStateStore.isItemVersionExist(context, itemId, versionId);
  }

  @Override
  public ItemVersion getItemVersion(SessionContext context, Id itemId, Id versionId) {
    return versionStateStore.getItemVersion(context, itemId, versionId);
  }

  @Override
  public void createItemVersion(SessionContext context, Id itemId, Id baseVersionId,
                                Id versionId, Info versionInfo) {

    versionStateStore.createItemVersion(context, itemId, baseVersionId, versionId, versionInfo);
  }

  @Override
  public void publishItemVersion(SessionContext context, Id itemId, Id versionId) {

    versionStateStore.publishItemVersion(context, itemId, versionId);
  }

  @Override
  public void syncItemVersion(SessionContext context, Id itemId, Id versionId) {

    versionStateStore.syncItemVersion(context, itemId, versionId);
  }

  @Override
  public Namespace getElementNamespace(SessionContext context,
                                       ElementContext elementContext, Id elementId) {
    return elementStateStore.getElementNamespace(context, elementContext, elementId);
  }

  @Override
  public boolean isElementExist(SessionContext context, ElementContext elementContext,
                                Id elementId) {
    return elementStateStore.isElementExist(context, elementContext, elementId);
  }

  @Override
  public ElementInfo getElement(SessionContext context, ElementContext elementContext,
                                Id elementId, FetchCriteria fetchCriteria) {
    return elementStateStore.getElement(context, elementContext, elementId, fetchCriteria);
  }

  @Override
  public void createElement(SessionContext context, ElementContext elementContext,
                            Namespace namespace, ElementInfo elementInfo) {
    elementStateStore.createElement(context, elementContext, namespace, elementInfo);
  }

  @Override
  public void saveElement(SessionContext context, ElementContext elementContext,
                          ElementInfo elementInfo) {
    elementStateStore.saveElement(context, elementContext, elementInfo);
  }

  @Override
  public void deleteElement(SessionContext context, ElementContext elementContext,
                            ElementInfo elementInfo) {
    elementStateStore.deleteElement(context, elementContext, elementInfo);
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

}
