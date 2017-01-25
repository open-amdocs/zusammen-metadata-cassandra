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
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Item;
import org.amdocs.zusammen.datatypes.item.ItemVersion;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDao;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ItemDaoFactory;

import java.util.Collection;

class ItemStateStore {

  private VersionStateStore versionStateStore;

  ItemStateStore(VersionStateStore versionStateStore) {
    this.versionStateStore = versionStateStore;
  }

  Collection<Item> listItems(SessionContext context) {
    return getItemDao(context).list(context);
  }

  boolean isItemExist(SessionContext context, Id itemId) {
    return getItemDao(context).get(context, itemId).isPresent();
  }

  Item getItem(SessionContext context, Id itemId) {
    return getItemDao(context).get(context, itemId).orElse(null);
  }

  void createItem(SessionContext context, Id itemId, Info itemInfo) {
    getItemDao(context).create(context, itemId, itemInfo);
  }

  void updateItem(SessionContext context, Id itemId, Info itemInfo) {
    getItemDao(context).update(context, itemId, itemInfo);
  }

  void deleteItem(SessionContext context, Id itemId) {
    Collection<ItemVersion> versions = versionStateStore.listItemVersions(context, itemId);
    versions.forEach(itemVersion ->
        versionStateStore.deleteItemVersion(context, itemId, itemVersion.getId(), Space.PRIVATE));

    getItemDao(context).delete(context, itemId);
  }

  protected ItemDao getItemDao(SessionContext context) {
    return ItemDaoFactory.getInstance().createInterface(context);
  }
}