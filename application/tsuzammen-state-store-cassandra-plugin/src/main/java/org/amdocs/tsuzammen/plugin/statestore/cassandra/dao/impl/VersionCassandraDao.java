package org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Query;
import org.amdocs.tsuzammen.commons.datatypes.Id;
import org.amdocs.tsuzammen.commons.datatypes.SessionContext;
import org.amdocs.tsuzammen.commons.datatypes.item.Info;
import org.amdocs.tsuzammen.commons.datatypes.item.ItemVersion;
import org.amdocs.tsuzammen.plugin.statestore.cassandra.dao.VersionDao;
import org.amdocs.tsuzammen.utils.fileutils.json.JsonUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VersionCassandraDao implements VersionDao {

  @Override
  public void create(SessionContext context, String space, Id itemId, Id versionId,
                     String baseVersionId, Info versionInfo) {
    getAccessor(context).create(space, itemId, versionId, baseVersionId,
        JsonUtil.object2Json(versionInfo));
  }

  @Override
  public void save(SessionContext context, String space, Id itemId, Id versionId,
                   Info versionInfo) {
    getAccessor(context).save(JsonUtil.object2Json(versionInfo), space, itemId, versionId);
  }

  @Override
  public void delete(SessionContext context, String space, Id itemId, Id versionId) {
    getAccessor(context).delete(space, itemId, versionId);
  }

  @Override
  public Collection<ItemVersion> list(SessionContext context, String space, Id itemId) {
    List<Row> rows = getAccessor(context).list(space, itemId).all();
    return rows == null ? new ArrayList<>() :
        rows.stream().map(this::createItemVersion).collect(Collectors.toList());
  }

  @Override
  public Optional<ItemVersion> get(SessionContext context, String space, Id itemId,
                                   Id versionId) {
    Row row = getAccessor(context).get(space, itemId, versionId).one();
    return row == null ? Optional.empty() : Optional.of(createItemVersion(row));
  }

  private ItemVersion createItemVersion(Row row) {
    ItemVersion itemVersion = new ItemVersion();
    itemVersion.setId(row.getString(VersionField.VERSION_ID));
    itemVersion.setBaseId(row.getString(VersionField.BASE_VERSION_ID));
    itemVersion.setInfo(JsonUtil.json2Object(row.getString(VersionField.VERSION_INFO), Info.class));
    return itemVersion;
  }

  private VersionAccessor getAccessor(SessionContext context) {
    return CassandraDaoUtils.getAccessor(context, VersionAccessor.class);
  }

  @Accessor
  interface VersionAccessor {

    @Query("INSERT INTO version (space, item_id, version_id, base_version_id, version_info) " +
        "VALUES (?, ?, ?, ?, ?)")
    void create(String space, String itemId, String versionId, String baseVersionId,
                String versionInfo);

    @Query("UPDATE version SET version_info=? WHERE space=? AND item_id=? AND version_id=?")
    void save(String versionInfo, String space, String itemId, String versionId);

    @Query("DELETE FROM version WHERE space=? AND item_id=? AND version_id=?")
    void delete(String space, String itemId, String versionId);

    @Query("SELECT version_id, base_version_id, version_info FROM version " +
        "WHERE space=? AND item_id=? AND version_id=?")
    ResultSet get(String space, String itemId, String versionId);

    @Query("SELECT version_id, base_version_id, version_info FROM version " +
        "WHERE space=? AND item_id=?")
    ResultSet list(String space, Id itemId);
  }

  private static final class VersionField {
    private static final String VERSION_ID = "version_id";
    private static final String BASE_VERSION_ID = "base_version_id";
    private static final String VERSION_INFO = "version_info";
  }

}
