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
import org.amdocs.zusammen.datatypes.item.ElementContext;
import org.amdocs.zusammen.datatypes.item.ElementInfo;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepositoryFactory;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.amdocs.zusammen.plugin.statestore.cassandra.StateStoreUtil.getSpaceName;

class ElementStateStore {

  Collection<ElementInfo> listElements(SessionContext context, ElementContext elementContext,
                                       Id elementId) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(StateStoreUtil.getPrivateSpaceName(context), elementContext);

    if (elementId == null) {
      elementId = StateStoreConstants.ROOT_ELEMENTS_PARENT_ID;
    }

    ElementRepository elementRepository = getElementRepository(context);
    return elementRepository.get(context, elementEntityContext, new ElementEntity(elementId))
        .map(ElementEntity::getSubElementIds).orElse(new HashSet<>()).stream()
        .map(subElementId -> elementRepository
            .get(context, elementEntityContext, new ElementEntity(subElementId)).get())
        .filter(Objects::nonNull)
        .map(subElement -> StateStoreUtil.getElementInfo(elementEntityContext, subElement))
        .collect(Collectors.toList());
  }

  boolean isElementExist(SessionContext context, ElementContext elementContext,
                         Id elementId) {
    return getElementRepository(context).get(context,
        new ElementEntityContext(StateStoreUtil.getPrivateSpaceName(context), elementContext),
        new ElementEntity(elementId)).isPresent();
  }

  ElementInfo getElement(SessionContext context, ElementContext elementContext,
                         Id elementId) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(StateStoreUtil.getPrivateSpaceName(context), elementContext);
    return getElementRepository(context)
        .get(context, elementEntityContext, new ElementEntity(elementId))
        .map(elementEntity -> StateStoreUtil.getElementInfo(elementEntityContext, elementEntity))
        .orElse(null);
  }

  void createElement(SessionContext context, ElementInfo elementInfo) {
    getElementRepository(context)
        .create(context,
            new ElementEntityContext(getSpaceName(context, elementInfo.getSpace()),
                elementInfo.getItemId(), elementInfo.getVersionId()),
            StateStoreUtil.getElementEntity(elementInfo));
  }

  void updateElement(SessionContext context, ElementInfo elementInfo) {
    getElementRepository(context)
        .update(context,
            new ElementEntityContext(getSpaceName(context, elementInfo.getSpace()),
                elementInfo.getItemId(), elementInfo.getVersionId()),
            StateStoreUtil.getElementEntity(elementInfo));
  }

  void deleteElement(SessionContext context, ElementInfo elementInfo) {
    deleteElementHierarchy(getElementRepository(context),
        context,
        new ElementEntityContext(getSpaceName(context, elementInfo.getSpace()),
            elementInfo.getItemId(), elementInfo.getVersionId()),
        StateStoreUtil.getElementEntity(elementInfo));
  }

  private void deleteElementHierarchy(ElementRepository elementRepository, SessionContext context,
                                      ElementEntityContext elementEntityContext,
                                      ElementEntity elementEntity) {
    Optional<ElementEntity> retrieved =
        elementRepository.get(context, elementEntityContext, elementEntity);
    if (!retrieved.isPresent()) {
      return;
    }
    retrieved.get().getSubElementIds().stream()
        .map(ElementEntity::new)
        .forEach(subElementEntity -> deleteElementHierarchy(
            elementRepository, context, elementEntityContext, subElementEntity));

    // only for the first one the parentId will populated (so it'll be removed from its parent)
    elementRepository.delete(context, elementEntityContext, elementEntity);
  }

  protected ElementRepository getElementRepository(SessionContext context) {
    return ElementRepositoryFactory.getInstance().createInterface(context);
  }
}