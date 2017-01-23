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
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.ItemVersion;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepositoryFactory;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.VersionDaoFactory;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Collection;
import java.util.Optional;

class VersionStateStore {

  Collection<ItemVersion> listItemVersions(SessionContext context, Id itemId) {
    return getVersionDao(context).list(context, context.getUser().getUserName(), itemId);
  }


  boolean isItemVersionExist(SessionContext context, Id itemId, Id versionId) {
    return true;
  }


  ItemVersion getItemVersion(SessionContext context, Id itemId, Id versionId) {
    String privateSpace = context.getUser().getUserName();
    return getOptionalItemVersion(context, privateSpace, itemId, versionId)
        .orElseThrow(() -> new RuntimeException(
            String.format(StateStoreMessages.ITEM_VERSION_NOT_EXIST,
                itemId, versionId, privateSpace)));
  }


  void createItemVersion(SessionContext context, Id itemId, Id baseVersionId,
                         Id versionId, Info versionInfo) {
    String space = context.getUser().getUserName();
    getVersionDao(context)
        .create(context, space, itemId, versionId, baseVersionId, versionInfo);

    if (baseVersionId == null) {
      return;
    }

    copyElements(context, space, itemId, baseVersionId, versionId);
  }

  private void copyElements(SessionContext context, String space, Id itemId, Id sourceVersionId,
                            Id targetVersionId) {
    ElementRepository elementRepository = getElementRepository(context);

    ElementEntityContext elementContext = new ElementEntityContext(space, itemId, sourceVersionId);
    Collection<ElementEntity> elementEntities = elementRepository.list(context, elementContext);

    elementContext.setVersionId(targetVersionId);
    elementEntities
        .forEach(elementEntity -> elementRepository.create(context, elementContext, elementEntity));
  }

  private Optional<ItemVersion> getOptionalItemVersion(SessionContext context, String space,
                                               Id itemId, Id versionId) {
    return getVersionDao(context).get(context, space, itemId, versionId);
  }

  VersionDao getVersionDao(SessionContext context) {
    return VersionDaoFactory.getInstance().createInterface(context);
  }

  private ElementRepository getElementRepository(SessionContext context) {
    return ElementRepositoryFactory.getInstance().createInterface(context);
  }
}