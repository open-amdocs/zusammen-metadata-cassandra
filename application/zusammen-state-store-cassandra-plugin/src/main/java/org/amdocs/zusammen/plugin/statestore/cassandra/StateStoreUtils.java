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

package org.amdocs.zusammen.plugin.statestore.cassandra;

import org.amdocs.zusammen.datatypes.item.ElementInfo;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.DaoConstants;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.stream.Collectors;

class StateStoreUtils {

  static ElementEntity getElementEntity(ElementInfo elementInfo) {
    ElementEntity elementEntity = new ElementEntity(elementInfo.getId());
    elementEntity.setNamespace(elementInfo.getNamespace());
    elementEntity.setParentId(elementInfo.getParentId() == null
        ? DaoConstants.ROOT_ELEMENTS_PARENT_ID
        : elementInfo.getParentId());
    elementEntity.setInfo(elementInfo.getInfo());
    elementEntity.setRelations(elementInfo.getRelations());
    return elementEntity;
  }

  static ElementInfo getElementInfo(ElementEntityContext elementEntityContext, ElementEntity
      elementEntity) {
    ElementInfo elementInfo = new ElementInfo(elementEntityContext.getItemId(),
        elementEntityContext.getVersionId(), elementEntity.getId(), elementEntity.getParentId());
    elementInfo.setInfo(elementEntity.getInfo());
    elementInfo.setRelations(elementEntity.getRelations());
    elementInfo.setSubElements(elementEntity.getSubElementIds().stream()
        .map(subElementId -> new ElementInfo(
            elementInfo.getItemId(), elementInfo.getVersionId(), subElementId, elementInfo.getId()))
        .collect(Collectors.toList()));
    return elementInfo;
  }
}
