package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;

import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.RelationInfo;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.RelationDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.util.Map;
import java.util.stream.Collectors;

public class RelationCassandraDao implements RelationDao {

  private static final class Field {
    private static final String RELATION_ID = "relation_id";
    private static final String RELATION = "relation";
  }

  @Override
  public void save(SessionContext context, String space, String itemId, String versionId,
                   String parentEntityId, String parentContentName, String entityId, String relationId,
                   RelationInfo relation) {
    CassandraDaoUtils.getSession(context)
        .execute(getAccessor(context).save(space, itemId,
            versionId,
            parentEntityId, parentContentName, entityId,
            relationId,
            JsonUtil.object2Json(relation)));
  }

  @Override
  public void save(SessionContext context, String space, String itemId, String versionId,
                   String parentEntityId, String parentContentName, String entityId,
                   Map<String, RelationInfo> relations) {
    RelationAccessor accessor = getAccessor(context);

    BatchStatement saveBatch = new BatchStatement();
    relations.entrySet().forEach(relationEntry ->
        saveBatch.add((accessor.save(space, itemId, versionId,
            parentEntityId, parentContentName, entityId,
            relationEntry.getKey(),
            JsonUtil.object2Json(relationEntry.getValue())))));

    CassandraDaoUtils.getSession(context).execute(saveBatch);
  }

  @Override
  public Map<String, RelationInfo> list(SessionContext context, String space, String itemId,
                                    String versionId, String parentEntityId, String parentContentName,
                                    String entityId) {
    ResultSet rows = getAccessor(context).list(space, itemId, versionId,
        parentEntityId, parentContentName, entityId);

    return rows.all().stream().collect(Collectors.toMap(
        row -> new String(row.getString(Field.RELATION_ID)),
        this::createRelation));
  }

  private RelationInfo createRelation(Row row) {
    return JsonUtil.json2Object(row.getString(Field.RELATION), RelationInfo.class);
  }

  private RelationAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, RelationAccessor.class);
  }

  @Accessor
  interface RelationAccessor {
    @Query("insert into relation (space, item_id, version_id, parent_entity_id, " +
        "parent_content_name, entity_id, relation_id, relation) values (?, ?, ?, ?, ?, ?, ?, ?)")
    Statement save(String space, String itemId, String versionId,
                   String parentEntityId, String contentName, String entityId, String relationId,
                   String relation);

    @Query("select relation_id, relation from relation where space=? and item_id=? " +
        "and version_id=? and parent_entity_id=? and parent_content_name=? and entity_id = ?")
    ResultSet list(String space, String itemId, String versionId,
                   String parentEntityId, String contentName, String entityId);
  }
}
