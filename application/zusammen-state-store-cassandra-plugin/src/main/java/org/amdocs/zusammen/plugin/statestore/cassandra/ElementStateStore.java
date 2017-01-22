/*
 * Copyright © 2016 European Support Limited
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
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.DaoConstants;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepositoryFactory;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

class ElementStateStore {

  Collection<ElementInfo> listElements(SessionContext context, ElementContext elementContext,
                                       Id elementId) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(getPrivateSpaceName(context), elementContext);

    if (elementId == null) {
      elementId = DaoConstants.ROOT_ELEMENTS_PARENT_ID;
    }

    ElementRepository elementRepository = getElementRepository(context);
    return elementRepository.get(context, elementEntityContext, new ElementEntity(elementId))
        .map(ElementEntity::getSubElementIds).orElse(new HashSet<>()).stream()
        .map(subElementId -> elementRepository
            .get(context, elementEntityContext, new ElementEntity(subElementId)).get())
        .filter(Objects::nonNull)
        .map(subElement -> StateStoreUtils.getElementInfo(elementEntityContext, subElement))
        .collect(Collectors.toList());
  }

  boolean isElementExist(SessionContext context, ElementContext elementContext,
                         Id elementId) {
    return getElementRepository(context).get(context,
        new ElementEntityContext(getPrivateSpaceName(context), elementContext),
        new ElementEntity(elementId)).isPresent();
  }

  ElementInfo getElement(SessionContext context, ElementContext elementContext,
                         Id elementId) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(getPrivateSpaceName(context), elementContext);
    return getElement(context, elementEntityContext, elementId);
  }

  void createElement(SessionContext context, ElementInfo elementInfo) {
    getElementRepository(context).create(context,
        new ElementEntityContext(getSpaceName(elementInfo.getSpace(), context),
            elementInfo.getItemId(),
            elementInfo.getVersionId()),
        StateStoreUtils.getElementEntity(elementInfo));
  }

  void updateElement(SessionContext context, ElementInfo elementInfo) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(getSpaceName(elementInfo.getSpace(), context),
            elementInfo.getItemId(),
            elementInfo.getVersionId());
    getElementRepository(context).update(context, elementEntityContext,
        StateStoreUtils.getElementEntity(elementInfo));
  }

  void deleteElement(SessionContext context, ElementInfo elementInfo) {
    deleteElementHierarchy(getElementRepository(context),
        context,
        new ElementEntityContext(getSpaceName(elementInfo.getSpace(), context),
            elementInfo.getItemId(), elementInfo.getVersionId()),
        StateStoreUtils.getElementEntity(elementInfo));
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

  private ElementInfo getElement(SessionContext context, ElementEntityContext elementEntityContext,
                                 Id elementId) {
    return getElementRepository(context)
        .get(context, elementEntityContext, new ElementEntity(elementId))
        .map(elementEntity -> StateStoreUtils.getElementInfo(elementEntityContext,
            elementEntity)).orElse(null);
  }

  private String getSpaceName(Space space, SessionContext context) {
    switch (space) {
      case PUBLIC:
        return StateStoreConstants.PUBLIC_SPACE;
      case PRIVATE:
        return getPrivateSpaceName(context);
      default:
        throw new IllegalArgumentException(String.format("Space %s is not supported.", space));
    }
  }

  private String getPrivateSpaceName(SessionContext context) {
    return context.getUser().getUserName();
  }

  protected ElementRepository getElementRepository(SessionContext context) {
    return ElementRepositoryFactory.getInstance().createInterface(context);
  }
}