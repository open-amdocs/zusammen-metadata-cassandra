package org.amdocs.zusammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import com.google.gson.reflect.TypeToken;
import org.amdocs.zusammen.datatypes.Id;
import org.amdocs.zusammen.datatypes.SessionContext;
import org.amdocs.zusammen.datatypes.item.Info;
import org.amdocs.zusammen.datatypes.item.ItemVersion;
import org.amdocs.zusammen.datatypes.item.ItemVersionData;
import org.amdocs.zusammen.datatypes.item.Relation;
import org.amdocs.zusammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.zusammen.utils.fileutils.json.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VersionCassandraDao implements VersionDao {

  @Override
  public void create(SessionContext context, String space, Id itemId, Id versionId,
                     Id baseVersionId, ItemVersionData data) {
    String baseVersion = baseVersionId != null ? baseVersionId.toString() : null;

    getAccessor(context)
        .create(space, itemId.toString(), versionId.toString(), baseVersion,
            JsonUtil.object2Json(data.getInfo()), JsonUtil.object2Json(data.getRelations()));
  }

  @Override
  public void update(SessionContext context, String space, Id itemId, Id versionId,
                     ItemVersionData data) {
    getAccessor(context)
        .update(JsonUtil.object2Json(data.getInfo()), JsonUtil.object2Json(data.getRelations()),
            space, itemId.toString(), versionId.toString());
  }

  @Override
  public void delete(SessionContext context, String space, Id itemId, Id versionId) {
    getAccessor(context).delete(space, itemId.toString(), versionId.toString());
  }

  @Override
  public Collection<ItemVersion> list(SessionContext context, String space, Id itemId) {
    List<Row> rows = getAccessor(context).list(space, itemId.toString()).all();
    return rows == null ? new ArrayList<>() :
        rows.stream().map(this::createItemVersion).collect(Collectors.toList());
  }

  @Override
  public Optional<ItemVersion> get(SessionContext context, String space, Id itemId,
                                   Id versionId) {
    Row row = getAccessor(context).get(space, itemId.toString(), versionId.toString()).one();
    return row == null ? Optional.empty() : Optional.of(createItemVersion(row));
  }

  private ItemVersion createItemVersion(Row row) {
    ItemVersion itemVersion = new ItemVersion();
    itemVersion.setId(new Id(row.getString(VersionField.VERSION_ID)));
    itemVersion.setBaseId(new Id(row.getString(VersionField.BASE_VERSION_ID)));
    itemVersion.setData(new ItemVersionData());
    itemVersion.getData()
        .setInfo(JsonUtil.json2Object(row.getString(VersionField.INFO), Info.class));
    itemVersion.getData()
        .setRelations(JsonUtil.json2Object(row.getString(VersionField.RELATIONS),
            new TypeToken<ArrayList<Relation>>() {
            }.getType()));
    return itemVersion;
  }

  private VersionAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionAccessor.class);
  }

  @Accessor
  interface VersionAccessor {

    @Query("INSERT INTO version (space, item_id, version_id, base_version_id, info, relations) " +
        "VALUES (?, ?, ?, ?, ?, ?)")
    void create(String space, String itemId, String versionId, String baseVersionId,
                String info, String relations);

    @Query("UPDATE version SET info=?, relations=? WHERE space=? AND item_id=? AND version_id=?")
    void update(String info, String relations, String space, String itemId, String versionId);

    @Query("DELETE FROM version WHERE space=? AND item_id=? AND version_id=?")
    void delete(String space, String itemId, String versionId);

    @Query("SELECT version_id, base_version_id, info, relations FROM version " +
        "WHERE space=? AND item_id=? AND version_id=?")
    ResultSet get(String space, String itemId, String versionId);

    @Query("SELECT version_id, base_version_id, info, relations FROM version " +
        "WHERE space=? AND item_id=?")
    ResultSet list(String space, String itemId);
  }

  private static final class VersionField {
    private static final String VERSION_ID = "version_id";
    private static final String BASE_VERSION_ID = "base_version_id";
    private static final String INFO = "info";
    private static final String RELATIONS = "relations";
  }

}
