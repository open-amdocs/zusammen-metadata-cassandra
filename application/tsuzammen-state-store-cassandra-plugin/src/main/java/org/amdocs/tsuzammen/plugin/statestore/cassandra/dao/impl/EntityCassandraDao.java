package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.EntityDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.net.URI;

public class EntityCassandraDao implements EntityDao {
  private static final String ROOT_ENTITY = "root";

  @Override
  public void create(SessionContext context, String space, String itemId, String versionId,
                     URI namespace, String entityId, Info entityInfo) {
    getVersionEntitiesAccessor(context).save(space, itemId, versionId, entityId);
    save(context, space, itemId, versionId, namespace, entityId, entityInfo);
  }

  @Override
  public void save(SessionContext context, String space, String itemId, String versionId,
                   URI namespace,
                   String entityId, Info entityInfo) {
    EntityContext entityContext = new EntityContext(namespace).invoke();

    getEntityAccessor(context).save(space, itemId, versionId,
        entityContext.getParentEntityId(), entityContext.getParentContent(), entityId,
        JsonUtil.object2Json(entityInfo), namespace.getPath());
  }

  @Override
  public void delete(SessionContext context, String space, String itemId, String versionId,
                     URI namespace,
                     String entityId) {
    // TODO: 12/15/2016 problem: need to remove also sub entities from version_entities!
    getVersionEntitiesAccessor(context).delete(space, itemId, versionId, entityId);

    EntityContext entityContext = new EntityContext(namespace).invoke();

    getEntityAccessor(context).delete(space, itemId, versionId,
        entityContext.getParentEntityId(), entityContext.getParentContent(), entityId);
    getEntityAccessor(context).deleteSubEntities(space, itemId, versionId, entityId);
  }

  @Override
  public Info get(SessionContext context, String space, String itemId, String versionId,
                  URI namespace, String entityId) {
    EntityContext entityContext = new EntityContext(namespace).invoke();

    ResultSet rows = getEntityAccessor(context).get(space, itemId, versionId,
        entityContext.getParentEntityId(), entityContext.getParentContent(), entityId);

    return JsonUtil.json2Object(rows.one().getString(EntityField.ENTITY_INFO), Info.class);
  }

  private EntityAccessor getEntityAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, EntityAccessor.class);
  }

  private VersionEntitiesAccessor getVersionEntitiesAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionEntitiesAccessor.class);
  }

  @Accessor
  interface EntityAccessor {

    @Query("INSERT INTO entity " +
        "(space, item_id, version_id, parent_entity_id, parent_content_name, entity_id, " +
        "entity_info, namespace) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")
    void save(String space, String itemId, String versionId, String parentEntityId, String
        parentContent, String entityId, String entity, String namespace);

    @Query("DELETE FROM entity WHERE space=? AND item_id=? AND version_id=? AND " +
        "parent_entity_id=? AND parent_content_name=? AND entity_id=?")
    void delete(String space, String itemId, String versionId, String parentEntityId, String
        parentContent, String entityId);

    @Query("DELETE FROM entity WHERE space=? AND item_id=? AND version_id=? AND parent_entity_id=?")
    void deleteSubEntities(String space, String itemId, String versionId, String entityId);

    @Query("SELECT entity_info FROM entity WHERE space=? AND item_id=? AND version_id=? AND " +
        "parent_entity_id=? AND parent_content_name=? AND entity_id=?")
    ResultSet get(String space, String itemId, String versionId, String parentEntityId, String
        parentContent, String entityId);
  }

  @Accessor
  interface VersionEntitiesAccessor {

    @Query(
        "INSERT INTO version_entities (space, item_id, version_id, entity_id) VALUES (?, ?, ?, ?)")
    void save(String space, String itemId, String versionId, String entityId);

    @Query("DELETE FROM entity WHERE space=? AND item_id=? AND version_id=? AND entity_id=?")
    void delete(String space, String itemId, String versionId, String entityId);
  }

  private static final class EntityField {
    private static final String ENTITY_INFO = "entity_info";
  }

  private static class EntityContext {
    private final URI namespace;
    private String parentEntityId;
    private String parentContent;

    private EntityContext(URI namespace) {
      this.namespace = namespace;
    }

    private String getParentEntityId() {
      return parentEntityId;
    }

    private String getParentContent() {
      return parentContent;
    }

    private EntityContext invoke() {
      String[] namespaceTokens = namespace.getPath().split("/");

      parentEntityId =
          namespaceTokens.length == 1 ? ROOT_ENTITY : namespaceTokens[namespaceTokens.length - 2];
      parentContent = namespaceTokens[namespaceTokens.length - 1];
      return this;
    }
  }
}
