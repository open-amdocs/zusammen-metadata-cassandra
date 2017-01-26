/*
 * Copyright © 2016-2017 European Support Limited
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

package org.amdocs.zusammen.plugin.statestore.cassandra;


import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.Space;
import org.amdocs.zusammen.datatypes.item.ElementContext;
import org.amdocs.zusammen.datatypes.item.ElementInfo;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Item;
import org.amdocs.zusammen.datatypes.item.ItemVersion;
import org.amdocs.zusammen.datatypes.item.ItemVersionData;
import org.amdocs.zusammen.datatypes.workspace.WorkspaceInfo;
import org.amdocs.zusammen.sdk.StateStore;

import java.util.Collection;
import java.util.List;

public class StateStoreImpl implements StateStore {

  private final ElementStateStore elementStateStore = new ElementStateStore();
  private final VersionStateStore versionStateStore = new VersionStateStore();
  private final ItemStateStore itemStateStore = new ItemStateStore(versionStateStore);

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
  public void updateItem(SessionContext context, Id itemId, Info itemInfo) {
    itemStateStore.updateItem(context, itemId, itemInfo);
  }

  @Override
  public void deleteItem(SessionContext context, Id itemId) {
    itemStateStore.deleteItem(context, itemId);
  }

  @Override
  public Collection<ItemVersion> listItemVersions(SessionContext context, Space space, Id itemId) {
    return versionStateStore.listItemVersions(context, space, itemId);
  }

  @Override
  public boolean isItemVersionExist(SessionContext context, Space space, Id itemId, Id versionId) {
    return versionStateStore.isItemVersionExist(context, space, itemId, versionId);
  }

  @Override
  public ItemVersion getItemVersion(SessionContext context, Space space, Id itemId, Id versionId) {
    return versionStateStore.getItemVersion(context, space, itemId, versionId);
  }

  @Override
  public void createItemVersion(SessionContext context, Space space, Id itemId, Id baseVersionId,
                                Id versionId, ItemVersionData data) {
    versionStateStore.createItemVersion(context, space, itemId, baseVersionId, versionId, data);
  }

  @Override
  public void updateItemVersion(SessionContext context, Space space, Id itemId, Id versionId,
                                ItemVersionData data) {
    versionStateStore.updateItemVersion(context, space, itemId, versionId, data);
  }

  @Override
  public void deleteItemVersion(SessionContext context, Space space, Id itemId, Id versionId) {
    versionStateStore.deleteItemVersion(context, space, itemId, versionId);
  }

  @Override
  public Collection<ElementInfo> listElements(SessionContext context,
                                              ElementContext elementContext, Id elementId) {
    return elementStateStore.listElements(context, elementContext, elementId);
  }

  @Override
  public boolean isElementExist(SessionContext context, ElementContext elementContext,
                                Id elementId) {
    return elementStateStore.isElementExist(context, elementContext, elementId);
  }

  @Override
  public ElementInfo getElement(SessionContext context, ElementContext elementContext,
                                Id elementId) {
    return elementStateStore.getElement(context, elementContext, elementId);
  }

  @Override
  public void createElement(SessionContext context, ElementInfo elementInfo) {
    elementStateStore.createElement(context, elementInfo);
  }

  @Override
  public void updateElement(SessionContext context, ElementInfo elementInfo) {
    elementStateStore.updateElement(context, elementInfo);
  }

  @Override
  public void deleteElement(SessionContext context, ElementInfo elementInfo) {
    elementStateStore.deleteElement(context, elementInfo);
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
