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
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepositoryFactory;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;

import java.util.Set;

public class ElementStateStore {

  public Namespace getElementNamespace(SessionContext context,
                                       ElementContext elementContext, Id elementId) {
    String space = context.getUser().getUserName();
    return getElementRepository(context).get(context,
        new ElementEntityContext(space, elementContext),
        new ElementEntity(elementId))
        .map(ElementEntity::getNamespace)
        .orElseThrow(() ->
            new RuntimeException(String.format(StateStoreMessages.ELEMENT_NOT_EXIST,
                elementContext.getItemId(), elementContext.getVersionId(), elementId, space)));
  }


  public boolean isElementExist(SessionContext context, ElementContext elementContext,
                                Id elementId) {
    return getElementRepository(context).get(context,
        new ElementEntityContext(context.getUser().getUserName(), elementContext),
        new ElementEntity(elementId)).isPresent();
  }


  public ElementInfo getElement(SessionContext context, ElementContext elementContext,
                                Id elementId, FetchCriteria fetchCriteria) {
    return StateStoreUtils.getElementInfo(
        getElementEntity(context,
            new ElementEntityContext(context.getUser().getUserName(), elementContext),
            new ElementEntity(elementId)));
  }

  private ElementEntity getElementEntity(SessionContext context,
                                 ElementEntityContext elementEntityContext,
                                 ElementEntity elementEntity) {
    return getElementRepository(context).get(context, elementEntityContext, elementEntity)
        .orElseThrow(() ->
            new RuntimeException(String.format(StateStoreMessages.ELEMENT_NOT_EXIST,
                elementEntityContext.getItemId(), elementEntityContext.getVersionId(),
                elementEntity, elementEntityContext.getSpace())));
  }


  public void createElement(SessionContext context, ElementContext elementContext,
                            Namespace namespace, ElementInfo elementInfo) {
    getElementRepository(context).create(context,
        new ElementEntityContext(context.getUser().getUserName(), elementContext),
        StateStoreUtils.getElementEntity(namespace, elementInfo));
  }


  public void saveElement(SessionContext context, ElementContext elementContext,
                          ElementInfo elementInfo) {
    getElement(context, elementContext, elementInfo.getId(), null);
    getElementRepository(context).update(context,
        new ElementEntityContext(context.getUser().getUserName(), elementContext),
        StateStoreUtils.getElementEntity(null, elementInfo));
  }


  public void deleteElement(SessionContext context, ElementContext elementContext,
                            ElementInfo elementInfo) {
    deleteElementHierarchy(getElementRepository(context),
        context,
        new ElementEntityContext(context.getUser().getUserName(), elementContext),
        StateStoreUtils.getElementEntity(null, elementInfo));
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

  protected ElementRepository getElementRepository(SessionContext context) {
    return ElementRepositoryFactory.getInstance().createInterface(context);
  }
}