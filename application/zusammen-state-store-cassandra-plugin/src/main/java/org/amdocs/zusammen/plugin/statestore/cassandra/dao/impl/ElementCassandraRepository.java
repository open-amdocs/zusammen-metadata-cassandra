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

package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.gson.reflect.TypeToken;
import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.Namespace;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.Relation;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.types.ElementEntityContext;
import org.amdocs.zusammen.utils.fileutils.json.JsonUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class ElementCassandraRepository implements ElementRepository {

  @Override
  public Collection<ElementEntity> list(SessionContext context,
                                        ElementEntityContext elementContext) {
    Set<String> elementIds = getVersionElementIds(context, elementContext);

    return elementIds.stream()
        .map(elementId -> get(context, elementContext, new ElementEntity(new Id(elementId))).get())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  @Override
  public void create(SessionContext context, ElementEntityContext elementContext,
                     ElementEntity element) {
    createElement(context, elementContext, element);
    addElementToParent(context, elementContext, element);
  }

  @Override
  public void update(SessionContext context, ElementEntityContext elementContext,
                     ElementEntity element) {
    updateElement(context, elementContext, element);
  }

  @Override
  public void delete(SessionContext context, ElementEntityContext elementContext,
                     ElementEntity element) {
    removeElementFromParent(context, elementContext, element);
    deleteElement(context, elementContext, element);
  }

  @Override
  public Optional<ElementEntity> get(SessionContext context, ElementEntityContext elementContext,
                                     ElementEntity element) {
    Row row = getElementAccessor(context).get(
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getId().toString()).one();

    return row == null ? Optional.empty() : Optional.of(getElementEntity(element, row));
  }

  private ElementAccessor getElementAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, ElementAccessor.class);
  }

  private VersionElementsAccessor getVersionElementsAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionElementsAccessor.class);
  }

  private void createElement(SessionContext context, ElementEntityContext elementContext,
                             ElementEntity element) {
    Set<String> subElementIds =
        element.getSubElementIds().stream().map(Id::toString).collect(Collectors.toSet());

    getElementAccessor(context).create(
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getId().toString(),
        element.getParentId().toString(),
        element.getNamespace().toString(),
        JsonUtil.object2Json(element.getInfo()),
        JsonUtil.object2Json(element.getRelations()),
        subElementIds);

    getVersionElementsAccessor(context).addElements(
        Collections.singleton(element.getId().toString()),
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString());
  }

  private void updateElement(SessionContext context, ElementEntityContext elementContext,
                             ElementEntity element) {
    getElementAccessor(context).update(
        JsonUtil.object2Json(element.getInfo()),
        JsonUtil.object2Json(element.getRelations()),
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getId().toString());
  }

  private void deleteElement(SessionContext context, ElementEntityContext elementContext,
                             ElementEntity element) {
    getElementAccessor(context).delete(
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getId().toString());

    getVersionElementsAccessor(context).removeElements(
        Collections.singleton(element.getId().toString()),
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString());
  }

  private void addElementToParent(SessionContext context, ElementEntityContext elementContext,
                                  ElementEntity element) {
    getElementAccessor(context).addSubElements(
        Collections.singleton(element.getId().toString()),
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getParentId().toString());
  }

  private void removeElementFromParent(SessionContext context, ElementEntityContext elementContext,
                                       ElementEntity element) {
    if (element.getParentId() == null) {
      return;
    }
    getElementAccessor(context).removeSubElements(
        Collections.singleton(element.getId().toString()),
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString(),
        element.getParentId().toString());
  }

  private ElementEntity getElementEntity(ElementEntity element, Row row) {
    element.setNamespace(getNamespace(row.getString(ElementField.NAMESPACE)));
    element.setInfo(json2Object(row.getString(ElementField.INFO), Info.class));
    element.setRelations(
        json2Object(row.getString(ElementField.RELATIONS), new TypeToken<ArrayList<Relation>>() {
        }.getType()));
    element.setSubElementIds(row.getSet(ElementField.SUB_ELEMENT_IDS, String.class)
        .stream().map(Id::new).collect(Collectors.toSet()));
    return element;
  }

  private Namespace getNamespace(String namespaceStr) {
    Namespace namespace = new Namespace();
    if (namespaceStr != null) {
      namespace.setValue(namespaceStr);
    }
    return namespace;
  }

  private static <T> T json2Object(String json, Type typeOfT) {
    return json == null ? null : JsonUtil.json2Object(json, typeOfT);
  }

  private Set<String> getVersionElementIds(SessionContext context,
                                           ElementEntityContext elementContext) {
    Row row = getVersionElementsAccessor(context).get(
        elementContext.getSpace(),
        elementContext.getItemId().toString(),
        elementContext.getVersionId().toString()).one();
    return row == null
        ? new HashSet<>()
        : row.getSet(VersionElementsField.ELEMENT_IDS, String.class);
  }

  /*
  CREATE TABLE IF NOT EXISTS element (
    space text,
    item_id text,
    version_id text,
    element_id text,
    parent_id text,
    namespace text,
    info text,
    relations text,
    sub_element_ids set<text>,
    PRIMARY KEY (( space, item_id, version_id, id ))
  );
   */
  @Accessor
  interface ElementAccessor {
    @Query(
        "UPDATE element SET parent_id=:parentId, namespace=:ns, info=:info, relations=:rels, " +
            "sub_element_ids=sub_element_ids+:subs " +
            "WHERE space=:space AND item_id=:item AND version_id=:ver AND element_id=:id")
    void create(@Param("space") String space,
                @Param("item") String itemId,
                @Param("ver") String versionId,
                @Param("id") String elementId,
                @Param("parentId") String parentElementId,
                @Param("ns") String namespace,
                @Param("info") String info,
                @Param("rels") String relations,
                @Param("subs") Set<String> subElementIds);

    @Query("UPDATE element SET info=?, relations=? " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void update(String info, String relations, String space, String itemId, String versionId,
                String elementId);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void delete(String space, String itemId, String versionId, String elementId);

    @Query("SELECT parent_id, namespace, info, relations, sub_element_ids FROM element " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    ResultSet get(String space, String itemId, String versionId, String elementId);

    @Query("UPDATE element SET sub_element_ids=sub_element_ids+? " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void addSubElements(Set<String> subElementIds, String space, String itemId, String versionId,
                        String elementId);

    @Query("UPDATE element SET sub_element_ids=sub_element_ids-? " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void removeSubElements(Set<String> subElementIds, String space, String itemId, String versionId,
                           String elementId);
  }

  private static final class ElementField {
    private static final String NAMESPACE = "namespace";
    private static final String INFO = "info";
    private static final String RELATIONS = "relations";
    private static final String SUB_ELEMENT_IDS = "sub_element_ids";
  }

  /*
  CREATE TABLE IF NOT EXISTS version_elements (
    space text,
    item_id text,
    version_id text,
    element_ids set<text>,
    PRIMARY KEY (( space, item_id, version_id ))
  );
   */
  @Accessor
  interface VersionElementsAccessor {

    @Query("UPDATE version_elements SET element_ids=element_ids+? " +
        "WHERE space=? AND item_id=? AND version_id=?")
    void addElements(Set<String> elementIds, String space, String itemId, String versionId);

    @Query("UPDATE version_elements SET element_ids=element_ids-? " +
        "WHERE space=? AND item_id=? AND version_id=?")
    void removeElements(Set<String> elementIds, String space, String itemId, String versionId);

    @Query("SELECT element_ids FROM version_elements WHERE space=? AND item_id=? AND version_id=?")
    ResultSet get(String space, String itemId, String versionId);
  }

  private static final class VersionElementsField {
    private static final String ELEMENT_IDS = "element_ids";
  }
}
