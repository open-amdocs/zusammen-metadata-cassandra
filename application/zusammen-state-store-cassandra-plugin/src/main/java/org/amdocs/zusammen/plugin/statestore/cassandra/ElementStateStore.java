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

import org.amdocs.zusammen.datatypes.FetchCriteria;
import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.ElementContext;
import org.amdocs.zusammen.datatypes.item.ElementInfo;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.DaoConstants;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepositoryFactory;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class ElementStateStore {

  Collection<ElementInfo> listElements(SessionContext context, ElementContext elementContext,
                                       Id elementId) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(context.getUser().getUserName(), elementContext);

    if (elementId == null) {
      elementId = DaoConstants.ROOT_ELEMENTS_PARENT_ID;
    }

    Set<Id> subElementIds =
        getElementEntity(context, elementEntityContext, new ElementEntity(elementId))
            .getSubElementIds();

    return subElementIds.stream()
        .map(subElementId -> getElementRepository(context)
            .get(context, elementEntityContext, new ElementEntity(subElementId)).get())
        .filter(Objects::nonNull)
        .map(subElement -> StateStoreUtils.getElementInfo(elementEntityContext, subElement))
        .collect(Collectors.toList());
  }

  boolean isElementExist(SessionContext context, ElementContext elementContext,
                         Id elementId) {
    return getElementRepository(context).get(context,
        new ElementEntityContext(context.getUser().getUserName(), elementContext),
        new ElementEntity(elementId)).isPresent();
  }

  ElementInfo getElement(SessionContext context, ElementContext elementContext,
                         Id elementId, FetchCriteria fetchCriteria) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(context.getUser().getUserName(), elementContext);
    return getElement(context, elementEntityContext, elementId);
  }

  private ElementInfo getElement(SessionContext context, ElementEntityContext elementEntityContext,
                                 Id elementId) {
    return StateStoreUtils.getElementInfo(elementEntityContext,
        getElementEntity(context, elementEntityContext, new ElementEntity(elementId)));
  }

  void createElement(SessionContext context, ElementInfo elementInfo) {
    getElementRepository(context).create(context,
        new ElementEntityContext(context.getUser().getUserName(), elementInfo.getItemId(),
            elementInfo.getVersionId()),
        StateStoreUtils.getElementEntity(elementInfo));
  }

  void updateElement(SessionContext context, ElementInfo elementInfo) {
    ElementEntityContext elementEntityContext =
        new ElementEntityContext(context.getUser().getUserName(),
            elementInfo.getItemId(),
            elementInfo.getVersionId());
    getElement(context, elementEntityContext, elementInfo.getId());
    getElementRepository(context).update(context, elementEntityContext,
        StateStoreUtils.getElementEntity(elementInfo));
  }

  void deleteElement(SessionContext context, ElementInfo elementInfo) {
    deleteElementHierarchy(getElementRepository(context),
        context,
        new ElementEntityContext(context.getUser().getUserName(), elementInfo.getItemId(),
            elementInfo.getVersionId()),
        StateStoreUtils.getElementEntity(elementInfo));
  }

  private void deleteElementHierarchy(ElementRepository elementRepository, SessionContext context,
                                      ElementEntityContext elementEntityContext,
                                      ElementEntity elementEntity) {
    Set<Id> subElementIds =
        getElementEntity(context, elementEntityContext, elementEntity).getSubElementIds();
    subElementIds.stream()
        .map(ElementEntity::new)
        .forEach(subElementEntity -> deleteElementHierarchy(
            elementRepository, context, elementEntityContext, subElementEntity));

    // only for the first one the parentId will populated (so it'll be removed from its parent)
    elementRepository.delete(context, elementEntityContext, elementEntity);
  }

  private ElementEntity getElementEntity(SessionContext context,
                                         ElementEntityContext elementEntityContext,
                                         ElementEntity elementEntity) {
    return getElementRepository(context).get(context, elementEntityContext, elementEntity)
        .orElseThrow(() -> elementEntity.getId() == DaoConstants.ROOT_ELEMENTS_PARENT_ID
            ? new RuntimeException(String.format(StateStoreMessages.ELEMENTS_NOT_EXIST,
            elementEntityContext.getItemId(), elementEntityContext.getVersionId(),
            elementEntityContext.getSpace()))
            : new RuntimeException(String.format(StateStoreMessages.ELEMENT_NOT_EXIST,
                elementEntityContext.getItemId(), elementEntityContext.getVersionId(),
                elementEntity, elementEntityContext.getSpace()))
        );
  }

  protected ElementRepository getElementRepository(SessionContext context) {
    return ElementRepositoryFactory.getInstance().createInterface(context);
  }
}