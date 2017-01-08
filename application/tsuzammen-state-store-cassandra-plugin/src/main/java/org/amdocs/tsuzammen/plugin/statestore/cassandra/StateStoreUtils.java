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

import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.item.ElementContext;
import org.amdocs.tsuzammen.datatypes.item.ElementInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;

import java.util.stream.Collectors;

public class StateStoreUtils {

  static ElementEntity getElementEntity(String space, ElementContext elementContext,
                                        Namespace namespace, ElementInfo elementInfo) {
    ElementEntity elementEntity =
        getElementEntity(space, elementContext, namespace, elementInfo.getId());
    elementEntity.setParentId(elementInfo.getParentId());
    elementEntity.setInfo(elementInfo.getInfo());
    return elementEntity;
  }

  static ElementEntity getElementEntity(String space, ElementContext elementContext,
                                        Namespace namespace, Id elementId) {
    ElementEntity elementEntity = new ElementEntity();
    elementEntity.setSpace(space);
    elementEntity.setItemId(elementContext.getItemId());
    elementEntity.setVersionId(elementContext.getVersionId());
    elementEntity.setNamespace(namespace);
    elementEntity.setId(elementId);
    return elementEntity;
  }

  static ElementInfo getElementInfo(ElementEntity elementEntity) {
    ElementInfo elementInfo = new ElementInfo(elementEntity.getId());
    elementInfo.setInfo(elementEntity.getInfo());
    elementInfo.setSubElements(elementEntity.getSubElementIds().stream()
        .map(ElementInfo::new)
        .collect(Collectors.toList()));
    return elementInfo;
  }
}
