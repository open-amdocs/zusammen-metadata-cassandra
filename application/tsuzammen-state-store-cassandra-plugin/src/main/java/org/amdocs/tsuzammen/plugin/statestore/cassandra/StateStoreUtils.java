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

import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.item.ElementInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.DaoConstants;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;

import java.util.stream.Collectors;

class StateStoreUtils {

  static ElementEntity getElementEntity(Namespace namespace, ElementInfo elementInfo) {
    ElementEntity elementEntity = new ElementEntity(elementInfo.getId());
    elementEntity.setNamespace(namespace);
    elementEntity.setParentId(elementInfo.getParentId() == null
        ? DaoConstants.ROOT_ELEMENTS_PARENT_ID
        : elementInfo.getParentId());
    elementEntity.setInfo(elementInfo.getInfo());
    elementEntity.setRelations(elementInfo.getRelations());
    return elementEntity;
  }

  static ElementInfo getElementInfo(ElementEntity elementEntity) {
    ElementInfo elementInfo = new ElementInfo(elementEntity.getId());
    elementInfo.setInfo(elementEntity.getInfo());
    elementInfo.setRelations(elementEntity.getRelations());
    elementInfo.setSubElements(elementEntity.getSubElementIds().stream()
        .map(ElementInfo::new)
        .collect(Collectors.toList()));
    return elementInfo;
  }
}
