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
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.ElementDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.net.URI;

public class ElementCassandraDao implements ElementDao {
  private static final String ROOT_ENTITY = "root";

  @Override
  public void create(SessionContext context, String space, String itemId, String versionId,
                     URI namespace, String elementId, Info elementInfo) {
    getVersionEntitiesAccessor(context).save(space, itemId, versionId, elementId);
    save(context, space, itemId, versionId, namespace, elementId, elementInfo);
  }

  @Override
  public void save(SessionContext context, String space, String itemId, String versionId,
                   URI namespace,
                   String elementId, Info elementInfo) {
    ElementContext elementContext = new ElementContext(namespace).invoke();

    getElementAccessor(context).save(space, itemId, versionId,
        elementContext.getParentElementId(), elementContext.getParentContent(), elementId,
        JsonUtil.object2Json(elementInfo), namespace.getPath());
  }

  @Override
  public void delete(SessionContext context, String space, String itemId, String versionId,
                     URI namespace,
                     String elementId) {
    // TODO: 12/15/2016 problem: need to remove also sub entities from version_entities!
    getVersionEntitiesAccessor(context).delete(space, itemId, versionId, elementId);

    ElementContext elementContext = new ElementContext(namespace).invoke();

    getElementAccessor(context).delete(space, itemId, versionId,
        elementContext.getParentElementId(), elementContext.getParentContent(), elementId);
    getElementAccessor(context).deleteSubEntities(space, itemId, versionId, elementId);
  }

  @Override
  public Info get(SessionContext context, String space, String itemId, String versionId,
                  URI namespace, String elementId) {
    ElementContext elementContext = new ElementContext(namespace).invoke();

    ResultSet rows = getElementAccessor(context).get(space, itemId, versionId,
        elementContext.getParentElementId(), elementContext.getParentContent(), elementId);

    return JsonUtil.json2Object(rows.one().getString(ElementField.ENTITY_INFO), Info.class);
  }

  private ElementAccessor getElementAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, ElementAccessor.class);
  }

  private VersionEntitiesAccessor getVersionEntitiesAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionEntitiesAccessor.class);
  }

  @Accessor
  interface ElementAccessor {

    @Query("INSERT INTO element " +
        "(space, item_id, version_id, parent_element_id, parent_content_name, element_id, " +
        "element_info, namespace) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
    void save(String space, String itemId, String versionId, String parentElementId, String
        parentContent, String elementId, String element, String namespace);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND " +
        "parent_element_id=? AND parent_content_name=? AND element_id=?")
    void delete(String space, String itemId, String versionId, String parentElementId, String
        parentContent, String elementId);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND parent_element_id=?")
    void deleteSubEntities(String space, String itemId, String versionId, String elementId);

    @Query("SELECT element_info FROM element WHERE space=? AND item_id=? AND version_id=? AND " +
        "parent_element_id=? AND parent_content_name=? AND element_id=?")
    ResultSet get(String space, String itemId, String versionId, String parentElementId, String
        parentContent, String elementId);
  }

  @Accessor
  interface VersionEntitiesAccessor {

    @Query(
        "INSERT INTO version_entities (space, item_id, version_id, element_id) VALUES (?, ?, ?, ?)")
    void save(String space, String itemId, String versionId, String elementId);

    @Query("DELETE FROM element WHERE space=? AND item_id=? AND version_id=? AND element_id=?")
    void delete(String space, String itemId, String versionId, String elementId);
  }

  private static final class ElementField {
    private static final String ENTITY_INFO = "element_info";
  }

  private static class ElementContext {
    private final URI namespace;
    private String parentElementId;
    private String parentContent;

    private ElementContext(URI namespace) {
      this.namespace = namespace;
    }

    private String getParentElementId() {
      return parentElementId;
    }

    private String getParentContent() {
      return parentContent;
    }

    private ElementContext invoke() {
      String[] namespaceTokens = namespace.getPath().split("/");

      parentElementId =
          namespaceTokens.length == 1 ? ROOT_ENTITY : namespaceTokens[namespaceTokens.length - 2];
      parentContent = namespaceTokens[namespaceTokens.length - 1];
      return this;
    }
  }
}
