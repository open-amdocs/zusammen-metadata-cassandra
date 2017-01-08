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

package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.datatypes.Id;
import org.amdocs.tsuzammen.datatypes.Namespace;
import org.amdocs.tsuzammen.datatypes.SessionContext;
import org.amdocs.tsuzammen.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementRepository;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.types.ElementEntity;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.util.stream.Collectors;

public class ElementCassandraRepository implements ElementRepository {

  @Override
  public void create(SessionContext context, ElementEntity elementEntity) {
    createElement(context, elementEntity);
    addElementToParent(context, elementEntity);
  }

  @Override
  public void update(SessionContext context, ElementEntity elementEntity) {
    updateElement(context, elementEntity);
  }

  @Override
  public void delete(SessionContext context, ElementEntity elementEntity) {
    removeElementFromParent(context, elementEntity);
    deleteElement(context, elementEntity);
  }

  @Override
  public ElementEntity get(SessionContext context, ElementEntity elementEntity) {
    Row row = getElementAccessor(context).get(
        elementEntity.getSpace(),
        elementEntity.getItemId().toString(),
        elementEntity.getVersionId().toString(),
        elementEntity.getId().toString()).one();

    return row == null ? null : getElementEntity(elementEntity, row);
  }

  private ElementAccessor getElementAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, ElementAccessor.class);
  }

  private VersionEntitiesAccessor getVersionEntitiesAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionEntitiesAccessor.class);
  }

  private void createElement(SessionContext context, ElementEntity elementEntity) {
    getElementAccessor(context).create(
        elementEntity.getSpace(),
        elementEntity.getItemId().toString(),
        elementEntity.getVersionId().toString(),
        elementEntity.getId().toString(),
        elementEntity.getNamespace().toString(),
        JsonUtil.object2Json(elementEntity.getInfo()));
  }

  private void updateElement(SessionContext context, ElementEntity elementEntity) {
    getElementAccessor(context).update(
        JsonUtil.object2Json(elementEntity.getInfo()),
        elementEntity.getSpace(),
        elementEntity.getItemId().toString(),
        elementEntity.getVersionId().toString(),
        elementEntity.getId().toString());
  }

  private void deleteElement(SessionContext context, ElementEntity elementEntity) {
    getElementAccessor(context).delete(
        elementEntity.getSpace(),
        elementEntity.getItemId().toString(),
        elementEntity.getVersionId().toString(),
        elementEntity.getId().toString());
  }

  private void addElementToParent(SessionContext context, ElementEntity elementEntity) {
    if (elementEntity.getParentId() != null) {
      getElementAccessor(context).addSubElement(
          elementEntity.getId().toString(),
          elementEntity.getSpace(),
          elementEntity.getItemId().toString(),
          elementEntity.getVersionId().toString(),
          elementEntity.getParentId().toString());
    }
  }

  private void removeElementFromParent(SessionContext context, ElementEntity elementEntity) {
    if (elementEntity.getParentId() != null) {
      getElementAccessor(context).removeSubElement(
          elementEntity.getId().toString(),
          elementEntity.getSpace(),
          elementEntity.getItemId().toString(),
          elementEntity.getVersionId().toString(),
          elementEntity.getParentId().toString());
    }
  }

  private ElementEntity getElementEntity(ElementEntity elementEntity, Row row) {
    elementEntity.setNamespace(
        JsonUtil.json2Object(row.getString(ElementField.NAMESPACE), Namespace.class));
    elementEntity.setInfo(JsonUtil.json2Object(row.getString(ElementField.INFO), Info.class));
    elementEntity.setSubElementIds(row.getSet(ElementField.SUB_ELEMENT_IDS, String.class)
        .stream().map(Id::new).collect(Collectors.toSet()));
    return elementEntity;
  }

  /*
  CREATE TABLE IF NOT EXISTS element (
    space text,
    item_id text,
    version_id text,
    element_id text,
    namespace text,
    info text,
    sub_element_ids set<text>,
    PRIMARY KEY (( space, item_id, version_id, element_id))
  );
   */
  @Accessor
  interface ElementAccessor {

    @Query("INSERT INTO element " +
        "(space, item_id, version_id, element_id, namespace, info) VALUES (?, ?, ?, ?, ?, ?)")
    void create(String space, String itemId, String versionId, String elementId,
                String namespace, String info);

    @Query(
        "UPDATE element SET info=? WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void update(String info, String space, String itemId, String versionId, String elementId);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void delete(String space, String itemId, String versionId, String elementId);

    @Query("SELECT namespace, info, sub_element_ids FROM element " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    ResultSet get(String space, String itemId, String versionId, String elementId);

    @Query("UPDATE element SET sub_element_ids=sub_element_ids+{?} " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void addSubElement(String subElementId, String space, String itemId, String versionId,
                       String elementId);

    @Query("UPDATE element SET sub_element_ids=sub_element_ids-{?} " +
        "WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void removeSubElement(String subElementId, String space, String itemId, String versionId,
                          String elementId);
  }

  private static final class ElementField {
    private static final String INFO = "info";
    private static final String NAMESPACE = "namespace";
    private static final String SUB_ELEMENT_IDS = "sub_element_ids";
  }

  @Accessor
  interface VersionEntitiesAccessor {

    @Query(
        "INSERT INTO version_entities (space, item_id, version_id, element_id) VALUES (?, ?, ?, ?)")
    void save(String space, String itemId, String versionId, String elementId);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void delete(String space, String itemId, String versionId, String elementId);
  }
}
